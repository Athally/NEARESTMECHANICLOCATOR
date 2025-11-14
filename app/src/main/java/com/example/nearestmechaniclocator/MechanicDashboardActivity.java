package com.example.nearestmechaniclocator;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MechanicDashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private ImageButton btnHome, btnRequests, btnNotifications, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_dashboard);

        // Initialize views
        welcomeText = findViewById(R.id.welcomeText);

        btnHome = findViewById(R.id.btnHome);
        btnRequests = findViewById(R.id.btnRequests);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnSettings = findViewById(R.id.btnSettings);

        // Apply gold gradient to welcome text
        applyGradientToWelcomeText();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new MechanicHomeFragment());
        }

        // Sidebar click listeners
        btnHome.setOnClickListener(v -> loadFragment(new MechanicHomeFragment()));
        btnRequests.setOnClickListener(v -> loadFragment(new RequestsFragment()));
        btnNotifications.setOnClickListener(v -> loadFragment(new NotificationFragment()));
        btnSettings.setOnClickListener(v -> loadFragment(new MechanicSettingsFragment()));
    }

    // Swap fragments into container
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mechanic_fragment_container, fragment)
                .commit();
    }

    // Apply gold gradient to welcome text
    private void applyGradientToWelcomeText() {
        Shader textShader = new LinearGradient(
                0, 0, 0, welcomeText.getTextSize(),
                new int[]{
                        Color.parseColor("#FFD700"),
                        Color.parseColor("#FFC107"),
                        Color.parseColor("#FFB300")
                },
                null,
                Shader.TileMode.CLAMP
        );
        welcomeText.getPaint().setShader(textShader);
    }
}
