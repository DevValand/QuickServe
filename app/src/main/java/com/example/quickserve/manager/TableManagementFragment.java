package com.example.quickserve.manager;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TableManagementFragment extends Fragment implements TableManagerAdapter.OnItemClickListener {

    private final List<TableItem> tables = new ArrayList<>();
    private final List<TableItem> allTables = new ArrayList<>();
    private TableManagerAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration tableListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_management, container, false);

        db = FirebaseFirestore.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.tables_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TableManagerAdapter(tables, this);
        recyclerView.setAdapter(adapter);

        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.table_search_view);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTables(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTables(newText);
                return true;
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab_add_table);
        fab.setOnClickListener(v -> showTableDialog(null));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        attachListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tableListener != null) tableListener.remove();
    }

    @Override
    public void onEdit(TableItem item) {
        showTableDialog(item);
    }

    @Override
    public void onDelete(TableItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Table")
                .setMessage("Delete " + item.getTable_number() + "?")
                .setPositiveButton("Delete", (d, w) -> {
                    String docId = item.getId();
                    if (docId != null && !docId.isEmpty()) {
                        db.collection("tables").document(docId).delete();
                    } else {
                        // Fallback: query by table_number if ID is null
                        db.collection("tables")
                                .whereEqualTo("table_number", item.getTable_number())
                                .get()
                                .addOnSuccessListener(query -> {
                                    if (!query.isEmpty()) {
                                        query.getDocuments().get(0).getReference().delete();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void attachListener() {
        tableListener = db.collection("tables").addSnapshotListener((snapshots, e) -> {
            if (e != null) return;
            allTables.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                TableItem t = doc.toObject(TableItem.class);
                String docId = doc.getId();
                
                // If the id field in Firestore is null or empty, update it
                String existingId = t.getId();
                if (existingId == null || existingId.isEmpty()) {
                    doc.getReference().update("id", docId);
                }
                
                // Always set the ID in the local object
                t.setId(docId);
                allTables.add(t);
            }
            filterTables("");
        });
    }

    private void showTableDialog(@Nullable TableItem item) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_table, null);
        EditText tableNumber = dialogView.findViewById(R.id.et_table_number);
        Spinner statusSpinner = dialogView.findViewById(R.id.spinner_status);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"empty", "occupied", "order_taken", "preparing", "prepared", "served"});
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        if (item != null) {
            tableNumber.setText(item.getTable_number());
            int pos = statusAdapter.getPosition(item.getStatus());
            if (pos >= 0) statusSpinner.setSelection(pos);
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(item == null ? "Add Table" : "Edit Table")
                .setView(dialogView)
                .setPositiveButton(item == null ? "Add" : "Update", (d, w) -> {
                    String num = tableNumber.getText().toString().trim();
                    String status = statusSpinner.getSelectedItem().toString();
                    if (TextUtils.isEmpty(num)) {
                        Toast.makeText(getContext(), "Table number required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (item == null) {
                        // Create new table
                        db.collection("tables").add(new TableItem(num, status))
                                .addOnSuccessListener(documentReference -> {
                                    // Store the document ID in the id field
                                    documentReference.update("id", documentReference.getId());
                                });
                    } else {
                        // Update existing table
                        String docId = item.getId();
                        if (docId != null && !docId.isEmpty()) {
                            // Use document ID if available - use update to preserve id field
                            db.collection("tables").document(docId)
                                    .update("table_number", num, "status", status);
                        } else {
                            // Fallback: query by table_number if ID is null
                            db.collection("tables")
                                    .whereEqualTo("table_number", item.getTable_number())
                                    .get()
                                    .addOnSuccessListener(query -> {
                                        if (!query.isEmpty()) {
                                            String foundDocId = query.getDocuments().get(0).getId();
                                            query.getDocuments().get(0).getReference()
                                                    .update("table_number", num, "status", status, "id", foundDocId);
                                        } else {
                                            // Create new if not found
                                            db.collection("tables").add(new TableItem(num, status))
                                                    .addOnSuccessListener(documentReference -> {
                                                        documentReference.update("id", documentReference.getId());
                                                    });
                                        }
                                    });
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void filterTables(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        tables.clear();
        if (q.isEmpty()) {
            tables.addAll(allTables);
        } else {
            for (TableItem t : allTables) {
                String num = t.getTable_number() != null ? t.getTable_number().toLowerCase() : "";
                String status = t.getStatus() != null ? t.getStatus().toLowerCase() : "";
                if (num.contains(q) || status.contains(q)) {
                    tables.add(t);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}

