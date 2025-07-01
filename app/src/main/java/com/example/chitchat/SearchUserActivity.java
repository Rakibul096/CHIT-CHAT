package com.example.chitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chitchat.adapter.SearchUserRecyclerAdapter;
import com.example.chitchat.model.UserModel;
import com.example.chitchat.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    EditText searchInput;
    ImageButton searchButton;
    ImageButton backButton;
    RecyclerView recyclerView;

    SearchUserRecyclerAdapter adapter;
    String loggedInEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchInput = findViewById(R.id.search_username_input);
        searchButton = findViewById(R.id.search_user_btn);
        backButton = findViewById(R.id.back_btn);
        recyclerView = findViewById(R.id.search_user_recycler_view);

        searchInput.requestFocus();

        // Retrieve the logged-in user's email
        loggedInEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        backButton.setOnClickListener(v -> onBackPressed());

        searchButton.setOnClickListener(v -> {
            String searchTerm = searchInput.getText().toString().trim();

            if (searchTerm.isEmpty()) {
                searchInput.setError("Search input cannot be empty");
                return;
            }

            if (isEmail(searchTerm)) {
                handleEmailSearch(searchTerm);
            } else {
                handleNameSearch(searchTerm);
            }
        });
    }

    // Check if the input is an email
    boolean isEmail(String input) {
        return input.contains("@");
    }

    // Handle email search
    void handleEmailSearch(String email) {
        // Check for valid email format
        if (!isValidEmail(email)) {
            searchInput.setError("Invalid email address");
            return;
        }

        // Full email provided (e.g., 'munnakhan01329@gmail.com')
        setupSearchRecyclerViewByEmail(email);
    }

    // Handle name search
    void handleNameSearch(String name) {
        // Check if the name contains domain-like patterns (e.g., ".com", ".co")
        if (name.matches(".*\\.(com|co|net|org|edu|gov|info).*")) {
            searchInput.setError("Invalid input: names cannot contain domain-like patterns (e.g., .com, .co)");
            return;
        }

        if (name.length() < 3) {
            searchInput.setError("Name must be at least 3 characters long");
            return;
        }

        // Split and use only the first part (e.g., 'Munna khan ' → 'Munna')
        String firstName = name.split(" ")[0].trim();
        setupSearchRecyclerViewByName(formatSearchTerm(firstName));
    }

    // Validate email format
    boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Format search term: capitalize the first letter, rest lowercase
    String formatSearchTerm(String input) {
        if (input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    void setupSearchRecyclerViewByName(String searchTerm) {
        // Query users by username
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("name", searchTerm)
                .whereLessThanOrEqualTo("name", searchTerm + '\uf8ff');

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        setupRecyclerView(options, searchTerm);
    }

    void setupSearchRecyclerViewByEmail(String email) {
        // Query users by email
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereEqualTo("email", email);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        setupRecyclerView(options, email);
    }

    void setupRecyclerView(FirestoreRecyclerOptions<UserModel> options, String searchTerm) {
        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new SearchUserRecyclerAdapter(options, getApplicationContext(), loggedInEmail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Register an observer to handle when no data is found
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount() == 0) {
                    // No user found, show message
                    Toast.makeText(SearchUserActivity.this, "User not found for: " + searchTerm, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                if (itemCount == 0) {
                    // No user found, show message
                    Toast.makeText(SearchUserActivity.this, "User not found for: " + searchTerm, Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
    }
}

//SearchUserActivity allows users to search for other users by email or name, displaying results in a RecyclerView; it validates inputs, handles both
//email and name-based searches, and provides feedback when no results are found.