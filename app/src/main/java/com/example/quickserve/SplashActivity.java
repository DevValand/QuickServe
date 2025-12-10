package com.example.quickserve;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1500; // 1.5 seconds

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        new Handler(Looper.getMainLooper()).postDelayed(this::checkUserStatus, SPLASH_DELAY);
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, fetch their role and go to MainActivity
            fetchUserRoleAndNavigate(currentUser);
        } else {
            // No user is signed in, go to LoginActivity
            navigateToLogin();
        }
    }

    private void fetchUserRoleAndNavigate(FirebaseUser user) {
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                if (role == null || role.trim().isEmpty()) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(this, "User role not set. Please contact admin.", Toast.LENGTH_LONG).show();
                    navigateToLogin();
                } else {
                    navigateToMain(role);
                }
            } else {
                // This could happen if a user is in Auth but not Firestore. Go to login.
                Toast.makeText(this, "User data is missing. Please log in again.", Toast.LENGTH_LONG).show();
                navigateToLogin();
            }
        }).addOnFailureListener(e -> {
            // Failed to get role, could be an offline issue. Go to login.
            Toast.makeText(this, "Failed to get user details. Please log in.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        });
    }

    private void navigateToMain(String userRole) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("USER_ROLE", userRole);
        startActivity(intent);
        finish();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
