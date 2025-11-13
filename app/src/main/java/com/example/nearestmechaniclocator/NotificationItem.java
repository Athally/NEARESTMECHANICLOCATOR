package com.example.nearestmechaniclocator;

public class NotificationItem {

        private String id;
        private String title;
        private String message;
        private String recipientId;
        private String chatId;
        private long timestamp;
        private boolean read;

        public NotificationItem() {} // Firebase requires

        public NotificationItem(String id, String title, String message, String recipientId, String chatId, long timestamp, boolean read) {
            this.id = id;
            this.title = title;
            this.message = message;
            this.recipientId = recipientId;
            this.chatId = chatId;
            this.timestamp = timestamp;
            this.read = read;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getMessage() { return message; }
        public String getRecipientId() { return recipientId; }
        public String getChatId() { return chatId; }
        public long getTimestamp() { return timestamp; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
    }


