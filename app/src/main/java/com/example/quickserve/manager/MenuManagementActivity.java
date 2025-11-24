package com.example.quickserve.manager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.data.MenuRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MenuManagementActivity extends AppCompatActivity implements MenuAdapter.OnItemClickListener {

    private List<MenuItem> menuItems;
    private MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_management);

        RecyclerView menuRecyclerView = findViewById(R.id.menu_recycler_view);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuItems = MenuRepository.getMenuItems();

        adapter = new MenuAdapter(menuItems, this);
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
        final Spinner categorySpinner = dialogView.findViewById(R.id.spinner_add_category);

        // Setup Spinner for categories
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.menu_categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameEditText.getText().toString();
            String priceStr = priceEditText.getText().toString();
            String category = categorySpinner.getSelectedItem().toString();

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(priceStr)) {
                double price = Double.parseDouble(priceStr);
                MenuItem newItem = new MenuItem(name, category, price);
                MenuRepository.addMenuItem(newItem);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    @Override
    public void onItemClick(MenuItem item, int position) {
        // This is a dummy implementation since this Activity is no longer in active use.
        // The real logic is in MenuManagementFragment.
    }
}
