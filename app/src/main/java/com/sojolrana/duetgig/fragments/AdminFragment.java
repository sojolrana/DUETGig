package com.sojolrana.duetgig.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.adapters.AdminCategoryAdapter;
import com.sojolrana.duetgig.models.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminFragment extends Fragment {

    private EditText etNewCategory;
    private MaterialButton btnAdd, btnSeed, btnClear;
    private TextView statsUsers, statsProjects;
    private RecyclerView recyclerView;
    private AdminCategoryAdapter adapter;
    private List<Map<String, String>> categoryList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        etNewCategory = view.findViewById(R.id.etNewCategory);
        btnAdd = view.findViewById(R.id.btnAddCategory);
        btnSeed = view.findViewById(R.id.btnSeedData);
        btnClear = view.findViewById(R.id.btnClearData);
        statsUsers = view.findViewById(R.id.statsUsers);
        statsProjects = view.findViewById(R.id.statsProjects);
        recyclerView = view.findViewById(R.id.adminCategoriesRecyclerView);

        setupRecyclerView();
        btnAdd.setOnClickListener(v -> addCategory());
        btnSeed.setOnClickListener(v -> seedSampleData());
        btnClear.setOnClickListener(v -> clearAllServices());
        
        loadCategories();
        loadStats();

        return view;
    }

    private void setupRecyclerView() {
        categoryList = new ArrayList<>();
        adapter = new AdminCategoryAdapter(categoryList, this::deleteCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadCategories() {
        db.collection("categories").addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;
            categoryList.clear();
            for (QueryDocumentSnapshot doc : value) {
                Map<String, String> cat = new HashMap<>();
                cat.put("id", doc.getId());
                cat.put("name", doc.getString("name"));
                categoryList.add(cat);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void deleteCategory(String categoryId) {
        db.collection("categories").document(categoryId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Category deleted", Toast.LENGTH_SHORT).show());
    }

    private void clearAllServices() {
        db.collection("services").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                doc.getReference().delete();
            }
            Toast.makeText(getContext(), "All marketplace services cleared!", Toast.LENGTH_SHORT).show();
        });
    }

    private void seedSampleData() {
        if (mAuth.getCurrentUser() == null) return;
        String currentUserId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(currentUserId).get().addOnSuccessListener(userDoc -> {
            if (!userDoc.exists()) {
                Toast.makeText(getContext(), "User profile not found. Please update your profile first.", Toast.LENGTH_LONG).show();
                return;
            }

            String adminName = userDoc.getString("name");
            String adminBio = userDoc.getString("bio");

            // Seed Categories
            String[] cats = {"Android Dev", "Web Dev", "AI/ML", "UI/UX", "Graphics"};
            for (String c : cats) {
                String id = UUID.randomUUID().toString();
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("name", c);
                db.collection("categories").document(id).set(map);
            }

            // Seed some services using the CURRENT ADMIN'S UID
            Service s1 = new Service(UUID.randomUUID().toString(), "Expert Android App", "Build professional apps.", 200, "Android Dev", currentUserId, adminName, adminBio, 5.0f);
            db.collection("services").document(s1.getServiceId()).set(s1);

            Service s2 = new Service(UUID.randomUUID().toString(), "CS Assignment Help", "Tutor for CS projects.", 50, "AI/ML", currentUserId, adminName, adminBio, 4.8f);
            db.collection("services").document(s2.getServiceId()).set(s2);

            Toast.makeText(getContext(), "Sample data generated for your account!", Toast.LENGTH_SHORT).show();
        });
    }

    private void addCategory() {
        String name = etNewCategory.getText().toString().trim();
        if (name.isEmpty()) return;

        String id = UUID.randomUUID().toString();
        Map<String, Object> category = new HashMap<>();
        category.put("id", id);
        category.put("name", name);

        db.collection("categories").document(id).set(category)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Category added", Toast.LENGTH_SHORT).show();
                    etNewCategory.setText("");
                });
    }

    private void loadStats() {
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            statsUsers.setText("Total Users: " + queryDocumentSnapshots.size());
        });

        db.collection("projects").get().addOnSuccessListener(queryDocumentSnapshots -> {
            statsProjects.setText("Total Projects: " + queryDocumentSnapshots.size());
        });
    }
}