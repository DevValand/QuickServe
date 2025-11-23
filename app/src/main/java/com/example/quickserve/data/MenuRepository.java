package com.example.quickserve.data;

import com.example.quickserve.manager.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuRepository {

    private static final ArrayList<MenuItem> menuItems = new ArrayList<>();

    // Static initializer to populate the list with some default data
    static {
        menuItems.add(new MenuItem("Paneer Butter Masala", "Main Course", 250.00));
        menuItems.add(new MenuItem("Vegetable Biryani", "Main Course", 220.00));
        menuItems.add(new MenuItem("Garlic Naan", "Breads", 45.00));
        menuItems.add(new MenuItem("Gulab Jamun", "Desserts", 80.00));
    }

    public static List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public static void addMenuItem(MenuItem menuItem) {
        menuItems.add(menuItem);
    }
}
