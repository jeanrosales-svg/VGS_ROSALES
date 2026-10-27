package com.rosales.vetgroom.ui;

import com.rosales.vetgroom.dao.UserDao;
import com.rosales.vetgroom.model.User;

import java.util.Scanner;

public class GroomerMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final User currentUser;

    public GroomerMenu(UserDao userDao, Scanner scanner, User currentUser) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== GROOMER MENU ===");
            System.out.println("  1. View assigned appointments");
            System.out.println("  2. Check pet information");
            System.out.println("  3. Update appointment status");
            System.out.println("  4. Record completed services");
            System.out.println("  0. Logout");
            System.out.print("Choose: ");
            String choice = scanner.nextLine().trim();
            if (choice.equals("0")) {
                running = false;
                System.out.println("Logged out.");
            } else {
                System.out.println("Coming soon in a later deliverable.");
            }
        }
    }
}
