package com.example.quickserve.waiter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.manager.MenuItem;

import java.util.ArrayList;
import java.util.Locale;

public class OrderMenuAdapter extends RecyclerView.Adapter<OrderMenuAdapter.OrderMenuViewHolder> {

    private ArrayList<MenuItem> menuList;
    private Context context;

    public OrderMenuAdapter(Context context, ArrayList<MenuItem> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public OrderMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_menu, parent, false);
        return new OrderMenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderMenuViewHolder holder, int position) {
        MenuItem menuItem = menuList.get(position);
        holder.itemName.setText(menuItem.getName());
        holder.itemPrice.setText(String.format(Locale.getDefault(), "$%.2f", menuItem.getPrice()));

        holder.addToOrderButton.setOnClickListener(v -> {
            // In a real app, you'd add this to a list of ordered items
            Toast.makeText(context, menuItem.getName() + " added to order", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class OrderMenuViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice;
        Button addToOrderButton;

        public OrderMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tv_menu_item_name);
            itemPrice = itemView.findViewById(R.id.tv_menu_item_price);
            addToOrderButton = itemView.findViewById(R.id.btn_add_to_order);
        }
    }
}
