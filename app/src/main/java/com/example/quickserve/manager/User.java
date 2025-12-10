package com.example.quickserve.manager;

public class User {
    private String id; // Firestore document ID
    private String email;
    private String role;

    // Required public no-arg constructor for Firebase
    public User() {}

    public User(String email, String role) {
        this.email = email;
        this.role = role;
    }

    // Getters
    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
}
