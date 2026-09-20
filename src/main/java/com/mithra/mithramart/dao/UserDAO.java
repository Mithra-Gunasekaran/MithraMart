package com.mithra.mithramart.dao;

import com.mithra.mithramart.listener.DataSourceListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    // Counts total rows in the users table.
    // Uses try-with-resources so Connection/Statement/ResultSet all auto-close,
    // per spec Section 2 rule 6.
    public int countUsers() throws Exception {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection conn = DataSourceListener.getDataSource().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

        public boolean createUser(String name, String email, String plainPassword, String role) throws Exception {
        String sql = "INSERT INTO users (name, email, password_hash, role, created_at) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        String hashedPassword = com.mithra.mithramart.util.PasswordUtil.hash(plainPassword);

        try (java.sql.Connection conn = DataSourceListener.getDataSource().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, hashedPassword);
            ps.setString(4, role);

            ps.executeUpdate();
            return true;
       } catch (java.sql.SQLException e) {
            return false;
        }
   }
}
