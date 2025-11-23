package com.example.quickserve;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.quickserve.chef.ChefDashboardFragment;
import com.example.quickserve.manager.ManagerDashboardFragment;
import com.example.quickserve.waiter.WaiterDashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Load the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, 
                    new ManagerDashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = 
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_dashboard) {
                    selectedFragment = new ManagerDashboardFragment();
                } else if (itemId == R.id.nav_orders) {
                    selectedFragment = new ChefDashboardFragment();
                } else if (itemId == R.id.nav_tables) {
                    selectedFragment = new WaiterDashboardFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, 
                            selectedFragment).commit();
                }
                return true;
            };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_user_management) {
            startActivity(new Intent(this, com.example.quickserve.manager.UserManagementActivity.class));
        } else if (itemId == R.id.nav_menu_management) {
            startActivity(new Intent(this, com.example.quickserve.manager.MenuManagementActivity.class));
        } else if (itemId == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (itemId == R.id.nav_logout) {
            // Navigate back to Login and clear the activity stack
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
