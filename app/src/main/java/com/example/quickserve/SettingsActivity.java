package com.example.quickserve;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button updateProfileButton = findViewById(R.id.btn_update_profile);
        Button logoutButton = findViewById(R.id.btn_logout);

        updateProfileButton.setOnClickListener(v -> {
            // In a real app, you would save the updated profile data
            Toast.makeText(SettingsActivity.this, "Profile Updated!", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            // Navigate back to Login and clear the activity stack
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(SettingsActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
        });
    }
}
