package com.example.nearestmechaniclocator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MechanicDashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private static final int RATING_REQUEST_CODE = 1001;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_dashboard);

        bottomNavigationView = findViewById(R.id.mechanicBottomNav);
        welcomeText = findViewById(R.id.welcomeText);

        // Apply premium gold gradient to welcome text
        applyGradientToWelcomeText();

        // Load default fragment
        if (savedInstanceState == null) {
            loadFragment(new MechanicHomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        // Handle bottom navigation clicks using if-else if
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new MechanicHomeFragment();
            } else if (id == R.id.nav_requests) {
                fragment = new RequestsFragment();
            } else if (id == R.id.nav_notifications) {
                fragment = new NotificationFragment();
            } else if (id == R.id.nav_settings) {
                fragment = new MechanicSettingsFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }

            return false;
        });
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, RatingsFragment.class);
        startActivityForResult(intent, RATING_REQUEST_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RATING_REQUEST_CODE) {
            finishAffinity();
        }
    }
}
