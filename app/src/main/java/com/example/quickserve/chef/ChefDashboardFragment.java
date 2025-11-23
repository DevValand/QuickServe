package com.example.quickserve.chef;

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
import com.example.quickserve.manager.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;

public class ChefDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chef_dashboard, container, false);

        RecyclerView ordersRecyclerView = view.findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Create static data for now
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(new Order(2, Arrays.asList(new MenuItem("Vegetable Biryani", "", 0)), "Pending"));
        orders.add(new Order(5, Arrays.asList(new MenuItem("Paneer Butter Masala", "", 0), new MenuItem("Garlic Naan", "", 0)), "Pending"));

        OrderAdapter adapter = new OrderAdapter(getContext(), orders);
        ordersRecyclerView.setAdapter(adapter);

        return view;
    }
}
