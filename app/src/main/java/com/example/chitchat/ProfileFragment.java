package com.example.chitchat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.AndroidUtil;
import com.example.chitchat.util.FirebaseUtil;
import com.github.dhaval2404.imagepicker.ImagePicker;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    EditText usernameInput, emailInput, currentPasswordInput, newPasswordInput;
    Button updateProfileBtn;
    ProgressBar progressBar;
    TextView logoutBtn;
    UserModel currentUserModel;
    ActivityResultLauncher<Intent> imagePickLauncher;
    Uri selectedImageUri;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            AndroidUtil.setProfilePic(getContext(), selectedImageUri, profilePic);
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profilePic = view.findViewById(R.id.profile_image_view);
        usernameInput = view.findViewById(R.id.profile_name);
        emailInput = view.findViewById(R.id.profile_email);
        currentPasswordInput = view.findViewById(R.id.current_password);
        newPasswordInput = view.findViewById(R.id.new_password);
        updateProfileBtn = view.findViewById(R.id.profile_update_btn);
        progressBar = view.findViewById(R.id.profile_progress_bar);
        logoutBtn = view.findViewById(R.id.logout_btn);

        getUserData();

        updateProfileBtn.setOnClickListener(v -> updateProfile());
        logoutBtn.setOnClickListener(v -> logout());

        profilePic.setOnClickListener(v -> ImagePicker.with(this)
                .cropSquare()
                .compress(512)
                .maxResultSize(512, 512)
                .createIntent(intent -> {
                    imagePickLauncher.launch(intent);
                    return null;
                }));
        return view;
    }

    void updateProfile() {
        String newUsername = usernameInput.getText().toString().trim();
        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();

        if (newUsername.isEmpty() || newUsername.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }

        if (!currentPassword.isEmpty() && !newPassword.isEmpty()) {
            if (newPassword.length() < 6) {
                newPasswordInput.setError("Password should be at least 6 characters long");
                return;
            }
            verifyAndChangePassword(currentPassword, newPassword);
        }

        currentUserModel.setName(newUsername);
        setInProgress(true);
        updateToFirestore();
    }

    void verifyAndChangePassword(String currentPassword, String newPassword) {
        FirebaseUtil.reauthenticateUser(currentPassword, task -> {
            if (task.isSuccessful()) {
                FirebaseUtil.updateUserPassword(newPassword, passwordTask -> {
                    if (passwordTask.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Password updated successfully");
                    } else {
                        AndroidUtil.showToast(getContext(), "Password update failed");
                    }
                });
            } else {
                AndroidUtil.showToast(getContext(), "Current password is incorrect");
            }
        });
    }

    void updateToFirestore() {
        FirebaseUtil.currentUserDetails().set(currentUserModel)
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        AndroidUtil.showToast(getContext(), "Updated successfully");
                        resetInputFields(); // Reset input fields after a successful update
                    } else {
                        AndroidUtil.showToast(getContext(), "Update failed");
                    }
                });
    }

    void resetInputFields() {
        usernameInput.setText(currentUserModel.getName());
        emailInput.setText(currentUserModel.getEmail());
        currentPasswordInput.setText(""); // Clear current password
        newPasswordInput.setText(""); // Clear new password
        selectedImageUri = null; // Reset the selected image if necessary
    }

    void getUserData() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful() && task.getResult() != null) {
                currentUserModel = task.getResult().toObject(UserModel.class);
                usernameInput.setText(currentUserModel.getName());
                emailInput.setText(currentUserModel.getEmail());
            }
        });
    }

    void logout() {
        FirebaseUtil.logout();
        Intent intent = new Intent(getContext(), SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        updateProfileBtn.setVisibility(inProgress ? View.GONE : View.VISIBLE);
    }
}
//ProfileFragment allows users to update their username, password, and profile picture, retrieve and display
//current user data, update Firestore with new details, and log out, redirecting to SplashActivity.