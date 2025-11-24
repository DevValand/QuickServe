package com.example.quickserve.manager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.data.MenuRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MenuManagementFragment extends Fragment implements MenuAdapter.OnItemClickListener {

    private List<MenuItem> menuItems;
    private MenuAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_management, container, false);

        RecyclerView menuRecyclerView = view.findViewById(R.id.menu_recycler_view);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        menuItems = MenuRepository.getMenuItems();
        // Pass the fragment as the OnItemClickListener
        adapter = new MenuAdapter(menuItems, this);
        menuRecyclerView.setAdapter(adapter);

        FloatingActionButton fabAddMenuItem = view.findViewById(R.id.fab_add_menu_item);
        fabAddMenuItem.setOnClickListener(v -> showAddMenuItemDialog());

        return view;
    }

    // This method is called when a menu item in the list is clicked
    @Override
    public void onItemClick(MenuItem item, int position) {
        showEditItemDialog(item, position);
    }

    private void showAddMenuItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_menu_item, null);
        builder.setView(dialogView);

        final EditText nameEditText = dialogView.findViewById(R.id.et_new_item_name);
        final EditText priceEditText = dialogView.findViewById(R.id.et_new_item_price);
        final Spinner categorySpinner = dialogView.findViewById(R.id.spinner_add_category);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
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
                adapter.notifyItemInserted(menuItems.size() - 1);
                Toast.makeText(getContext(), "Item Added", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showEditItemDialog(final MenuItem item, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_menu_item, null);
        builder.setView(dialogView);

        final EditText nameEditText = dialogView.findViewById(R.id.et_edit_item_name);
        final EditText priceEditText = dialogView.findViewById(R.id.et_edit_item_price);
        final Spinner categorySpinner = dialogView.findViewById(R.id.spinner_edit_category);

        nameEditText.setText(item.getName());
        priceEditText.setText(String.valueOf(item.getPrice()));

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.menu_categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        categorySpinner.setSelection(spinnerAdapter.getPosition(item.getCategory()));

        final AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_update_item).setOnClickListener(v -> {
            String newName = nameEditText.getText().toString();
            String newPriceStr = priceEditText.getText().toString();
            String newCategory = categorySpinner.getSelectedItem().toString();

            if (!TextUtils.isEmpty(newName) && !TextUtils.isEmpty(newPriceStr)) {
                item.setName(newName);
                item.setPrice(Double.parseDouble(newPriceStr));
                item.setCategory(newCategory);
                adapter.notifyItemChanged(position);
                Toast.makeText(getContext(), "Item Updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btn_delete_item).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Item")
                    .setMessage("Are you sure you want to delete '" + item.getName() + "'?")
                    .setPositiveButton("Delete", (d, w) -> {
                        menuItems.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, menuItems.size());
                        Toast.makeText(getContext(), "Item Deleted", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        dialog.show();
    }
}
