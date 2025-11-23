package com.example.quickserve;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.btn_login);
        TextView registerText = findViewById(R.id.tv_register);

        // The registration flow has been removed, so hide this text
        registerText.setVisibility(View.GONE);

        loginButton.setOnClickListener(v -> {
            // For now, simply navigate to the MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish LoginActivity so the user can't go back
        });
    }
}
