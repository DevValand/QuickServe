package com.example.quickserve.data;

import com.example.quickserve.waiter.Table;

import java.util.ArrayList;
import java.util.List;

public class TableRepository {

    private static final ArrayList<Table> tables = new ArrayList<>();

    // Static initializer to populate the list with some default tables
    static {
        tables.add(new Table(1, "Empty"));
        tables.add(new Table(2, "Occupied"));
        tables.add(new Table(3, "Preparing"));
        tables.add(new Table(4, "Empty"));
        tables.add(new Table(5, "Empty"));
        tables.add(new Table(6, "Occupied"));
    }

    public static List<Table> getTables() {
        return tables;
    }

    public static Table findTableByNumber(int tableNumber) {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                return table;
            }
        }
        return null;
    }
}
