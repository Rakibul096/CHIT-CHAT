package com.example.chitchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.ChatActivity;
import com.example.chitchat.R;
import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.AndroidUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {
    private final Context context;
    private final String loggedInEmail; // Logged-in user's email

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context, String loggedInEmail) {
        super(options);
        this.context = context;
        this.loggedInEmail = loggedInEmail;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        Log.d("SearchUserAdapter", "name: " + model.getName() + ", email: " + model.getEmail());
        holder.usernameText.setText(model.getName());
        holder.emailText.setText(model.getEmail());

        // Check if this is the logged-in user and append " (Me)" to their name
        if (model.getEmail().equals(loggedInEmail)) {
            holder.usernameText.append(" (Me)");
        }
        holder.itemView.setOnClickListener(v ->{
            //navigate to chat activity.
             Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent,model);

             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    static class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView emailText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            emailText = itemView.findViewById(R.id.email_text); // For email
            profilePic = itemView.findViewById(R.id.profile_pic_image_view); // Profile picture (optional)
        }
    }
}

//The SearchUserRecyclerAdapter binds user data to a RecyclerView, displaying each user's name and email, appending " (Me)" to the logged-in user's name,
//and navigating to the ChatActivity when a user is selected, passing the selected user's details via an intent.