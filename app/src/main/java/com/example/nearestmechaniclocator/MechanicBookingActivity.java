package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;




public class MechanicBookingActivity extends AppCompatActivity {

    private RadioGroup bookingOptions;
    private RadioButton radioRoadside, radioSchedule;
    private Button btnConfirmBooking;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private DocumentReference currentBookingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_booking);

        bookingOptions = findViewById(R.id.radioBookingOptions);
        radioRoadside  = findViewById(R.id.radioRoadside);
        radioSchedule  = findViewById(R.id.radioSchedule);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnConfirmBooking.setOnClickListener(v -> {
            int selectedId = bookingOptions.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(this, "Please select a booking option", Toast.LENGTH_SHORT).show();
                return;
            }

            String bookingType = selectedId == R.id.radioRoadside ? "Roadside Service" : "Scheduled Service";
            createBooking(bookingType);
        });
    }

    private void createBooking(String type) {
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> booking = new HashMap<>();
        booking.put("driverId", userId);
        booking.put("type", type);
        booking.put("status", "Pending");
        booking.put("timestamp", System.currentTimeMillis());

        db.collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this,
                            "Booking created: " + type, Toast.LENGTH_SHORT).show();

                    currentBookingRef = documentReference;
                    listenForMechanicAcceptance();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            "Failed to create booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void listenForMechanicAcceptance() {
        if (currentBookingRef == null) return;

        currentBookingRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MechanicBookingActivity.this,
                            "Error listening for booking updates", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String status = snapshot.getString("status");

                    if ("Accepted".equals(status)) {
                        String mechanicId = snapshot.getString("mechanicId");
                        String mechanicName = snapshot.getString("mechanicName");

                        Toast.makeText(MechanicBookingActivity.this,
                                "Mechanic accepted your booking!", Toast.LENGTH_LONG).show();

                        openChat(snapshot.getId(), mechanicId, mechanicName);
                    }
                }
            }
        });
    }

    private void openChat(String bookingId, String mechanicId, String mechanicName) {
        Intent intent = new Intent(MechanicBookingActivity.this, ChatFragment.class);
        intent.putExtra("chatId", bookingId);
        intent.putExtra("otherUserId", mechanicId);
        intent.putExtra("otherUserName", mechanicName);
        startActivity(intent);
        finish(); // close booking screen once chat starts
    }
}
