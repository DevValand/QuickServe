package com.example.quickserve.data;

import androidx.annotation.Nullable;

import com.example.quickserve.waiter.Table;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Centralized repository that keeps a single table list in sync with Firestore
 * so manager/chef/waiter all see the same state.
 */
public class TableRepository {

    public interface TablesListener {
        void onTablesUpdated(List<Table> tables);
    }

    private static final ArrayList<Table> tables = new ArrayList<>();
    private static final CopyOnWriteArrayList<TablesListener> listeners = new CopyOnWriteArrayList<>();
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ListenerRegistration registration;

    static {
        startListening();
    }

    private static void startListening() {
        if (registration != null) return;
        registration = db.collection("tables").addSnapshotListener((snapshots, e) -> {
            if (e != null || snapshots == null) return;

            tables.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                Table t = mapDocToTable(doc);
                if (t != null) tables.add(t);
            }
            notifyListeners();
        });
    }

    @Nullable
    private static Table mapDocToTable(QueryDocumentSnapshot doc) {
        String numStr = doc.getString("table_number");
        String statusRaw = doc.getString("status");
        if (numStr == null) return null;
        try {
            int num = Integer.parseInt(numStr.replaceAll("[^0-9]", ""));
            String status = normalizeStatus(statusRaw);
            return new Table(num, status);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String normalizeStatus(String status) {
        if (status == null) return "Empty";
        switch (status.toLowerCase(Locale.US)) {
            case "occupied":
                return "Occupied";
            case "order_taken":
                return "Order Taken";
            case "preparing":
                return "Preparing";
            case "prepared":
                return "Prepared";
            case "served":
                return "Served";
            default:
                return "Empty";
        }
    }

    private static void notifyListeners() {
        for (TablesListener listener : listeners) {
            listener.onTablesUpdated(tables);
        }
    }

    public static List<Table> getTables() {
        return tables;
    }

    public static void addListener(TablesListener listener) {
        if (listener == null) return;
        listeners.add(listener);
        listener.onTablesUpdated(tables);
    }

    public static void removeListener(TablesListener listener) {
        listeners.remove(listener);
    }

    public static Table findTableByNumber(int tableNumber) {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                return table;
            }
        }
        return null;
    }

    /**
     * Update a table status in Firestore; snapshot listener will refresh local list.
     */
    public static void updateTableStatus(int tableNumber, String newStatus) {
        String target = String.valueOf(tableNumber);
        String statusToSave = newStatus == null ? "empty" : newStatus.toLowerCase(Locale.US);
        
        // Try to find table by exact match first (e.g., "1")
        db.collection("tables")
                .whereEqualTo("table_number", target)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        // Found exact match, update it
                        query.getDocuments().get(0).getReference().update("status", statusToSave);
                    } else {
                        // Try to find by number in any format (e.g., "T1", "Table 1", etc.)
                        db.collection("tables")
                                .get()
                                .addOnSuccessListener(allTables -> {
                                    boolean found = false;
                                    for (QueryDocumentSnapshot doc : allTables) {
                                        String numStr = doc.getString("table_number");
                                        if (numStr != null) {
                                            // Extract numeric part and compare
                                            String numericPart = numStr.replaceAll("[^0-9]", "");
                                            if (numericPart.equals(target)) {
                                                doc.getReference().update("status", statusToSave);
                                                found = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!found) {
                                        // Create if missing
                                        db.collection("tables").add(new com.example.quickserve.manager.TableItem(target, statusToSave))
                                                .addOnSuccessListener(documentReference -> {
                                                    documentReference.update("id", documentReference.getId());
                                                });
                                    }
                                });
                    }
                });
    }
}
