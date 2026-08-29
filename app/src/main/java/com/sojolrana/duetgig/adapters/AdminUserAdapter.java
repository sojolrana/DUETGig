package com.sojolrana.duetgig.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.models.User;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(String userId);
        void onApproveUser(User user);
    }

    private final List<User> userList;
    private final OnUserActionListener listener;

    public AdminUserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_generic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvTitle.setText(user.getName() != null ? user.getName() : "Unnamed");
        
        String status = user.getStatus() != null ? user.getStatus() : "Approved";
        holder.tvSubtitle.setText(user.getEmail() + " • Role: " + (user.getRole() != null ? user.getRole() : "Client") + " • Status: " + status);
        
        if ("Pending".equals(status)) {
            holder.btnAction.setText("Approve");
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onApproveUser(user);
            });
        } else {
            holder.btnAction.setText("Edit");
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) listener.onEditUser(user);
            });
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteUser(user.getUid());
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        MaterialButton btnAction, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAdminTitle);
            tvSubtitle = itemView.findViewById(R.id.tvAdminSubtitle);
            btnAction = itemView.findViewById(R.id.btnAdminAction);
            btnDelete = itemView.findViewById(R.id.btnAdminDelete);
        }
    }
}
