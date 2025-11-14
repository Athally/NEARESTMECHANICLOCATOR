package com.example.nearestmechaniclocator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.*;

import java.util.*;

public class EditProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 101;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private String role;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private StorageReference storageRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        tabLayout = view.findViewById(R.id.tabLayoutProfile);
        viewPager = view.findViewById(R.id.viewPagerProfile);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        fetchUserRole();

        return view;
    }

    private void fetchUserRole() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        role = doc.getString("role"); // "driver" or "mechanic"
                        setupTabs();
                    }
                });
    }

    private void setupTabs() {
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();

        // Common personal info tab
        fragments.add(new TabFragmentPersonalInfo());
        titles.add("Personal Info");

        if ("driver".equals(role)) {
            fragments.add(new TabFragmentDriverCar());
            titles.add("Car Info");
        } else if ("mechanic".equals(role)) {
            fragments.add(new TabFragmentMechanicCredentials());
            titles.add("Credentials");
            fragments.add(new TabFragmentMechanicAvailability());
            titles.add("Availability");
        }

        ProfilePagerAdapter adapter = new ProfilePagerAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(titles.get(position))
        ).attach();
    }

    private static class ProfilePagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        private final List<Fragment> fragmentList;

        public ProfilePagerAdapter(@NonNull Fragment fragment, List<Fragment> fragments) {
            super(fragment);
            this.fragmentList = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }

    // ------------------------ PERSONAL INFO TAB ------------------------
    public static class TabFragmentPersonalInfo extends Fragment {
        private ImageView profileImageView;
        private EditText editName;
        private MaterialButton btnSave;
        private Uri selectedImageUri;

        private FirebaseAuth auth;
        private FirebaseFirestore db;
        private StorageReference storageRef;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.tab_personal_info, container, false);

            profileImageView = view.findViewById(R.id.profileImageView);
            editName = view.findViewById(R.id.editName);
            btnSave = view.findViewById(R.id.btnSaveProfile);

            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            storageRef = FirebaseStorage.getInstance().getReference("profiles");

            loadProfile();

            profileImageView.setOnClickListener(v -> openImagePicker());
            btnSave.setOnClickListener(v -> saveProfile());

            return view;
        }

        private void loadProfile() {
            String uid = auth.getCurrentUser().getUid();
            db.collection("users").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            editName.setText(doc.getString("name"));
                            String url = doc.getString("profileImage");
                            if (url != null) Glide.with(this).load(url).circleCrop().into(profileImageView);
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
                selectedImageUri = data.getData();
                Glide.with(this).load(selectedImageUri).circleCrop().into(profileImageView);
            }
        }

        private void saveProfile() {
            String uid = auth.getCurrentUser().getUid();
            String name = editName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getContext(), "Enter your name", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);

            if (selectedImageUri != null) {
                StorageReference fileRef = storageRef.child(uid + ".jpg");
                fileRef.putFile(selectedImageUri)
                        .addOnSuccessListener(task -> fileRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    updates.put("profileImage", uri.toString());
                                    db.collection("users").document(uid).update(updates);
                                    Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                                }));
            } else {
                db.collection("users").document(uid).update(updates);
                Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ------------------------ DRIVER CAR INFO TAB ------------------------
    public static class TabFragmentDriverCar extends Fragment {
        private ImageView carImageView;
        private EditText editMake, editModel, editYear, editColor;
        private MaterialButton btnSave;
        private Uri selectedCarUri;

        private FirebaseAuth auth;
        private FirebaseFirestore db;
        private StorageReference storageRef;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.tab_driver_car, container, false);

            carImageView = view.findViewById(R.id.carImageView);
            editMake = view.findViewById(R.id.editCarMake);
            editModel = view.findViewById(R.id.editCarModel);
            editYear = view.findViewById(R.id.editCarYear);
            editColor = view.findViewById(R.id.editCarColor);
            btnSave = view.findViewById(R.id.btnSaveCar);

            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            storageRef = FirebaseStorage.getInstance().getReference("car_photos");

            loadCarInfo();

            carImageView.setOnClickListener(v -> openCarPicker());
            btnSave.setOnClickListener(v -> saveCarInfo());

            return view;
        }

        private void loadCarInfo() {
            String uid = auth.getCurrentUser().getUid();
            db.collection("drivers").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            editMake.setText(doc.getString("make"));
                            editModel.setText(doc.getString("model"));
                            editYear.setText(doc.getString("year"));
                            editColor.setText(doc.getString("color"));
                            String url = doc.getString("carImage");
                            if (url != null) Glide.with(this).load(url).into(carImageView);
                        }
                    });
        }

        private void openCarPicker() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
                selectedCarUri = data.getData();
                Glide.with(this).load(selectedCarUri).into(carImageView);
            }
        }

        private void saveCarInfo() {
            String uid = auth.getCurrentUser().getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("make", editMake.getText().toString());
            updates.put("model", editModel.getText().toString());
            updates.put("year", editYear.getText().toString());
            updates.put("color", editColor.getText().toString());

            if (selectedCarUri != null) {
                StorageReference fileRef = storageRef.child(uid + ".jpg");
                fileRef.putFile(selectedCarUri)
                        .addOnSuccessListener(task -> fileRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    updates.put("carImage", uri.toString());
                                    db.collection("drivers").document(uid).update(updates);
                                    Toast.makeText(getContext(), "Car info updated", Toast.LENGTH_SHORT).show();
                                }));
            } else {
                db.collection("drivers").document(uid).update(updates);
                Toast.makeText(getContext(), "Car info updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ------------------------ MECHANIC CREDENTIALS TAB ------------------------
    public static class TabFragmentMechanicCredentials extends Fragment {
        private ImageView certificateImage;
        private EditText editSpecialization;
        private MaterialButton btnSave;
        private Uri selectedCertUri;

        private FirebaseAuth auth;
        private FirebaseFirestore db;
        private StorageReference storageRef;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.tab_mechanic_credentials, container, false);

            certificateImage = view.findViewById(R.id.certificateImage);
            editSpecialization = view.findViewById(R.id.editSpecialization);
            btnSave = view.findViewById(R.id.btnSaveCredentials);

            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            storageRef = FirebaseStorage.getInstance().getReference("certificates");

            loadCredentials();

            certificateImage.setOnClickListener(v -> openCertPicker());
            btnSave.setOnClickListener(v -> saveCredentials());

            return view;
        }

        private void loadCredentials() {
            String uid = auth.getCurrentUser().getUid();
            db.collection("mechanics").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            editSpecialization.setText(doc.getString("specialization"));
                            String url = doc.getString("certificate");
                            if (url != null) Glide.with(this).load(url).into(certificateImage);
                        }
                    });
        }

        private void openCertPicker() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
                selectedCertUri = data.getData();
                Glide.with(this).load(selectedCertUri).into(certificateImage);
            }
        }

        private void saveCredentials() {
            String uid = auth.getCurrentUser().getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("specialization", editSpecialization.getText().toString());

            if (selectedCertUri != null) {
                StorageReference fileRef = storageRef.child(uid + ".jpg");
                fileRef.putFile(selectedCertUri)
                        .addOnSuccessListener(task -> fileRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    updates.put("certificate", uri.toString());
                                    db.collection("mechanics").document(uid).update(updates);
                                    Toast.makeText(getContext(), "Credentials updated", Toast.LENGTH_SHORT).show();
                                }));
            } else {
                db.collection("mechanics").document(uid).update(updates);
                Toast.makeText(getContext(), "Credentials updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ------------------------ MECHANIC AVAILABILITY TAB ------------------------
    public static class TabFragmentMechanicAvailability extends Fragment {
        private Switch switchAvailability;
        private EditText editArea, editHours;
        private MaterialButton btnSave;

        private FirebaseAuth auth;
        private FirebaseFirestore db;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.tab_mechanic_availability, container, false);

            switchAvailability = view.findViewById(R.id.switchAvailability);
            editArea = view.findViewById(R.id.editArea);
            editHours = view.findViewById(R.id.editHours);
            btnSave = view.findViewById(R.id.btnSaveAvailability);

            auth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();

            loadAvailability();

            btnSave.setOnClickListener(v -> saveAvailability());

            return view;
        }

        private void loadAvailability() {
            String uid = auth.getCurrentUser().getUid();
            db.collection("mechanics").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            switchAvailability.setChecked(Boolean.TRUE.equals(doc.getBoolean("available")));
                            editArea.setText(doc.getString("area"));
                            editHours.setText(doc.getString("hours"));
                        }
                    });
        }

        private void saveAvailability() {
            String uid = auth.getCurrentUser().getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("available", switchAvailability.isChecked());
            updates.put("area", editArea.getText().toString());
            updates.put("hours", editHours.getText().toString());

            db.collection("mechanics").document(uid).update(updates);
            Toast.makeText(getContext(), "Availability updated", Toast.LENGTH_SHORT).show();
        }
    }
}
