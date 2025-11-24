package com.example.quickserve.manager;

import android.content.DialogInterface;
import android.os.Bundle;
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

import java.util.ArrayList;

public class UserManagementFragment extends Fragment implements UserAdapter.OnItemClickListener {

    private ArrayList<User> users = new ArrayList<>();
    private UserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        RecyclerView usersRecyclerView = view.findViewById(R.id.users_recycler_view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (users.isEmpty()) {
            users.add(new User("John Doe", "Waiter"));
            users.add(new User("Jane Smith", "Chef"));
        }

        // Pass the listener to the adapter
        adapter = new UserAdapter(users, this);
        usersRecyclerView.setAdapter(adapter);

        FloatingActionButton fabAddUser = view.findViewById(R.id.fab_add_user);
        fabAddUser.setOnClickListener(v -> showAddUserDialog());

        return view;
    }

    @Override
    public void onItemClick(User user, int position) {
        showEditUserDialog(user, position);
    }

    private void showAddUserDialog() {
        // Logic for adding a new user (already implemented)
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        final EditText usernameEditText = dialogView.findViewById(R.id.et_new_username);
        final Spinner roleSpinner = dialogView.findViewById(R.id.spinner_role);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.user_roles, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(spinnerAdapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String username = usernameEditText.getText().toString();
            String role = roleSpinner.getSelectedItem().toString();

            if (!username.isEmpty()) {
                users.add(new User(username, role));
                adapter.notifyItemInserted(users.size() - 1);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showEditUserDialog(final User user, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);
        builder.setView(dialogView);

        final EditText usernameEditText = dialogView.findViewById(R.id.et_edit_username);
        final Spinner roleSpinner = dialogView.findViewById(R.id.spinner_edit_role);
        
        usernameEditText.setText(user.getName());

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.user_roles, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(spinnerAdapter);
        roleSpinner.setSelection(spinnerAdapter.getPosition(user.getRole()));

        final AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_update_user).setOnClickListener(v -> {
            String newName = usernameEditText.getText().toString();
            String newRole = roleSpinner.getSelectedItem().toString();

            if (!newName.isEmpty()) {
                user.setName(newName);
                user.setRole(newRole);
                adapter.notifyItemChanged(position);
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btn_delete_user).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete " + user.getName() + "?")
                    .setPositiveButton("Delete", (d, w) -> {
                        users.remove(position);
                        adapter.notifyItemRemoved(position);
                        adapter.notifyItemRangeChanged(position, users.size());
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        dialog.show();
    }
}
