package com.example.chitchat.model;

import com.google.firebase.Timestamp;

public class UserModel {
    private String email; // Changed from phone to email
    private String name;
    private Timestamp createdTimestamp;
    private String userId;
    private String fcmToken;
    private String password;
    private boolean isMe;

    // Default constructor (required for Firestore)
    public UserModel() {
    }

    // Constructor with fields
    public UserModel(String email, String name, Timestamp createdTimestamp, String userId,boolean isMe,String password) {
        this.email = email;
        this.name = name;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.isMe = isMe;
        this.password=password;
    }

    // Getter and Setter for email
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    // Getter and Setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and Setter for fcmToken
    public String getFcmToken() {
        return fcmToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter and Setter for fcmToken

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public boolean isMe() {
        return isMe;
    }
}
//The UserModel class represents a user with details such as email, name, user ID, creation timestamp,
//        password, FCM token, and a flag indicating if the user is the currently logged-in user.