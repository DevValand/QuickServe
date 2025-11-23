package com.example.quickserve.waiter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;

import java.util.ArrayList;

public class WaiterDashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiter_dashboard, container, false);

        RecyclerView tablesRecyclerView = view.findViewById(R.id.tables_recycler_view);
        tablesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Create static data for now
        ArrayList<Table> tables = new ArrayList<>();
        tables.add(new Table(1, "Empty"));
        tables.add(new Table(2, "Occupied"));
        tables.add(new Table(3, "Preparing"));
        tables.add(new Table(4, "Empty"));
        tables.add(new Table(5, "Empty"));
        tables.add(new Table(6, "Occupied"));

        TableAdapter adapter = new TableAdapter(getContext(), tables);
        tablesRecyclerView.setAdapter(adapter);

        return view;
    }
}
