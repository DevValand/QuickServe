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
        toggle.syncState();

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
        switch (userRole) {
            case "WAITER":
                loadFragment(new WaiterDashboardFragment(), false, "Waiter Dashboard");
                navigationView.setCheckedItem(R.id.nav_waiter_dashboard);
                break;
            case "CHEF":
                loadFragment(new ChefDashboardFragment(), false, "Chef Dashboard");
                navigationView.setCheckedItem(R.id.nav_chef_dashboard);
                break;
            default: // MANAGER
                loadFragment(new ManagerDashboardFragment(), false, "Manager Dashboard");
                navigationView.setCheckedItem(R.id.nav_manager_dashboard);
                break;
        }
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

        navMenu.findItem(R.id.nav_manager_dashboard).setVisible(false);
        navMenu.findItem(R.id.nav_waiter_dashboard).setVisible(false);
        navMenu.findItem(R.id.nav_chef_dashboard).setVisible(false);
        navMenu.setGroupVisible(R.id.group_admin, false);

        switch (userRole) {
            case "MANAGER":
                bottomNav.setVisibility(View.VISIBLE);
                navMenu.findItem(R.id.nav_manager_dashboard).setVisible(true);
                navMenu.setGroupVisible(R.id.group_admin, true);
                break;
            case "WAITER":
                bottomNav.setVisibility(View.GONE);
                navMenu.findItem(R.id.nav_waiter_dashboard).setVisible(true);
                break;
            case "CHEF":
                bottomNav.setVisibility(View.GONE);
                navMenu.findItem(R.id.nav_chef_dashboard).setVisible(true);
                break;
        }
    }

    private boolean onBottomNavigationItemSelected(MenuItem item) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        int itemId = item.getItemId();
        if (itemId == R.id.nav_dashboard) {
            loadFragment(new ManagerDashboardFragment(), false, "Manager Dashboard");
            navigationView.setCheckedItem(R.id.nav_manager_dashboard);
        } else if (itemId == R.id.nav_orders) {
            loadFragment(new ChefDashboardFragment(), false, "Kitchen Orders");
            navigationView.setCheckedItem(R.id.nav_chef_dashboard);
        } else if (itemId == R.id.nav_tables) {
            loadFragment(new WaiterDashboardFragment(), false, "Table Status");
            navigationView.setCheckedItem(R.id.nav_waiter_dashboard);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        int itemId = item.getItemId();
        if (itemId == R.id.nav_manager_dashboard) {
            loadFragment(new ManagerDashboardFragment(), false, "Manager Dashboard");
            bottomNav.setSelectedItemId(R.id.nav_dashboard);
        } else if (itemId == R.id.nav_waiter_dashboard) {
            loadFragment(new WaiterDashboardFragment(), false, "Table Status");
        } else if (itemId == R.id.nav_chef_dashboard) {
            loadFragment(new ChefDashboardFragment(), false, "Kitchen Orders");
        } else if (itemId == R.id.nav_user_management) {
            loadFragment(new UserManagementFragment(), true, "User Management");
        } else if (itemId == R.id.nav_menu_management) {
            loadFragment(new MenuManagementFragment(), true, "Menu Management");
        } else if (itemId == R.id.nav_settings) {
            loadFragment(new SettingsFragment(), true, "Settings");
        } else if (itemId == R.id.nav_logout) {
            showLogoutConfirmationDialog();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack, String title) {
        getSupportActionBar().setTitle(title);
        FragmentManager fragmentManager = getSupportFragmentManager();
        var transaction = fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment);
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