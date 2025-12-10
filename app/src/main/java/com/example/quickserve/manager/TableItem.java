package com.example.quickserve.manager;

public class TableItem {
    private String id;
    private String table_number;
    private String status;

    // Firestore requires no-arg constructor
    public TableItem() {}

    public TableItem(String tableNumber, String status) {
        this.table_number = tableNumber;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTable_number() { return table_number; }
    public void setTable_number(String table_number) { this.table_number = table_number; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

