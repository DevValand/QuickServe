package com.example.quickserve;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private TextView usernameView;
    private TextView emailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        usernameView = findViewById(R.id.tv_username);
        emailView = findViewById(R.id.tv_email);

        Button editProfileButton = findViewById(R.id.btn_edit_profile);
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());

        Button logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_user, null); // Reusing the add user dialog
        builder.setView(dialogView);

        final EditText usernameEditText = dialogView.findViewById(R.id.et_new_username);
        final EditText passwordEditText = dialogView.findViewById(R.id.et_new_password);
        final TextView title = dialogView.findViewById(R.id.tv_app_title);

        // Pre-fill the dialog with current data
        title.setText("Update Profile");
        usernameEditText.setText(usernameView.getText());
        passwordEditText.setHint("New Password");

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newUsername = usernameEditText.getText().toString();
            
            // Update the text views
            usernameView.setText(newUsername);

            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(SettingsActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
