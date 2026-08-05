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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sojolrana.duetgig.PostProjectActivity;
import com.sojolrana.duetgig.ProjectDetailActivity;
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.adapters.ProjectAdapter;
import com.sojolrana.duetgig.models.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private List<Project> projectList;
    private List<Project> fullProjectList;
    private FloatingActionButton fab;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private View emptyStateLayout;
    private EditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.projectsRecyclerView);
        fab = view.findViewById(R.id.fabPostProject);
        progressBar = view.findViewById(R.id.projectProgressBar);
        emptyStateLayout = view.findViewById(R.id.projectEmptyStateLayout);
        searchEditText = view.findViewById(R.id.projectSearchEditText);

        setupRecyclerView();
        setupSearch();
        loadProjects();

        fab.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), PostProjectActivity.class));
        });

        return view;
    }

    private void setupRecyclerView() {
        projectList = new ArrayList<>();
        fullProjectList = new ArrayList<>();
        adapter = new ProjectAdapter(projectList, project -> {
            Intent intent = new Intent(getContext(), ProjectDetailActivity.class);
            intent.putExtra("projectId", project.getProjectId());
            intent.putExtra("title", project.getTitle());
            intent.putExtra("budget", project.getBudget());
            intent.putExtra("description", project.getDescription());
            intent.putExtra("posterName", project.getPosterName());
            intent.putExtra("posterId", project.getPosterId());
            startActivity(intent);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProjects(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterProjects(String query) {
        projectList.clear();
        if (query.isEmpty()) {
            projectList.addAll(fullProjectList);
        } else {
            for (Project project : fullProjectList) {
                if (project.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    project.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    projectList.add(project);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void loadProjects() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyStateLayout.setVisibility(View.GONE);

        db.collection("projects")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()) {
                        fullProjectList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Project project = document.toObject(Project.class);
                            fullProjectList.add(project);
                        }
                        filterProjects(searchEditText.getText().toString());
                    }
                });
    }

    private void updateEmptyState() {
        if (projectList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
        }
    }
}