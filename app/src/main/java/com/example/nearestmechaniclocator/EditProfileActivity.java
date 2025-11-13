
package com.example.nearestmechaniclocator;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nameField, phoneField;
    private Button btnSave;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameField = findViewById(R.id.edit_name);
        phoneField = findViewById(R.id.edit_phone);
        btnSave = findViewById(R.id.btn_save_profile);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String phone = phoneField.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("phone", phone);

            db.collection("users").document(user.getUid())
                    .update(updates)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}

