package com.rosales.vetgroom;

import com.rosales.vetgroom.dao.DatabaseInitializer;
import com.rosales.vetgroom.dao.UserDao;
import com.rosales.vetgroom.ui.Authentication;

import java.util.Scanner;

/**
 * Single entry point. Flow:
 * Initialize Database -> Instantiate DAOs & Utilities -> Authentication.start()
 */
public class Main {

    public static void main(String[] args) {
        // 1. Initialize database (tables must exist before any menu appears)
        try {
            DatabaseInitializer.initialize();
        } catch (RuntimeException e) {
            System.err.println("Startup failed: " + e.getMessage());
            return;
        }

        // 2. Instantiate shared DAOs & utilities (one instance each)
        UserDao userDao = new UserDao();
        Scanner scanner = new Scanner(System.in);

        // 3. Delegate everything else to Authentication
        try {
            new Authentication(userDao, scanner).start();
        } finally {
            scanner.close();
        }
    }
}
