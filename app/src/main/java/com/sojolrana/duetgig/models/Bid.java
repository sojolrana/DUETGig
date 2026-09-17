package com.sojolrana.duetgig.models;

import com.google.firebase.Timestamp;

public class Bid {
    private String bidId;
    private String projectId;
    private String bidderId;
    private String bidderName;
    private double amount;
    private String proposal;
    private String status; // e.g., "Pending", "Accepted", "Rejected"
    private Timestamp timestamp;

    public Bid() {
        // Required for Firebase
    }

    public Bid(String bidId, String projectId, String bidderId, String bidderName, double amount, String proposal, String status, Timestamp timestamp) {
        this.bidId = bidId;
        this.projectId = projectId;
        this.bidderId = bidderId;
        this.bidderName = bidderName;
        this.amount = amount;
        this.proposal = proposal;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getBidId() { return bidId; }
    public void setBidId(String bidId) { this.bidId = bidId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getBidderId() { return bidderId; }
    public void setBidderId(String bidderId) { this.bidderId = bidderId; }

    public String getBidderName() { return bidderName; }
    public void setBidderName(String bidderName) { this.bidderName = bidderName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getProposal() { return proposal; }
    public void setProposal(String proposal) { this.proposal = proposal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
