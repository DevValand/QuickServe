package com.example.quickserve.chef;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.manager.MenuItem;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tableNumber, orderItems, orderStatus;
        LinearLayout pendingButtons;
        Button acceptButton, rejectButton, markAsReadyButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tableNumber = itemView.findViewById(R.id.tv_order_table_number);
            orderItems = itemView.findViewById(R.id.tv_order_items);
            orderStatus = itemView.findViewById(R.id.tv_order_status);
            pendingButtons = itemView.findViewById(R.id.layout_pending_buttons);
            acceptButton = itemView.findViewById(R.id.btn_accept_order);
            rejectButton = itemView.findViewById(R.id.btn_reject_order);
            markAsReadyButton = itemView.findViewById(R.id.btn_mark_as_ready);
        }

        void bind(final Order order) {
            tableNumber.setText("Table " + order.getTableNumber());
            orderStatus.setText(order.getStatus());

            StringBuilder itemsText = new StringBuilder();
            for (MenuItem item : order.getItems()) {
                itemsText.append("- ").append(item.getName()).append("\n");
            }
            orderItems.setText(itemsText.toString().trim());

            switch (order.getStatus()) {
                case "Pending":
                    pendingButtons.setVisibility(View.VISIBLE);
                    markAsReadyButton.setVisibility(View.GONE);
                    orderStatus.setBackgroundResource(R.color.primary_light);
                    break;
                case "Preparing":
                    pendingButtons.setVisibility(View.GONE);
                    markAsReadyButton.setVisibility(View.VISIBLE);
                    orderStatus.setBackgroundResource(R.color.accent);
                    break;
                case "Ready":
                    pendingButtons.setVisibility(View.GONE);
                    markAsReadyButton.setVisibility(View.GONE);
                    orderStatus.setBackgroundResource(R.color.primary);
                    orderStatus.setTextColor(context.getResources().getColor(R.color.white));
                    break;
            }

            acceptButton.setOnClickListener(v -> {
                order.setStatus("Preparing");
                notifyItemChanged(getAdapterPosition());
                Toast.makeText(context, "Order for Table " + order.getTableNumber() + " is now preparing.", Toast.LENGTH_SHORT).show();
            });

            rejectButton.setOnClickListener(v -> {
                // In a real app, you might notify the waiter or remove the order
                orderList.remove(getAdapterPosition());
                notifyItemRemoved(getAdapterPosition());
                Toast.makeText(context, "Order for Table " + order.getTableNumber() + " rejected.", Toast.LENGTH_SHORT).show();
            });

            markAsReadyButton.setOnClickListener(v -> {
                order.setStatus("Ready");
                notifyItemChanged(getAdapterPosition());
                Toast.makeText(context, "Order for Table " + order.getTableNumber() + " is ready!", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
