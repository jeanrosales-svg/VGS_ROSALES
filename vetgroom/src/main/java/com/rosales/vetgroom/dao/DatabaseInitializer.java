package com.rosales.vetgroom.dao;

import com.rosales.vetgroom.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** Creates all required tables (if missing) before the CLI menu starts. */
public class DatabaseInitializer {

    private static final String CREATE_USERS =
        "CREATE TABLE IF NOT EXISTS users ("
      + " user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
      + " username TEXT NOT NULL UNIQUE,"
      + " password_hash TEXT NOT NULL,"
      + " full_name TEXT NOT NULL,"
      + " email TEXT NOT NULL,"
      + " role TEXT NOT NULL CHECK (role IN ('PET_OWNER','VETERINARIAN','GROOMER','ADMINISTRATOR')),"
      + " account_status TEXT NOT NULL DEFAULT 'PENDING'"
      + "   CHECK (account_status IN ('PENDING','APPROVED','INACTIVE'))"
      + ")";

    private static final String CREATE_PET_OWNERS =
        "CREATE TABLE IF NOT EXISTS pet_owners ("
      + " owner_id INTEGER PRIMARY KEY AUTOINCREMENT,"
      + " full_name TEXT NOT NULL,"
      + " contact_number TEXT NOT NULL,"
      + " email TEXT,"
      + " address TEXT,"
      + " account_status TEXT NOT NULL DEFAULT 'ACTIVE'"
      + "   CHECK (account_status IN ('ACTIVE','INACTIVE'))"
      + ")";

    private static final String CREATE_APPOINTMENTS =
        "CREATE TABLE IF NOT EXISTS appointments ("
      + " appointment_id INTEGER PRIMARY KEY AUTOINCREMENT,"
      + " owner_id INTEGER NOT NULL,"
      + " staff_id INTEGER,"
      + " pet_name TEXT NOT NULL,"
      + " pet_type TEXT NOT NULL,"
      + " pet_breed TEXT,"
      + " service_type TEXT NOT NULL CHECK (service_type IN ('VETERINARY','GROOMING')),"
      + " service_description TEXT,"
      + " appointment_date TEXT NOT NULL,"
      + " appointment_time TEXT NOT NULL,"
      + " estimated_fee REAL NOT NULL DEFAULT 0,"
      + " appointment_status TEXT NOT NULL DEFAULT 'PENDING'"
      + "   CHECK (appointment_status IN ('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED')),"
      + " service_notes TEXT,"
      + " FOREIGN KEY (owner_id) REFERENCES pet_owners(owner_id),"
      + " FOREIGN KEY (staff_id) REFERENCES users(user_id)"
      + ")";

    private DatabaseInitializer() { }

    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {

            // Parent tables first, then the table holding foreign keys
            st.execute(CREATE_USERS);
            st.execute(CREATE_PET_OWNERS);
            st.execute(CREATE_APPOINTMENTS);

            seedDefaultAdmin(conn);
            System.out.println("[DB] Database initialized.");
        } catch (SQLException e) {
            throw new RuntimeException("Could not initialize database: " + e.getMessage(), e);
        }
    }

    /** Ensures someone can approve PENDING accounts on a fresh database. */
    private static void seedDefaultAdmin(Connection conn) throws SQLException {
        String check = "SELECT COUNT(*) FROM users WHERE role = 'ADMINISTRATOR' AND account_status = 'APPROVED'";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(check)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }
        String insert = "INSERT OR IGNORE INTO users (username, password_hash, full_name, email, role, account_status) "
                      + "VALUES (?, ?, ?, ?, 'ADMINISTRATOR', 'APPROVED')";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            ps.setString(1, "admin");
            ps.setString(2, PasswordUtil.hash("admin123"));
            ps.setString(3, "Default Administrator");
            ps.setString(4, "admin@clinic.local");
            ps.executeUpdate();
            System.out.println("[DB] Default admin created (username: admin / password: admin123).");
        }
    }
}
