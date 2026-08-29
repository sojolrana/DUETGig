package com.sojolrana.duetgig.models;

public class Service {
    private String serviceId;
    private String title;
    private String description;
    private double price;
    private String category;
    private String providerId;
    private String providerName;
    private String providerBio;
    private float rating;
    private String status; // "Pending", "Approved", "Rejected"

    public Service() {
        // Required for Firebase
    }

    public Service(String serviceId, String title, String description, double price, String category, String providerId, String providerName, String providerBio, float rating, String status) {
        this.serviceId = serviceId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.providerId = providerId;
        this.providerName = providerName;
        this.providerBio = providerBio;
        this.rating = rating;
        this.status = status != null ? status : "Pending";
    }

    public String getServiceId() { return serviceId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getProviderId() { return providerId; }
    public String getProviderName() { return providerName; }
    public String getProviderBio() { return providerBio; }
    public float getRating() { return rating; }
    public String getStatus() { return status != null ? status : "Pending"; }

    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setCategory(String category) { this.category = category; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public void setProviderName(String providerName) { this.providerName = providerName; }
    public void setProviderBio(String providerBio) { this.providerBio = providerBio; }
    public void setRating(float rating) { this.rating = rating; }
    public void setStatus(String status) { this.status = status; }
}
