package com.example.quickserve;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.quickserve.chef.ChefDashboardFragment;
import com.example.quickserve.manager.ManagerDashboardFragment;
import com.example.quickserve.manager.MenuManagementFragment;
import com.example.quickserve.manager.UserManagementFragment;
import com.example.quickserve.waiter.WaiterDashboardFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNav;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userRole = getIntent().getStringExtra("USER_ROLE");
        if (userRole == null) userRole = "MANAGER";

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // Ensure hamburger is shown initially

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(this::onBottomNavigationItemSelected);

        setupToolbarNavigation();
        setupUIForRole();

        if (savedInstanceState == null) {
            loadInitialFragment();
        }
    }

    private void loadInitialFragment() {
        // Default to Manager Dashboard for all roles
        loadFragment(new ManagerDashboardFragment(), false, "Manager Dashboard");
        navigationView.setCheckedItem(R.id.nav_manager_dashboard);
    }

    private void setupToolbarNavigation() {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            boolean isRoot = getSupportFragmentManager().getBackStackEntryCount() == 0;
            toggle.setDrawerIndicatorEnabled(isRoot);
            getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
            if (isRoot) {
                toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
                toggle.syncState();
            } else {
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        });
    }

    private void setupUIForRole() {
        Menu navMenu = navigationView.getMenu();
        TextView navHeaderRole = navigationView.getHeaderView(0).findViewById(R.id.nav_header_role);
        navHeaderRole.setText(userRole.substring(0, 1).toUpperCase() + userRole.substring(1).toLowerCase());

        // Show all dashboard links for all roles
        bottomNav.setVisibility(View.VISIBLE);
        navMenu.setGroupVisible(R.id.group_dashboards, true);
        navMenu.setGroupVisible(R.id.group_admin, true);
    }

    private boolean onBottomNavigationItemSelected(MenuItem item) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        int itemId = item.getItemId();
        if (itemId == R.id.nav_dashboard) {
            loadFragment(new ManagerDashboardFragment(), false, "Manager Dashboard");
            navigationView.setCheckedItem(R.id.nav_manager_dashboard);
        } else if (itemId == R.id.nav_orders) {
            loadFragment(new ChefDashboardFragment(), false, "Kitchen Orders");
            navigationView.setCheckedItem(R.id.nav_orders);
        } else if (itemId == R.id.nav_tables) {
            loadFragment(new WaiterDashboardFragment(), false, "Table Status");
            navigationView.setCheckedItem(R.id.nav_tables);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        new android.os.Handler().postDelayed(() -> {
            int itemId = item.getItemId();
            if (item.getGroupId() == R.id.group_dashboards) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            
            if (itemId == R.id.nav_manager_dashboard) {
                loadFragment(new ManagerDashboardFragment(), false, "Manager Dashboard");
                bottomNav.setSelectedItemId(R.id.nav_dashboard);
            } else if (itemId == R.id.nav_tables) {
                loadFragment(new WaiterDashboardFragment(), false, "Table Status");
                bottomNav.setSelectedItemId(R.id.nav_tables);
            } else if (itemId == R.id.nav_orders) {
                loadFragment(new ChefDashboardFragment(), false, "Kitchen Orders");
                bottomNav.setSelectedItemId(R.id.nav_orders);
            } else if (itemId == R.id.nav_user_management) {
                loadFragment(new UserManagementFragment(), true, "User Management");
            } else if (itemId == R.id.nav_menu_management) {
                loadFragment(new MenuManagementFragment(), true, "Menu Management");
            } else if (itemId == R.id.nav_settings) {
                loadFragment(new SettingsFragment(), true, "Settings");
            } else if (itemId == R.id.nav_logout) {
                showLogoutConfirmationDialog();
            }
        }, 250);
        return true;
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack, String title) {
        getSupportActionBar().setTitle(title);
        var transaction = getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(title);
        }
        transaction.commit();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }
}
