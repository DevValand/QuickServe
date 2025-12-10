package com.example.quickserve;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.et_username);
        passwordEditText = findViewById(R.id.et_password);
        loginButton = findViewById(R.id.btn_login);
        forgotPasswordText = findViewById(R.id.tv_forgot_password);

        loginButton.setOnClickListener(v -> loginUser());
        forgotPasswordText.setOnClickListener(v -> showForgotDialog());
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and fetch their role.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            fetchUserRoleAndNavigate(currentUser);
        }
    }

    private void loginUser() {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        fetchUserRoleAndNavigate(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showForgotDialog() {
        ForgotPasswordDialogFragment dialog = new ForgotPasswordDialogFragment();
        dialog.show(getSupportFragmentManager(), "ForgotPassword");
    }
    private void fetchUserRoleAndNavigate(FirebaseUser user) {
        if (user == null) return;

        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                if (role == null || role.trim().isEmpty()) {
                    // Missing role metadata — sign out and force re-login to prevent landing without role
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(LoginActivity.this, "User role not set. Please contact admin.", Toast.LENGTH_LONG).show();
                } else {
                    navigateToMainActivity(role);
                }
            } else {
                // This case should ideally not happen for a valid user.
                // It means the user exists in Auth but not in Firestore.
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(LoginActivity.this, "User data not found. Please contact admin.", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching user role", e);
            Toast.makeText(LoginActivity.this, "Error getting user details.", Toast.LENGTH_SHORT).show();
        });
    }

    private void navigateToMainActivity(String userRole) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("USER_ROLE", userRole); // Pass the role to MainActivity
        startActivity(intent);
        finish(); // Finish LoginActivity so user can't navigate back
    }
}
