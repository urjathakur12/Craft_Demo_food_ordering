package com.example.demo.project.food.ordering.repository;


import com.example.demo.project.food.ordering.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByName(String itemName);
}
