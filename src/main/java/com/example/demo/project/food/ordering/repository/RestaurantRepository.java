package com.example.demo.project.food.ordering.repository;

import com.example.demo.project.food.ordering.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByMenuItems_Name(String menuItemName);
}
