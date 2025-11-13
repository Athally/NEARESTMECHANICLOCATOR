package com.example.nearestmechaniclocator;

public class ServiceHistory {

    private String mechanicName;
    private String carMake;
    private String requestType;
    private String issue;
    private long timestamp;
    private String location;
    private String status;

    public ServiceHistory() {
        // Needed for Firebase
    }

    public ServiceHistory(String mechanicName, String carMake, String requestType,
                          String issue, long timestamp, String location, String status) {
        this.mechanicName = mechanicName;
        this.carMake = carMake;
        this.requestType = requestType;
        this.issue = issue;
        this.timestamp = timestamp;
        this.location = location;
        this.status = status;
    }

    // Getters
    public String getMechanicName() { return mechanicName; }
    public String getCarMake() { return carMake; }
    public String getRequestType() { return requestType; }
    public String getIssue() { return issue; }
    public long getTimestamp() { return timestamp; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
}
