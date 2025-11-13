package com.example.nearestmechaniclocator;

public class Message {
    private String senderId;
    private String text;
    private long timestamp;
    private boolean delivered;
    private boolean seen;

    // Empty constructor required for Firestore
    public Message() {}

    public Message(String senderId, String text, long timestamp) {
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
        this.delivered = false;
        this.seen = false;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
