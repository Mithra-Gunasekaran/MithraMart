package com.mithra.mithramart.dao;

import com.mithra.mithramart.listener.DataSourceListener;
import com.mithra.mithramart.model.CartItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    // Adds a product to cart, or increases quantity if it's already there
    public void addItem(long userId, long productId, int quantity) throws Exception {
        String checkSql = "SELECT id, quantity FROM cart_items WHERE user_id = ? AND product_id = ?";

        try (Connection conn = DataSourceListener.getDataSource().getConnection()) {

            long existingId = -1;
            int existingQty = 0;

            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setLong(1, userId);
                ps.setLong(2, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        existingId = rs.getLong("id");
                        existingQty = rs.getInt("quantity");
                    }
                }
            }

            if (existingId != -1) {
                String updateSql = "UPDATE cart_items SET quantity = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                    ps.setInt(1, existingQty + quantity);
                    ps.setLong(2, existingId);
                    ps.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO cart_items (user_id, product_id, quantity, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setLong(1, userId);
                    ps.setLong(2, productId);
                    ps.setInt(3, quantity);
                    ps.executeUpdate();
                }
            }
        }
    }

    public boolean updateQuantity(long cartItemId, long userId, int newQuantity) throws Exception {
        String sql = "UPDATE cart_items SET quantity = ? WHERE id = ? AND user_id = ?";

        try (Connection conn = DataSourceListener.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newQuantity);
            ps.setLong(2, cartItemId);
            ps.setLong(3, userId);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeItem(long cartItemId, long userId) throws Exception {
        String sql = "DELETE FROM cart_items WHERE id = ? AND user_id = ?";

        try (Connection conn = DataSourceListener.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cartItemId);
            ps.setLong(2, userId);

            return ps.executeUpdate() > 0;
        }
    }

    // Joins cart_items with products to get name and price for display
    public List<CartItem> getCart(long userId) throws Exception {
        String sql = "SELECT ci.id, ci.product_id, ci.quantity, p.name, p.price " +
                     "FROM cart_items ci JOIN products p ON ci.product_id = p.id " +
                     "WHERE ci.user_id = ?";

        List<CartItem> results = new ArrayList<>();

        try (Connection conn = DataSourceListener.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(userId);
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setProductName(rs.getString("name"));
                    item.setUnitPrice(rs.getBigDecimal("price"));
                    results.add(item);
                }
            }
        }
        return results;
    }

    public BigDecimal getCartTotal(long userId) throws Exception {
        List<CartItem> items = getCart(userId);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getLineTotal());
        }
        return total;
    }
}
