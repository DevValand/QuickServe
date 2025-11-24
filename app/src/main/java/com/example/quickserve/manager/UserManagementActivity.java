package com.example.quickserve.manager;

import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class UserManagementActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {

    private ArrayList<User> users = new ArrayList<>();
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        RecyclerView usersRecyclerView = findViewById(R.id.users_recycler_view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create static data for now
        users.add(new User("John Doe", "Waiter"));
        users.add(new User("Jane Smith", "Chef"));

        // Correctly pass the listener to the adapter with the correct argument order
        adapter = new UserAdapter(users, this);
        usersRecyclerView.setAdapter(adapter);

        FloatingActionButton fabAddUser = findViewById(R.id.fab_add_user);
        fabAddUser.setOnClickListener(view -> showAddUserDialog());
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_user, null);
        builder.setView(dialogView);

        final EditText usernameEditText = dialogView.findViewById(R.id.et_new_username);
        final EditText passwordEditText = dialogView.findViewById(R.id.et_new_password);
        final Spinner roleSpinner = dialogView.findViewById(R.id.spinner_role);

        // Setup spinner with roles
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_roles, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(spinnerAdapter);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String username = usernameEditText.getText().toString();
            String role = roleSpinner.getSelectedItem().toString();

            if (!username.isEmpty()) {
                users.add(new User(username, role));
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    @Override
    public void onItemClick(User user, int position) {
        // This is a dummy implementation since this Activity is no longer in active use.
        // The real logic is in UserManagementFragment.
    }
}
