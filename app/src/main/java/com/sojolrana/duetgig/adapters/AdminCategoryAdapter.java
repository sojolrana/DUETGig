package com.sojolrana.duetgig.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sojolrana.duetgig.R;

import java.util.List;
import java.util.Map;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.CategoryViewHolder> {

    private List<Map<String, String>> categoryList;
    private OnCategoryDeleteListener listener;

    public interface OnCategoryDeleteListener {
        void onDelete(String categoryId);
    }

    public AdminCategoryAdapter(List<Map<String, String>> categoryList, OnCategoryDeleteListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_admin, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Map<String, String> category = categoryList.get(position);
        holder.bind(category, listener);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        MaterialButton btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.categoryName);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }

        public void bind(Map<String, String> category, OnCategoryDeleteListener listener) {
            name.setText(category.get("name"));
            btnDelete.setOnClickListener(v -> listener.onDelete(category.get("id")));
        }
    }
}