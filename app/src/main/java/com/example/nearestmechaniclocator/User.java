package com.example.nearestmechaniclocator;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String email;
    private String role;
    private String specialization; // make sure this field exists
    private String profileImageUrl;
    private String location; // optional

    public User() {
        // Firestore needs an empty constructor
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getLocation() {
        return location;
    }

    // Setters (optional, needed for Firestore if you manually create objects)
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}

