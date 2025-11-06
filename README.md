
-----

# ShopMatrix E-Commerce Backend

> ShopMatrix — A smart and scalable e-commerce backend system for product browsing, cart management, and secure checkout.

This repository contains the backend REST API for ShopMatrix, a modern e-commerce platform. It's built with Spring Boot and uses MongoDB for data persistence. The API features secure JWT authentication and role-based access control (RBAC) to distinguish between `CUSTOMER` and `ADMIN` user actions.

-----

## ✨ Features

  * **Authentication:** Secure user registration and login using JWT (JSON Web Tokens).
  * **Role-Based Access:** Clear distinction between public, customer (`ROLE_CUSTOMER`), and admin (`ROLE_ADMIN`) endpoints.
  * **Product Management:** Full CRUD operations for products (Admin only).
  * **Category Management:** Full CRUD operations for categories (Admin only).
  * **Public Catalog:** Public endpoints for browsing products and categories.
  * **Cart Management:** Full-service cart for authenticated customers (add, update, remove, clear).
  * **Order Management:** Customers can place orders (converting their cart) and view their order history. Admins can view and manage all orders.

-----

## 🛠️ Tech Stack

  * **Framework:** Spring Boot
  * **Security:** Spring Security & JWT (JSON Web Tokens)
  * **Database:** MongoDB (using Spring Data MongoDB)
  * **Utilities:** Lombok
  * **Build Tool:** Maven

-----

## 📁 Project Structure

```
src/main/java/com/ved/ShopMatrix/
├── config/
│   ├── DataSeeder.java         # Seeds the DB with an admin user
│   └── SecurityConfig.java     # Main Spring Security setup
├── controller/
│   ├── AuthController.java     # Public /auth/register, /auth/login
│   ├── CartController.java     # Customer /cart/**
│   ├── CategoryController.java # Public /categories
│   ├── OrderController.java    # Customer /orders/**
│   ├── ProductController.java  # Public /products/**
│   └── admin/                  # Admin-only controllers
│       ├── AdminCategoryController.java
│       ├── AdminOrderController.java
│       └── AdminProductController.java
├── dto/                        # Data Transfer Objects (for API requests/responses)
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── CartDto.java
│   ├── OrderDto.java
│   ├── ProductDto.java
│   └── ...
├── model/                      # MongoDB Document models
│   ├── Cart.java               # (Embedded in User)
│   ├── CartItem.java           # (Embedded in Cart)
│   ├── Category.java
│   ├── Order.java
│   ├── OrderItem.java          # (Embedded in Order)
│   ├── Product.java
│   ├── Role.java               # (Enum)
│   └── User.java               # (Implements UserDetails)
├── repository/                 # Spring Data MongoDB interfaces
│   ├── CategoryRepository.java
│   ├── OrderRepository.java
│   ├── ProductRepository.java
│   └── UserRepository.java
├── security/
│   ├── UserDetailsServiceImpl.java # Loads user for Spring Security
│   └── jwt/
│       ├── JwtAuthenticationFilter.java # Validates token on each request
│       └── JwtService.java              # Creates and parses JWTs
├── service/
│   ├── impl/                   # Service implementations
│   └── AuthService.java        # Service interfaces
│   └── CartService.java
│   └── ...
└── ShopMatrixApplication.java  # Main application entry point
```

-----

## 🗄️ Database Diagram

This diagram shows the logical relationships between the collections. In MongoDB, `Cart` and `CartItem` are **embedded** within the `User` document, and `OrderItem` is **embedded** within the `Order` document.

You can paste the code below into **[dbdiagram.io](https://dbdiagram.io/)** to visualize the schema.
* Or Open Diagram -> https://dbdiagram.io/d/ShopMatrix-690c5d596735e11170845cc5 


```dbml
// ---
// ShopMatrix E-Commerce (MongoDB Schema)
// Note: This diagram shows logical relationships.
// 'CartItem' is embedded in 'User'; 'OrderItem' is in 'Order'.
// ---

Table User {
  id string [pk, note: 'MongoDB ObjectID']
  username string [unique, not null]
  email string [unique, not null]
  password string [not null]
  firstName string
  lastName string
  roles string[] [note: '["ROLE_CUSTOMER", "ROLE_ADMIN"]']
  cart Cart [note: 'Embedded Document']
  createdAt timestamp
}

Table Category {
  id string [pk, note: 'MongoDB ObjectID']
  name string [not null]
  description string
  imageUrl string
}

Table Product {
  id string [pk, note: 'MongoDB ObjectID']
  name string [not null]
  description string
  price decimal
  stock int
  imageUrl string
  categoryId string [ref: > Category.id]
}

Table Order {
  id string [pk, note: 'MongoDB ObjectID']
  userId string [ref: > User.id, not null]
  status string [note: '"PENDING", "SHIPPED", "DELIVERED"']
  total decimal
  items OrderItem[] [note: 'Embedded Array of Documents']
  createdAt timestamp
}

// --- Definitions for Embedded Documents (for clarity) ---

Ref: "User.cart.items.productId" > "Product.id"
Ref: "Order.items.productId" > "Product.id"

Table Cart [note: 'Embedded in User'] {
 items CartItem[]
}

Table CartItem [note: 'Embedded in Cart'] {
  productId string
  productName string
  quantity int
  price decimal
  imageUrl string
}

Table OrderItem [note: 'Embedded in Order'] {
  productId string
  productName string
  quantity int
  priceAtPurchase decimal
}
```

-----

## Endpoints (API Reference)

### 🟢 Public Endpoints

No authentication is required.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/auth/register` | Creates a new customer account. |
| `POST` | `/auth/login` | Authenticates a user and returns a JWT. |
| `GET` | `/products` | Gets a list of all products. |
| `GET` | `/products/{productId}` | Gets details for a single product. |
| `GET` | `/products/category/{categoryId}` | Gets all products in a specific category. |
| `GET` | `/categories` | Gets a list of all product categories. |

### 🟡 Customer Endpoints (Role: `ROLE_CUSTOMER`)

Requires `Authorization: Bearer <token>` header.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/user/profile` | Gets the profile of the logged-in user. |
| `PATCH`| `/user/profile` | Partially updates the user's profile (e.g., name). |
| `GET` | `/cart` | Retrieves the current user's shopping cart. |
| `POST`| `/cart/add/{productId}` | Adds a product to the cart. |
| `PUT` | `/cart/update/{productId}` | Updates an item's quantity (e.g., `?quantity=3`). |
| `DELETE`| `/cart/remove/{productId}` | Removes a single item from the cart. |
| `DELETE`| `/cart/clear` | Removes all items from the cart. |
| `POST`| `/orders/place` | Places an order by converting the cart. |
| `GET` | `/orders` | Retrieves the user's order history. |
| `GET` | `/orders/{orderId}` | Retrieves details for a single order. |

### 🔴 Admin Endpoints (Role: `ROLE_ADMIN`)

Requires `Authorization: Bearer <token>` header and `ROLE_ADMIN`.

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/admin/products` | Creates a new product. |
| `PUT` | `/admin/products/{productId}` | Replaces all details of an existing product. |
| `PATCH`| `/admin/products/{productId}` | Partially updates a product (e.g., stock/price). |
| `DELETE`| `/admin/products/{productId}` | Deletes a product. |
| `POST` | `/admin/categories` | Creates a new product category. |
| `PUT` | `/admin/categories/{categoryId}`| Replaces all details of an existing category. |
| `DELETE`| `/admin/categories/{categoryId}`| Deletes a category. |
| `GET` | `/admin/orders` | Retrieves a list of all orders from all users. |
| `PUT` | `/admin/orders/{orderId}/status`| Updates an order's status (e.g., `?value=SHIPPED`). |

-----

## 🚀 Getting Started

### Prerequisites

  * Java (JDK 21+)
  * Maven
  * MongoDB (running locally on port 27017 or on a cloud instance)
  * Git

### 1\. Clone the Repository

```bash
git clone https://github.com/your-username/shopmatrix.git
cd shopmatrix
```

### 2\. Configure the Application

Open the `src/main/resources/application.yml` file and configure your settings.

```yaml
spring:
  application:
    name: shop-matrix-backend
  
  data:
    mongodb:
      # Update this URI to point to your MongoDB instance
      uri: mongodb://localhost:27017/shopmatrixdb

# --- JWT Configuration ---
jwt:
  # IMPORTANT: Generate your own 64-character (512-bit) secret key
  secret-key: "your-super-strong-512-bit-secret-key-that-no-one-can-guess-12345"
  
  # 24 hours in milliseconds
  expiration-ms: 86400000 
```

### 3\. Run the Application

You can run the application using the Spring Boot Maven plugin:

```bash
mvn spring-boot:run
```

The API will be live at `http://localhost:8080`.

### 4\. Create an Admin User

There are two ways to create your first admin user:

**Method 1: Automatic (Recommended)**
The project includes a `DataSeeder` (`config/DataSeeder.java`). When you run the application for the first time, it will:

1.  Check if any admin user exists.
2.  If not, it will create a default admin user.

<!-- end list -->

  * **Username:** `admin`
  * **Password:** `admin123`

**Method 2: Manual (MongoDB CLI)**

1.  Use the `ShopMatrixApplication.java`'s `printHash()` method to generate a hash for a password (e.g., "admin123").
2.  Connect to your database with `mongosh`.
3.  Insert the admin document manually:

<!-- end list -->

```javascript
db.users.insertOne({
    "username": "admin",
    "email": "admin@shopmatrix.com",
    "password": "PASTE_YOUR_HASHED_PASSWORD_HERE",
    "firstName": "Admin",
    "lastName": "User",
    "cart": { "items": [] },
    "roles": [ "ROLE_CUSTOMER", "ROLE_ADMIN" ],
    "createdAt": new Date(),
    "enabled": true,
    "accountNonExpired": true,
    "accountNonLocked": true,
    "credentialsNonExpired": true,
    "_class": "com.ved.ShopMatrix.model.User"
})
```
