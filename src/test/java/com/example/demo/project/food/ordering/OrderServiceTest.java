package com.example.demo.project.food.ordering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.project.food.ordering.exception.CustomException;
import com.example.demo.project.food.ordering.model.MenuItem;
import com.example.demo.project.food.ordering.model.Orders;
import com.example.demo.project.food.ordering.model.Restaurant;
import com.example.demo.project.food.ordering.repository.MenuItemRepository;
import com.example.demo.project.food.ordering.repository.OrderRepository;
import com.example.demo.project.food.ordering.repository.RestaurantRepository;
import com.example.demo.project.food.ordering.service.OrderService;

public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService; 

    @Mock
    private MenuItemRepository menuItemRepository; 
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RestaurantRepository restaurantRepository; 
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPlaceOrder_Success() {
     
        Map<String, Integer> orderItems = new HashMap<>();
        orderItems.put("Pizza", 2);

        Restaurant restaurant = new Restaurant();
        restaurant.setCurrentCapacity(5); 
        
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Pizza");
        menuItem.setPrice(10.0);
        menuItem.setRestaurant(restaurant);

        when(menuItemRepository.findByName("Pizza")).thenReturn(Collections.singletonList(menuItem));

     
        List<Orders> result = orderService.placeOrder(orderItems);

        
        assertEquals(1, result.size());
        assertEquals("Pizza", result.get(0).getItemName());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals("Pending", result.get(0).getStatus());
        assertEquals(3, restaurant.getCurrentCapacity()); 
    }

    @Test
    public void testPlaceOrder_ItemNotFound() {
        
        Map<String, Integer> orderItems = new HashMap<>();
        orderItems.put("Burger", 1);

        when(menuItemRepository.findByName("Burger")).thenReturn(Collections.emptyList());

       
        Exception exception = assertThrows(CustomException.class, () -> {
            orderService.placeOrder(orderItems);
        });

        assertEquals("Menu item not found: Burger", exception.getMessage());
    }

    @Test
    public void testPlaceOrder_InsufficientCapacity() {
       
        Map<String, Integer> orderItems = new HashMap<>();
        orderItems.put("Pizza", 5); 

        Restaurant restaurant = new Restaurant();
        restaurant.setCurrentCapacity(3); 
        
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Pizza");
        menuItem.setPrice(10.0);
        menuItem.setRestaurant(restaurant);

        when(menuItemRepository.findByName("Pizza")).thenReturn(Collections.singletonList(menuItem));

   
        Exception exception = assertThrows(CustomException.class, () -> {
            orderService.placeOrder(orderItems);
        });

        assertEquals("Cannot fulfill the order for item: Pizza. No restaurant available with sufficient capacity.", exception.getMessage());
    }
    
    
    @Test
    public void testCompleteOrder_Success() {
        Long orderId = 1L;

        Restaurant restaurant = new Restaurant();
        restaurant.setCurrentCapacity(3);

        Orders order = new Orders();
        order.setId(orderId);
        order.setStatus("Pending");
        order.setQuantity(2);
        order.setRestaurant(restaurant);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));


        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(invocation -> {
            Restaurant updatedRestaurant = invocation.getArgument(0);
            restaurant.setCurrentCapacity(updatedRestaurant.getCurrentCapacity());
            return updatedRestaurant; 
        });

        when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Orders result = orderService.completeOrder(orderId);

        assertNotNull(result, "Expected a non-null result from completeOrder"); 
        assertEquals("Completed", result.getStatus());
        assertEquals(5, restaurant.getCurrentCapacity()); 

    
        verify(orderRepository).save(order);
        verify(restaurantRepository).save(restaurant); 
    }

    @Test
    public void testCompleteOrder_OrderNotFound() {
       
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(CustomException.class, () -> {
            orderService.completeOrder(orderId);
        });

        assertEquals("Order not found", exception.getMessage());
    }

    @Test
    public void testCompleteOrder_RestaurantNotFound() {
        
        Long orderId = 1L;
        Orders order = new Orders();
        order.setId(orderId);
        order.setStatus("Pending");
        order.setQuantity(2);
        order.setRestaurant(null); 

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(CustomException.class, () -> {
            orderService.completeOrder(orderId);
        });

        assertEquals("Restaurant not found for the order", exception.getMessage());
    }

}
