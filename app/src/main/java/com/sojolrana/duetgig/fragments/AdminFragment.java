package com.sojolrana.duetgig.fragments;

import android.app.AlertDialog;
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
import com.sojolrana.duetgig.adapters.AdminProjectAdapter;
import com.sojolrana.duetgig.adapters.AdminUserAdapter;
import com.sojolrana.duetgig.models.Project;
import com.sojolrana.duetgig.models.Service;
import com.sojolrana.duetgig.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminFragment extends Fragment {

    private EditText etNewCategory;
    private MaterialButton btnAdd, btnSeed, btnClear;
    private TextView statsUsers, statsProjects, statsServices, statsPending;
    
    private RecyclerView categoriesRecyclerView, usersRecyclerView, projectsRecyclerView;
    
    private AdminCategoryAdapter categoryAdapter;
    private AdminUserAdapter userAdapter;
    private AdminProjectAdapter projectAdapter;
    
    private List<Map<String, String>> categoryList;
    private List<User> userList;
    private List<Project> projectList;
    
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
        statsServices = view.findViewById(R.id.statsServices);
        statsPending = view.findViewById(R.id.statsPending);

        categoriesRecyclerView = view.findViewById(R.id.adminCategoriesRecyclerView);
        usersRecyclerView = view.findViewById(R.id.adminUsersRecyclerView);
        projectsRecyclerView = view.findViewById(R.id.adminProjectsRecyclerView);

        setupRecyclerViews();

        btnAdd.setOnClickListener(v -> addCategory());
        btnSeed.setOnClickListener(v -> seedSampleData());
        btnClear.setOnClickListener(v -> clearAllServices());
        
        loadCategories();
        loadUsers();
        loadProjects();
        loadStats();

        return view;
    }

    private void setupRecyclerViews() {
        // Categories
        categoryList = new ArrayList<>();
        categoryAdapter = new AdminCategoryAdapter(categoryList, this::deleteCategory);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoriesRecyclerView.setAdapter(categoryAdapter);

        // Users
        userList = new ArrayList<>();
        userAdapter = new AdminUserAdapter(userList, new AdminUserAdapter.OnUserActionListener() {
            @Override
            public void onEditUser(User user) {
                showEditUserDialog(user);
            }

            @Override
            public void onDeleteUser(String userId) {
                deleteUser(userId);
            }

            @Override
            public void onApproveUser(User user) {
                approveUser(user);
            }
        });
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersRecyclerView.setAdapter(userAdapter);

        // Projects
        projectList = new ArrayList<>();
        projectAdapter = new AdminProjectAdapter(projectList, new AdminProjectAdapter.OnProjectActionListener() {
            @Override
            public void onApproveProject(Project project) {
                updateProjectStatus(project);
            }

            @Override
            public void onDeleteProject(String projectId) {
                deleteProject(projectId);
            }
        });
        projectsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        projectsRecyclerView.setAdapter(projectAdapter);
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
            categoryAdapter.notifyDataSetChanged();
        });
    }

    private void loadUsers() {
        db.collection("users").addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;
            userList.clear();
            for (QueryDocumentSnapshot doc : value) {
                User user = doc.toObject(User.class);
                if (user.getUid() == null) {
                    user.setUid(doc.getId());
                }
                userList.add(user);
            }
            userAdapter.notifyDataSetChanged();
            statsUsers.setText("Users: " + userList.size());
        });
    }

    private void loadProjects() {
        db.collection("projects").addSnapshotListener((value, error) -> {
            if (error != null || value == null) return;
            projectList.clear();
            int pendingCount = 0;
            for (QueryDocumentSnapshot doc : value) {
                Project project = doc.toObject(Project.class);
                projectList.add(project);
                if ("Pending".equals(project.getStatus())) {
                    pendingCount++;
                }
            }
            projectAdapter.notifyDataSetChanged();
            statsProjects.setText("Projects: " + projectList.size());
            statsPending.setText("Pending: " + pendingCount);
        });
    }

    private void loadStats() {
        db.collection("services").addSnapshotListener((value, error) -> {
            if (value != null) {
                statsServices.setText("Services: " + value.size());
            }
        });
    }

    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_user, null);
        EditText etName = dialogView.findViewById(R.id.etEditUserName);
        android.widget.RadioGroup rgRole = dialogView.findViewById(R.id.rgEditUserRole);
        android.widget.RadioGroup rgStatus = dialogView.findViewById(R.id.rgEditUserStatus);

        etName.setText(user.getName());
        String currentRole = user.getRole() != null ? user.getRole() : "Client";
        if ("Admin".equals(currentRole)) {
            rgRole.check(R.id.rbRoleAdmin);
        } else if ("Service Provider".equals(currentRole)) {
            rgRole.check(R.id.rbRoleProvider);
        } else {
            rgRole.check(R.id.rbRoleClient);
        }

        String currentStatus = user.getStatus() != null ? user.getStatus() : "Approved";
        if ("Pending".equals(currentStatus)) {
            rgStatus.check(R.id.rbStatusPending);
        } else if ("Blocked".equals(currentStatus) || "Rejected".equals(currentStatus)) {
            rgStatus.check(R.id.rbStatusBlocked);
        } else {
            rgStatus.check(R.id.rbStatusApproved);
        }

        builder.setView(dialogView)
                .setTitle("Manage User Account")
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    int selectedRoleId = rgRole.getCheckedRadioButtonId();
                    String newRole = "Client";
                    if (selectedRoleId == R.id.rbRoleAdmin) {
                        newRole = "Admin";
                    } else if (selectedRoleId == R.id.rbRoleProvider) {
                        newRole = "Service Provider";
                    }

                    int selectedStatusId = rgStatus.getCheckedRadioButtonId();
                    String newStatus = "Approved";
                    if (selectedStatusId == R.id.rbStatusPending) {
                        newStatus = "Pending";
                    } else if (selectedStatusId == R.id.rbStatusBlocked) {
                        newStatus = "Blocked";
                    }

                    boolean isAdmin = "Admin".equals(newRole);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("name", newName);
                    updates.put("role", newRole);
                    updates.put("status", newStatus);
                    updates.put("isAdmin", isAdmin);

                    db.collection("users").document(user.getUid())
                            .update(updates)
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "User updated successfully", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUser(String userId) {
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "User deleted", Toast.LENGTH_SHORT).show());
    }

    private void approveUser(User user) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Approved");
        db.collection("users").document(user.getUid())
                .update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "User approved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateProjectStatus(Project project) {
        db.collection("projects").document(project.getProjectId())
                .update("status", project.getStatus())
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Project status updated to " + project.getStatus(), Toast.LENGTH_SHORT).show());
    }

    private void deleteProject(String projectId) {
        db.collection("projects").document(projectId).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Project deleted", Toast.LENGTH_SHORT).show());
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

            // Seed services
            Service s1 = new Service(UUID.randomUUID().toString(), "Expert Android App", "Build professional apps.", 200, "Android Dev", currentUserId, adminName, adminBio, 5.0f);
            db.collection("services").document(s1.getServiceId()).set(s1);

            Service s2 = new Service(UUID.randomUUID().toString(), "CS Assignment Help", "Tutor for CS projects.", 50, "AI/ML", currentUserId, adminName, adminBio, 4.8f);
            db.collection("services").document(s2.getServiceId()).set(s2);

            Toast.makeText(getContext(), "Sample data generated successfully!", Toast.LENGTH_SHORT).show();
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
}
