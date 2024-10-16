package com.example.demo.project.food.ordering.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;

@Entity
public class Restaurant {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	
	private Double rating;
	
	private Integer maxCapacity;
	
	private Integer currentCapacity;

	@Version
	private Integer version;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "restaurant")
	private List<MenuItem> menuItems = new ArrayList<>();

	public void addMenuItem(MenuItem menuItem) {
		menuItems.add(menuItem);
		menuItem.setRestaurant(this);
	}

	public void removeMenuItem(MenuItem menuItem) {
		menuItems.remove(menuItem);
		menuItem.setRestaurant(null);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public Integer getMaxCapacity() {
		return maxCapacity;
	}

	public Integer getCurrentCapacity() {
		return currentCapacity;
	}

	public void setMaxCapacity(Integer maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public void setCurrentCapacity(Integer curCapacity) {
		this.currentCapacity = curCapacity;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

}
