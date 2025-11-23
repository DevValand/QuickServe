package com.example.quickserve;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private TextView usernameView;
    private TextView emailView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        usernameView = view.findViewById(R.id.tv_username);
        emailView = view.findViewById(R.id.tv_email);

        Button editProfileButton = view.findViewById(R.id.btn_edit_profile);
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());

        Button logoutButton = view.findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        return view;
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        final EditText usernameEditText = dialogView.findViewById(R.id.et_edit_username);
        final EditText emailEditText = dialogView.findViewById(R.id.et_edit_email);

        // Pre-fill the dialog with the current user data
        usernameEditText.setText(usernameView.getText());
        emailEditText.setText(emailView.getText());

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newUsername = usernameEditText.getText().toString();
            String newEmail = emailEditText.getText().toString();

            // Update the text views on the main screen
            usernameView.setText(newUsername);
            emailView.setText(newEmail);

            Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }
}
