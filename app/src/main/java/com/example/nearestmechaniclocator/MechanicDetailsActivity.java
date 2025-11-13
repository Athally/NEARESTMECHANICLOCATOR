package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class MechanicDetailsActivity extends AppCompatActivity {

    private ImageView imageMechanic;
    private TextView textMechanicName, textMechanicSpecialization, textMechanicRating, textMechanicLocation;
    private Button btnChat, btnRequestService;

    private String mechanicId; // needed for chat and service requests

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_details);

        initViews();

        // Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            mechanicId = intent.getStringExtra("mechanicId");
            String name = intent.getStringExtra("name");
            String specialization = intent.getStringExtra("specialization");
            String photoUrl = intent.getStringExtra("photoUrl");
            String location = intent.getStringExtra("location");
            String rating = intent.getStringExtra("rating");

            textMechanicName.setText(name);
            textMechanicSpecialization.setText(specialization);
            textMechanicLocation.setText("Workshop: " + (location != null ? location : "Unknown"));
            textMechanicRating.setText("⭐ " + (rating != null ? rating : "N/A"));
            Glide.with(this).load(photoUrl).placeholder(R.drawable.ic_user_placeholder).into(imageMechanic);
        }

        // Chat button click
        btnChat.setOnClickListener(view -> {
            Intent chatIntent = new Intent(MechanicDetailsActivity.this, ChatActivity.class);
            chatIntent.putExtra("receiverId", mechanicId);
            chatIntent.putExtra("receiverName", textMechanicName.getText().toString());
            startActivity(chatIntent);
        });

        // Request Service button click
        btnRequestService.setOnClickListener(view -> {
            Intent requestIntent = new Intent(MechanicDetailsActivity.this, ServiceRequestActivity.class);
            requestIntent.putExtra("mechanicId", mechanicId);
            requestIntent.putExtra("mechanicName", textMechanicName.getText().toString());
            startActivity(requestIntent);
        });
    }

    private void initViews() {
        imageMechanic = findViewById(R.id.imageMechanic);
        textMechanicName = findViewById(R.id.textMechanicName);
        textMechanicSpecialization = findViewById(R.id.textMechanicSpecialization);
        textMechanicRating = findViewById(R.id.textMechanicRating);
        textMechanicLocation = findViewById(R.id.textMechanicLocation);
        btnChat = findViewById(R.id.btnChat);
        btnRequestService = findViewById(R.id.btnRequestService);
    }
}
