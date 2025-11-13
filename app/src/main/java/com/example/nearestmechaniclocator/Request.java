package com.example.nearestmechaniclocator;

public class Request {
    private String id;           // <- store the Firebase key
    private String ownerName;
    private String carMake;
    private String requestType;  // "Booking" | "Roadside"
    private String issue;        // OBD-II text or user issue
    private long timestamp;      // epoch millis
    private Location location;
    private String status;       // "Pending", "Accepted", "Rejected"

    public Request() {}

    public Request(String id, String ownerName, String carMake, String requestType, String issue,
                   long timestamp, Location location, String status) {
        this.id = id;
        this.ownerName = ownerName;
        this.carMake = carMake;
        this.requestType = requestType;
        this.issue = issue;
        this.timestamp = timestamp;
        this.location = location;
        this.status = status;
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerName() { return ownerName; }
    public String getCarMake() { return carMake; }
    public String getRequestType() { return requestType; }
    public String getIssue() { return issue; }
    public long getTimestamp() { return timestamp; }
    public Location getLocation() { return location; }
    public String getStatus() { return status; }

    public void setOwnerName(String v){ this.ownerName=v; }
    public void setCarMake(String v){ this.carMake=v; }
    public void setRequestType(String v){ this.requestType=v; }
    public void setIssue(String v){ this.issue=v; }
    public void setTimestamp(long v){ this.timestamp=v; }
    public void setLocation(Location v){ this.location=v; }
    public void setStatus(String v){ this.status=v; }

    public String getOwnerId() {
        return ownerId;
    }

    public static class Location {
        private double lat;
        private double lng;
        private String address;

        public Location(){}

        public Location(double lat, double lng, String address) {
            this.lat = lat;
            this.lng = lng;
            this.address = address;
        }

        public double getLat() { return lat; }
        public double getLng() { return lng; }
        public String getAddress() { return address; }

        public void setLat(double v){ this.lat=v; }
        public void setLng(double v){ this.lng=v; }
        public void setAddress(String v){ this.address=v; }
    }
}
