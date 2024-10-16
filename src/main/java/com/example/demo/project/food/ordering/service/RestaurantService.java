package com.example.demo.project.food.ordering.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.project.food.ordering.exception.CustomException;
import com.example.demo.project.food.ordering.model.MenuItem;
import com.example.demo.project.food.ordering.model.Restaurant;
import com.example.demo.project.food.ordering.repository.RestaurantRepository;
import com.example.demo.project.food.ordering.util.Constants;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;


    @Transactional
    public Restaurant updateMenu(Long restaurantId, List<MenuItem> updatedMenuItems) {
        Restaurant existingRestaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException(Constants.RESTAURANT_NOT_FOUND_EXCEPTION));

        existingRestaurant.getMenuItems().clear();

        for (MenuItem updatedMenuItem : updatedMenuItems) {
            updatedMenuItem.setRestaurant(existingRestaurant);
            existingRestaurant.addMenuItem(updatedMenuItem);
        }

        return restaurantRepository.save(existingRestaurant);
    }


    public Optional<Restaurant> getRestaurant(Long restaurantId) {
        return restaurantRepository.findById(restaurantId);
    }

    public Restaurant saveRestaurant(Restaurant restaurant) {
    	 for (MenuItem menuItem : restaurant.getMenuItems()) {
             menuItem.setRestaurant(restaurant); 
         }
        return restaurantRepository.save(restaurant);
    }
    
    public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
       
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new CustomException(Constants.RESTAURANT_NOT_FOUND_EXCEPTION));

        return restaurant.getMenuItems();
    }
}
