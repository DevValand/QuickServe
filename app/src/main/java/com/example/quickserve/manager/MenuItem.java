package com.example.quickserve.manager;

import com.google.firebase.firestore.Exclude;

public class MenuItem {
    private String id;
    private String name;
    private String category;
    private double price;

    // No-argument constructor required for Firestore
    public MenuItem() {}

    public MenuItem(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    @Exclude // Exclude this from being saved to Firestore, as it's the document ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
