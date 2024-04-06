package com.example.ecommerceapplication.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.activities.EditProfileActivity;
import com.example.ecommerceapplication.activities.FollowersActivity;
import com.example.ecommerceapplication.activities.OptionsActivity;
import com.example.ecommerceapplication.activities.OrderStatusActivity;
import com.example.ecommerceapplication.adapters.MyFotoAdapter;
import com.example.ecommerceapplication.models.PostModel;
import com.example.ecommerceapplication.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment {

    ImageView image_profile, options;
    TextView posts, followers, following, bio, username, name;
    Button edit_profile, order_status;

    private List<String> mySaves;

    RecyclerView recyclerView_saves;
    MyFotoAdapter myFotoAdapter_saves;
    List<PostModel> postList_saves;

    RecyclerView recyclerView;
    MyFotoAdapter myFotoAdapter;
    List<PostModel> postList;

    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String profileid;

    ImageButton my_fotos, saved_fotos;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        SharedPreferences prefs = getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        name = view.findViewById(R.id.name);
        edit_profile = view.findViewById(R.id.edit_profile);
        my_fotos = view.findViewById(R.id.my_fotos);
        saved_fotos = view.findViewById(R.id.saved_fotos);
        order_status = view.findViewById(R.id.order_status);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myFotoAdapter = new MyFotoAdapter(getContext(), postList);
        recyclerView.setAdapter(myFotoAdapter);

        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        myFotoAdapter_saves = new MyFotoAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myFotoAdapter_saves);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        userInfo();
        getFollowers();
        getNrPosts();
        myFotos();
        mysaves();



        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        }else {
            checkFollow();
            saved_fotos.setVisibility(View.GONE);
        }

        order_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderStatusActivity.class);
                startActivity(intent);
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit Profile")){
                    startActivity(new Intent(getContext(), EditProfileActivity.class));

                } else if (btn.equals("Follow")) {

                    DocumentReference followingRef = firestore.collection("Follow").document(firebaseUser.getUid()).collection("following").document(profileid);
                    DocumentReference followerRef = firestore.collection("Follow").document(profileid).collection("followers").document(firebaseUser.getUid());

                    // Set the follow status to true
                    followingRef.set(new Object())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update UI and add notification
                                    edit_profile.setText("following");
                                    addNotification();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle error
                                    Toast.makeText(getContext(), "Failed to follow user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                    followerRef.set(new Object());
                } else if (btn.equals("following")) {

                    DocumentReference followingRef = firestore.collection("Follow").document(firebaseUser.getUid()).collection("following").document(profileid);
                    DocumentReference followerRef = firestore.collection("Follow").document(profileid).collection("followers").document(firebaseUser.getUid());

                    followingRef.delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update UI
                                    edit_profile.setText("Follow");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle error
                                    Toast.makeText(getContext(), "Failed to unfollow user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                    followerRef.delete();
                }
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });

        my_fotos.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });

        saved_fotos.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void addNotification(){
        // Get reference to the "notifications" collection in Firestore
        CollectionReference notificationsRef = FirebaseFirestore.getInstance().collection("notifications").document(profileid).collection("notifications");

        // Create a HashMap to store notification data
        HashMap<String, Object> notificationData = new HashMap<>();
        notificationData.put("userid", firebaseUser.getUid());
        notificationData.put("text", "started following you");
        notificationData.put("isRead", false);

        // Add the notification to Firestore
        notificationsRef.add(notificationData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Notification added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding notification", e);
                    }
                });
    }


    private void userInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("CurrentUser").document(firebaseUser.getUid());

        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (getContext() == null) {
                    return;
                }

                if (documentSnapshot.exists()) {
                    UserModel user = documentSnapshot.toObject(UserModel.class);

                    // Update UI with user information
                    Glide.with(getContext()).load(user.getProfileImg()).into(image_profile);
                    username.setText(user.getUsername());
                    name.setText(user.getUsername());
                    bio.setText(user.getBio());
                } else {
                    // Handle case where document does not exist
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle error
                Toast.makeText(getContext(), "Failed to retrieve user information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkFollow(){
        // Get reference to the "Follow" collection in Firestore
        DocumentReference followRef = FirebaseFirestore.getInstance().collection("Follow")
                .document(firebaseUser.getUid()).collection("following").document(profileid);

        // Listen for changes in the following status
        followRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null && snapshot.exists()) {
                    edit_profile.setText("following");
                } else {
                    edit_profile.setText("follow");
                }
            }
        });
    }

    private void getFollowers(){
        // Get reference to the "Follow" collection in Firestore for followers
        CollectionReference followersRef = FirebaseFirestore.getInstance().collection("Follow")
                .document(profileid).collection("followers");

        // Get reference to the "Follow" collection in Firestore for following
        CollectionReference followingRef = FirebaseFirestore.getInstance().collection("Follow")
                .document(profileid).collection("following");

        // Listen for changes in the number of followers
        followersRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null) {
                    followers.setText("" + snapshot.size());
                }
            }
        });

        // Listen for changes in the number of following
        followingRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null) {
                    following.setText("" + snapshot.size());
                }
            }
        });
    }


    private void getNrPosts() {
        // Get reference to the "Posts" collection in Firestore
        CollectionReference postsRef = FirebaseFirestore.getInstance().collection("Posts");

        // Listen for changes in the "Posts" collection
        postsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null) {
                    int count = 0;
                    // Iterate through each document in the snapshot
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        // Get the data of the document and check if the publisher matches the profileid
                        String publisher = document.getString("publisher");
                        if (publisher != null && publisher.equals(profileid)) {
                            count++;
                        }
                    }
                    // Update the TextView with the number of posts
                    posts.setText(String.valueOf(count));
                }
            }
        });
    }


    private void myFotos() {
        // Get reference to the "Posts" collection in Firestore
        CollectionReference postsRef = FirebaseFirestore.getInstance().collection("Posts");

        // Query the posts where the publisher is equal to the profileid
        Query query = postsRef.whereEqualTo("publisher", profileid);

        // Listen for changes in the query results
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null) {
                    postList.clear(); // Clear the existing list
                    // Iterate through each document in the snapshot
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        // Get the data of the document and convert it to a PostModel object
                        PostModel post = document.toObject(PostModel.class);
                        if (post != null) {
                            postList.add(post);
                        }
                    }
                    // Reverse the order of the list to display the newest posts first
                    Collections.reverse(postList);
                    // Notify the adapter about the data change
                    myFotoAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void mysaves() {
        mySaves = new ArrayList<>();

        // Get reference to the "Saves" collection in Firestore
        CollectionReference savesRef = FirebaseFirestore.getInstance().collection("Saves")
                .document(firebaseUser.getUid()).collection("Products");

        // Query all documents in the "Saves" collection under the user's ID
        savesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Add the document ID (post ID) to mySaves list
                        mySaves.add(document.getId());
                    }
                    // Once all saves are retrieved, call readSaves()
                    readSaves();
                } else {
                    // Handle errors
                    Log.e("mysaves", "Error getting saved posts", task.getException());
                }
            }
        });
    }


    private void readSaves() {
        postList_saves.clear();

        // Get reference to the "SavedPosts" collection for the current user
        CollectionReference savedPostsRef = FirebaseFirestore.getInstance()
                .collection("Saves").document(FirebaseAuth.getInstance().getUid())
                .collection("SavedPosts");

        // Query all documents in the "SavedPosts" collection
        savedPostsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get the post ID from the document
                        String postId = document.getId();
                        // Retrieve the corresponding post from the "Posts" collection
                        FirebaseFirestore.getInstance().collection("Posts").document(postId)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        // Check if the document exists and convert it to a PostModel object
                                        if (documentSnapshot.exists()) {
                                            PostModel post = documentSnapshot.toObject(PostModel.class);
                                            // Add the post to the list if it's not null
                                            if (post != null) {
                                                postList_saves.add(post);
                                                // Notify the adapter about the data change
                                                myFotoAdapter_saves.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                    }
                } else {
                    // Handle errors
                    Log.e("readSaves", "Error getting saved posts", task.getException());
                }
            }
        });
    }


}