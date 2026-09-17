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
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }

    public String getReviewerName() { return reviewerName; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
