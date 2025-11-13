
package com.example.nearestmechaniclocator;


import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.annotations.Nullable;

public class MechanicDetailsBottomSheet extends BottomSheetDialogFragment {

    private ImageView mechanicImage;
    private TextView nameText, specializationText, ratingText, distanceText;
    private Button callButton, requestButton, viewLocationButton, trackButton;

    private String name, specialization, rating, distance, phone, imageUrl, mechanicId;
    private double lat, lng;

    public static MechanicDetailsBottomSheet newInstance(String name, String specialization, String rating,
                                                         String distance, String phone, String imageUrl,
                                                         double lat, double lng, String mechanicId,boolean isOnline) {
        MechanicDetailsBottomSheet fragment = new MechanicDetailsBottomSheet();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("specialization", specialization);
        args.putString("rating", rating);
        args.putString("distance", distance);
        args.putString("phone", phone);
        args.putString("imageUrl", imageUrl);
        args.putDouble("lat", lat);
        args.putDouble("lng", lng);
        args.putBoolean("isOnline", isOnline);
        args.putString("mechanicId", mechanicId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_mechanic_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mechanicImage = view.findViewById(R.id.mechanicImage);
        nameText = view.findViewById(R.id.mechanicName);
        specializationText = view.findViewById(R.id.specialization);
        ratingText = view.findViewById(R.id.rating);
        distanceText = view.findViewById(R.id.distance);
        callButton = view.findViewById(R.id.callButton);
        requestButton = view.findViewById(R.id.requestButton);
        viewLocationButton = view.findViewById(R.id.locationButton);
        trackButton = view.findViewById(R.id.trackButton);
        assert getArguments() != null;
        boolean isOnline = getArguments().getBoolean("isOnline", false);
        TextView statusBadge = view.findViewById(R.id.statusBadge);

        if (isOnline) {
            statusBadge.setText("🟢 Online");
            statusBadge.setTextColor(Color.GREEN);
            requestButton.setEnabled(true);
            trackButton.setEnabled(true);
        } else {
            statusBadge.setText("🔴 Offline");
            statusBadge.setTextColor(Color.RED);
            requestButton.setEnabled(false);
            trackButton.setEnabled(false);
        }


        if (getArguments() != null) {
            name = getArguments().getString("name");
            specialization = getArguments().getString("specialization");
            rating = getArguments().getString("rating");
            distance = getArguments().getString("distance");
            phone = getArguments().getString("phone");
            imageUrl = getArguments().getString("imageUrl");
            lat = getArguments().getDouble("lat");
            lng = getArguments().getDouble("lng");
            mechanicId = getArguments().getString("mechanicId");

            nameText.setText(name);
            new StringBuilder().append(specializationText.setText().append(specialization).toString());
            ratingText.setText("Rating" + rating);
            distanceText.setText( "Distance"+ distance + " km");

            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .into(mechanicImage);
        }

        callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            startActivity(intent);
        });

        viewLocationButton.setOnClickListener(v -> {
            Uri mapUri = Uri.parse("geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + name + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        requestButton.setOnClickListener(v -> {
            // Handle service request (Firebase or Dialog)
            Toast.makeText(getContext(), "Service request sent to " + name, Toast.LENGTH_SHORT).show();
        });

        trackButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MechanicTrackingActivity.class);
            intent.putExtra("mechanicId", mechanicId);
            intent.putExtra("lat", lat);
            intent.putExtra("lng", lng);
            startActivity(intent);
        });
    }
}





