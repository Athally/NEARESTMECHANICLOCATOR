package com.example.nearestmechaniclocator;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Mechanic  implements Serializable {
    private String id;
    private String name;
    private String specialization;
    private String phone;
    private String photoUrl;
    private double latitude;
    private double longitude;
    private double rating;
    private double distanceFromDriver; // optional: show distance

    public Mechanic() {
    }

    public Mechanic(String id, String name, String specialization, String phone, String photoUrl, double latitude, double longitude, double rating) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.photoUrl = photoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public String getName() {return name;}

    public String getSpecialization() {
        return specialization;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getRating() {
        return rating;
    }

    public double getDistanceFromDriver() {
        return distanceFromDriver;
    }

    public void setDistanceFromDriver(double distanceFromDriver) {
        this.distanceFromDriver = distanceFromDriver;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getLocation() {
        return new GeoPoint(latitude,longitude);

    }

    public void setLocation(GeoPoint location) {
        this.latitude=location.getLatitude();
        this.longitude = location.getLongitude();
    }
}
