
package com.example.nearestmechaniclocator;

public class ChatUser {
    private String driverId;
    private String driverName;

    public ChatUser() {} // Firestore requires empty constructor

    public ChatUser(String driverId, String driverName) {
        this.driverId = driverId;
        this.driverName = driverName;
    }

    public String getDriverId() {
        return driverId;
    }

    public String getDriverName() {
        return driverName;
    }
}
