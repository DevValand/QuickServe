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
import com.example.quickserve.data.OrderRepository;

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
            orderStatus.setText(capitalize(order.getStatus()));

            StringBuilder itemsText = new StringBuilder();
            if (order.getItems() != null) {
                for (OrderLine item : order.getItems()) {
                    itemsText.append(item.getQuantity()).append(" x ").append(item.getName()).append("\n");
                }
            }
            orderItems.setText(itemsText.toString().trim());

            switch (order.getStatus() == null ? "" : order.getStatus().toLowerCase()) {
                case "pending":
                    pendingButtons.setVisibility(View.VISIBLE);
                    markAsReadyButton.setVisibility(View.GONE);
                    orderStatus.setBackgroundResource(R.color.primary_light);
                    orderStatus.setTextColor(context.getResources().getColor(R.color.black));
                    break;
                case "preparing":
                    pendingButtons.setVisibility(View.GONE);
                    markAsReadyButton.setVisibility(View.VISIBLE);
                    orderStatus.setBackgroundResource(R.color.accent);
                    orderStatus.setTextColor(context.getResources().getColor(R.color.black));
                    break;
                case "prepared":
                    pendingButtons.setVisibility(View.GONE);
                    markAsReadyButton.setVisibility(View.GONE);
                    orderStatus.setBackgroundResource(R.color.primary);
                    orderStatus.setTextColor(context.getResources().getColor(R.color.white));
                    break;
                default:
                    pendingButtons.setVisibility(View.GONE);
                    markAsReadyButton.setVisibility(View.GONE);
                    orderStatus.setBackgroundResource(R.color.light_grey);
                    orderStatus.setTextColor(context.getResources().getColor(R.color.black));
            }

            acceptButton.setOnClickListener(v -> {
                OrderRepository.updateStatus(order, "preparing");
                Toast.makeText(context, "Order for Table " + order.getTableNumber() + " is now preparing.", Toast.LENGTH_SHORT).show();
            });

            rejectButton.setOnClickListener(v -> {
                OrderRepository.updateStatus(order, "served");
                Toast.makeText(context, "Order for Table " + order.getTableNumber() + " cleared.", Toast.LENGTH_SHORT).show();
            });

            markAsReadyButton.setOnClickListener(v -> {
                OrderRepository.updateStatus(order, "prepared");
                Toast.makeText(context, "Order for Table " + order.getTableNumber() + " is ready!", Toast.LENGTH_SHORT).show();
            });
        }

        private String capitalize(String status) {
            if (status == null || status.isEmpty()) return "";
            return status.substring(0, 1).toUpperCase() + status.substring(1);
        }
    }
}
