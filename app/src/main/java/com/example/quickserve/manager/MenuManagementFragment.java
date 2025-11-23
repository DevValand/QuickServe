package com.example.quickserve.manager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.data.MenuRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MenuManagementFragment extends Fragment {

    private List<MenuItem> menuItems;
    private MenuAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_management, container, false);

        RecyclerView menuRecyclerView = view.findViewById(R.id.menu_recycler_view);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        menuItems = MenuRepository.getMenuItems();
        
        // Correctly initialize the adapter without the cast
        adapter = new MenuAdapter(menuItems);
        menuRecyclerView.setAdapter(adapter);

        FloatingActionButton fabAddMenuItem = view.findViewById(R.id.fab_add_menu_item);
        fabAddMenuItem.setOnClickListener(v -> showAddMenuItemDialog());

        return view;
    }

    private void showAddMenuItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_menu_item, null);
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
                MenuItem newItem = new MenuItem(name, category, price);
                MenuRepository.addMenuItem(newItem);
                adapter.notifyItemInserted(menuItems.size() - 1);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }
}
