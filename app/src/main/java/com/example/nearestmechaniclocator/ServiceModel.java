package com.example.nearestmechaniclocator;


    public class ServiceModel {
        private String serviceType, mechanicName, date, userId;

        public ServiceModel() {}

        public ServiceModel(String serviceType, String mechanicName, String date, String userId) {
            this.serviceType = serviceType;
            this.mechanicName = mechanicName;
            this.date = date;
            this.userId = userId;
        }

        public String getServiceType() { return serviceType; }
        public String getMechanicName() { return mechanicName; }
        public String getDate() { return date; }
        public String getUserId() { return userId; }
    }
