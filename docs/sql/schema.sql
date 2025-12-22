DROP SCHEMA IF EXISTS mydb CASCADE;
CREATE SCHEMA mydb;
SET search_path TO mydb;

-- USERS
CREATE TABLE Users (
  user_id SERIAL PRIMARY KEY,
  name VARCHAR(45) NOT NULL,
  surname VARCHAR(45) NOT NULL,
  email VARCHAR(120) NOT NULL UNIQUE,
  has_welcome_discount BOOLEAN NOT NULL DEFAULT false,
  password VARCHAR(200) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

-- CART
CREATE TABLE Cart (
  cart_id SERIAL PRIMARY KEY,
  user_id INT NOT NULL UNIQUE,
  FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- CATEGORIES
CREATE TABLE Categories (
  category_id SERIAL PRIMARY KEY,
  name VARCHAR(45) NOT NULL
);

-- PRODUCTS
CREATE TABLE Products (
  product_id SERIAL PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  price NUMERIC(10,2) NOT NULL,
  description VARCHAR(255),
  url_image VARCHAR(255),
  stock INT NOT NULL DEFAULT 0,
  category_id INT NOT NULL,
  FOREIGN KEY (category_id) REFERENCES Categories(category_id)
);

-- ORDERS
CREATE TYPE order_status AS ENUM ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED');

CREATE TABLE Orders (
  order_id SERIAL PRIMARY KEY,
  total_price NUMERIC(10,2) NOT NULL,
  status order_status NOT NULL DEFAULT 'PENDING',
  shipping_address VARCHAR(255),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

-- COMMENTS
CREATE TABLE Comments (
  comment_id SERIAL PRIMARY KEY,
  description VARCHAR(255) NOT NULL,
  rating INT NOT NULL,
  user_id INT NOT NULL,
  product_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES Users(user_id),
  FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

-- CART ITEMS (Many-to-many)
CREATE TABLE Cart_items (
  product_id INT NOT NULL,
  cart_id INT NOT NULL,
  quantity INT NOT NULL,
  PRIMARY KEY (product_id, cart_id),
  FOREIGN KEY (product_id) REFERENCES Products(product_id) ON DELETE CASCADE,
  FOREIGN KEY (cart_id)  REFERENCES Cart(cart_id) ON DELETE CASCADE
);

-- ORDER ITEMS
CREATE TABLE Order_items (
  order_id INT NOT NULL,
  product_id INT NOT NULL,
  quantity INT NOT NULL,
  price_at_purchase NUMERIC(10,2) NOT NULL,
  PRIMARY KEY (order_id, product_id),
  FOREIGN KEY (order_id) REFERENCES Orders(order_id) ON DELETE CASCADE,
  FOREIGN KEY (product_id) REFERENCES Products(product_id)
);
