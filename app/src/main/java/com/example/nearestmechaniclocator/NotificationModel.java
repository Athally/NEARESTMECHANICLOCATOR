
package com.example.nearestmechaniclocator;

public class NotificationModel {
    private String title;
    private String message;

    // Default constructor
    public NotificationModel() {}

    public NotificationModel(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
