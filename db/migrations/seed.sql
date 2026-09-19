-- ================================
-- Mithra-mart Seed Data
-- ================================

-- USERS
INSERT INTO users (name, email, password_hash, role, created_at) VALUES
('Mithra Admin', 'admin@mithramart.com', 'hashed_admin_password', 'ADMIN', CURRENT_TIMESTAMP),
('Arun Kumar', 'arun@example.com', 'hashed_arun_password', 'SELLER', CURRENT_TIMESTAMP),
('Priya Sharma', 'priya@example.com', 'hashed_priya_password', 'BUYER', CURRENT_TIMESTAMP),
('Rahul Das', 'rahul@example.com', 'hashed_rahul_password', 'BUYER', CURRENT_TIMESTAMP);

-- PRODUCTS
INSERT INTO products
(seller_id, name, description, price, stock_qty, category, created_at)
VALUES
(2, 'Wireless Headphones', 'Bluetooth wireless headphones', 1499.00, 25, 'Electronics', CURRENT_TIMESTAMP),
(2, 'Mechanical Keyboard', 'RGB mechanical keyboard', 2499.00, 15, 'Electronics', CURRENT_TIMESTAMP),
(2, 'College Backpack', 'Water-resistant laptop backpack', 999.00, 30, 'Bags', CURRENT_TIMESTAMP);

-- ORDERS
INSERT INTO orders
(buyer_id, status, total_amount, created_at)
VALUES
(3, 'PLACED', 1499.00, CURRENT_TIMESTAMP),
(4, 'DELIVERED', 2499.00, CURRENT_TIMESTAMP);

-- ORDER ITEMS
INSERT INTO order_items
(order_id, product_id, quantity, unit_price, created_at)
VALUES
(1, 1, 1, 1499.00, CURRENT_TIMESTAMP),
(2, 2, 1, 2499.00, CURRENT_TIMESTAMP);

-- CART ITEMS
INSERT INTO cart_items
(user_id, product_id, quantity, created_at)
VALUES
(3, 3, 2, CURRENT_TIMESTAMP),
(4, 1, 1, CURRENT_TIMESTAMP);

-- REVIEWS
INSERT INTO reviews
(product_id, user_id, rating, comment, created_at)
VALUES
(1, 3, 5, 'Good sound quality and comfortable to use.', CURRENT_TIMESTAMP),
(2, 4, 4, 'Good keyboard with nice RGB lighting.', CURRENT_TIMESTAMP);