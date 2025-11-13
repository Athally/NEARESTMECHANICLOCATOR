package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DriverServicesFragment extends Fragment {

    private CardView cardMotel, cardSpares, cardPetrol, cardMechanic, cardOBD, cardChat;
    private FirebaseFirestore db;

    public DriverServicesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_services, container, false);

        db = FirebaseFirestore.getInstance();

        cardMotel   = view.findViewById(R.id.cardMotel);
        cardSpares  = view.findViewById(R.id.cardSpares);
        cardPetrol  = view.findViewById(R.id.cardPetrol);
        cardMechanic= view.findViewById(R.id.cardMechanic);
        cardOBD     = view.findViewById(R.id.cardOBD);
        cardChat    = view.findViewById(R.id.cardChat); // NEW card for chat
        cardMotel.setOnClickListener(v -> {
                    Toast.makeText(getContext(), "Navigating to Mechanic Booking", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), MechanicBookingActivity.class));
                });
        // Book Motel

        cardMotel.setOnClickListener(v ->
                startActivity(new Intent(getContext(), NearbyPlacesTabsActivity.class))
        );

        cardSpares.setOnClickListener(v ->
                startActivity(new Intent(getContext(), NearbyPlacesTabsActivity.class))
        );

        cardPetrol.setOnClickListener(v ->
                startActivity(new Intent(getContext(), NearbyPlacesTabsActivity.class))
        );


        // Book Mechanic → creates booking doc
        cardMechanic.setOnClickListener(v -> {
            createBooking("mechanicId123"); // Example mechanicId, later replace with dynamic
        });

        // OBD2 Data (only visible if enabled in settings)
        boolean obdEnabled = getOBDSettingFromFirestore();
        cardOBD.setVisibility(obdEnabled ? View.VISIBLE : View.GONE);

        cardOBD.setOnClickListener(v -> {
            Intent obdIntent = new Intent(getContext(), OBDBottomSheet.class);
            obdIntent.putExtra("userId", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
            startActivity(obdIntent);
        });

        // Open Chat if mechanic accepted
        cardChat.setOnClickListener(v -> {
            String bookingId = "latestBookingId"; // Replace with logic to fetch bookingId
            Intent chatIntent = new Intent(getContext(), ChatFragment.class);
            chatIntent.putExtra("bookingId", bookingId);
            chatIntent.putExtra("otherUserId", "mechanicId123");
            chatIntent.putExtra("otherUserName", "Selected Mechanic");
            startActivity(chatIntent);
        });

        return view;
    }

    // Booking creation
    private void createBooking(String mechanicId) {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Map<String, Object> booking = new HashMap<>();
        booking.put("driverId", userId);
        booking.put("mechanicId", mechanicId);
        booking.put("status", "pending"); // mechanic will accept/reject
        booking.put("timestamp", System.currentTimeMillis());

        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Booking request sent!", Toast.LENGTH_SHORT).show();
                    // You could now listen to this booking doc for mechanic response
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean getOBDSettingFromFirestore() {
        // TODO: Replace with Firestore fetch of user setting
        return true; // For now always true
    }
}
