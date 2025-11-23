package com.example.quickserve.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.chef.Order;
import com.example.quickserve.chef.OrderAdapter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;

public class ManagerDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        // Card navigation
        MaterialCardView userManagementCard = view.findViewById(R.id.card_user_management);
        MaterialCardView menuManagementCard = view.findViewById(R.id.card_menu_management);

        userManagementCard.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UserManagementActivity.class));
        });

        menuManagementCard.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), MenuManagementActivity.class));
        });

        // Recent Orders List
        RecyclerView recentOrdersRecyclerView = view.findViewById(R.id.recent_orders_recycler_view);
        recentOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create static data for now
        ArrayList<Order> recentOrders = new ArrayList<>();
        recentOrders.add(new Order(2, Arrays.asList(new MenuItem("Vegetable Biryani", "", 0)), "Ready"));
        recentOrders.add(new Order(5, Arrays.asList(new MenuItem("Paneer Butter Masala", "", 0), new MenuItem("Garlic Naan", "", 0)), "Preparing"));

        // Reuse the OrderAdapter from the chef package
        OrderAdapter adapter = new OrderAdapter(getContext(), recentOrders);
        recentOrdersRecyclerView.setAdapter(adapter);

        return view;
    }
}
