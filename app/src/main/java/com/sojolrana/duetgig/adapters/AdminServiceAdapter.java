package com.sojolrana.duetgig.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.models.Service;

import java.util.List;

public class AdminServiceAdapter extends RecyclerView.Adapter<AdminServiceAdapter.ViewHolder> {

    public interface OnServiceActionListener {
        void onApproveService(Service service);
        void onDeleteService(String serviceId);
    }

    private final List<Service> serviceList;
    private final OnServiceActionListener listener;

    public AdminServiceAdapter(List<Service> serviceList, OnServiceActionListener listener) {
        this.serviceList = serviceList;
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
        Service service = serviceList.get(position);
        holder.tvTitle.setText(service.getTitle());
        String status = service.getStatus() != null ? service.getStatus() : "Pending";
        holder.tvSubtitle.setText("Price: $" + service.getPrice() + " • Status: " + status + " • By: " + service.getProviderName());

        if ("Approved".equals(status)) {
            holder.btnAction.setText("Reject");
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    service.setStatus("Rejected");
                    listener.onApproveService(service);
                }
            });
        } else {
            holder.btnAction.setText("Approve");
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    service.setStatus("Approved");
                    listener.onApproveService(service);
                }
            });
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteService(service.getServiceId());
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
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
