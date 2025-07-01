package com.example.chitchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chitchat.databinding.ActivityLoginSignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginSignUpActivity extends AppCompatActivity {

    private ActivityLoginSignUpBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Sign Up button functionality
        binding.signUpButton.setOnClickListener(v -> {
            String name = binding.nameInput.getText().toString().trim();
            String email = binding.emailInput.getText().toString().trim();
            String password = binding.passwordInput.getText().toString().trim();
            String confirmPassword = binding.confirmPasswordInput.getText().toString().trim();

            if (!validateInputs(name, email, password, confirmPassword)) {
                return; // Exit if validation fails
            }

            // Register the user using Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Save user data to Firestore
                            saveUserDataToFirestore(name, email, password);
                        } else {
                            // Show error message
                            Toast.makeText(LoginSignUpActivity.this,
                                    "Registration failed: " + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Log In link functionality
        binding.loginLink.setOnClickListener(v -> {
            // Navigate to the Sign In page
            Intent intent = new Intent(LoginSignUpActivity.this, LoginEmailActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Name is required.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Password and Confirm Password do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveUserDataToFirestore(String name, String email, String password) {
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String uniqueId = user.getUid();

        // Create a map to store user data
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", uniqueId); // Unique ID
        userData.put("name", name);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("timestamp", FieldValue.serverTimestamp()); // Timestamp

        // Save user data to Firestore (automatically generates a unique document ID)
        db.collection("users").document(uniqueId) // Or use db.collection("users").document(uniqueId)
                .set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginSignUpActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                        // Optionally navigate to another activity
                    } else {
                        Log.e("FirestoreError", "Error saving data: " + task.getException().getLocalizedMessage());
                        Toast.makeText(LoginSignUpActivity.this,
                                "Failed to save user data: " + task.getException().getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
