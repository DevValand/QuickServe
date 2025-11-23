package com.example.quickserve.waiter;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.manager.MenuItem;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class TakeOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_order);

        RecyclerView menuRecyclerView = findViewById(R.id.menu_items_recycler_view);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Using the same MenuItem from the manager for now
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Paneer Butter Masala", "Main Course", 12.99));
        menuItems.add(new MenuItem("Vegetable Biryani", "Main Course", 10.50));
        menuItems.add(new MenuItem("Garlic Naan", "Breads", 3.50));
        menuItems.add(new MenuItem("Gulab Jamun", "Desserts", 4.00));

        OrderMenuAdapter adapter = new OrderMenuAdapter(this, menuItems);
        menuRecyclerView.setAdapter(adapter);

        ExtendedFloatingActionButton fabPlaceOrder = findViewById(R.id.fab_place_order);
        fabPlaceOrder.setOnClickListener(view -> {
            Toast.makeText(this, "Order Placed!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to the table view
        });
    }
}
