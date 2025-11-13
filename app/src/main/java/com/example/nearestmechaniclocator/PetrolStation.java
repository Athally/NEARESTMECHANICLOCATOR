package com.example.nearestmechaniclocator;

public class PetrolStation {
    private String name;
    private String address;
    private double lat;
    private double lng;
    private double distance; // distance from user in km

    public PetrolStation() {} // empty constructor needed for Firestore

    public PetrolStation(String name, String address, double lat, double lng, double distance) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
    }

    // ===== Getters & Setters =====
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
}
