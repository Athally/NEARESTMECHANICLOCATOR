package com.example.nearestmechaniclocator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class MechanicHomeFragment extends Fragment {

    private TextView mechanicName, mechanicSpecialization, mechanicRating;
    private CardView cardRequests, cardEarnings, cardRatings, cardEditProfile, cardChangePassword;

    public MechanicHomeFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mechanic_home, container, false);

        // Profile Card
        mechanicName = view.findViewById(R.id.textMechanicName);
        mechanicSpecialization = view.findViewById(R.id.textMechanicSpecialization);
        mechanicRating = view.findViewById(R.id.textMechanicRating);

        // Quick Action Cards
        cardRequests = view.findViewById(R.id.cardRequests);
        cardEarnings = view.findViewById(R.id.cardEarnings);
        cardRatings = view.findViewById(R.id.cardRatings);
        cardEditProfile = view.findViewById(R.id.cardEditProfile);
        cardChangePassword = view.findViewById(R.id.cardChangePassword);

        // Example: Fetch mechanic info (replace with Firestore query later)
        mechanicName.setText("John Doe");
        mechanicSpecialization.setText("Engine Specialist");
        mechanicRating.setText("⭐ 4.8");

        // Navigation
        cardRequests.setOnClickListener(v -> openFragment(new RequestsFragment()));
        cardEarnings.setOnClickListener(v -> openFragment(new EarningsFragment()));
        cardRatings.setOnClickListener(v -> openFragment(new RatingsFragment()));
        cardEditProfile.setOnClickListener(v -> openFragment(new EditProfileFragment()));
        cardChangePassword.setOnClickListener(v -> openFragment(new ChangePasswordFragment()));

        return view;
    }

    private void openFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mechanic_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
