package com.sojolrana.duetgig.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sojolrana.duetgig.R;
import com.sojolrana.duetgig.adapters.ProjectAdapter;
import com.sojolrana.duetgig.models.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private List<Project> projectList;
    private FloatingActionButton fab;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.projectsRecyclerView);
        fab = view.findViewById(R.id.fabPostProject);

        setupRecyclerView();
        loadProjects();

        fab.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), PostProjectActivity.class));
        });

        return view;
    }

    private void setupRecyclerView() {
        projectList = new ArrayList<>();
        adapter = new ProjectAdapter(projectList, project -> {
            Toast.makeText(getContext(), "Clicked: " + project.getTitle(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadProjects() {
        db.collection("projects")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        projectList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Project project = document.toObject(Project.class);
                            projectList.add(project);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}