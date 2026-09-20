package com.mithra.mithramart.dao;

import com.mithra.mithramart.listener.DataSourceListener;
import com.mithra.mithramart.model.CartItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class OrderDAO {

    // Places an order from the given cart items, as ONE atomic transaction:
    // insert order -> insert order_items -> clear cart -> commit.
    // If anything fails partway, everything rolls back - no broken half-orders.
    public long placeOrder(long buyerId, List<CartItem> cartItems) throws Exception {
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart.");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            total = total.add(item.getLineTotal());
        }

        Connection conn = null;
        try {
            conn = DataSourceListener.getDataSource().getConnection();
            conn.setAutoCommit(false); // start transaction - nothing commits until we say so

            long orderId;
            String insertOrderSql = "INSERT INTO orders (buyer_id, status, total_amount, created_at) VALUES (?, 'CONFIRMED', ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement ps = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, buyerId);
                ps.setBigDecimal(2, total);
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Failed to create order - no ID returned.");
                    }
                    orderId = keys.getLong(1);
                }
            }

            String insertItemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";
            try (PreparedStatement ps = conn.prepareStatement(insertItemSql)) {
                for (CartItem item : cartItems) {
                    ps.setLong(1, orderId);
                    ps.setLong(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.setBigDecimal(4, item.getUnitPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            String clearCartSql = "DELETE FROM cart_items WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(clearCartSql)) {
                ps.setLong(1, buyerId);
                ps.executeUpdate();
            }

            conn.commit(); // all-or-nothing: everything above becomes permanent now
            return orderId;

        } catch (Exception e) {
            if (conn != null) {
                conn.rollback(); // undo everything since setAutoCommit(false)
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true); // restore default behavior before returning connection to pool
                conn.close();
            }
        }
    }
}
