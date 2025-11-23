package com.example.quickserve.waiter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {

    private ArrayList<Table> tableList;
    private Context context;

    public TableAdapter(Context context, ArrayList<Table> tableList) {
        this.context = context;
        this.tableList = tableList;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        Table table = tableList.get(position);
        holder.tableNumber.setText("Table " + table.getTableNumber());
        holder.tableStatus.setText(table.getStatus());

        // Set background color based on status
        int colorResId;
        switch (table.getStatus()) {
            case "Occupied":
                colorResId = R.color.accent;
                break;
            case "Preparing":
                colorResId = R.color.primary_light;
                break;
            default: // Empty
                colorResId = R.color.white;
        }
        holder.tableCard.setCardBackgroundColor(ContextCompat.getColor(context, colorResId));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TakeOrderActivity.class);
            // Pass the table number to the next activity
            intent.putExtra("TABLE_NUMBER", table.getTableNumber());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }

    public static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tableNumber, tableStatus;
        MaterialCardView tableCard;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tableCard = itemView.findViewById(R.id.card_table);
            tableNumber = itemView.findViewById(R.id.tv_table_number);
            tableStatus = itemView.findViewById(R.id.tv_table_status);
        }
    }
}
