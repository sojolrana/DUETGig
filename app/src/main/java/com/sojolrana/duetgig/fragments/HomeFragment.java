package com.sojolrana.duetgig.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sojolrana.duetgig.PostServiceActivity;
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
    private List<Service> fullServiceList; // For local search
    private ChipGroup categoryChipGroup;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private View emptyStateLayout;
    private EditText searchEditText;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.servicesRecyclerView);
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);
        progressBar = view.findViewById(R.id.progressBar);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        searchEditText = view.findViewById(R.id.searchEditText);
        fab = view.findViewById(R.id.fabPostService);

        setupRecyclerView();
        setupSearch();
        checkUserRole();
        loadCategories();
        loadServices("All");

        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), PostServiceActivity.class)));

        return view;
    }

    private void checkUserRole() {
        if (mAuth.getCurrentUser() == null) return;
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                if ("Service Provider".equals(role)) {
                    fab.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupRecyclerView() {
        serviceList = new ArrayList<>();
        fullServiceList = new ArrayList<>();
        adapter = new ServiceAdapter(serviceList, service -> {
            Intent intent = new Intent(getContext(), ServiceDetailActivity.class);
            intent.putExtra("serviceId", service.getServiceId());
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

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterServices(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterServices(String query) {
        serviceList.clear();
        if (query.isEmpty()) {
            serviceList.addAll(fullServiceList);
        } else {
            for (Service service : fullServiceList) {
                if (service.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    service.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    serviceList.add(service);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void loadCategories() {
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        categoryChipGroup.removeAllViews();
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
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        com.google.firebase.firestore.Query query;
        if (category.equals("All")) {
            query = db.collection("services");
        } else {
            query = db.collection("services").whereEqualTo("category", category);
        }

        query.get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()) {
                        fullServiceList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Service service = document.toObject(Service.class);
                            fullServiceList.add(service);
                        }
                        filterServices(searchEditText.getText().toString());
                    }
                });
    }

    private void updateEmptyState() {
        if (serviceList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
}