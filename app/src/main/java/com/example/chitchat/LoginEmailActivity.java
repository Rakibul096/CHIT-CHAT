package com.example.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chitchat.databinding.ActivityLoginEmailBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginEmailActivity extends AppCompatActivity {

    private ActivityLoginEmailBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        // Log In button click listener
        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailInput.getEditableText().toString().trim();
            String password = binding.passwordInput.getEditableText().toString().trim();

            if (!validateInputs(email, password)) {
                return; // Exit if validation fails
            }

            // Attempt to log in with Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Login successful, navigate to MainActivity
                            Toast.makeText(LoginEmailActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginEmailActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Close LoginEmailActivity
                        } else {
                            // Login failed, show error message
                            Toast.makeText(LoginEmailActivity.this,
                                    "Login failed: " + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // "Re-Enter" or "Sign Up" text click listener
        binding.reEnterOrSignUpText.setOnClickListener(v -> {
            // Navigate to LoginSignUpActivity
            Intent intent = new Intent(LoginEmailActivity.this, LoginSignUpActivity.class);
            startActivity(intent);
            finish(); // Close LoginEmailActivity
        });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
//The LoginEmailActivity handles user login via email and password using Firebase Authentication, validates input,
//and navigates to the MainActivity upon successful login or shows an error
//        message if login fails, with an option to navigate to the LoginSignUpActivity for registration.