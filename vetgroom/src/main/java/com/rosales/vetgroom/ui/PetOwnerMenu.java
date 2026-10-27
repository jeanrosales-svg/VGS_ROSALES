package com.rosales.vetgroom.ui;

import com.rosales.vetgroom.dao.UserDao;
import com.rosales.vetgroom.model.User;

import java.util.Scanner;

public class PetOwnerMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final User currentUser;

    public PetOwnerMenu(UserDao userDao, Scanner scanner, User currentUser) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== PET OWNER MENU ===");
            System.out.println("  1. Request appointment");
            System.out.println("  2. View schedules and service details");
            System.out.println("  3. Check appointment status / history");
            System.out.println("  4. Request reschedule or cancellation");
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
