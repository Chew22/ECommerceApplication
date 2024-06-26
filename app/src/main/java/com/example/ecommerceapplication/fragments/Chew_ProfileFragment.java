package com.example.ecommerceapplication.fragments;

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
import android.widget.LinearLayout;
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
import com.example.ecommerceapplication.activities.Chew_ChatActivity;
import com.example.ecommerceapplication.activities.EditProfileActivity;
import com.example.ecommerceapplication.activities.FollowersActivity;
import com.example.ecommerceapplication.activities.OptionsActivity;
import com.example.ecommerceapplication.activities.OrderListActivity;
import com.example.ecommerceapplication.adapters.MyFotoAdapter;
import com.example.ecommerceapplication.adapters.Chew_PostAdapter;
import com.example.ecommerceapplication.models.PostModel;
import com.example.ecommerceapplication.models.SellerModel;
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


public class Chew_ProfileFragment extends Fragment {

    private static final String TAG = "Chew_ProfileFragment";

    ImageView image_profile, options;
    TextView posts, followers, following, bio, username, name;
    Button edit_profile, order_status, followButton;
    Button messageButton;
    LinearLayout followersLayout;

    private List<String> mySaves;

    RecyclerView recyclerView_saves;
    MyFotoAdapter myFotoAdapter_saves;
    List<PostModel> postList_saves;

    RecyclerView recyclerView_products;
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

        View view = inflater.inflate(R.layout.chew_fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if an intent was received from Chew_PostAdapter
        if (getArguments() != null) {
            profileid = getArguments().getString("sellerId");

        } else {

            SharedPreferences prefs = getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
            profileid = prefs.getString("profileid", "none");

        }

        Chew_PostAdapter adapter = new Chew_PostAdapter(getContext(), postList);


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        image_profile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        followButton = view.findViewById(R.id.follow_button);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        name = view.findViewById(R.id.name);
        edit_profile = view.findViewById(R.id.edit_profile);
        my_fotos = view.findViewById(R.id.my_fotos);
        saved_fotos = view.findViewById(R.id.saved_fotos);
        order_status = view.findViewById(R.id.order_status);
        messageButton = view.findViewById(R.id.message_button);
        followersLayout = view.findViewById(R.id.followersLayout);

        recyclerView_products = view.findViewById(R.id.recycler_view);
        recyclerView_products.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView_products.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        myFotoAdapter = new MyFotoAdapter(getContext(), postList);
        recyclerView_products.setAdapter(myFotoAdapter);

        recyclerView_saves = view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves = new GridLayoutManager(getContext(), 3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves = new ArrayList<>();
        myFotoAdapter_saves = new MyFotoAdapter(getContext(), postList_saves);
        recyclerView_saves.setAdapter(myFotoAdapter_saves);

        userInfo();
        getFollowers();
        getNrPosts();
        loadSavedPosts();
        myFotos();


        // Check if the profile belongs to the current user
        if (profileid.equals(firebaseUser.getUid())) {
            // Profile belongs to the current user
            edit_profile.setText("Edit Profile");
            order_status.setVisibility(View.VISIBLE);
            saved_fotos.setVisibility(View.VISIBLE);
            followButton.setVisibility(View.GONE);
            messageButton.setVisibility(View.GONE);
            followersLayout.setVisibility(View.GONE);
            my_fotos.setVisibility(View.GONE);
            Log.d(TAG, "Profile belongs to the current user. Setting text to 'Edit Profile' and making order_status button visible.");
        } else {
            checkFollow();
            order_status.setVisibility(View.GONE);
            saved_fotos.setVisibility(View.GONE);
            Log.d(TAG, "Profile does not belong to the current user. Hiding order_status button and saved_fotos button.");
        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the text of the follow button to determine the action
                String buttonText = followButton.getText().toString();
                if (buttonText.equals(getString(R.string.follow_button_text))) {
                    // User wants to follow
                    followUser();
                } else if (buttonText.equals(getString(R.string.following_button_text))) {
                    // User wants to unfollow
                    unfollowUser();
                }
            }
        });


        order_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OrderListActivity.class);
                startActivity(intent);
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getContext(), EditProfileActivity.class));

            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), OptionsActivity.class);
                startActivity(intent);
            }
        });

        my_fotos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                recyclerView_products.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });

        saved_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView_products.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });


        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "Followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "Following");
                startActivity(intent);
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the ChatActivity with the seller ID
                Intent intent = new Intent(getContext(), Chew_ChatActivity.class);
                intent.putExtra("sellerId", profileid); // Assuming profileid is the seller's ID
                startActivity(intent);
            }

        });

        // Inflate the layout for this fragment
        return view;
    }

    private void followUser() {
        // Get reference to the "Follow" collection in Firestore
        DocumentReference followingRef = firestore.collection("Follow").document(firebaseUser.getUid()).collection("following").document(profileid);
        DocumentReference followerRef = firestore.collection("Follow").document(profileid).collection("followers").document(firebaseUser.getUid());

        // Create a HashMap to store follow status
        HashMap<String, Object> followData = new HashMap<>();
        followData.put("followed", true); // You can add more properties if needed

        // Set the follow status in Firestore
        followingRef.set(followData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update UI and add notification
                        followButton.setText(R.string.following_button_text);
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

        followerRef.set(followData);
    }

    private void unfollowUser() {
        // Get reference to the "Follow" collection in Firestore
        DocumentReference followingRef = firestore.collection("Follow").document(firebaseUser.getUid()).collection("following").document(profileid);
        DocumentReference followerRef = firestore.collection("Follow").document(profileid).collection("followers").document(firebaseUser.getUid());

        // Delete the follow status from Firestore
        followingRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Update UI
                        followButton.setText(R.string.follow_button_text);
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


    private void addNotification() {
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

        // Check if the profileid is the current user's ID
        if (profileid.equals(firebaseUser.getUid())) {
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
        } else {
            // Fetch seller's information
            DocumentReference sellerRef = db.collection("seller").document(profileid);

            sellerRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (getContext() == null) {
                        return;
                    }

                    if (documentSnapshot.exists()) {
                        SellerModel seller = documentSnapshot.toObject(SellerModel.class);

                        // Update UI with seller information
                        Glide.with(getContext()).load(seller.getImagePath()).into(image_profile);
                        username.setText(seller.getShopName());
                        name.setText(seller.getShopName());
                        bio.setText(""); // Seller might not have bio

                        // Hide edit_profile button for seller profile
                        edit_profile.setVisibility(View.GONE);
                    } else {
                        // Handle case where document does not exist
                        Toast.makeText(getContext(), "Seller not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle error
                    Toast.makeText(getContext(), "Failed to retrieve seller information: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkFollow() {
        // Get reference to the "Follow" collection in Firestore
        DocumentReference followRef = FirebaseFirestore.getInstance().collection("Follow")
                .document(firebaseUser.getUid()).collection("following").document(profileid);

        // Listen for changes in the following status
        followRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null && snapshot.exists()) {
                    // User is already following the profile
                    edit_profile.setText(R.string.following_button_text);
                } else {
                    // User is not following the profile
                    edit_profile.setText(R.string.follow_button_text);
                }
            }
        });
    }

    private void getFollowers() {
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
        CollectionReference postsRef = FirebaseFirestore.getInstance().collection("Products");

        // Listen for changes in the "Posts" collection
        postsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                if (snapshot != null) {
                    int count = 0;
                    // Iterate through each document in the snapshot
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        // Get the data of the document and check if the publisher matches the profileid
                        String publisher = document.getString("sellerID");
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
        CollectionReference postsRef = FirebaseFirestore.getInstance().collection("Products");

        // Query the posts where the publisher is equal to the profileid
        Query query = postsRef.whereEqualTo("sellerID", profileid);

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



    private void loadSavedPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference savesRef = db
                .collection("Saves")
                .document(firebaseUser.getUid())
                .collection("SavedPosts");

        savesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<String> documentIds = new ArrayList<>();

                    Log.i(TAG, "Retrieving saved post IDs.");

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String documentId = document.getId();
                        documentIds.add(documentId);
                        Log.d(TAG, "Saved post ID: " + documentId);
                    }

                    Log.i(TAG, "Retrieved " + documentIds.size() + " saved posts.");

                    // Fetch product details using document IDs
                    fetchProductDetails(documentIds);
                } else {
                    Log.e(TAG, "Error retrieving saved posts", task.getException());
                }
            }
        });
    }

    private void fetchProductDetails(List<String> documentIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final List<PostModel> savedPosts = new ArrayList<>();
        final int totalDocuments = documentIds.size();
        final int[] fetchedCount = {0}; // To track how many details have been fetched

        for (String documentId : documentIds) {
            db.collection("Products").whereEqualTo("productId", documentId).get()
                    .addOnSuccessListener(querySnapshot -> {
                        fetchedCount[0]++; // Increment counter
                        if (!querySnapshot.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot) {
                                PostModel post = documentSnapshot.toObject(PostModel.class);
                                if (post != null) {
                                    savedPosts.add(post);
                                }
                            }
                        }

                        if (fetchedCount[0] == totalDocuments) {
                            updateAdapterWithSavedPosts(savedPosts); // Only update when all fetched
                        }
                    })
                    .addOnFailureListener(e -> {
                        fetchedCount[0]++;
                        if (fetchedCount[0] == totalDocuments) {
                            updateAdapterWithSavedPosts(savedPosts); // Even if some fail, still update
                        }
                    });
        }
    }
    private void updateAdapterWithSavedPosts(List<PostModel> savedPosts) {
        myFotoAdapter_saves.setPosts(savedPosts);
        myFotoAdapter_saves.notifyDataSetChanged();
    }

}