package com.example.quickserve.manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;

import java.util.List;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(MenuItem item, int position);
    }

    private List<MenuItem> menuList;
    private final OnItemClickListener listener;

    public MenuAdapter(List<MenuItem> menuList, OnItemClickListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.bind(menuList.get(position), listener, position);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView itemIcon;
        TextView itemName, itemCategory, itemPrice;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            itemIcon = itemView.findViewById(R.id.iv_menu_item_icon);
            itemName = itemView.findViewById(R.id.tv_menu_item_name);
            itemCategory = itemView.findViewById(R.id.tv_menu_item_category);
            itemPrice = itemView.findViewById(R.id.tv_menu_item_price);
        }

        public void bind(final MenuItem menuItem, final OnItemClickListener listener, final int position) {
            itemName.setText(menuItem.getName());
            itemCategory.setText(menuItem.getCategory());
            itemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", menuItem.getPrice()));

            switch (menuItem.getCategory()) {
                case "Starters":
                    itemIcon.setImageResource(R.drawable.ic_starter);
                    break;
                case "Main Course":
                    itemIcon.setImageResource(R.drawable.ic_main_course);
                    break;
                case "Desserts":
                    itemIcon.setImageResource(R.drawable.ic_dessert);
                    break;
                case "Beverages":
                    itemIcon.setImageResource(R.drawable.ic_beverages);
                    break;
                default:
                    itemIcon.setImageResource(R.drawable.ic_menu);
                    break;
            }
            
            itemView.setOnClickListener(v -> listener.onItemClick(menuItem, position));
        }
    }
}
