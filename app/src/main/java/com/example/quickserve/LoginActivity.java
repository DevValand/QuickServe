package com.example.quickserve;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.btn_login);

        // For now, any login attempt defaults to the Manager role
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USER_ROLE", "MANAGER");
            startActivity(intent);
            finish();
        });
    }
}
