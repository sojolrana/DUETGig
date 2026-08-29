package com.sojolrana.duetgig.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.models.Project;

import java.util.List;

public class AdminProjectAdapter extends RecyclerView.Adapter<AdminProjectAdapter.ViewHolder> {

    public interface OnProjectActionListener {
        void onApproveProject(Project project);
        void onDeleteProject(String projectId);
    }

    private final List<Project> projectList;
    private final OnProjectActionListener listener;

    public AdminProjectAdapter(List<Project> projectList, OnProjectActionListener listener) {
        this.projectList = projectList;
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
        Project project = projectList.get(position);
        holder.tvTitle.setText(project.getTitle());
        String status = project.getStatus() != null ? project.getStatus() : "Pending";
        holder.tvSubtitle.setText("Budget: $" + project.getBudget() + " • Status: " + status + " • By: " + project.getPosterName());

        if ("Approved".equals(status)) {
            holder.btnAction.setText("Reject");
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    project.setStatus("Rejected");
                    listener.onApproveProject(project);
                }
            });
        } else {
            holder.btnAction.setText("Approve");
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    project.setStatus("Approved");
                    listener.onApproveProject(project);
                }
            });
        }

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteProject(project.getProjectId());
        });
    }

    @Override
    public int getItemCount() {
        return projectList.size();
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
