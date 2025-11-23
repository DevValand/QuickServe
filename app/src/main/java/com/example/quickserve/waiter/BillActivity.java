package com.example.quickserve.waiter;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.manager.MenuItem;

import java.util.ArrayList;
import java.util.Locale;

public class BillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        // In a real app, you would pass the list of ordered items here
        // For now, we'll use static data
        ArrayList<MenuItem> orderedItems = new ArrayList<>();
        orderedItems.add(new MenuItem("Paneer Butter Masala", "", 12.99));
        orderedItems.add(new MenuItem("Garlic Naan", "", 3.50));
        orderedItems.add(new MenuItem("Garlic Naan", "", 3.50));

        int tableNumber = getIntent().getIntExtra("TABLE_NUMBER", 0);

        TextView billTitle = findViewById(R.id.tv_bill_title);
        RecyclerView billItemsRecyclerView = findViewById(R.id.bill_items_recycler_view);
        TextView subtotalView = findViewById(R.id.tv_subtotal);
        TextView taxView = findViewById(R.id.tv_tax);
        TextView totalView = findViewById(R.id.tv_total);
        Button closeButton = findViewById(R.id.btn_close_bill);

        billTitle.setText("Final Bill - Table " + tableNumber);

        BillAdapter adapter = new BillAdapter(this, orderedItems);
        billItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        billItemsRecyclerView.setAdapter(adapter);

        // Calculate costs
        double subtotal = 0;
        for (MenuItem item : orderedItems) {
            subtotal += item.getPrice();
        }
        double tax = subtotal * 0.10; // 10% tax
        double total = subtotal + tax;

        subtotalView.setText(String.format(Locale.getDefault(), "$%.2f", subtotal));
        taxView.setText(String.format(Locale.getDefault(), "$%.2f", tax));
        totalView.setText(String.format(Locale.getDefault(), "$%.2f", total));

        closeButton.setOnClickListener(v -> finish());
    }
}
