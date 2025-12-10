package com.example.quickserve.manager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserManagementFragment extends Fragment implements UserAdapter.OnItemClickListener {

    private static final String TAG = "UserManagementFragment";

    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<User> allUsers = new ArrayList<>();
    private UserAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseAuth secondaryAuth;
    private FirebaseApp secondaryApp;
    private ListenerRegistration userListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        initSecondaryAuth();

        RecyclerView usersRecyclerView = view.findViewById(R.id.users_recycler_view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UserAdapter(userList, this);
        usersRecyclerView.setAdapter(adapter);

        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.user_search_view);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterUsers(newText);
                return true;
            }
        });

        FloatingActionButton fabAddUser = view.findViewById(R.id.fab_add_user);
        fabAddUser.setOnClickListener(v -> showAddUserDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("User Management");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        attachUserListListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userListener != null) {
            userListener.remove();
        }
    }

    private void attachUserListListener() {
        userListener = db.collection("users").addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }
            allUsers.clear();
            for (QueryDocumentSnapshot doc : snapshots) {
                User user = doc.toObject(User.class);
                // Fallbacks to avoid null crashes if fields are missing
                if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                    // Try explicit "email" field (preferred)
                    user.setEmail(doc.getString("email"));
                }
                if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                    // Backward compatibility: some old docs used "name" to store email
                    user.setEmail(doc.getString("name"));
                }
                if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                    // Last resort: show doc id
                    user.setEmail(doc.getId());
                }
                if (user.getRole() == null) {
                    user.setRole("UNKNOWN");
                }
                user.setId(doc.getId());
                allUsers.add(user);
            }
            filterUsers(""); // show all
        });
    }

    @Override
    public void onItemClick(User user, int position) {
        showEditUserDialog(user);
    }

    @Override
    public void onDelete(User user, int position) {
        // Logic to delete the user from Firestore
        db.collection("users").document(user.getId()).delete()
            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "User deleted.", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting user.", Toast.LENGTH_SHORT).show());
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        final EditText emailEditText = dialogView.findViewById(R.id.et_new_email);
        final EditText passwordEditText = dialogView.findViewById(R.id.et_new_password);
        final Spinner roleSpinner = dialogView.findViewById(R.id.spinner_role);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.user_roles, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(spinnerAdapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(getContext(), "Email and Password are required.", Toast.LENGTH_SHORT).show();
                return;
            }

            secondaryAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            Map<String, Object> newUser = new HashMap<>();
                            newUser.put("email", email);
                            newUser.put("role", role);

                            db.collection("users").document(uid).set(newUser)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "User created successfully.", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(err -> Toast.makeText(getContext(), "Error saving user details.", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(getContext(), "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        // Keep original manager session intact
                        secondaryAuth.signOut();
                    });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showEditUserDialog(final User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        builder.setView(dialogView);

        final EditText emailEditText = dialogView.findViewById(R.id.et_edit_email);
        final Spinner roleSpinner = dialogView.findViewById(R.id.spinner_edit_role);

        emailEditText.setText(user.getEmail());

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.user_roles, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(spinnerAdapter);
        roleSpinner.setSelection(spinnerAdapter.getPosition(user.getRole()));

        final AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_update_user).setOnClickListener(v -> {
            String newRole = roleSpinner.getSelectedItem().toString();

            db.collection("users").document(user.getId()).update("role", newRole)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "User updated.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating user.", Toast.LENGTH_SHORT).show());
        });

        // The delete button in the dialog is now redundant, as there's a delete icon on the item itself
        dialogView.findViewById(R.id.btn_delete_user).setVisibility(View.GONE);

        dialog.show();
    }

    private void initSecondaryAuth() {
        try {
            secondaryApp = FirebaseApp.getInstance("Secondary");
        } catch (IllegalStateException e) {
            FirebaseOptions options = FirebaseApp.getInstance().getOptions();
            secondaryApp = FirebaseApp.initializeApp(requireContext(), options, "Secondary");
        }
        secondaryAuth = FirebaseAuth.getInstance(secondaryApp);
    }

    private void filterUsers(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        userList.clear();
        if (q.isEmpty()) {
            userList.addAll(allUsers);
        } else {
            for (User u : allUsers) {
                String email = u.getEmail() != null ? u.getEmail().toLowerCase() : "";
                String role = u.getRole() != null ? u.getRole().toLowerCase() : "";
                if (email.contains(q) || role.contains(q)) {
                    userList.add(u);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
