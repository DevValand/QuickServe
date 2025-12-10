package com.example.quickserve.manager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.google.android.material.card.MaterialCardView;

public class ManagerDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        // Card navigation
        MaterialCardView userManagementCard = view.findViewById(R.id.card_user_management);
        MaterialCardView menuManagementCard = view.findViewById(R.id.card_menu_management);

        userManagementCard.setOnClickListener(v -> {
            // Correctly navigate to the UserManagementFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new UserManagementFragment())
                    .addToBackStack(null) // Allows user to navigate back to the dashboard
                    .commit();
        });

        menuManagementCard.setOnClickListener(v -> {
            // Navigate to the MenuManagementFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new MenuManagementFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }
}
