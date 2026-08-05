package com.sojolrana.duetgig.models;

import com.google.firebase.Timestamp;

public class Review {
    private String reviewId;
    private String serviceId;
    private String reviewerName;
    private float rating;
    private String comment;
    private Timestamp timestamp;

    public Review() {
        // Required for Firebase
    }

    public Review(String reviewId, String serviceId, String reviewerName, float rating, String comment, Timestamp timestamp) {
        this.reviewId = reviewId;
        this.serviceId = serviceId;
        this.reviewerName = reviewerName;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getReviewId() { return reviewId; }
    public String getServiceId() { return serviceId; }
    public String getReviewerName() { return reviewerName; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public Timestamp getTimestamp() { return timestamp; }
}