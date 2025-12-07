package com.example.quickserve.waiter.model;

import com.example.quickserve.manager.MenuItem;
import java.util.List;

public class ExpandableMenuItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    public int type;
    public String category;
    public boolean isExpanded = false;

    // This now correctly holds MenuItem objects, not other ExpandableMenuItems
    public List<MenuItem> subItems;

    // Constructor for Header
    public ExpandableMenuItem(String category, List<MenuItem> subItems) {
        this.type = TYPE_HEADER;
        this.category = category;
        this.subItems = subItems;
    }
}
