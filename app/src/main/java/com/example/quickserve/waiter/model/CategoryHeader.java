package com.example.quickserve.waiter.model;

public class CategoryHeader {
    private String name;
    private boolean isExpanded = false;

    public CategoryHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
