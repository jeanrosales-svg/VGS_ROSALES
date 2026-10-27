package com.rosales.vetgroom.ui;

import com.rosales.vetgroom.dao.UserDao;
import com.rosales.vetgroom.model.User;

import java.util.List;
import java.util.Scanner;

/** Administrator menu. All data access goes through UserDao; no SQL here. */
public class AdminMenu {

    private final UserDao userDao;
    private final Scanner scanner;
    private final User currentUser;

    public AdminMenu(UserDao userDao, Scanner scanner, User currentUser) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.currentUser = currentUser;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== ADMINISTRATOR MENU ===");
            System.out.println("  1. View All System Users");
            System.out.println("  2. View Pending Registrations");
            System.out.println("  3. Approve User Account");
            System.out.println("  0. Logout");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1": viewAllUsers(); break;
                case "2": viewPendingUsers(); break;
                case "3": approveUserAccount(); break;
                case "0": running = false; System.out.println("Logged out."); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void viewAllUsers() {
        System.out.println("\n--- ALL SYSTEM USERS ---");
        printUserTable(userDao.getAllUsers());
    }

    private void viewPendingUsers() {
        System.out.println("\n--- PENDING REGISTRATIONS ---");
        printUserTable(userDao.getPendingUsers());
    }

    private void approveUserAccount() {
        List<User> pending = userDao.getPendingUsers();
        System.out.println("\n--- APPROVE USER ACCOUNT ---");
        if (pending.isEmpty()) {
            System.out.println("No pending accounts to approve.");
            return;
        }
        printUserTable(pending);

        while (true) {
            System.out.print("Enter Target User ID to approve (0 to cancel): ");
            String input = scanner.nextLine().trim();

            int targetId;
            try {
                targetId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric User ID.");
                continue;
            }

            if (targetId == 0) {
                System.out.println("Approval cancelled.");
                return;
            }

            boolean inPendingList = pending.stream().anyMatch(u -> u.getUserId() == targetId);
            if (!inPendingList) {
                System.out.println("User ID " + targetId + " is not in the pending list. Try again.");
                continue;
            }

            if (userDao.approveUser(targetId)) {
                System.out.println("SUCCESS: User ID " + targetId + " has been APPROVED.");
            } else {
                System.out.println("Approval failed. The account may no longer be pending.");
            }
            return;
        }
    }

    /** Renders any list of users as an aligned, bordered table. */
    private void printUserTable(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        String border = "+-------+-----------------+----------------+------------+";
        System.out.println(border);
        System.out.printf("| %-5s | %-15s | %-14s | %-10s |%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);
        for (User u : users) {
            System.out.printf("| %-5s | %-15s | %-14s | %-10s |%n",
                    u.getUserId(), u.getUsername(), u.getRole().name(), u.getAccountStatus());
        }
        System.out.println(border);
        System.out.println(users.size() + " record(s).");
    }
}
