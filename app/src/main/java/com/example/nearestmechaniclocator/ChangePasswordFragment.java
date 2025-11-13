package com.example.nearestmechaniclocator;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordFragment extends DialogFragment {

    private EditText currentPassword, newPassword, confirmPassword;
    private Button updateButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        currentPassword = view.findViewById(R.id.currentPassword);
        newPassword = view.findViewById(R.id.newPassword);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        updateButton = view.findViewById(R.id.updatePasswordBtn);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        updateButton.setOnClickListener(v -> {
            if (user == null) {
                showToast("User not logged in.");
                return;
            }

            String oldPass = currentPassword.getText().toString().trim();
            String newPass = newPassword.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                showToast("All fields are required.");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                showToast("Passwords do not match.");
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
            user.reauthenticate(credential)
                    .addOnSuccessListener(unused -> user.updatePassword(newPass)
                            .addOnSuccessListener(aVoid -> {
                                showToast("Password updated successfully!");
                                dismiss();
                            })
                            .addOnFailureListener(e -> showToast("Failed to update password: " + e.getMessage())))
                    .addOnFailureListener(e -> showToast("Re-authentication failed: " + e.getMessage()));
        });

        return view;
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
