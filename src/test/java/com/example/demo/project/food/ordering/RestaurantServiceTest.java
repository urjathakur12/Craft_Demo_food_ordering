package com.example.demo.project.food.ordering;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.demo.project.food.ordering.model.MenuItem;
import com.example.demo.project.food.ordering.model.Restaurant;
import com.example.demo.project.food.ordering.repository.RestaurantRepository;
import com.example.demo.project.food.ordering.service.RestaurantService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RestaurantServiceTest {

    @InjectMocks
    private RestaurantService restaurantService; 

    @Mock
    private RestaurantRepository restaurantRepository; 

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateMenu_Success() {
        Long restaurantId = 1L;
        Restaurant existingRestaurant = new Restaurant();
        existingRestaurant.setId(restaurantId);
        existingRestaurant.setMenuItems(new ArrayList<>());


        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));

       
        MenuItem updatedItem1 = new MenuItem();
        updatedItem1.setId(1L);  
        updatedItem1.setName("Pizza");
        updatedItem1.setPrice(10.0);

        MenuItem updatedItem2 = new MenuItem();
        updatedItem2.setId(2L);  
        updatedItem2.setName("Pasta");
        updatedItem2.setPrice(12.0);

        List<MenuItem> updatedMenuItems = Arrays.asList(updatedItem1, updatedItem2);

        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(existingRestaurant);

        Restaurant result = restaurantService.updateMenu(restaurantId, updatedMenuItems);

        assertEquals(2, result.getMenuItems().size());
        assertTrue(result.getMenuItems().contains(updatedItem1));
        assertTrue(result.getMenuItems().contains(updatedItem2));
        assertEquals(existingRestaurant, result); 

        verify(restaurantRepository).save(existingRestaurant);
    }

    
    @Test
    public void testUpdateMenu_RestaurantNotFound() {
    
        Long restaurantId = 1L;

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            restaurantService.updateMenu(restaurantId, new ArrayList<>());
        });

        assertEquals("Restaurant not found", exception.getMessage());
    }

    @Test
    public void testUpdateMenu_EmptyMenuItems() {
        Long restaurantId = 1L;
        Restaurant existingRestaurant = new Restaurant();
        existingRestaurant.setId(restaurantId);
        existingRestaurant.setMenuItems(new ArrayList<>(Arrays.asList(new MenuItem(), new MenuItem()))); // Existing menu has items

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));

        List<MenuItem> updatedMenuItems = new ArrayList<>(); 

     
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(existingRestaurant);

        Restaurant result = restaurantService.updateMenu(restaurantId, updatedMenuItems);

        assertTrue(result.getMenuItems().isEmpty(), "Menu items should be empty after update");
        assertEquals(existingRestaurant, result, "The returned restaurant should be the same as the existing restaurant");

   
        verify(restaurantRepository).save(existingRestaurant);
    }

}
