package com.example.ecommerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.UserAdapter;
import com.example.ecommerceapplication.models.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    String id; // User ID for which followers, following, or likes are being retrieved
    String title; // Title of the activity (e.g., "followers", "following", "likes")

    List<String> idList; // List to store user IDs of followers, following, or likes

    RecyclerView recyclerView; // RecyclerView to display the list of users
    UserAdapter userAdapter; // Adapter for the RecyclerView
    List<UserModel> userList; // List to store user data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        // Get user ID and title from the intent
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        title = intent.getStringExtra("title");

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, false);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();

        // Depending on the title, fetch followers, following, or likes
        switch (title){
            case "likes":
                getLikes();
                break;
            case "following":
                getFollowing();
                break;
            case "followers":
                getFollowers();
                break;
        }
    }

    // Method to retrieve liked posts
    private void getLikes() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                //  Populates the idList with the keys
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    idList.add(snapshot.getKey());
                }
                // Keys are used to retrieve the details of the users
                showUsers();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Method to retrieve users that the current user is following
    private void getFollowing() {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                    .child(id).child("following");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    idList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        idList.add(snapshot.getKey());
                    }
                    showUsers();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    // Method to retrieve followers of the current user
    private void getFollowers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Method to display the list of users
    private void showUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CurrentUser");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    UserModel user = snapshot.getValue(UserModel.class);
                    for (String id : idList){
                        if(user.getId().equals(id))
                            userList.add(user);

                    }
                }
                userAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
