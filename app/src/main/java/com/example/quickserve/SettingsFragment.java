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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SettingsFragment extends Fragment {

    private TextView roleView;
    private TextView emailView;
    private String userEmail;
    private String userRole;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userEmail = getArguments().getString("USER_EMAIL");
            userRole = getArguments().getString("USER_ROLE");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        roleView = view.findViewById(R.id.tv_role);
        emailView = view.findViewById(R.id.tv_email);

        // Set the user details
        roleView.setText(userRole);
        emailView.setText(userEmail);

        Button logoutButton = view.findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        return view;
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showEditDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        EditText emailEdit = dialogView.findViewById(R.id.et_edit_email);
        EditText passwordEdit = dialogView.findViewById(R.id.et_edit_password);

        emailEdit.setText(userEmail);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Profile")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newEmail = emailEdit.getText().toString().trim();
                    String newPassword = passwordEdit.getText().toString().trim();
                    updateProfile(newEmail, newPassword);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateProfile(String newEmail, String newPassword) {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        if (newEmail.isEmpty()) {
            Toast.makeText(getContext(), "Email is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update email
        currentUser.updateEmail(newEmail)
                .addOnSuccessListener(aVoid -> {
                    emailView.setText(newEmail);
                    userEmail = newEmail;
                    // Mirror to Firestore users/{uid}
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("email", newEmail);
                    db.collection("users").document(currentUser.getUid()).update(updates);
                    Toast.makeText(getContext(), "Email updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Email update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());

        // Update password if provided
        if (!newPassword.isEmpty()) {
            currentUser.updatePassword(newPassword)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Password updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Password update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}
