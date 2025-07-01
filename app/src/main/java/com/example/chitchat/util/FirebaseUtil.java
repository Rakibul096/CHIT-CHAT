package com.example.chitchat.util;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    // Get the current user's ID
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // Check if a user is logged in
    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    // Get a reference to the current user's Firestore document
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    // Get a reference to the "users" collection
    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    // Get a reference to a specific chatroom by ID
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    // Get a reference to the messages in a specific chatroom
    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    // Generate a unique chatroom ID based on two user IDs
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    // Get a reference to all chatrooms
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    // Get a reference to the other user's document in a chatroom
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    // Convert a Firestore timestamp to a formatted string
    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("hh:mm aa").format(timestamp.toDate());
    }

    // Logout the current user
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    // Reauthenticate the user with their current password
    public static void reauthenticateUser(String currentPassword, OnCompleteListener<Void> listener) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (email != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);
            FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(listener);
        }
    }

    // Update the user's password
    public static void updateUserPassword(String newPassword, OnCompleteListener<Void> listener) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;
        FirebaseAuth.getInstance().getCurrentUser().updatePassword(newPassword).addOnCompleteListener(listener);
    }
}

//The FirebaseUtil class offers various helper methods to interact with Firebase services, including user authentication, accessing Firestore data,
//        reauthenticating users, updating passwords, and formatting timestamps.