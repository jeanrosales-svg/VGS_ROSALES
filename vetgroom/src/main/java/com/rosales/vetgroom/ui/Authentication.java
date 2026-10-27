package com.rosales.vetgroom.ui;

import com.rosales.vetgroom.dao.UserDao;
import com.rosales.vetgroom.model.Role;
import com.rosales.vetgroom.model.User;
import com.rosales.vetgroom.util.PasswordUtil;

import java.util.Optional;
import java.util.Scanner;

/** Handles login, registration, and role-based routing. No SQL lives here. */
public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== VETERINARY AND GROOMING APPOINTMENT SYSTEM ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            switch (scanner.nextLine().trim()) {
                case "1": login(); break;
                case "2": register(); break;
                case "0": running = false; System.out.println("Goodbye!"); break;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        Optional<User> found = userDao.findByUsername(username);
        if (found.isEmpty() || !PasswordUtil.verify(password, found.get().getPasswordHash())) {
            System.out.println("Invalid username or password.");
            return;
        }

        User user = found.get();
        switch (user.getAccountStatus()) {
            case "APPROVED":
                System.out.println("\nWelcome, " + user.getFullName() + " (" + user.getRole().getLabel() + ")");
                routeToMenu(user);
                break;
            case "PENDING":
                System.out.println("Your account is still PENDING approval by an administrator.");
                break;
            default:
                System.out.println("This account is deactivated. Please contact the clinic.");
        }
    }

    private void register() {
        System.out.println("\n--- Register New Account ---");

        String username;
        while (true) {
            System.out.print("Username: ");
            username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
            } else if (userDao.usernameExists(username)) {
                System.out.println("Username already taken.");
            } else {
                break;
            }
        }

        String password;
        while (true) {
            System.out.print("Password (min 6 characters): ");
            password = scanner.nextLine();
            if (password.length() >= 6) {
                break;
            }
            System.out.println("Password too short.");
        }

        System.out.print("Full name: ");
        String fullName = scanner.nextLine().trim();

        String email;
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();
            if (email.contains("@")) {
                break;
            }
            System.out.println("Enter a valid email.");
        }

        Role role = chooseRole();

        User newUser = new User(username, PasswordUtil.hash(password), fullName, email, role);
        if (userDao.register(newUser)) {
            System.out.println("Registration successful! Status: PENDING (wait for administrator approval).");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }

    private Role chooseRole() {
        Role[] roles = Role.values();
        while (true) {
            System.out.println("Select your role:");
            for (int i = 0; i < roles.length; i++) {
                System.out.println("  " + (i + 1) + ". " + roles[i].getLabel());
            }
            System.out.print("Choose: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= roles.length) {
                    return roles[choice - 1];
                }
            } catch (NumberFormatException ignored) {
                // fall through to the error message
            }
            System.out.println("Invalid role choice.");
        }
    }

    /** Sends the logged-in user to the menu for their assigned role. */
    private void routeToMenu(User user) {
        switch (user.getRole()) {
            case PET_OWNER:     new PetOwnerMenu(userDao, scanner, user).show(); break;
            case VETERINARIAN:  new VeterinarianMenu(userDao, scanner, user).show(); break;
            case GROOMER:       new GroomerMenu(userDao, scanner, user).show(); break;
            case ADMINISTRATOR: new AdminMenu(userDao, scanner, user).show(); break;
        }
    }
}
