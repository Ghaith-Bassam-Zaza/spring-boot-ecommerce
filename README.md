# Spring Boot E-Commerce Application

## Introduction

The **Spring Boot E-Commerce Application** is a backend system designed to provide the core functionality of an online shopping platform. It enables secure authentication, product and category management, order processing, and user-specific operations. The project is implemented using **Spring Boot** and follows a layered architecture to promote modularity, scalability, and ease of maintenance.

The application targets both developers looking for a ready-made backend solution and those seeking a strong foundation for further customization and enhancement.

---

## Used Technologies

- **Language**: Java 17  
- **Framework**: Spring Boot 3.x  
- **Database**: Microsoft SQL Server  
- **Security**: Spring Security with JWT for authentication and authorization  
- **Persistence**: Spring Data JPA  
- **Build Tool**: Maven  
- **Testing**: JUnit 5, Mockito, and H2 in-memory database for testing  

---

## Features

1. **Authentication and Authorization**: Secure user authentication with role-based access control using JWT.  
2. **Product Management**: Full CRUD operations for products, including category assignment and stock management.  
3. **Category Management**: Manage product categories to organize items efficiently.  
4. **Order Management**: Place orders, view user-specific order history, and track inventory updates.  
5. **Input Validation**: Comprehensive validation for incoming requests to ensure data integrity.  
6. **Error Handling**: Centralized exception handling for user-friendly error messages.  
7. **Testing**: Comprehensive unit and integration tests to validate application reliability.  

---

## Code Structure

The project follows a layered architecture with the following key packages:

- **`controller`**: REST controllers to handle HTTP requests and responses.  
- **`service`**: Business logic implementation to manage operations.  
- **`repository`**: Interfaces for database operations using Spring Data JPA.  
- **`model`**: Entity classes mapped to database tables.  
- **`dto`**: Data Transfer Objects for handling input and output payloads.  
- **`config`**: Configuration classes for security and application setup.  
- **`exception`**: Custom exception classes and global exception handling logic.

---

## Detailed Code Descriptions

### Controllers

- **`AuthController`**:  
  - Handles user login and token generation.  
  - Implements endpoints for secure authentication.  

- **`ProductController`**:  
  - Manages products through endpoints for CRUD operations.  
  - Includes filtering and category-based listing functionality.  

- **`CategoryController`**:  
  - Provides endpoints for managing product categories.  

- **`OrderController`**:  
  - Handles order placement and user-specific order retrieval.  
  - Ensures inventory updates during order processing.  

### Services

- **`AuthService`**:  
  - Implements logic for user authentication and JWT token creation.  
  - Validates user roles for secure access to endpoints.  

- **`ProductService`**:  
  - Orchestrates product-related operations like creation, retrieval, and updates.  
  - Ensures stock validation during updates and deletions.  

- **`CategoryService`**:  
  - Provides business logic for managing product categories.  

- **`OrderService`**:  
  - Handles order placement, updates inventory, and calculates total prices.  
  - Retrieves user-specific order history.  

### Repositories

- **`ProductRepository`**:  
  - Extends Spring Data JPA's `JpaRepository` to handle product-related database queries.  

- **`CategoryRepository`**:  
  - Manages database operations for product categories.  

- **`OrderRepository`**:  
  - Performs persistence and retrieval operations for orders.  

- **`UserRepository`**:  
  - Manages user data for authentication and role-based queries.  

### Entities

- **`Product`**:  
  Represents a product with attributes like `id`, `name`, `price`, `stock`, and `category`.  

- **`Category`**:  
  Represents a category with fields like `id` and `name`, and a relationship to products.  

- **`Order`**:  
  Represents an order with relationships to users and products.  

- **`User`**:  
  Represents application users with roles and credentials for secure access.  

### Configurations

- **`SecurityConfig`**:  
  Configures Spring Security to enforce JWT-based authentication and role-based authorization.  

- **`AppConfig`**:  
  Contains application-wide bean definitions and settings.  

### Exceptions

- **`ResourceNotFoundException`**:  
  Thrown when an entity is not found in the database.  

- **`GlobalExceptionHandler`**:  
  Provides centralized error handling to return structured error messages.

---

## Real-Time Address Updates: WebSocket

The application uses **WebSocket** for real-time communication, allowing immediate address updates to be pushed to clients when changes occur. This approach ensures that users have up-to-date address information without needing to refresh their page or poll the server.

### How It Works

- **User Updates Address**:  
  When a user updates their address (for example, during the checkout process), the server sends a WebSocket message to notify connected clients about the change.

- **Client Subscribes to WebSocket Channel**:  
  Clients can establish a WebSocket connection to a specific endpoint to receive real-time updates about the user's address changes. This ensures that active sessions always have the latest address data.

- **Two-Way Communication**:  
  WebSocket provides full-duplex communication, allowing both the server and the client to send messages to each other. In this case, the server pushes address updates to the client, but this can be extended for bi-directional communication if needed.

### Implementation Details

1. **WebSocket Configuration**:  
   The WebSocket configuration is set up in a dedicated configuration class that enables WebSocket support across the application. This allows for real-time connections to be established and messages to be broadcasted.

   - Example configuration:
     ```java
     @Configuration
     @EnableWebSocket
     public class WebSocketConfig implements WebSocketConfigurer {
     
         @Override
         public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
             registry.addHandler(addressUpdateHandler(), "/ws/address-updates").setAllowedOrigins("*");
         }
     
         @Bean
         public WebSocketHandler addressUpdateHandler() {
             return new AddressUpdateHandler();
         }
     }
     ```

2. **WebSocket Controller**:  
   - The `AddressWebSocketController` is responsible for handling WebSocket connections and broadcasting address updates to subscribed clients.  
   - Example endpoint: `/ws/address-updates` is the WebSocket endpoint clients connect to.

3. **Service Layer**:  
   - The `AddressService` triggers updates whenever an address is created, updated, or deleted.  
   - The service uses the WebSocket handler to send the update to connected clients.

4. **Client Handling**:  
   - Clients (e.g., frontend applications) can open a WebSocket connection to the server. Here's an example using JavaScript:
     ```javascript
     const socket = new WebSocket('ws://localhost:8080/ws/address-updates');
     
     socket.onmessage = function(event) {
         const updatedAddress = JSON.parse(event.data);
         console.log('Address updated:', updatedAddress);
         // Update the UI or perform necessary actions
     };
     
     socket.onopen = function() {
         console.log('WebSocket connection established');
     };
     
     socket.onerror = function(error) {
         console.log('WebSocket Error:', error);
     };
     ```

### Benefits of WebSocket in the Application

- **Real-Time Data**: Provides immediate updates, ensuring users always have the most up-to-date address information.  
- **Efficient Communication**: WebSocket reduces the need for polling, thereby lowering the load on both the client and server.  
- **Scalability**: WebSocket can handle a large number of connections, making it suitable for real-time features in high-traffic environments like e-commerce platforms.

This WebSocket implementation adds a powerful real-time layer to the application, particularly beneficial for situations where users' address data is dynamically updated, such as during checkout or order processing.

---

## Testing

### Testing Approach

The project includes a comprehensive test suite to ensure the reliability of its functionalities. It uses **JUnit 5** for unit testing and **Mockito** for mocking dependencies. Integration tests are conducted with an in-memory H2 database to simulate real-world scenarios.

### Example Test Cases

#### Authentication Tests
- **Valid Login Test**: Verifies successful authentication with correct credentials.  
- **Invalid Login Test**: Ensures an error is returned for invalid credentials.  

#### Product Tests
- **Create Product Test**: Verifies a product is successfully saved in the database.  
- **Get Product by ID Test**: Ensures the correct product details are returned for a valid ID.  
- **Delete Product Test**: Confirms a product is deleted successfully and no longer retrievable.  

#### Order Tests
- **Place Order Test**: Verifies that an order is created and inventory is updated correctly.  
- **Retrieve User Orders Test**: Confirms all orders for a specific user are retrieved successfully.  

---

## Later To Do

While the project provides a strong foundation, there are several potential extensions and features that can be added in future iterations:

- **Shopping Cart Management**: Implement functionality to allow users to add, update, and remove products from a shopping cart.  
- **Advanced Product Filtering**: Enhance product search with more complex filters, such as filtering by brand, price range, or product attributes.  
- **Payment Gateway Integration**: Integrate with third-party payment systems such as Stripe or PayPal to handle payments securely.  
- **Admin Dashboard**: Develop a backend dashboard for administrators to manage users, products, orders, and view analytical data.  
- **Email Notifications**: Add email notifications for order confirmations, shipping updates, and promotional offers.  
- **Rating and Reviews**: Implement a rating and review system for products, allowing users to leave feedback.

---
