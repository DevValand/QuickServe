package com.example.quickserve.chef;

import com.example.quickserve.manager.MenuItem;

import java.util.List;

public class Order {
    private int tableNumber;
    private List<MenuItem> items;
    private String status; // e.g., "Pending", "Preparing", "Ready"

    public Order(int tableNumber, List<MenuItem> items, String status) {
        this.tableNumber = tableNumber;
        this.items = items;
        this.status = status;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
