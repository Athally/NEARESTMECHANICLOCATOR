package com.example.nearestmechaniclocator;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class DeleteAccountFragment extends Fragment {

    public DeleteAccountFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_account, container, false);

        Button btnDelete = view.findViewById(R.id.btn_confirm_delete);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        btnDelete.setOnClickListener(v -> confirmDelete());
        btnCancel.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void confirmDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to permanently delete your account? This cannot be undone.")
                .setPositiveButton("Yes", (dialog, which) -> showReauthDialog())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showReauthDialog() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ask for password to reauthenticate
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        EditText input = new EditText(requireContext());
        input.setHint("Enter your password");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(input);

        new AlertDialog.Builder(requireContext())
                .setTitle("Reauthenticate")
                .setMessage("Please confirm your password to continue.")
                .setView(layout)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String password = input.getText().toString().trim();
                    if (password.isEmpty()) {
                        Toast.makeText(requireContext(), "Password required.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(user.getEmail()), password);
                    user.reauthenticate(credential)
                            .addOnSuccessListener(unused -> deleteAccountAfterReauth(user))
                            .addOnFailureListener(e -> Toast.makeText(requireContext(), "Reauthentication failed. Wrong password.", Toast.LENGTH_LONG).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccountAfterReauth(FirebaseUser user) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uid = user.getUid();

        db.collection("users").document(uid)
                .delete()
                .addOnSuccessListener(unused -> user.delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "Account deleted successfully.", Toast.LENGTH_LONG).show();

                            auth.signOut();
                            Intent intent = new Intent(requireContext(), LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(requireContext(), "Failed to delete account. Try again.", Toast.LENGTH_LONG).show()))
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to delete Firestore data.", Toast.LENGTH_SHORT).show());
    }

}
