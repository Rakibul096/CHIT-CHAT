package com.example.chitchat;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chitchat.adapter.RecentChatRecyclerAdapter;
import com.example.chitchat.adapter.SearchUserRecyclerAdapter;
import com.example.chitchat.model.ChatroomModel;
import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class ChatFragment extends Fragment {
    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;


    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view =   inflater.inflate(R.layout.fragment_chat,container,false);
       recyclerView = view.findViewById(R.id.recycler_view);
       setupRecyclerView();
       return view;

    }

    void setupRecyclerView() {
        // Query users by username or email
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds",FirebaseUtil.currentUserId())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class)
                .build();

        // Pass logged-in email to the adapter
        adapter = new RecentChatRecyclerAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    //@SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }


}
//The ChatFragment class displays recent chatrooms in a RecyclerView, queries Firestore for chatrooms containing the logged-in user, and uses a
//RecentChatRecyclerAdapter to bind data, managing adapter lifecycle during fragment lifecycle events.