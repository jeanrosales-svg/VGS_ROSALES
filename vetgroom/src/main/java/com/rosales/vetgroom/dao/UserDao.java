package com.rosales.vetgroom.dao;

import com.rosales.vetgroom.model.Role;
import com.rosales.vetgroom.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao {

    /** Inserts a new user. Status is PENDING until an administrator approves it. */
    public boolean register(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role, account_status) "
                   + "VALUES (?, ?, ?, ?, ?, 'PENDING')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole().name());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username) {
        return findByUsername(username).isPresent();
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lookup failed: " + e.getMessage());
        }
        return Optional.empty();
    }

    /** All registered accounts, regardless of role or status. */
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY user_id";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(map(rs));
            }
        } catch (SQLException e) {
            System.err.println("Could not load users: " + e.getMessage());
        }
        return users;
    }

    /** All accounts with status PENDING, across every role. */
    public List<User> getPendingUsers() {
        String sql = "SELECT * FROM users WHERE account_status = ? ORDER BY user_id";
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "PENDING");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(map(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not load pending users: " + e.getMessage());
        }
        return users;
    }

    /** Changes a PENDING account to APPROVED. Returns true if a row was updated. */
    public boolean approveUser(int userId) {
        String sql = "UPDATE users SET account_status = 'APPROVED' WHERE user_id = ? AND account_status = 'PENDING'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Approval failed: " + e.getMessage());
            return false;
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("user_id"),
            rs.getString("username"),
            rs.getString("password_hash"),
            rs.getString("full_name"),
            rs.getString("email"),
            Role.valueOf(rs.getString("role")),
            rs.getString("account_status"));
    }
}
