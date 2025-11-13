package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> loginUser());
        tvSignup.setOnClickListener(v -> startActivity(new Intent(this, RegistrationActivity.class)));
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_email_password), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Optional: Check email verification
                            if (!user.isEmailVerified()) {
                                Toast.makeText(this, R.string.email_not_verified, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            db.collection("users").document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(this::handleUserRole)
                                    .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.failed_user_data), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleUserRole(@NonNull DocumentSnapshot document) {
        if (!document.exists()) {
            Toast.makeText(this, getString(R.string.user_profile_not_found), Toast.LENGTH_SHORT).show();
            return;
        }

        String role = document.getString("role");

        Intent intent;

        if (role == null || role.isEmpty()) {
            // 🚨 If no role found, redirect to Role Selection
            intent = new Intent(this, RoleSelectionActivity.class);
        } else if ("Driver".equalsIgnoreCase(role)) {
            intent = new Intent(this, DriverDashboardActivity.class);
        } else if ("Mechanic".equalsIgnoreCase(role)) {
            intent = new Intent(this, MechanicDashboardActivity.class);
        } else {
            // Unknown role -> redirect to Role Selection
            intent = new Intent(this, RoleSelectionActivity.class);
        }

        // Clear activity back stack so user can't press back to return to login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish LoginActivity
    }
}
