package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MechanicDetailsActivity extends AppCompatActivity {

    private ImageView imageMechanic;
    private TextView textMechanicName, textMechanicSpecialization, textMechanicRating, textMechanicLocation;
    private RadioGroup bookingOptions;
    private RadioButton radioRoadside, radioSchedule;
    private Button btnRequestService;

    private String mechanicId;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private DocumentReference currentBookingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_details);

        initViews();

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Get mechanic data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            mechanicId = intent.getStringExtra("mechanicId");
            String name = intent.getStringExtra("name");
            String specialization = intent.getStringExtra("specialization");
            String photoUrl = intent.getStringExtra("photoUrl");
            String location = intent.getStringExtra("location");
            String rating = intent.getStringExtra("rating");

            if (mechanicId == null) {
                Toast.makeText(this, "Mechanic ID missing!", Toast.LENGTH_SHORT).show();
                finish();
            }

            textMechanicName.setText(name);
            textMechanicSpecialization.setText(specialization);
            textMechanicLocation.setText("Workshop: " + (location != null ? location : "Unknown"));
            textMechanicRating.setText("⭐ " + (rating != null ? rating : "N/A"));
            Glide.with(this).load(photoUrl).placeholder(R.drawable.ic_user_placeholder).into(imageMechanic);
        }

        btnRequestService.setOnClickListener(v -> {
            int selectedId = bookingOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a booking type", Toast.LENGTH_SHORT).show();
                return;
            }

            String bookingType = selectedId == R.id.radioRoadside ? "Roadside Service" : "Scheduled Service";
            createBooking(bookingType);
        });
    }

    private void initViews() {
        imageMechanic = findViewById(R.id.imageMechanic);
        textMechanicName = findViewById(R.id.textMechanicName);
        textMechanicSpecialization = findViewById(R.id.textMechanicSpecialization);
        textMechanicRating = findViewById(R.id.textMechanicRating);
        textMechanicLocation = findViewById(R.id.textMechanicLocation);

        bookingOptions = findViewById(R.id.radioBookingOptions);
        radioRoadside = findViewById(R.id.radioRoadside);
        radioSchedule = findViewById(R.id.radioSchedule);
        btnRequestService = findViewById(R.id.btnRequestService);
    }

    private void createBooking(String type) {
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> booking = new HashMap<>();
        booking.put("driverId", userId);
        booking.put("mechanicId", mechanicId);
        booking.put("mechanicName", textMechanicName.getText().toString());
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
                    Toast.makeText(MechanicDetailsActivity.this,
                            "Error listening for booking updates", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String status = snapshot.getString("status");
                    if ("Accepted".equals(status)) {
                        Toast.makeText(MechanicDetailsActivity.this,
                                "Mechanic accepted your booking!", Toast.LENGTH_LONG).show();
                        openChat(snapshot.getId(), mechanicId, textMechanicName.getText().toString());
                    } else if ("Declined".equals(status)) {
                        Toast.makeText(MechanicDetailsActivity.this,
                                "Mechanic declined your booking", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void openChat(String bookingId, String mechanicId, String mechanicName) {
        Intent intent = new Intent(MechanicDetailsActivity.this, ChatFragment.class);
        intent.putExtra("chatId", bookingId);
        intent.putExtra("otherUserId", mechanicId);
        intent.putExtra("otherUserName", mechanicName);
        startActivity(intent);
        finish(); // Close booking screen once chat starts
    }
}
