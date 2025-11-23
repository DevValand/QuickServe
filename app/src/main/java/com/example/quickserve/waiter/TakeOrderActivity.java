package com.example.quickserve.waiter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.data.MenuRepository;
import com.example.quickserve.manager.MenuItem;

import java.util.ArrayList;

public class TakeOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_order);

        int tableNumber = getIntent().getIntExtra("TABLE_NUMBER", 0);

        RecyclerView menuRecyclerView = findViewById(R.id.menu_items_recycler_view);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch menu items from the central repository
        ArrayList<MenuItem> menuItems = (ArrayList<MenuItem>) MenuRepository.getMenuItems();

        OrderMenuAdapter adapter = new OrderMenuAdapter(this, menuItems);
        menuRecyclerView.setAdapter(adapter);

        Button placeOrderButton = findViewById(R.id.btn_place_order);
        Button finalizeBillButton = findViewById(R.id.btn_finalize_bill);

        placeOrderButton.setOnClickListener(view -> {
            Toast.makeText(this, "Order Placed!", Toast.LENGTH_SHORT).show();
            // In a real app, this would send the order to the kitchen/backend
            finish(); // Go back to the table view
        });

        finalizeBillButton.setOnClickListener(view -> {
            Intent intent = new Intent(TakeOrderActivity.this, BillActivity.class);
            intent.putExtra("TABLE_NUMBER", tableNumber);
            // In a real app, you would pass the actual list of ordered items
            startActivity(intent);
        });
    }
}
