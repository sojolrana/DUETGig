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

    public Service() {
        // Required for Firebase
    }

    public Service(String serviceId, String title, String description, double price, String category, String providerId, String providerName, String providerBio, float rating) {
        this.serviceId = serviceId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.providerId = providerId;
        this.providerName = providerName;
        this.providerBio = providerBio;
        this.rating = rating;
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
}