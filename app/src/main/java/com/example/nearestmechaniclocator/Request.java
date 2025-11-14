package com.example.nearestmechaniclocator;

public class Request {
    private String id;           // Firebase key
    private String ownerId;      // driver UID
    private String ownerName;
    private String carMake;
    private String requestType;  // "Booking" | "Roadside"
    private String issue;        // OBD-II text or user issue
    private long timestamp;      // epoch millis
    private Location location;
    private String status;       // "Pending", "Accepted", "Rejected"

    public Request() {}

    public Request(String id, String ownerId, String ownerName, String carMake, String requestType,
                   String issue, long timestamp, Location location, String status) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.carMake = carMake;
        this.requestType = requestType;
        this.issue = issue;
        this.timestamp = timestamp;
        this.location = location;
        this.status = status;
    }

    // Getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getCarMake() { return carMake; }
    public void setCarMake(String carMake) { this.carMake = carMake; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public String getIssue() { return issue; }
    public void setIssue(String issue) { this.issue = issue; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Nested Location class
    public static class Location {
        private double lat;
        private double lng;
        private String address;

        public Location() {}

        public Location(double lat, double lng, String address) {
            this.lat = lat;
            this.lng = lng;
            this.address = address;
        }

        public double getLat() { return lat; }
        public void setLat(double lat) { this.lat = lat; }

        public double getLng() { return lng; }
        public void setLng(double lng) { this.lng = lng; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }
}
