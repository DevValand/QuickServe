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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNav;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private String userRole;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRole = getIntent().getStringExtra("USER_ROLE");

        // Handle cases where user might not be passed correctly
        if (currentUser == null) {
            // This shouldn't happen, but as a safeguard, send back to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        if (userRole == null) userRole = "WAITER"; // Default to least privileged role

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(this::onBottomNavigationItemSelected);

        setupToolbarNavigation();
        updateNavHeader();
        setupUIForRole();

        if (savedInstanceState == null) {
            loadInitialFragment();
        }
    }

    private void updateNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderRole = headerView.findViewById(R.id.nav_header_role);
        TextView navHeaderEmail = headerView.findViewById(R.id.nav_header_email);

        navHeaderRole.setText(userRole);
        navHeaderEmail.setText(currentUser.getEmail());
    }

    private void loadInitialFragment() {
        switch (userRole) {
            case "MANAGER":
                loadFragment(new ManagerDashboardFragment(), false, "Manager Dashboard");
                navigationView.setCheckedItem(R.id.nav_manager_dashboard);
                break;
            case "WAITER":
                loadFragment(new WaiterDashboardFragment(), false, "Waiter Dashboard");
                navigationView.setCheckedItem(R.id.nav_tables);
                break;
            case "CHEF":
                loadFragment(new ChefDashboardFragment(), false, "Chef Dashboard");
                navigationView.setCheckedItem(R.id.nav_orders);
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

        if ("MANAGER".equals(userRole)) {
            // Manager sees all dashboards and admin tools
            bottomNav.setVisibility(View.VISIBLE);
            navMenu.findItem(R.id.nav_manager_dashboard).setVisible(true);
            navMenu.findItem(R.id.nav_tables).setVisible(true);
            navMenu.findItem(R.id.nav_orders).setVisible(true);
            navMenu.findItem(R.id.nav_user_management).setVisible(true);
            navMenu.findItem(R.id.nav_menu_management).setVisible(true);
            navMenu.findItem(R.id.nav_table_management).setVisible(true);
            navMenu.setGroupVisible(R.id.group_admin, true);
        } else {
            // Other roles only see their own dashboard
            bottomNav.setVisibility(View.GONE);
            navMenu.findItem(R.id.nav_manager_dashboard).setVisible(false);
            navMenu.findItem(R.id.nav_tables).setVisible("WAITER".equals(userRole));
            navMenu.findItem(R.id.nav_orders).setVisible("CHEF".equals(userRole));
            navMenu.findItem(R.id.nav_user_management).setVisible(false);
            navMenu.findItem(R.id.nav_menu_management).setVisible(false);
            navMenu.findItem(R.id.nav_table_management).setVisible(false);
            navMenu.setGroupVisible(R.id.group_admin, false);
        }
    }

    private boolean onBottomNavigationItemSelected(MenuItem item) {
        // This is primarily for the manager now
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        int itemId = item.getItemId();
        if (itemId == R.id.nav_dashboard) {
            loadFragment(new ManagerDashboardFragment(), false, "Manager Dashboard");
            navigationView.setCheckedItem(R.id.nav_manager_dashboard);
        } else if (itemId == R.id.nav_orders) {
            loadFragment(new ChefDashboardFragment(), false, "Chef Dashboard");
            navigationView.setCheckedItem(R.id.nav_orders);
        } else if (itemId == R.id.nav_tables) {
            loadFragment(new WaiterDashboardFragment(), false, "Waiter Dashboard");
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
                if ("MANAGER".equals(userRole)) bottomNav.setSelectedItemId(R.id.nav_dashboard);
            } else if (itemId == R.id.nav_tables) {
                loadFragment(new WaiterDashboardFragment(), false, "Waiter Dashboard");
                if ("MANAGER".equals(userRole)) bottomNav.setSelectedItemId(R.id.nav_tables);
            } else if (itemId == R.id.nav_orders) {
                loadFragment(new ChefDashboardFragment(), false, "Chef Dashboard");
                if ("MANAGER".equals(userRole)) bottomNav.setSelectedItemId(R.id.nav_orders);
            } else if (itemId == R.id.nav_user_management) {
                loadFragment(new UserManagementFragment(), true, "User Management");
            } else if (itemId == R.id.nav_menu_management) {
                loadFragment(new MenuManagementFragment(), true, "Menu Management");
            } else if (itemId == R.id.nav_table_management) {
                loadFragment(new com.example.quickserve.manager.TableManagementFragment(), true, "Table Management");
            } else if (itemId == R.id.nav_settings) {
                // Pass user info to SettingsFragment
                Fragment settingsFragment = new SettingsFragment();
                Bundle args = new Bundle();
                args.putString("USER_EMAIL", currentUser.getEmail());
                args.putString("USER_ROLE", userRole);
                settingsFragment.setArguments(args);
                loadFragment(settingsFragment, true, "Settings");
            } else if (itemId == R.id.nav_logout) {
                showLogoutConfirmationDialog();
            }
        }, 250);
        return true;
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack, String title) {
        getSupportActionBar().setTitle(title);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("No", null)
                .show();
    }
}
