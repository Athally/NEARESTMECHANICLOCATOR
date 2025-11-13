package com.example.nearestmechaniclocator;

public class ChatItem {
    private String name;
    private String lastMessage;
    private String timestamp;

    public ChatItem(String name, String lastMessage, String timestamp) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

