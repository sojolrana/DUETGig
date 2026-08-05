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

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private List<Service> serviceList;
    private ChipGroup categoryChipGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.servicesRecyclerView);
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);

        setupRecyclerView();
        setupCategories();
        loadDummyServices();

        return view;
    }

    private void setupRecyclerView() {
        serviceList = new ArrayList<>();
        adapter = new ServiceAdapter(serviceList, service -> {
            Intent intent = new Intent(getContext(), ServiceDetailActivity.class);
            // Pass service details (simplified for now)
            intent.putExtra("title", service.getTitle());
            intent.putExtra("price", service.getPrice());
            intent.putExtra("provider", service.getProviderName());
            intent.putExtra("bio", service.getProviderBio());
            intent.putExtra("description", service.getDescription());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);
    }

    private void setupCategories() {
        // Dynamic categories from "database" (dummy for now)
        String[] categories = {"All", "Android", "Web", "AI/ML", "Design", "Graphics"};
        for (String category : categories) {
            Chip chip = new Chip(getContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setClickable(true);
            categoryChipGroup.addView(chip);
        }
    }

    private void loadDummyServices() {
        serviceList.add(new Service("1", "Android App Development", "Professional Android apps using Java/Kotlin.", 150.0, "Android", "p1", "Sojol Rana", "Expert Android Developer with 3 years of experience.", 4.9f));
        serviceList.add(new Service("2", "Web Development", "Modern and responsive websites.", 100.0, "Web", "p2", "John Doe", "Full stack web developer specializing in React and Node.", 4.7f));
        serviceList.add(new Service("3", "AI/ML Solutions", "Build smart models for your data.", 250.0, "AI/ML", "p3", "Jane Smith", "Data Scientist and AI researcher.", 4.8f));
        serviceList.add(new Service("4", "UI/UX Design", "Beautiful interfaces for your apps.", 80.0, "Design", "p4", "Alice Brown", "Creative designer with a focus on user experience.", 4.6f));
        adapter.notifyDataSetChanged();
    }
}