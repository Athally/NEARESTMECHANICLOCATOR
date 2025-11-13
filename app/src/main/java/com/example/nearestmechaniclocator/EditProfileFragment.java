package com.example.nearestmechaniclocator;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 101;

    private ImageView profileImageView;
    private EditText editName, editLocation;
    private Spinner spinnerSpecialization;
    private MaterialButton btnSave, btnGetLocation;

    private Uri selectedImageUri;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("mechanic_profiles");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        profileImageView = view.findViewById(R.id.profileImageView);
        editName = view.findViewById(R.id.editMechanicName);
        editLocation = view.findViewById(R.id.editMechanicLocation);
        spinnerSpecialization = view.findViewById(R.id.spinnerSpecialization);
        btnSave = view.findViewById(R.id.btnSaveProfile);
        btnGetLocation = view.findViewById(R.id.btnGetLocation);

        // Spinner setup
        String[] specializations = {"General Repair", "Tires & Wheels", "Engine", "Brakes", "AC & Heating", "Electrical"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, specializations);
        spinnerSpecialization.setAdapter(adapter);

        // Load current profile
        loadMechanicProfile();

        // Profile image picker
        profileImageView.setOnClickListener(v -> openImagePicker());

        // Save profile
        btnSave.setOnClickListener(v -> saveProfile());

        // Get location
        btnGetLocation.setOnClickListener(v -> fetchCurrentLocation());

        return view;
    }

    private void loadMechanicProfile() {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection("mechanics").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        editName.setText(doc.getString("name"));
                        editLocation.setText(doc.getString("location"));

                        String specialization = doc.getString("specialization");
                        if (specialization != null) {
                            int pos = ((ArrayAdapter) spinnerSpecialization.getAdapter())
                                    .getPosition(specialization);
                            spinnerSpecialization.setSelection(pos);
                        }

                        String profileUrl = doc.getString("profileImage");
                        if (profileUrl != null && !profileUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(profileUrl)
                                    .placeholder(R.drawable.ic_person_edit)
                                    .circleCrop()
                                    .into(profileImageView);
                        }
                    }
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    // Check size
                    if (!isImageSizeValid(uri)) {
                        // Compress if larger than 2MB
                        uri = compressImage(uri);
                    }
                    selectedImageUri = uri;
                    Glide.with(this).load(selectedImageUri).circleCrop().into(profileImageView);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Check if image is <= 2 MB
    private boolean isImageSizeValid(Uri uri) throws IOException {
        InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
        if (inputStream == null) return false;

        int fileSizeInBytes = inputStream.available();
        inputStream.close();

        float fileSizeInMB = fileSizeInBytes / (1024f * 1024f);
        return fileSizeInMB <= 2f;
    }

    // Compress image to under 2 MB
    private Uri compressImage(Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);

        while (out.toByteArray().length / (1024f * 1024f) > 2f && quality > 10) {
            out.reset();
            quality -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        }

        String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), bitmap, "compressed_image", null);
        return Uri.parse(path);
    }

    private void saveProfile() {
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String name = editName.getText().toString().trim();
        String location = editLocation.getText().toString().trim();
        String specialization = spinnerSpecialization.getSelectedItem().toString();

        if (name.isEmpty() || location.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("location", location);
        updates.put("specialization", specialization);

        if (selectedImageUri != null) {
            StorageReference fileRef = storageRef.child(uid + ".jpg");
            fileRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updates.put("profileImage", uri.toString());
                        saveToFirestore(uid, updates);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show());
        } else {
            saveToFirestore(uid, updates);
        }
    }

    private void saveToFirestore(String uid, Map<String, Object> updates) {
        db.collection("mechanics").document(uid)
                .update(updates)
                .addOnSuccessListener(unused -> Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show());
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        String loc = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                        editLocation.setText(loc);
                    } else {
                        Toast.makeText(getContext(), "Could not fetch location", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
