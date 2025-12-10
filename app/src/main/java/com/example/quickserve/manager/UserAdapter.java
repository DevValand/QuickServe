package com.example.quickserve.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickserve.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(User user, int position);
        void onDelete(User user, int position);
    }

    private ArrayList<User> userList;
    private final OnItemClickListener listener;
    private Context context;

    public UserAdapter(ArrayList<User> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(userList.get(position), listener, position);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userRole;
        ImageView userIcon, deleteIcon;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.tv_user_name);
            userRole = itemView.findViewById(R.id.tv_user_role);
            userIcon = itemView.findViewById(R.id.iv_user_icon);
            deleteIcon = itemView.findViewById(R.id.iv_delete_user);
        }

        public void bind(final User user, final OnItemClickListener listener, final int position) {
            userName.setText(user.getEmail());
            userRole.setText(user.getRole());

            itemView.setOnClickListener(v -> listener.onItemClick(user, position));

            deleteIcon.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete " + user.getEmail() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> listener.onDelete(user, position))
                    .setNegativeButton("Cancel", null)
                    .show();
            });
        }
    }
}
