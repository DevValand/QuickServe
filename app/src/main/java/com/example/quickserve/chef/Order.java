package com.example.quickserve.chef;

import java.util.List;

/**
 * Firestore-backed order model.
 */
public class Order {
    private String id;
    private int tableNumber;
    private List<OrderLine> items;
    private String status; // pending, preparing, prepared

    // No-arg for Firestore
    public Order() {}

    public Order(String id, int tableNumber, List<OrderLine> items, String status) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.items = items;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public List<OrderLine> getItems() {
        return items;
    }

    public void setItems(List<OrderLine> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

