package com.example.quickserve.chef;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.data.OrderRepository;
import com.example.quickserve.data.TableRepository;
import com.example.quickserve.waiter.Table;
import com.example.quickserve.waiter.TableAdapter;

import java.util.ArrayList;

public class ChefDashboardFragment extends Fragment {

    private TableAdapter tableAdapter;
    private OrderAdapter orderAdapter;
    private final TableRepository.TablesListener tablesListener = tables -> {
        if (tableAdapter != null) {
            // Ensure adapter has the latest list reference
            tableAdapter.notifyDataSetChanged();
        }
    };
    private final OrderRepository.OrdersListener ordersListener = orders -> {
        if (orderAdapter != null) orderAdapter.notifyDataSetChanged();
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chef_dashboard, container, false);

        RecyclerView ordersRecyclerView = view.findViewById(R.id.orders_recycler_view);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView tablesRecyclerView = view.findViewById(R.id.tables_recycler_view);
        tablesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        ArrayList<Order> orders = (ArrayList<Order>) OrderRepository.getOrders();
        orderAdapter = new OrderAdapter(getContext(), orders);
        ordersRecyclerView.setAdapter(orderAdapter);
        OrderRepository.addListener(ordersListener);

        // Shared table list view for chef
        ArrayList<Table> tables = (ArrayList<Table>) TableRepository.getTables();
        tableAdapter = new TableAdapter(getContext(), tables);
        tablesRecyclerView.setAdapter(tableAdapter);
        TableRepository.addListener(tablesListener);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        TableRepository.removeListener(tablesListener);
        OrderRepository.removeListener(ordersListener);
    }
}
