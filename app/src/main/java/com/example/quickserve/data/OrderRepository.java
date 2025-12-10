package com.example.quickserve.data;

import androidx.annotation.Nullable;

import com.example.quickserve.chef.Order;
import com.example.quickserve.chef.OrderLine;
import com.example.quickserve.manager.MenuItem;
import com.example.quickserve.waiter.model.OrderItem;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central Firestore-backed order repository.
 */
public class OrderRepository {

    public interface OrdersListener {
        void onOrdersUpdated(List<Order> orders);
    }

    private static final ArrayList<Order> orders = new ArrayList<>();
    private static final CopyOnWriteArrayList<OrdersListener> listeners = new CopyOnWriteArrayList<>();
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static ListenerRegistration registration;

    static {
        startListening();
    }

    private static void startListening() {
        if (registration != null) return;
        registration = db.collection("orders").addSnapshotListener((snapshots, e) -> {
            if (e != null || snapshots == null) return;
            orders.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                Order o = mapDoc(doc);
                if (o != null) orders.add(o);
            }
            notifyListeners();
        });
    }

    @Nullable
    private static Order mapDoc(QueryDocumentSnapshot doc) {
        // Try both field names for compatibility
        Long tableNumber = doc.getLong("tableNumber");
        if (tableNumber == null) {
            tableNumber = doc.getLong("table_number"); // Fallback for old format
        }
        String status = doc.getString("status");
        List<OrderLine> lines = new ArrayList<>();
        List<?> raw = (List<?>) doc.get("items");
        if (raw != null) {
            for (Object o : raw) {
                if (o instanceof java.util.Map) {
                    java.util.Map<?, ?> m = (java.util.Map<?, ?>) o;
                    String name = m.get("name") != null ? m.get("name").toString() : "";
                    int qty = 1;
                    Object qObj = m.get("quantity");
                    if (qObj instanceof Number) qty = ((Number) qObj).intValue();
                    lines.add(new OrderLine(name, qty));
                }
            }
        }
        if (tableNumber == null) return null;
        Order o = new Order(doc.getId(), tableNumber.intValue(), lines, normalizeStatus(status));
        return o;
    }

    private static String normalizeStatus(String status) {
        if (status == null) return "pending";
        return status.toLowerCase(Locale.US);
    }

    private static void notifyListeners() {
        for (OrdersListener listener : listeners) {
            listener.onOrdersUpdated(orders);
        }
    }

    public static List<Order> getOrders() {
        return orders;
    }

    public static void addListener(OrdersListener listener) {
        if (listener == null) return;
        listeners.add(listener);
        listener.onOrdersUpdated(orders);
    }

    public static void removeListener(OrdersListener listener) {
        listeners.remove(listener);
    }

    public static void createOrder(int tableNumber, List<OrderItem> items) {
        List<OrderLine> lines = new ArrayList<>();
        for (OrderItem item : items) {
            MenuItem m = item.getMenuItem();
            lines.add(new OrderLine(m.getName(), item.getQuantity()));
        }

        // Create order in Firestore (collection will be auto-created if it doesn't exist)
        db.collection("orders").add(new Order(null, tableNumber, lines, "pending"))
                .addOnSuccessListener(ref -> {
                    // Table status is already updated in TakeOrderActivity, 
                    // but we ensure it's set here as well in case of any issues
                    TableRepository.updateTableStatus(tableNumber, "order_taken");
                })
                .addOnFailureListener(e -> {
                    // Even if order creation fails, we should still try to update table status
                    // This ensures the table status reflects the user's action
                    TableRepository.updateTableStatus(tableNumber, "order_taken");
                });
    }

    public static void updateStatus(Order order, String newStatus) {
        if (order == null || order.getId() == null) return;
        String normalized = normalizeStatus(newStatus);
        db.collection("orders").document(order.getId())
                .update("status", normalized)
                .addOnSuccessListener(v -> {
                    // Keep table state in sync
                    switch (normalized) {
                        case "preparing":
                            TableRepository.updateTableStatus(order.getTableNumber(), "preparing");
                            break;
                        case "prepared":
                            TableRepository.updateTableStatus(order.getTableNumber(), "prepared");
                            break;
                        case "served":
                            TableRepository.updateTableStatus(order.getTableNumber(), "served");
                            break;
                        case "pending":
                        case "order_taken":
                            TableRepository.updateTableStatus(order.getTableNumber(), "order_taken");
                            break;
                        default:
                            TableRepository.updateTableStatus(order.getTableNumber(), "empty");
                    }
                });
    }
}

