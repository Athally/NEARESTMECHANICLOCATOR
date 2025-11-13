package com.example.nearestmechaniclocator;

class ServiceRequest {
    private String driverId;
    private String driverName;
    private String description;
    private String timestamp;
    private String chatId;

    public ServiceRequest() {}

    public String getDriverId() { return driverId; }
    public String getDriverName() { return driverName; }
    public String getDescription() { return description; }
    public String getTimestamp() { return timestamp; }
    public String getChatId() { return chatId; }

    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setDriverName(String driverName) { this.driverName = driverName; }
    public void setDescription(String description) { this.description = description; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setChatId(String chatId) { this.chatId = chatId; }
}
