package com.example.ecommerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.CommentAdapter;
import com.example.ecommerceapplication.models.CommentModel;
import com.example.ecommerceapplication.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    // UI elements
    RecyclerView recyclerView; // RecyclerView to display comments
    CommentAdapter commentAdapter; // Adapter for the RecyclerView
    List<CommentModel> commentModelList; // List to store comment data

    EditText addcomment; // EditText for adding a new comment
    ImageView image_profile; // ImageView for user profile image
    TextView post; // TextView for posting a comment

    // IDs for the post and its publisher
    String postid;
    String publisherid;

    // Current Firebase user
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle back press on toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.comment_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentModelList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentModelList);
        recyclerView.setAdapter(commentAdapter);

        // Initialize UI elements
        addcomment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);
        post = findViewById(R.id.post);

        // Get the current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Get postid and publisherid from intent
        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        // Handle click on post button
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addcomment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this, "You can't send empty comment", Toast.LENGTH_SHORT).show();
                }else {
                    addComment();
                }
            }
        });
        getImage();  // Load user image
        readComments(); // Read comments from Firebase
    }

    // Method to add a comment to the database
    private void addComment() {
        // Get reference to the "Comments" collection in Firestore for the specific post
        CollectionReference commentsRef = FirebaseFirestore.getInstance().collection("Comments").document(postid).collection("Comments");

        // Create a HashMap to store comment data
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("comment", addcomment.getText().toString()); // Add the comment text
        commentData.put("publisherid", firebaseUser.getUid()); // Add the publisher ID

        // Add the comment to Firestore
        commentsRef.add(commentData)
                .addOnSuccessListener(documentReference -> {
                    // Comment added successfully
                    Log.d("addComment", "Comment added successfully");
                    // Add notification for the comment
                    addNotification();
                    // Clear the EditText after adding the comment
                    addcomment.setText("");
                })
                .addOnFailureListener(e -> Log.e("addComment", "Error adding comment: " + e.getMessage()));
    }


    // Method to add a notification to the database
    private void addNotification() {
        // Get reference to the "Notifications" collection in Firestore
        CollectionReference notificationsRef = FirebaseFirestore.getInstance().collection("Notifications").document(publisherid).collection("notifications");

        // Create a HashMap to store notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userid", firebaseUser.getUid()); // Add user ID of the commenter
        notificationData.put("text", "commented: " + addcomment.getText().toString()); // Add text of the notification
        notificationData.put("postid", postid); // Add post ID to which the comment is made
        notificationData.put("isComment", true);  // Set the value to true to indicate it's a comment notification

        // Add the notification to Firestore
        notificationsRef.add(notificationData)
                .addOnSuccessListener(documentReference -> Log.d("addNotification", "Notification added successfully"))
                .addOnFailureListener(e -> Log.e("addNotification", "Error adding notification: " + e.getMessage()));
    }


    private void getImage() {
        // Get reference to the current user's document in Firestore
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("CurrentUser").document(firebaseUser.getUid());

        // Retrieve user data asynchronously
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Retrieve UserModel object from the document
                UserModel user = documentSnapshot.toObject(UserModel.class);
                // Load the profile image using Glide and display it in the ImageView
                Glide.with(getApplicationContext()).load(user.getProfileImg()).into(image_profile);
            } else {
                Log.e("getImage", "User document does not exist");
            }
        }).addOnFailureListener(e -> Log.e("getImage", "Error retrieving user data: " + e.getMessage()));
    }


    // Method to read comments
    private void readComments() {
        // Get reference to the Comments collection in Firestore for the specified post
        CollectionReference commentsRef = FirebaseFirestore.getInstance().collection("Comments").document(postid).collection("Comments");

        // Retrieve comments asynchronously
        commentsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Clear the current list of comments
            commentModelList.clear();
            // Iterate through each document in the collection
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Retrieve a CommentModel object from the document
                CommentModel comment = document.toObject(CommentModel.class);
                // Add the comment to the list of comments
                commentModelList.add(comment);
            }
            // Notify the adapter that the data set has changed
            commentAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e("readComments", "Error retrieving comments: " + e.getMessage()));
    }


}