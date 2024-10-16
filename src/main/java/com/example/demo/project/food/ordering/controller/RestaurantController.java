package com.example.demo.project.food.ordering.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.project.food.ordering.exception.CustomException;
import com.example.demo.project.food.ordering.model.MenuItem;
import com.example.demo.project.food.ordering.model.Restaurant;
import com.example.demo.project.food.ordering.service.RestaurantService;
import com.example.demo.project.food.ordering.util.Constants;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

	@Autowired
	private RestaurantService restaurantService;

	@PostMapping
	public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
		Restaurant savedRestaurant = restaurantService.saveRestaurant(restaurant);
		return ResponseEntity.status(201).body(savedRestaurant);
	}

	@PutMapping("/{restaurantId}/menu")
	public ResponseEntity<Restaurant> updateMenu(@PathVariable Long restaurantId,
			@RequestBody List<MenuItem> updatedMenuItems) {
		Restaurant updatedRestaurant = restaurantService.updateMenu(restaurantId, updatedMenuItems);
		return ResponseEntity.ok(updatedRestaurant);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Restaurant> getRestaurant(@PathVariable Long id) {
		Restaurant restaurant = restaurantService.getRestaurant(id)
				.orElseThrow(() -> new RuntimeException(Constants.RESTAURANT_NOT_FOUND_EXCEPTION));
		return ResponseEntity.ok(restaurant);
	}

	@GetMapping("/{restaurantId}/menu-items")
	public ResponseEntity<List<MenuItem>> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
		List<MenuItem> menuItems = restaurantService.getMenuItemsByRestaurant(restaurantId);
		if (menuItems.isEmpty()) {
			throw new CustomException(Constants.ITEM_NOT_FOUND);
		}
		return ResponseEntity.ok(menuItems);
	}
}
