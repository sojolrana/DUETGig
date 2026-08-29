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
    private String status; // "Pending", "Approved", "Rejected"
    private Timestamp timestamp;

    public Project() {
        // Required for Firebase
    }

    public Project(String projectId, String title, String description, double budget, String posterId, String posterName, String category, String status, Timestamp timestamp) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.budget = budget;
        this.posterId = posterId;
        this.posterName = posterName;
        this.category = category;
        this.status = status != null ? status : "Pending";
        this.timestamp = timestamp;
    }

    public String getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getBudget() { return budget; }
    public String getPosterId() { return posterId; }
    public String getPosterName() { return posterName; }
    public String getCategory() { return category; }
    public String getStatus() { return status != null ? status : "Pending"; }
    public Timestamp getTimestamp() { return timestamp; }

    public void setProjectId(String projectId) { this.projectId = projectId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setBudget(double budget) { this.budget = budget; }
    public void setPosterId(String posterId) { this.posterId = posterId; }
    public void setPosterName(String posterName) { this.posterName = posterName; }
    public void setCategory(String category) { this.category = category; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
