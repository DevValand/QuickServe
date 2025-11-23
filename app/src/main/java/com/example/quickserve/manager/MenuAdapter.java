package com.example.quickserve.manager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;

import java.util.ArrayList;
import java.util.Locale;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private ArrayList<MenuItem> menuList;

    public MenuAdapter(ArrayList<MenuItem> menuList) {
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem menuItem = menuList.get(position);
        holder.itemName.setText(menuItem.getName());
        holder.itemCategory.setText(menuItem.getCategory());
        holder.itemPrice.setText(String.format(Locale.getDefault(), "$%.2f", menuItem.getPrice()));
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemCategory, itemPrice;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tv_menu_item_name);
            itemCategory = itemView.findViewById(R.id.tv_menu_item_category);
            itemPrice = itemView.findViewById(R.id.tv_menu_item_price);
        }
    }
}
