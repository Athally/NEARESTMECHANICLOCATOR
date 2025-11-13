package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RoleSelectionActivity extends AppCompatActivity {

    private Button btnDriver, btnMechanic;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        btnDriver = findViewById(R.id.btnDriver);
        btnMechanic = findViewById(R.id.btnMechanic);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnDriver.setOnClickListener(v -> saveRoleAndFinish("Driver"));
        btnMechanic.setOnClickListener(v -> saveRoleAndFinish("Mechanic"));
    }

    private void saveRoleAndFinish(String role) {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        Map<String, Object> userData = new HashMap<>();
        userData.put("role", role);
        userData.put("email", mAuth.getCurrentUser().getEmail());

        db.collection("users").document(uid)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Registration complete as " + role, Toast.LENGTH_SHORT).show();

                    // Send to the correct dashboard
                    Intent intent;
                    if ("Driver".equalsIgnoreCase(role)) {
                        intent = new Intent(this, DriverDashboardActivity.class);
                    } else {
                        intent = new Intent(this, MechanicDashboardActivity.class);
                    }

                    // Clear back stack so user can’t go back to registration
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save role", Toast.LENGTH_SHORT).show()
                );
    }
}
