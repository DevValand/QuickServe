package com.example.quickserve.manager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MenuManagementActivity extends AppCompatActivity {

    private ArrayList<MenuItem> menuItems = new ArrayList<>();
    private MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_management);

        RecyclerView menuRecyclerView = findViewById(R.id.menu_recycler_view);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create static data for now
        menuItems.add(new MenuItem("Paneer Butter Masala", "Main Course", 12.99));
        menuItems.add(new MenuItem("Vegetable Biryani", "Main Course", 10.50));
        menuItems.add(new MenuItem("Garlic Naan", "Breads", 3.50));
        menuItems.add(new MenuItem("Gulab Jamun", "Desserts", 4.00));

        adapter = new MenuAdapter(menuItems);
        menuRecyclerView.setAdapter(adapter);

        FloatingActionButton fabAddMenuItem = findViewById(R.id.fab_add_menu_item);
        fabAddMenuItem.setOnClickListener(view -> showAddMenuItemDialog());
    }

    private void showAddMenuItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_menu_item, null);
        builder.setView(dialogView);

        final EditText nameEditText = dialogView.findViewById(R.id.et_new_item_name);
        final EditText priceEditText = dialogView.findViewById(R.id.et_new_item_price);
        final EditText categoryEditText = dialogView.findViewById(R.id.et_new_item_category);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            String priceStr = priceEditText.getText().toString();
            String category = categoryEditText.getText().toString();

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(priceStr)) {
                double price = Double.parseDouble(priceStr);
                menuItems.add(new MenuItem(name, category, price));
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
