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
import com.example.quickserve.data.TableRepository;

import java.util.ArrayList;

public class WaiterDashboardFragment extends Fragment {

    private TableAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waiter_dashboard, container, false);

        RecyclerView tablesRecyclerView = view.findViewById(R.id.tables_recycler_view);
        tablesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Get tables from the central repository
        ArrayList<Table> tables = (ArrayList<Table>) TableRepository.getTables();

        adapter = new TableAdapter(getContext(), tables);
        tablesRecyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the adapter every time the fragment is shown
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
