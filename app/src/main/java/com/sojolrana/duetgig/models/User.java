package com.sojolrana.duetgig.models;

public class User {
    private String uid;
    private String name;
    private String email;
    private String role; // "Client", "Service Provider", "Admin"
    private String bio;
    private String status; // "Pending", "Approved", "Rejected"
    private boolean isAdmin;

    public User() {
        // Required for Firestore
    }

    public User(String uid, String name, String email, String role, String bio, String status, boolean isAdmin) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.bio = bio;
        this.status = status != null ? status : "Pending";
        this.isAdmin = isAdmin;
    }

    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getBio() { return bio; }
    public String getStatus() { return status != null ? status : "Pending"; }
    public boolean isAdmin() { return isAdmin; }

    public void setUid(String uid) { this.uid = uid; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setBio(String bio) { this.bio = bio; }
    public void setStatus(String status) { this.status = status; }
    public void setAdmin(boolean admin) { isAdmin = admin; }
}
