package com.example.ecommerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.IdentifableAdapter;
import com.example.ecommerceapplication.adapters.UserAdapter;
import com.example.ecommerceapplication.models.Identifiable;
import com.example.ecommerceapplication.models.SellerModel;
import com.example.ecommerceapplication.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    private static final String TAG = "FollowersActivity";

    String id; // User ID for which followers, following, or likes are being retrieved
    String title; // Title of the activity (e.g., "followers", "following", "likes")

    List<String> idList; // List to store user IDs of followers, following, or likes
   
    RecyclerView recyclerView; // RecyclerView to display the list of users
    UserAdapter userAdapter; // Adapter for the RecyclerView
    List<UserModel> userList; // List to store user data
    List<SellerModel> sellerList; // List for seller data (add this if you don't have it yet)
    List<Identifiable> combinedList;

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
        sellerList = new ArrayList<>(); // Make sure you have this line
        combinedList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList, false);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();

        // Depending on the title, fetch followers, following, or likes
        switch (title){
            case "Likes":
                getLikes();
                break;
            case "Following":
                getFollowing();
                break;
            case "Followers":
                getFollowers();
                break;
        }
    }

    // Method to retrieve liked posts
    private void getLikes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference likesRef = db.collection("Likes").document(id).collection("Likes");

        likesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    idList.clear();
                    // Iterate through each document in the query result
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Add the document ID (post ID) to idList
                        idList.add(document.getId());
                    }
                    // Once all liked posts are retrieved, you can show the users who liked the posts
                    showUsers();
                } else {
                    // Handle errors
                    Log.e(TAG, "Error getting liked posts: ", task.getException());
                }
            }
        });
    }


    private void getFollowing() {
        FirebaseFirestore.getInstance().collection("Follow")
                .document(id)
                .collection("following")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        idList.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            idList.add(snapshot.getId());
                        }
                        showUsers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }

    private void getFollowers() {
        FirebaseFirestore.getInstance().collection("Follow")
                .document(id)
                .collection("followers")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        idList.clear();
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                            idList.add(snapshot.getId());
                        }
                        showUsers();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                    }
                });
    }


    // Update showUsers to get data from both "CurrentUser" and "seller"
    private void showUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve data from both collections
        Task<QuerySnapshot> currentUserTask = db.collection("CurrentUser").get();
        Task<QuerySnapshot> sellerTask = db.collection("seller").get();

        // Wait for both tasks to complete
        Tasks.whenAllSuccess(currentUserTask, sellerTask).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> results) {
                userList.clear();

                // Process results from "CurrentUser"
                QuerySnapshot currentUserSnapshot = (QuerySnapshot) results.get(0);
                for (DocumentSnapshot snapshot : currentUserSnapshot.getDocuments()) {
                    UserModel user = snapshot.toObject(UserModel.class);
                    if (user != null && idList.contains(user.getId())) {
                        combinedList.addAll(userList); // Add UserModel objects
                        combinedList.addAll(sellerList);
                    }
                }

                // Process results from "seller"
                QuerySnapshot sellerSnapshot = (QuerySnapshot) results.get(1);
                for (DocumentSnapshot snapshot : sellerSnapshot.getDocuments()) {
                    SellerModel seller = snapshot.toObject(SellerModel.class);
                    if (seller != null && idList.contains(seller.getSellerID())) {
                        combinedList.addAll(userList); // Add UserModel objects
                        combinedList.addAll(sellerList);
                    }
                }

                // Create an adapter with combinedList and set it to the RecyclerView
                IdentifableAdapter adapter = new IdentifableAdapter(combinedList);
                recyclerView.setAdapter(adapter); // Set the adapter to display combined data

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure case
                Log.e(TAG, "Error getting users: ", e);
            }
        });
    }

}
