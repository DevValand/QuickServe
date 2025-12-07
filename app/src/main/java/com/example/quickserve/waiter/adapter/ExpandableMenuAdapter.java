package com.example.quickserve.waiter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.manager.MenuItem;
import com.example.quickserve.waiter.model.ExpandableMenuItem;
import com.example.quickserve.waiter.model.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExpandableMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Object> items;
    private final Map<MenuItem, Integer> quantities = new HashMap<>();

    public ExpandableMenuAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof ExpandableMenuItem) {
            return ExpandableMenuItem.TYPE_HEADER;
        } else {
            return ExpandableMenuItem.TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ExpandableMenuItem.TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_menu_expandable_row, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((ExpandableMenuItem) items.get(position));
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind((MenuItem) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<OrderItem> getOrderedItems() {
        List<OrderItem> orderedItems = new ArrayList<>();
        for (Map.Entry<MenuItem, Integer> entry : quantities.entrySet()) {
            if (entry.getValue() > 0) {
                orderedItems.add(new OrderItem(entry.getKey(), entry.getValue()));
            }
        }
        return orderedItems;
    }

    // --- This is the complete and corrected HeaderViewHolder ---
    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView categoryImage, expandArrow;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.tv_category_name);
            categoryImage = itemView.findViewById(R.id.iv_category_image);
            expandArrow = itemView.findViewById(R.id.iv_expand_arrow);
        }

        void bind(final ExpandableMenuItem headerItem) {
            categoryName.setText(headerItem.category);
            
            // Set category image based on name
            switch (headerItem.category) {
                case "Starters":
                    categoryImage.setImageResource(R.drawable.ic_starter);
                    break;
                case "Main Course":
                    categoryImage.setImageResource(R.drawable.ic_main_course);
                    break;
                case "Desserts":
                    categoryImage.setImageResource(R.drawable.ic_dessert);
                    break;
                case "Beverages":
                    categoryImage.setImageResource(R.drawable.ic_beverages);
                    break;
                default:
                    categoryImage.setImageResource(R.drawable.ic_main_course);
                    break;
            }
            
            expandArrow.setRotation(headerItem.isExpanded ? 0 : -90);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                headerItem.isExpanded = !headerItem.isExpanded;
                expandArrow.animate().rotation(headerItem.isExpanded ? 0 : -90).setDuration(300).start();

                if (headerItem.isExpanded) {
                    items.addAll(position + 1, headerItem.subItems);
                    notifyItemRangeInserted(position + 1, headerItem.subItems.size());
                } else {
                    items.removeAll(headerItem.subItems);
                    notifyItemRangeRemoved(position + 1, headerItem.subItems.size());
                }
            });
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, quantity;
        ImageButton increaseButton, decreaseButton;

        ItemViewHolder(@NonNull View itemView) {
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
            
            int currentQuantity = quantities.getOrDefault(menuItem, 0);
            quantity.setText(String.valueOf(currentQuantity));

            increaseButton.setOnClickListener(v -> {
                int newQuantity = quantities.getOrDefault(menuItem, 0) + 1;
                quantities.put(menuItem, newQuantity);
                quantity.setText(String.valueOf(newQuantity));
            });

            decreaseButton.setOnClickListener(v -> {
                int newQuantity = quantities.getOrDefault(menuItem, 0);
                if (newQuantity > 0) {
                    newQuantity--;
                    quantities.put(menuItem, newQuantity);
                    quantity.setText(String.valueOf(newQuantity));
                }
            });
        }
    }
}