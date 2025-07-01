package com.example.chitchat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chitchat.adapter.ChatRecyclerAdapter;
import com.example.chitchat.model.ChatMessageModel;
import com.example.chitchat.model.ChatroomModel;
import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.AndroidUtil;
import com.example.chitchat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    String chatroomId;
    EditText messageInput;
    ImageButton backBtn;
    ImageButton sendMessageBtn;
    TextView otherUsername;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getUserId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backBtn.setOnClickListener((v) -> onBackPressed());

        otherUsername.setText(otherUser.getName());
        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty())
                return;
            sendMessageToUser(message);
        }));
        getOrCreateChatroomModel();

        setupChatRecyclerView();
    }

    void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUser(String message) {
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        messageInput.setText("");
                        //sendNotification(message);
                    }
                });
    }

    void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    // First time chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Hide the keyboard if open
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        // Navigate back to the previous activity
        super.onBackPressed();
    }
//    void sendNotification(String message){
//
//        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
//            if(task.isSuccessful()){
//                UserModel currentUser = task.getResult().toObject(UserModel.class);
//                try{
//                    JSONObject jsonObject  = new JSONObject();
//
//                    JSONObject notificationObj = new JSONObject();
//                    notificationObj.put("title",currentUser.getUsername());
//                    notificationObj.put("body",message);
//
//                    JSONObject dataObj = new JSONObject();
//                    dataObj.put("userId",currentUser.getUserId());
//
//                    jsonObject.put("notification",notificationObj);
//                    jsonObject.put("data",dataObj);
//                    jsonObject.put("to",otherUser.getFcmToken());
//
//                    callApi(jsonObject);
//
//
//                }catch (Exception e){
//
//                }
//
//            }
//        });
//
//    }
//    void callApi(JSONObject jsonObject){
//        MediaType JSON = MediaType.get("application/json; charset=utf-8");
//        OkHttpClient client = new OkHttpClient();
//        String url = "https://fcm.googleapis.com/fcm/send";
//        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .header("Authorization","Bearer YOUR_API_KEY")
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//
//            }
//        });
}

//The ChatActivity class manages the user interface and functionality for a one-on-one chat
//between two users, handling message sending, chatroom creation, and display of messages.