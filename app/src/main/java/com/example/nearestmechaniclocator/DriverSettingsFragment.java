package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DriverSettingsFragment extends Fragment {

    private Switch switchDarkMode, switchLocationSharing, switchObdSharing;
    private Button btnChangePassword, btnEditProfile, btnServiceHistory, btnLogout, btnDeleteAccount;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_settings, container, false);

        // Initialize Views
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        switchLocationSharing = view.findViewById(R.id.switch_location_sharing);
        switchObdSharing = view.findViewById(R.id.switch_obd_sharing);
        btnChangePassword = view.findViewById(R.id.btn_change_password);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnServiceHistory = view.findViewById(R.id.btn_service_history);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnDeleteAccount = view.findViewById(R.id.btn_delete_account);
        profileImage = view.findViewById(R.id.profile_image);

        // Set Click Listeners
        btnEditProfile.setOnClickListener(v -> openFragment(new EditProfileFragment()));
        btnChangePassword.setOnClickListener(v -> openFragment(new ChangePasswordFragment()));
        btnServiceHistory.setOnClickListener(v -> openFragment(new ServiceHistoryFragment()));
        btnLogout.setOnClickListener(v -> performLogout());
        btnDeleteAccount.setOnClickListener(v -> openFragment(new DeleteAccountFragment()));

        // Switch Listeners
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> toggleDarkMode(isChecked));
        switchLocationSharing.setOnCheckedChangeListener((buttonView, isChecked) -> toggleLocationSharing(isChecked));
        switchObdSharing.setOnCheckedChangeListener((buttonView, isChecked) -> toggleObdSharing(isChecked));

        return view;
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_driver, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void performLogout() {
        // Clear shared preferences or session
        // FirebaseAuth.getInstance().signOut(); // If using Firebase
        Intent intent = new Intent(requireContext(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void toggleDarkMode(boolean isEnabled) {
        // Save setting in SharedPreferences and apply
        // For now: just a stub
    }

    private void toggleLocationSharing(boolean isEnabled) {
        // Save setting in SharedPreferences or Firestore
    }

    private void toggleObdSharing(boolean isEnabled) {
        // Save setting in SharedPreferences or Firestore
    }
}
