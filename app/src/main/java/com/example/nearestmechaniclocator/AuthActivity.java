package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.*;

import java.util.Arrays;

public class AuthActivity extends AppCompatActivity {

    private EditText editEmail, editPassword, editConfirmPassword, editThirdPartyInput;
    private Button btnSubmit, btnGoogle, btnFacebook, btnOTP;
    private TextView txtSwitchMode, textAuthTitle;

    private boolean isLoginMode = true;

    private FirebaseAuth mAuth;
    private DatabaseReference db;

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private final int RC_GOOGLE_SIGN_IN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Init views
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        editThirdPartyInput = findViewById(R.id.editThirdPartyInput);

        btnSubmit = findViewById(R.id.btnSubmit);
        txtSwitchMode = findViewById(R.id.txtSwitchMode);
        textAuthTitle = findViewById(R.id.textAuthTitle);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnOTP = findViewById(R.id.btnOTP);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference("users");

        setupSwitchMode();
        setupThirdPartyAuth();

        // Auto redirect if already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) checkUserRoleAndRedirect();
    }

    private void setupSwitchMode() {
        txtSwitchMode.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            if (isLoginMode) {
                textAuthTitle.setText("Login");
                btnSubmit.setText("Login");
                editConfirmPassword.setVisibility(View.GONE);
                txtSwitchMode.setText("Don't have an account? Sign Up");
            } else {
                textAuthTitle.setText("Register");
                btnSubmit.setText("Sign Up");
                editConfirmPassword.setVisibility(View.VISIBLE);
                txtSwitchMode.setText("Already have an account? Login");
            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (isLoginMode) loginUser();
            else registerUser();
        });
    }

    private void loginUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) checkUserRoleAndRedirect();
                    else Toast.makeText(AuthActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void registerUser() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        db.child(mAuth.getCurrentUser().getUid()).child("role").setValue("not_set");
                        startActivity(new Intent(AuthActivity.this, RoleSelectionActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AuthActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupThirdPartyAuth() {
        // OTP
        btnOTP.setOnClickListener(v -> {
            editThirdPartyInput.setVisibility(View.VISIBLE);
            editThirdPartyInput.setHint("Enter OTP");
        });

        // Google
        btnGoogle.setOnClickListener(v -> {
            editThirdPartyInput.setVisibility(View.VISIBLE);
            editThirdPartyInput.setHint("Enter Google email");
            signInWithGoogle();
        });

        // Facebook
        callbackManager = CallbackManager.Factory.create();
        btnFacebook.setOnClickListener(v -> {
            editThirdPartyInput.setVisibility(View.VISIBLE);
            editThirdPartyInput.setHint("Enter Facebook email");
            signInWithFacebook();
        });
    }

    private void signInWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_GOOGLE_SIGN_IN);
    }

    private void signInWithFacebook() {
        com.facebook.login.LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        com.facebook.login.LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override public void onSuccess(LoginResult loginResult) {
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) checkUserRoleAndRedirect();
                    else Toast.makeText(AuthActivity.this, "Facebook auth failed", Toast.LENGTH_SHORT).show();
                });
            }
            @Override public void onCancel() {}
            @Override public void onError(FacebookException error) {
                Toast.makeText(AuthActivity.this, "Facebook login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(Exception.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(t -> {
                    if (t.isSuccessful()) checkUserRoleAndRedirect();
                    else Toast.makeText(AuthActivity.this, "Google auth failed", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkUserRoleAndRedirect() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.child(user.getUid()).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                if (role == null || role.equals("not_set")) {
                    startActivity(new Intent(AuthActivity.this, RoleSelectionActivity.class));
                } else if (role.equals("Mechanic")) {
                    startActivity(new Intent(AuthActivity.this, MechanicDashboardActivity.class));
                } else if (role.equals("Driver")) {
                    startActivity(new Intent(AuthActivity.this, DriverDashboardActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AuthActivity.this, "Error fetching role", Toast.LENGTH_SHORT).show();
            }
        });
    }
}