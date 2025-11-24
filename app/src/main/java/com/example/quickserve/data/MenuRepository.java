package com.example.quickserve.data;

import com.example.quickserve.manager.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuRepository {

    private static final ArrayList<MenuItem> menuItems = new ArrayList<>();

    static {
        // Default Menu Items
        menuItems.add(new MenuItem("Veg Samosa", "Starters", 60.00));
        menuItems.add(new MenuItem("Paneer Tikka", "Starters", 180.00));
        menuItems.add(new MenuItem("Paneer Butter Masala", "Main Course", 250.00));
        menuItems.add(new MenuItem("Vegetable Biryani", "Main Course", 220.00));
        menuItems.add(new MenuItem("Gulab Jamun", "Desserts", 80.00));
        menuItems.add(new MenuItem("Ras Malai", "Desserts", 100.00));
        menuItems.add(new MenuItem("Masala Chai", "Beverages", 30.00));
        menuItems.add(new MenuItem("Lassi", "Beverages", 70.00));
    }

    public static List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public static void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
    }
}
