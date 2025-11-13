package com.example.nearestmechaniclocator;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class RatingsFragment extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText reviewText;
    private Button submitButton;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String mechanicId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ratingBar = findViewById(R.id.ratingBar);
        reviewText = findViewById(R.id.reviewText);
        submitButton = findViewById(R.id.submitButton);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        mechanicId = getIntent().getStringExtra("mechanicId");
        if (mechanicId == null) {
            Toast.makeText(this, "Mechanic not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Submitting rating...");
        progressDialog.setCancelable(false);

        submitButton.setOnClickListener(v -> submitRating());
    }

    private void submitRating() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "You must be logged in to submit a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        float stars = ratingBar.getRating();
        String review = reviewText.getText().toString().trim();
        String driverId = auth.getCurrentUser().getUid();

        if (stars == 0) {
            Toast.makeText(this, "Please select a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (review.length() > 500) {
            Toast.makeText(this, "Review too long (max 500 characters)", Toast.LENGTH_SHORT).show();
            return;
        }

        submitButton.setEnabled(false);
        progressDialog.show();

        Map<String, Object> rating = new HashMap<>();
        rating.put("driverId", driverId);
        rating.put("stars", stars);
        rating.put("review", review);
        rating.put("timestamp", FieldValue.serverTimestamp());

        // Add rating to Firestore
        db.collection("Ratings")
                .document(mechanicId)
                .collection("reviews")
                .add(rating)
                .addOnSuccessListener(docRef -> updateMechanicAverage(stars))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    submitButton.setEnabled(true);
                    Toast.makeText(this, "Failed to submit rating", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateMechanicAverage(float newRating) {
        DocumentReference mechRef = db.collection("Mechanics").document(mechanicId);

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(mechRef);

            long total = snapshot.contains("totalRatings") ? snapshot.getLong("totalRatings") : 0;
            double avg = snapshot.contains("avgRating") ? snapshot.getDouble("avgRating") : 0.0;

            long updatedTotal = total + 1;
            double updatedAvg = ((avg * total) + newRating) / updatedTotal;

            transaction.update(mechRef, "avgRating", updatedAvg);
            transaction.update(mechRef, "totalRatings", updatedTotal);

            return null;
        }).addOnSuccessListener(aVoid -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Rating submitted successfully", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            submitButton.setEnabled(true);
            Toast.makeText(this, "Failed to update mechanic rating", Toast.LENGTH_SHORT).show();
        });
    }
}
