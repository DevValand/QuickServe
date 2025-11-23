package com.example.quickserve.manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class UserManagementFragment extends Fragment {

    private ArrayList<User> users = new ArrayList<>();
    private UserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);

        RecyclerView usersRecyclerView = view.findViewById(R.id.users_recycler_view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // In a real app, this data would come from a database or API
        if (users.isEmpty()) {
            users.add(new User("John Doe", "Waiter"));
            users.add(new User("Jane Smith", "Chef"));
        }

        adapter = new UserAdapter(users);
        usersRecyclerView.setAdapter(adapter);

        FloatingActionButton fabAddUser = view.findViewById(R.id.fab_add_user);
        fabAddUser.setOnClickListener(v -> showAddUserDialog());

        return view;
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        final EditText usernameEditText = dialogView.findViewById(R.id.et_new_username);
        final EditText passwordEditText = dialogView.findViewById(R.id.et_new_password);
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
}
