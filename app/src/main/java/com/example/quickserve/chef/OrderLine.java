package com.example.quickserve.chef;

/**
 * Lightweight item line for an order.
 */
public class OrderLine {
    private String name;
    private int quantity;

    public OrderLine() {}

    public OrderLine(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

