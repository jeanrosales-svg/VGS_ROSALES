package com.rosales.vetgroom.model;

public class User {
    private final int userId;
    private final String username;
    private final String passwordHash;
    private final String fullName;
    private final String email;
    private final Role role;
    private final String accountStatus;

    public User(String username, String passwordHash, String fullName, String email, Role role) {
        this(0, username, passwordHash, fullName, email, role, "PENDING");
    }

    public User(int userId, String username, String passwordHash, String fullName,
                String email, Role role, String accountStatus) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.accountStatus = accountStatus;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public String getAccountStatus() { return accountStatus; }
}
