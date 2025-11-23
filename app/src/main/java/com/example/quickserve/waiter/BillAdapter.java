package com.example.quickserve.waiter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;
import com.example.quickserve.manager.MenuItem;

import java.util.List;
import java.util.Locale;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private List<MenuItem> itemList;
    private Context context;

    public BillAdapter(Context context, List<MenuItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bill, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        MenuItem item = itemList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.tv_bill_item_name);
            itemPrice = itemView.findViewById(R.id.tv_bill_item_price);
        }
    }
}
