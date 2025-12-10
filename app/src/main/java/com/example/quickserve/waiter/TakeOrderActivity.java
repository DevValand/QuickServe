package com.example.quickserve.waiter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.data.MenuRepository;
import com.example.quickserve.manager.MenuItem;
import com.example.quickserve.data.TableRepository;
import com.example.quickserve.data.OrderRepository;
import com.example.quickserve.waiter.adapter.ExpandableMenuAdapter;
import com.example.quickserve.waiter.adapter.OrderSummaryAdapter;
import com.example.quickserve.waiter.model.ExpandableMenuItem;
import com.example.quickserve.waiter.model.OrderItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TakeOrderActivity extends AppCompatActivity {

    private ExpandableMenuAdapter adapter;
    private int tableNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_order);

        tableNumber = getIntent().getIntExtra("TABLE_NUMBER", 0);

        // Set table status to "occupied" when waiter starts taking order
        TableRepository.updateTableStatus(tableNumber, "occupied");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Take Order - Table " + tableNumber);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        RecyclerView recyclerView = findViewById(R.id.menu_items_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Object> expandableList = buildExpandableList();
        adapter = new ExpandableMenuAdapter(this, expandableList);
        recyclerView.setAdapter(adapter);

        Button placeOrderButton = findViewById(R.id.btn_place_order);
        placeOrderButton.setOnClickListener(v -> showOrderSummaryDialog());

        Button finalizeBillButton = findViewById(R.id.btn_finalize_bill);
        finalizeBillButton.setOnClickListener(v -> {
            // Update table status to "empty" when finalizing bill
            TableRepository.updateTableStatus(tableNumber, "empty");
            Intent intent = new Intent(this, BillActivity.class);
            intent.putExtra("TABLE_NUMBER", tableNumber);
            startActivity(intent);
        });
    }

    private void showOrderSummaryDialog() {
        List<OrderItem> orderedItems = adapter.getOrderedItems();

        if (orderedItems.isEmpty()) {
            Toast.makeText(this, "Please add items to the order first.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_view_order, null);
        builder.setView(dialogView);

        RecyclerView summaryRecyclerView = dialogView.findViewById(R.id.rv_order_summary);
        summaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        OrderSummaryAdapter summaryAdapter = new OrderSummaryAdapter(this, orderedItems);
        summaryRecyclerView.setAdapter(summaryAdapter);

        final AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_confirm_order).setOnClickListener(v -> {
            OrderRepository.createOrder(tableNumber, orderedItems);

            Toast.makeText(this, "Order for Table " + tableNumber + " placed successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            finish(); // Go back to the table selection screen
        });

        dialogView.findViewById(R.id.btn_cancel_order).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private List<Object> buildExpandableList() {
        // ... (This method remains the same)
        List<Object> consolidatedList = new ArrayList<>();
        List<MenuItem> menuItems = MenuRepository.getMenuItems();

        Map<String, List<MenuItem>> groupedMap = new LinkedHashMap<>();
        for (MenuItem item : menuItems) {
            String category = item.getCategory();
            if (!groupedMap.containsKey(category)) {
                groupedMap.put(category, new ArrayList<>());
            }
            groupedMap.get(category).add(item);
        }

        for (Map.Entry<String, List<MenuItem>> entry : groupedMap.entrySet()) {
            consolidatedList.add(new ExpandableMenuItem(entry.getKey(), entry.getValue()));
        }
        
        return consolidatedList;
    }
}
