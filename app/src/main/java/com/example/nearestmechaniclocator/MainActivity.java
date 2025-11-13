package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // User is not signed in → go to LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            // Check user's role and redirect appropriately
            checkUserRoleAndRedirect(currentUser.getUid());
        }
    }

    private void checkUserRoleAndRedirect(String uid) {
        firestore.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("driver".equalsIgnoreCase(role)) {
                            startActivity(new Intent(MainActivity.this, DriverDashboardActivity.class));
                        } else if ("mechanic".equalsIgnoreCase(role)) {
                            startActivity(new Intent(MainActivity.this, MechanicDashboardActivity.class));
                        } else {
                            Toast.makeText(this, "Unknown user role. Logging out.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            startActivity(new Intent(this, LoginActivity.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(this, "User profile not found.", Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                });
    }
}
