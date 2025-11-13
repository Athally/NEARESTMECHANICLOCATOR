package com.example.nearestmechaniclocator;

import android.location.Location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MechanicViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Mechanic>> filteredMechanics = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private ListenerRegistration mechanicListener;

    public LiveData<List<Mechanic>> getFilteredMechanics() {
        return filteredMechanics;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void filterMechanics(
            String specialization,
            double userLat,
            double userLng,
            double radiusKm,
            double minRating
    ) {
        isLoading.setValue(true);

        if (mechanicListener != null) {
            mechanicListener.remove(); // Remove previous listener
        }

        mechanicListener = db.collection("users")
                .whereEqualTo("role", "mechanic")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        error.postValue("Failed to fetch mechanics: " + e.getMessage());
                        isLoading.postValue(false);
                        return;
                    }

                    List<Mechanic> result = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(snapshots)) {
                        Mechanic mechanic = doc.toObject(Mechanic.class);
                        mechanic.setId(doc.getId()); // Set Firestore ID

                        if (!"All".equalsIgnoreCase(specialization) &&
                                !specialization.equalsIgnoreCase(mechanic.getSpecialization())) {
                            continue;
                        }

                        double distance = calculateDistance(
                                userLat, userLng,
                                mechanic.getLatitude(), mechanic.getLongitude()
                        );

                        if (distance <= radiusKm && mechanic.getRating() >= minRating) {
                            mechanic.setDistanceFromDriver(distance); // optional for display
                            result.add(mechanic);
                        }
                    }

                    filteredMechanics.postValue(result);
                    isLoading.postValue(false);
                });
    }

    public void filterMechanicsBySpecialization(String selectedSpecialization) {
        filterMechanics(selectedSpecialization, 0.0, 0.0, Double.MAX_VALUE, 0.0);
        // Defaults: no distance/rating filtering
    }

    public void clearListener() {
        if (mechanicListener != null) {
            mechanicListener.remove();
        }
    }

    // Haversine formula to calculate distance in KM
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in KM
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        clearListener();
    }

    public void setCurrentLocation(Location location) {
    }
}
