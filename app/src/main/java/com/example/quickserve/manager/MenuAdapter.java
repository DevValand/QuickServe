package com.example.quickserve.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private final List<MenuItem> menuList;
    private final OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onEdit(MenuItem item);
        void onDelete(MenuItem item);
    }

    public MenuAdapter(List<MenuItem> menuList, OnItemClickListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuItem item = menuList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, price;
        ImageView icon, deleteIcon;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name);
            category = itemView.findViewById(R.id.item_category);
            price = itemView.findViewById(R.id.item_price);
            icon = itemView.findViewById(R.id.item_icon);
            deleteIcon = itemView.findViewById(R.id.iv_delete_menu);
        }

        public void bind(final MenuItem item, final OnItemClickListener listener) {
            name.setText(item.getName());
            category.setText(item.getCategory());
            price.setText(String.format("₹%.2f", item.getPrice()));
            icon.setImageResource(iconForCategory(item.getCategory()));
            icon.setColorFilter(itemView.getResources().getColor(R.color.white));

            itemView.setOnClickListener(v -> listener.onEdit(item));
            itemView.setOnLongClickListener(v -> {
                listener.onDelete(item);
                return true;
            });
            itemView.findViewById(R.id.iv_edit_menu).setOnClickListener(v -> listener.onEdit(item));
            deleteIcon.setOnClickListener(v -> listener.onDelete(item));
        }

        private int iconForCategory(String category) {
            if (category == null) return R.drawable.ic_menu;
            switch (category.trim().toLowerCase()) {
                case "starters":
                    return R.drawable.ic_starter;
                case "main course":
                    return R.drawable.ic_main_course;
                case "desserts":
                    return R.drawable.ic_dessert;
                case "beverages":
                    return R.drawable.ic_beverages;
                default:
                    return R.drawable.ic_menu;
            }
        }
    }
}
