package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DriverHomeFragment extends Fragment {

    private TextView welcomeText;
    private Button findMechanicsBtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public DriverHomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_home, container, false);

        welcomeText = view.findViewById(R.id.welcomeText);
        findMechanicsBtn = view.findViewById(R.id.findMechanicsBtn);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserName();

        findMechanicsBtn.setOnClickListener(v -> {
            // Navigate to DriverServicesFragment programmatically
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_driver, new DriverServicesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadUserName() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                welcomeText.setText("Welcome back, " + name + " 👋");
            }
        });
    }
}

