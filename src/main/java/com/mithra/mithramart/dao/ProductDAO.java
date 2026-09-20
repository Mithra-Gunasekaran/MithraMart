package com.mithra.mithramart.dao;

import com.mithra.mithramart.listener.DataSourceListener;
import com.mithra.mithramart.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public long createProduct(Product p) throws Exception {
        String sql = "INSERT INTO products (seller_id, name, description, price, stock_qty, category, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DataSourceListener.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, p.getSellerId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getDescription());
            ps.setBigDecimal(4, p.getPrice());
            ps.setInt(5, p.getStockQty());
            ps.setString(6, p.getCategory());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
            return -1;
        }
    }

    public List<Product> findBySeller(long sellerId) throws Exception {
        String sql = "SELECT id, seller_id, name, description, price, stock_qty, category FROM products WHERE seller_id = ?";
        List<Product> results = new ArrayList<>();

        try (Connection conn = DataSourceListener.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, sellerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

        // Public browse/search - returns ALL products (not filtered by seller),
    // optionally filtered by category and/or a keyword match on name.
    public List<Product> searchProducts(String category, String keyword) throws Exception {
        StringBuilder sql = new StringBuilder(
            "SELECT id, seller_id, name, description, price, stock_qty, category FROM products WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (category != null && !category.isBlank()) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND LOWER(name) LIKE ?");
            params.add("%" + keyword.toLowerCase() + "%");
        }

        List<Product> results = new ArrayList<>();

        try (Connection conn = DataSourceListener.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRow(rs));
                }
            }
        }
        return results;
    }

    public Product findById(long id) throws Exception {
        String sql = "SELECT id, seller_id, name, description, price, stock_qty, category FROM products WHERE id = ?";

        try (Connection conn = DataSourceListener.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    // Only updates if the product belongs to this seller - prevents editing someone else's listing
    public boolean updateProduct(Product p) throws Exception {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock_qty = ?, category = ? " +
                     "WHERE id = ? AND seller_id = ?";

        try (Connection conn = DataSourceListener.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getStockQty());
            ps.setString(5, p.getCategory());
            ps.setLong(6, p.getId());
            ps.setLong(7, p.getSellerId());

            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public boolean deleteProduct(long id, long sellerId) throws Exception {
        String sql = "DELETE FROM products WHERE id = ? AND seller_id = ?";

        try (Connection conn = DataSourceListener.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.setLong(2, sellerId);

            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setSellerId(rs.getLong("seller_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setStockQty(rs.getInt("stock_qty"));
        p.setCategory(rs.getString("category"));
        return p;
    }
}