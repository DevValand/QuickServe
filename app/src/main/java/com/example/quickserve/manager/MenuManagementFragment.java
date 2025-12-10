package com.example.quickserve.manager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView;
import android.widget.ArrayAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuManagementFragment extends Fragment implements MenuAdapter.OnItemClickListener {

    private static final String TAG = "MenuManagementFragment";

    private final List<MenuItem> menuList = new ArrayList<>();
    private final List<MenuItem> allMenuItems = new ArrayList<>();
    private MenuAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration menuListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_management, container, false);

        db = FirebaseFirestore.getInstance();

        RecyclerView menuRecyclerView = view.findViewById(R.id.menu_recycler_view);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MenuAdapter(menuList, this);
        menuRecyclerView.setAdapter(adapter);

        FloatingActionButton fabAddMenuItem = view.findViewById(R.id.fab_add_menu_item);
        fabAddMenuItem.setOnClickListener(v -> showMenuItemDialog(null));

        SearchView searchView = view.findViewById(R.id.menu_search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterMenu(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMenu(newText);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Menu Management");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        attachMenuListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (menuListener != null) {
            menuListener.remove();
        }
    }

    private void attachMenuListener() {
        menuListener = db.collection("menuItems").addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            allMenuItems.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                MenuItem item = doc.toObject(MenuItem.class);
                item.setId(doc.getId());
                allMenuItems.add(item);
            }
            filterMenu("");
        });
    }

    @Override
    public void onEdit(MenuItem item) {
        showMenuItemDialog(item);
    }

    @Override
    public void onDelete(MenuItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setPositiveButton("Delete", (d, w) -> deleteMenuItem(item.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMenuItemDialog(@Nullable final MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_menu_item, null);
        builder.setView(dialogView);

        final TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        final EditText nameEditText = dialogView.findViewById(R.id.et_item_name);
        final Spinner categorySpinner = dialogView.findViewById(R.id.spinner_item_category);
        final EditText priceEditText = dialogView.findViewById(R.id.et_item_price);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Starters", "Main Course", "Desserts", "Beverages"});
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        if (item != null) {
            dialogTitle.setText("Edit Menu Item");
            nameEditText.setText(item.getName());
            int pos = categoryAdapter.getPosition(item.getCategory());
            if (pos >= 0) categorySpinner.setSelection(pos);
            priceEditText.setText(String.valueOf(item.getPrice()));
            builder.setNeutralButton("Delete", (dialog, which) -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete " + item.getName() + "?")
                        .setPositiveButton("Delete", (d, w) -> deleteMenuItem(item.getId()))
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        builder.setPositiveButton(item == null ? "Add" : "Update", (dialog, which) -> {
            String name = nameEditText.getText().toString().trim();
            String category = categorySpinner.getSelectedItem().toString();
            String priceStr = priceEditText.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(getContext(), "All fields are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            MenuItem newItem = new MenuItem(name, category, price);

            if (item == null) {
                addMenuItem(newItem);
            } else {
                updateMenuItem(item.getId(), newItem);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void addMenuItem(MenuItem item) {
        db.collection("menuItems").add(item)
                .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Item added.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error adding item.", Toast.LENGTH_SHORT).show());
    }

    private void updateMenuItem(String id, MenuItem item) {
        db.collection("menuItems").document(id).set(item)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Item updated.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating item.", Toast.LENGTH_SHORT).show());
    }

    private void deleteMenuItem(String id) {
        db.collection("menuItems").document(id).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Item deleted.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting item.", Toast.LENGTH_SHORT).show());
    }

    private void filterMenu(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        menuList.clear();
        if (q.isEmpty()) {
            menuList.addAll(allMenuItems);
        } else {
            for (MenuItem item : allMenuItems) {
                String name = item.getName() != null ? item.getName().toLowerCase() : "";
                String category = item.getCategory() != null ? item.getCategory().toLowerCase() : "";
                if (name.contains(q) || category.contains(q)) {
                    menuList.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
