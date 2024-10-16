# Online Food Ordering System

This project is an online food ordering system that enables users to browse menus from multiple restaurants, place orders for various items, and handle restaurant processing capacities and selection strategies. The backend is implemented using Spring Boot.

## Features

1. **Restaurant Registration**:

   - Restaurants can register on the platform and upload or update their menus, including item names and prices.

2. **Menu Display**:

   - Users can view a combined menu listing all items available across registered restaurants.

3. **Ordering System**:

   - Customers can place an order by selecting multiple items and specifying quantities.
   - The system automatically selects restaurants based on a configurable restaurant selection strategy, ensuring that orders are fulfilled from one or more restaurants.
   - Restaurants cannot exceed their maximum processing capacity.

4. **Restaurant Selection Strategy**:

   - Configurable strategy to select the restaurant fulfilling an item.
   - The default strategy implemented selects the restaurant offering the item at the **lowest cost**.
   - The strategy is designed to be extensible to include other criteria, such as restaurant ratings.

5. **Concurrency Handling**:
   - The system handles multiple users ordering at the same time, ensuring no restaurant exceeds its capacity.
   - When an item is dispatched by a restaurant, the system is notified, and the restaurant’s processing capacity is freed for new orders.

## Technologies

- **Spring Boot**: Backend framework.
- **Maven**: Build and dependency management.
- **Java 11**: Programming language.
- **Concurrency Handling**: Managed through Java's concurrency utilities for ensuring safe, parallel processing of multiple orders.

## Prerequisites

- **Java 11** or higher installed.
- **Maven** installed.
- Ensure the environment has a database for the application to store restaurant and order information.

## Getting Started

```bash
git clone https://github.com/urjathakur12/Craft_Demo_food_ordering.git
cd online-food-ordering-system
mvn clean install
mvn spring-boot:run
```
