package com.example.quickserve.waiter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
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
        holder.bind(menuItem);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class OrderMenuViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, quantity;
        ImageButton increaseButton, decreaseButton;
        private int currentQuantity = 0;

        public OrderMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tv_menu_item_name);
            itemPrice = itemView.findViewById(R.id.tv_menu_item_price);
            quantity = itemView.findViewById(R.id.tv_quantity);
            increaseButton = itemView.findViewById(R.id.btn_increase_quantity);
            decreaseButton = itemView.findViewById(R.id.btn_decrease_quantity);
        }

        void bind(final MenuItem menuItem) {
            itemName.setText(menuItem.getName());
            itemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", menuItem.getPrice()));
            quantity.setText(String.valueOf(currentQuantity));

            increaseButton.setOnClickListener(v -> {
                currentQuantity++;
                quantity.setText(String.valueOf(currentQuantity));
            });

            decreaseButton.setOnClickListener(v -> {
                if (currentQuantity > 0) {
                    currentQuantity--;
                    quantity.setText(String.valueOf(currentQuantity));
                }
            });
        }
    }
}
