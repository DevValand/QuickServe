package com.example.quickserve.waiter;

public class Table {
    private int tableNumber;
    private String status;

    public Table(int tableNumber, String status) {
        this.tableNumber = tableNumber;
        this.status = status;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getStatus() {
        return status;
    }

    // --- Add the missing setter method ---
    public void setStatus(String status) {
        this.status = status;
    }
}
