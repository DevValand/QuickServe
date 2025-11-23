package com.example.quickserve.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quickserve.R;
import com.google.android.material.card.MaterialCardView;

public class ManagerDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        MaterialCardView userManagementCard = view.findViewById(R.id.card_user_management);
        MaterialCardView menuManagementCard = view.findViewById(R.id.card_menu_management);

        userManagementCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserManagementActivity.class);
            startActivity(intent);
        });

        menuManagementCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenuManagementActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
