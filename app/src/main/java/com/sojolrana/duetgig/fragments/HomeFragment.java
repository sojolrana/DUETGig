package com.sojolrana.duetgig.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.ServiceDetailActivity;
import com.sojolrana.duetgig.adapters.ServiceAdapter;
import com.sojolrana.duetgig.models.Service;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private List<Service> serviceList;
    private ChipGroup categoryChipGroup;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.servicesRecyclerView);
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);

        setupRecyclerView();
        loadCategories();
        loadServices("All");

        return view;
    }

    private void setupRecyclerView() {
        serviceList = new ArrayList<>();
        adapter = new ServiceAdapter(serviceList, service -> {
            Intent intent = new Intent(getContext(), ServiceDetailActivity.class);
            intent.putExtra("title", service.getTitle());
            intent.putExtra("price", service.getPrice());
            intent.putExtra("provider", service.getProviderName());
            intent.putExtra("bio", service.getProviderBio());
            intent.putExtra("description", service.getDescription());
            intent.putExtra("providerId", service.getProviderId());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    private void loadCategories() {
        // Fetch categories from Firestore
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categoryChipGroup.removeAllViews();
                        
                        // Always add "All" first
                        addCategoryChip("All");
                        
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String categoryName = document.getString("name");
                            if (categoryName != null) {
                                addCategoryChip(categoryName);
                            }
                        }
                    }
                });
    }

    private void addCategoryChip(String categoryName) {
        Chip chip = new Chip(getContext());
        chip.setText(categoryName);
        chip.setCheckable(true);
        chip.setClickable(true);
        
        if (categoryName.equals("All")) {
            chip.setChecked(true);
        }

        chip.setOnClickListener(v -> loadServices(categoryName));
        categoryChipGroup.addView(chip);
    }

    private void loadServices(String category) {
        com.google.firebase.firestore.Query query;
        if (category.equals("All")) {
            query = db.collection("services");
        } else {
            query = db.collection("services").whereEqualTo("category", category);
        }

        query.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        serviceList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service service = document.toObject(Service.class);
                            serviceList.add(service);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}