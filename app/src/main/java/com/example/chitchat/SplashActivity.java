package com.example.chitchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.AndroidUtil;
import com.example.chitchat.util.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        auth = FirebaseAuth.getInstance();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                // If user is already authenticated, navigate to the Home page
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                // Otherwise, navigate to the Login/Sign-up page
                startActivity(new Intent(SplashActivity.this, LoginSignUpActivity.class));
            }
            finish();
        }, 3000);





//        if(FirebaseUtil.isLoggedIn() && getIntent().getExtras()!=null){
//            //from notification
//            String userId = getIntent().getExtras().getString("userId");
//            FirebaseUtil.allUserCollectionReference().document(userId).get()
//                    .addOnCompleteListener(task -> {
//                        if(task.isSuccessful()){
//                            UserModel model = task.getResult().toObject(UserModel.class);
//                            Intent mainIntent = new Intent(this,MainActivity.class);
//                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            startActivity(mainIntent);
//
//                            Intent intent = new Intent(this, ChatActivity.class);
//                            AndroidUtil.passUserModelAsIntent(intent,model);
//
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            finish();
//                        }
//                    });
//
//        }else {
            // Initialize FirebaseAuth instance



        }



}
