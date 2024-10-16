package com.example.demo.project.food.ordering.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.project.food.ordering.exception.CustomException;
import com.example.demo.project.food.ordering.model.Orders;
import com.example.demo.project.food.ordering.service.OrderService;
import com.example.demo.project.food.ordering.util.Constants;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<List<Orders>>  placeOrder(@RequestBody Map<String, Integer> orderItems) {
        List<Orders> orders = orderService.placeOrder(orderItems);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/allOrders")
    public List<Orders> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}")
    public Orders updateOrder(@PathVariable Long id, @RequestBody Orders updatedOrder) {
        return orderService.updateOrder(id, updatedOrder);
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<Orders>  completeOrder(@PathVariable Long id) {
        Orders orders = orderService.completeOrder(id);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Orders>> getOrdersByRestaurant(@PathVariable Long restaurantId) {
        List<Orders> orders = orderService.getOrdersByRestaurant(restaurantId);
        if (orders.isEmpty()) {
            throw new CustomException(Constants.ORDER_NOT_AVAILABLE);
        }
        return ResponseEntity.ok(orders);
    }
}
