package com.example.quickserve.manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;

import java.util.List;

public class TableManagerAdapter extends RecyclerView.Adapter<TableManagerAdapter.TableViewHolder> {

    public interface OnItemClickListener {
        void onEdit(TableItem item);
        void onDelete(TableItem item);
    }

    private final List<TableItem> tables;
    private final OnItemClickListener listener;

    public TableManagerAdapter(List<TableItem> tables, OnItemClickListener listener) {
        this.tables = tables;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        holder.bind(tables.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    static class TableViewHolder extends RecyclerView.ViewHolder {
        TextView tableNumber, status;
        com.google.android.material.card.MaterialCardView card;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            tableNumber = itemView.findViewById(R.id.tv_table_number);
            status = itemView.findViewById(R.id.tv_table_status);
            card = itemView.findViewById(R.id.card_table);
        }

        public void bind(final TableItem item, final OnItemClickListener listener) {
            tableNumber.setText(item.getTable_number());
            status.setText(formatStatus(item.getStatus()));

            int color = colorForStatus(item.getStatus());
            int textColor = textColorForStatus(item.getStatus());
            // Set card background and text color
            if (card != null) {
                card.setCardBackgroundColor(ContextCompat.getColor(itemView.getContext(), color));
            }
            tableNumber.setTextColor(ContextCompat.getColor(itemView.getContext(), textColor));
            status.setTextColor(ContextCompat.getColor(itemView.getContext(), textColor));

            itemView.setOnClickListener(v -> listener.onEdit(item));
            itemView.setOnLongClickListener(v -> {
                listener.onDelete(item);
                return true;
            });
        }

        private String formatStatus(String status) {
            if (status == null) return "";
            String s = status.replace('_', ' ');
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }

        private int colorForStatus(String status) {
            if (status == null) return R.color.light_grey;
            switch (status.toLowerCase()) {
                case "empty":
                    return R.color.light_grey;
                case "occupied":
                    return R.color.accent;
                case "order_taken":
                    return R.color.primary_light;
                case "preparing":
                    return R.color.primary_dark;
                case "prepared":
                    return R.color.primary;
                case "served":
                    return R.color.primary;
                default:
                    return R.color.light_grey;
            }
        }

        private int textColorForStatus(String status) {
            if (status == null) return R.color.black;
            switch (status.toLowerCase()) {
                case "preparing":
                case "prepared":
                case "served":
                    return R.color.white;
                default:
                    return R.color.black;
            }
        }
    }
}

