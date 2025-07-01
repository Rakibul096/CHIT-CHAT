package com.example.chitchat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.ChatActivity;
import com.example.chitchat.R;
import com.example.chitchat.model.ChatroomModel;
import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.AndroidUtil;
import com.example.chitchat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {
    private final Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;

    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());


                        UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                        holder.usernameText.setText(otherUserModel.getName());
                        if(lastMessageSentByMe)
                            holder.lastMessageText.setText("You : "+model.getLastMessage());
                        else
                            holder.lastMessageText.setText(model.getLastMessage());
                            holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                            holder.itemView.setOnClickListener(v ->{
                            //navigate to chat activity.
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent,otherUserModel);

                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }

                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    static class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;


        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
           profilePic = itemView.findViewById(R.id.profile_pic_image_view); // Profile picture (optional)
        }
    }
}
//The RecentChatRecyclerAdapter binds chatroom data to a RecyclerView, displaying each chatroom's name, last message,
//and timestamp, and navigates to the ChatActivity when a chatroom is clicked, passing the details of the other user involved in the chat.