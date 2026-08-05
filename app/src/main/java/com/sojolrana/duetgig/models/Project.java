package com.sojolrana.duetgig.models;

import com.google.firebase.Timestamp;

public class Project {
    private String projectId;
    private String title;
    private String description;
    private double budget;
    private String posterId;
    private String posterName;
    private String category;
    private Timestamp timestamp;

    public Project() {
        // Required for Firebase
    }

    public Project(String projectId, String title, String description, double budget, String posterId, String posterName, String category, Timestamp timestamp) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.posterId = posterId;
        this.posterName = posterName;
        this.category = category;
        this.timestamp = timestamp;
    }

    public String getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getBudget() { return budget; }
    public String getPosterId() { return posterId; }
    public String getPosterName() { return posterName; }
    public String getCategory() { return category; }
    public Timestamp getTimestamp() { return timestamp; }
}