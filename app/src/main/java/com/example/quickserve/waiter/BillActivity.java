package com.example.quickserve.waiter;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.data.TableRepository;
import com.example.quickserve.manager.MenuItem;

import java.util.ArrayList;
import java.util.Locale;

public class BillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        // In a real app, you would pass the actual list of ordered items
        ArrayList<MenuItem> orderedItems = new ArrayList<>();
        orderedItems.add(new MenuItem("Paneer Butter Masala", "", 250.00));
        orderedItems.add(new MenuItem("Garlic Naan", "", 45.00));
        orderedItems.add(new MenuItem("Garlic Naan", "", 45.00));

        int tableNumber = getIntent().getIntExtra("TABLE_NUMBER", 0);

        TextView billTitle = findViewById(R.id.tv_bill_title);
        RecyclerView billItemsRecyclerView = findViewById(R.id.bill_items_recycler_view);
        TextView subtotalView = findViewById(R.id.tv_subtotal);
        TextView taxView = findViewById(R.id.tv_tax);
        TextView totalView = findViewById(R.id.tv_total);
        Button finishButton = findViewById(R.id.btn_finish);

        billTitle.setText("Final Bill - Table " + tableNumber);

        BillAdapter adapter = new BillAdapter(this, orderedItems);
        billItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        billItemsRecyclerView.setAdapter(adapter);

        // --- Correctly Calculate and Display Totals ---
        double subtotal = 0;
        for (MenuItem item : orderedItems) {
            subtotal += item.getPrice();
        }
        double tax = 0.00; // Tax is always zero as requested
        double total = subtotal + tax;

        subtotalView.setText(String.format(Locale.getDefault(), "₹%.2f", subtotal));
        taxView.setText(String.format(Locale.getDefault(), "₹%.2f", tax));
        totalView.setText(String.format(Locale.getDefault(), "₹%.2f", total));
        // --- End of Fix ---

        finishButton.setOnClickListener(v -> {
            Table table = TableRepository.findTableByNumber(tableNumber);
            if (table != null) {
                table.setStatus("Empty");
            }
            Toast.makeText(this, "Table " + tableNumber + " has been cleared.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
