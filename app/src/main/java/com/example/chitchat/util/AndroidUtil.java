package com.example.chitchat.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chitchat.model.UserModel;


public class AndroidUtil {
    public static void showToast(Context context, String message){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }
    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("name",model.getName());
        intent.putExtra("email",model.getEmail());
        intent.putExtra("userId",model.getUserId());
        intent.putExtra("password",model.getPassword());
        //intent.putExtra("fcmToken",model.getFcmToken());

    }
    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setName(intent.getStringExtra("name"));
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setUserId(intent.getStringExtra("userId"));
        userModel.setPassword(intent.getStringExtra("password"));
       // userModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return userModel;
    }
    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}

//The AndroidUtil class provides helpful methods to show a toast message, send and receive a UserModel (user information) through an Intent, and set a profile
//picture in an ImageView using Glide with a circular crop.