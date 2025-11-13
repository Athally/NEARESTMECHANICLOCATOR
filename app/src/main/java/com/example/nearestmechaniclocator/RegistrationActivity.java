package com.example.nearestmechaniclocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.*;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private String verificationId;

    private LinearLayout headerEmail, expandEmail, headerPhone, expandPhone;
    private Button btnEmailRegister, btnSendOtp, btnVerifyOtp;
    private EditText etEmail, etPassword, etPhone, etOtp;

    private LinearLayout layoutGoogle, layoutFacebook;
    private static final int RC_GOOGLE = 9001;
    private TextView tvHaveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        initViews();
        setupExpandableSections();
        setupGoogleSignIn();
        setupFacebookSignIn();
        setupLoginNavigation();
    }

    private void initViews() {
        layoutGoogle = findViewById(R.id.layoutGoogle);
        layoutFacebook = findViewById(R.id.layoutFacebook);
        headerEmail = findViewById(R.id.headerEmail);
        expandEmail = findViewById(R.id.expandEmail);
        headerPhone = findViewById(R.id.headerPhone);
        expandPhone = findViewById(R.id.expandPhone);

        btnEmailRegister = findViewById(R.id.btnEmailRegister);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);

        tvHaveAccount = findViewById(R.id.tvHaveAccount);
    }

    private void setupExpandableSections() {
        headerEmail.setOnClickListener(v -> toggleSection(expandEmail, expandPhone));
        headerPhone.setOnClickListener(v -> toggleSection(expandPhone, expandEmail));

        btnEmailRegister.setOnClickListener(v -> registerWithEmail());
        btnSendOtp.setOnClickListener(v -> sendOtp());
    }

    private void toggleSection(View expand, View collapse) {
        if (expand.getVisibility() == View.VISIBLE) {
            expand.animate().alpha(0f).setDuration(200)
                    .withEndAction(() -> expand.setVisibility(View.GONE)).start();
        } else {
            collapse.setVisibility(View.GONE);
            expand.setAlpha(0f);
            expand.setVisibility(View.VISIBLE);
            expand.animate().alpha(1f).setDuration(300)
                    .setInterpolator(new AccelerateDecelerateInterpolator()).start();
        }
    }

    // ---------------- GOOGLE SIGN IN ----------------
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        layoutGoogle.setOnClickListener(v ->
                startActivityForResult(googleSignInClient.getSignInIntent(), RC_GOOGLE)
        );
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show();
                        // TODO: navigate to dashboard
                    } else {
                        Toast.makeText(this, "Google Sign-In failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ---------------- FACEBOOK SIGN IN ----------------
    private void setupFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create();
        layoutFacebook.setOnClickListener(v ->
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        );

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(RegistrationActivity.this, "Facebook login canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(RegistrationActivity.this, "Facebook login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Facebook Sign-In successful!", Toast.LENGTH_SHORT).show();
                        // TODO: navigate to dashboard
                    } else {
                        Toast.makeText(this, "Facebook Sign-In failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ---------------- EMAIL SIGN UP ----------------
    private void registerWithEmail() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Email registration successful!", Toast.LENGTH_SHORT).show();
                        // TODO: navigate to dashboard
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ---------------- PHONE SIGN UP WITH OTP ----------------
    private void sendOtp() {
        String phone = etPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                auth.signInWithCredential(credential);
                                Toast.makeText(RegistrationActivity.this, "Phone verified automatically!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(RegistrationActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(id, token);
                                verificationId = id;
                                showOtpField();
                                Toast.makeText(RegistrationActivity.this, "OTP sent!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void showOtpField() {
        if (etOtp == null) {
            etOtp = new EditText(this);
            etOtp.setHint("Enter OTP");
            etOtp.setTextColor(getResources().getColor(android.R.color.white));
            etOtp.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            expandPhone.addView(etOtp);

            btnVerifyOtp = new Button(this);
            btnVerifyOtp.setText("Verify OTP");
            btnVerifyOtp.setBackgroundTintList(getColorStateList(android.R.color.holo_blue_dark));
            btnVerifyOtp.setTextColor(getResources().getColor(android.R.color.white));
            expandPhone.addView(btnVerifyOtp);

            btnVerifyOtp.setOnClickListener(v -> verifyOtp());
        }
    }

    private void verifyOtp() {
        String code = etOtp.getText().toString().trim();
        if (code.isEmpty()) {
            Toast.makeText(this, "Enter the OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Phone verification successful!", Toast.LENGTH_SHORT).show();
                        // TODO: navigate to dashboard
                    } else {
                        Toast.makeText(this, "Invalid OTP!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ---------------- HANDLE GOOGLE / FACEBOOK RESULT ----------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // ---------------- NAVIGATE TO LOGIN ----------------
    private void setupLoginNavigation() {
        tvHaveAccount.setOnClickListener(v -> {
            // Replace with your LoginActivity
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });
    }
}
