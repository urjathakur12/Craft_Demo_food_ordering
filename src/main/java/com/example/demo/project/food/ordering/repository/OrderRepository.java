package com.example.demo.project.food.ordering.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.project.food.ordering.model.Orders;
import com.example.demo.project.food.ordering.model.Restaurant;


public interface OrderRepository extends JpaRepository<Orders, Long> {

	List<Orders> findByRestaurant(Restaurant restaurant);
}
