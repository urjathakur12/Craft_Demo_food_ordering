package com.example.demo.project.food.ordering.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.project.food.ordering.exception.CustomException;
import com.example.demo.project.food.ordering.logger.LoggingAspect;
import com.example.demo.project.food.ordering.model.MenuItem;
import com.example.demo.project.food.ordering.model.Orders;
import com.example.demo.project.food.ordering.model.Restaurant;
import com.example.demo.project.food.ordering.repository.MenuItemRepository;
import com.example.demo.project.food.ordering.repository.OrderRepository;
import com.example.demo.project.food.ordering.repository.RestaurantRepository;
import com.example.demo.project.food.ordering.util.Constants;

import jakarta.persistence.OptimisticLockException;

@Service
public class OrderService {

	@Value("${restaurant.selection.strategy}")
	private String selectionStrategy;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private RestaurantRepository restaurantRepository;

	@Autowired
	private MenuItemRepository menuItemRepository;

	private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	public Restaurant selectLowestPriceRestaurant(String itemName) {

		List<Restaurant> availableRestaurants = restaurantRepository.findByMenuItems_Name(itemName);

		if (availableRestaurants.isEmpty()) {
			throw new CustomException(Constants.RESTAURANT_NOT_AVAILABLE);
		}

		return availableRestaurants.stream()
				.min(Comparator.comparing(restaurant -> restaurant.getMenuItems().stream()
						.filter(menuItem -> menuItem.getName().equals(itemName)).findFirst()
						.orElseThrow(() -> new CustomException(Constants.ITEM_NOT_FOUND)).getPrice()))
				.orElseThrow(() -> new CustomException(Constants.RESTAURANT_NOT_AVAILABLE));
	}

	@Transactional
	public List<Orders> placeOrder(Map<String, Integer> orderItems) {
		List<Orders> ordersList = new ArrayList<>();

		for (Map.Entry<String, Integer> entry : orderItems.entrySet()) {
			String itemName = entry.getKey();
			int quantity = entry.getValue();

			List<MenuItem> menuItems = menuItemRepository.findByName(itemName);

			if (menuItems.isEmpty()) {
				throw new CustomException(Constants.ITEM_NOT_FOUND + " " + itemName);
			}

			int remainingQuantity = quantity;
			Set<Restaurant> exhaustedRestaurants = new HashSet<>();

			while (remainingQuantity > 0) {

				List<MenuItem> availableMenuItems = menuItems.stream()
						.filter(menuItem -> !exhaustedRestaurants.contains(menuItem.getRestaurant()))
						.collect(Collectors.toList());

				if (availableMenuItems.isEmpty()) {
					throw new CustomException(
							Constants.ORDER_NOT_FULFILLED + itemName + ". " +Constants.RESTAURANT_NOT_AVAILABLE);
				}

				logger.info("Finding restaurant with " + selectionStrategy);

				MenuItem selectedMenuItem = new MenuItem();

				if (selectionStrategy.equalsIgnoreCase(Constants.STRATEGY)) {
					selectedMenuItem = availableMenuItems.stream()
							.min((m1, m2) -> Double.compare(m1.getPrice(), m2.getPrice()))
							.orElseThrow(() -> new CustomException(Constants.ITEM_NOT_FOUND + " " + itemName));
				} else {
					selectedMenuItem = availableMenuItems.stream().max(
							(m1, m2) -> Double.compare(m1.getRestaurant().getRating(), m2.getRestaurant().getRating())) // rating
							.orElseThrow(() -> new CustomException(Constants.ITEM_NOT_FOUND + " " + itemName));
				}

				Restaurant selectedRestaurant = selectedMenuItem.getRestaurant();
				int currentCapacity = selectedRestaurant.getCurrentCapacity();

				if (currentCapacity == 0) {

					exhaustedRestaurants.add(selectedRestaurant);
					continue;
				}

				int orderQuantity = Math.min(remainingQuantity, currentCapacity);

				Orders order = new Orders();
				order.setRestaurant(selectedRestaurant);
				order.setItemName(selectedMenuItem.getName());
				order.setQuantity(orderQuantity);
				order.setStatus(Constants.PENDING_ORDER_STATUS);

				orderRepository.save(order);
				ordersList.add(order);

				selectedRestaurant.setCurrentCapacity(currentCapacity - orderQuantity);
				restaurantRepository.save(selectedRestaurant);

				remainingQuantity -= orderQuantity;

				if (selectedRestaurant.getCurrentCapacity() == 0) {
					exhaustedRestaurants.add(selectedRestaurant);
				}
			}
		}

		return ordersList;
	}

	@Transactional
	public Orders completeOrder(Long order_id) {

		Orders order = orderRepository.findById(order_id)
				.orElseThrow(() -> new CustomException(Constants.ORDER_NOT_AVAILABLE));

		order.setStatus(Constants.COMPLETED_ORDER_STATUS);

		Restaurant restaurant = order.getRestaurant();
		if (restaurant != null) {
			int currentCapacity = restaurant.getCurrentCapacity();
			int updatedCapacity = currentCapacity + order.getQuantity();
			restaurant.setCurrentCapacity(updatedCapacity);
			restaurantRepository.save(restaurant);
		} else {
			throw new CustomException(Constants.RESTAURANT_NOT_AVAILABLE);
		}

		return orderRepository.save(order);
	}

	public Orders updateOrder(Long orderId, Orders updatedOrder) {
		try {
			return orderRepository.save(updatedOrder);
		} catch (OptimisticLockException e) {
			throw new CustomException(Constants.ORDER_MISMATCH);
		}
	}

	public List<Orders> getAllOrders() {
		return orderRepository.findAll();
	}

	public List<Orders> getOrdersByRestaurant(Long restaurantId) {

		Restaurant restaurant = restaurantRepository.findById(restaurantId)
				.orElseThrow(() -> new CustomException(Constants.RESTAURANT_NOT_FOUND_EXCEPTION));

		return orderRepository.findByRestaurant(restaurant);
	}

}
