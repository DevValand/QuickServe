package com.example.quickserve.waiter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.waiter.model.OrderItem;

import java.util.List;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.SummaryViewHolder> {

    private final Context context;
    private final List<OrderItem> orderedItems;

    public OrderSummaryAdapter(Context context, List<OrderItem> orderedItems) {
        this.context = context;
        this.orderedItems = orderedItems;
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_summary, parent, false);
        return new SummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        OrderItem item = orderedItems.get(position);
        // Correctly display quantity and name
        String summary = item.getQuantity() + " x " + item.getMenuItem().getName();
        holder.summaryText.setText(summary);
    }

    @Override
    public int getItemCount() {
        return orderedItems.size();
    }

    static class SummaryViewHolder extends RecyclerView.ViewHolder {
        TextView summaryText;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            summaryText = itemView.findViewById(R.id.tv_item_summary);
        }
    }
}
