package com.example.ecommerceapplication.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.activities.PostActivity;
import com.example.ecommerceapplication.adapters.Chew_PostAdapter;
import com.example.ecommerceapplication.adapters.NewProductsAdapter;
import com.example.ecommerceapplication.adapters.StoryAdapter;
import com.example.ecommerceapplication.models.NewProductsModel;
import com.example.ecommerceapplication.models.PostModel;
import com.example.ecommerceapplication.models.StoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
   This fragment represents the home screen of the application, displaying various categories,
   new products and user stories.
*/
public class Chew_HomeFragment extends Fragment {

    // Hide logout icon
    private boolean isHomeFragmentActive = true;

    TextView newProductShowAll;
    LinearLayout linearLayout;
    ProgressDialog progressDialog;
    RecyclerView newProductRecyclerview;

    // New Product Recyclerview
    NewProductsAdapter newProductsAdapter;
    List<NewProductsModel> newProductsModelList;

    // Post Recyclerview
    RecyclerView postRecyclerview;
    Chew_PostAdapter chewPostAdapter;
    List<PostModel> postLists;
    List<String> followingList;

    // Story Recyclerview
    RecyclerView recyclerView_story;
    StoryAdapter storyAdapter;
    List<StoryModel> storyList;

    ProgressBar progressBar;

    // FireStore
    FirebaseFirestore db;

    public Chew_HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.chew_fragment_home, container, false);

        // Initializing views and progress dialog
        progressBar = view.findViewById(R.id.progress_circular);
        progressDialog = new ProgressDialog(getActivity());

        newProductRecyclerview = view.findViewById(R.id.new_product_rec);
        postRecyclerview = view.findViewById(R.id.post_recycle_view);

        newProductShowAll = view.findViewById(R.id.newProducts_see_all);

//        // Initialization for story
//        recyclerView_story = view.findViewById(R.id.recycler_view_story);
//        recyclerView_story.setHasFixedSize(true);
//
//        recyclerView_story.setLayoutManager(linearLayoutManager);
//        storyList = new ArrayList<>();
//        storyAdapter = new StoryAdapter(getContext(), storyList);
//        recyclerView_story.setAdapter(storyAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);

        db = FirebaseFirestore.getInstance();

        linearLayout = view.findViewById(R.id.home_layout);
        linearLayout.setVisibility(View.GONE);

        // Initializing image slider
        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        List<SlideModel> slideModels = new ArrayList<>();
        // Adding slides to the image slider
        slideModels.add(new SlideModel(R.drawable.banner1,"Ben's Cookies On Sales", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner2,"Discount On Homebake Cookies", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner3,"Discount On Cupcakes", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner4,"Labour Day Discount", ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.banner5,"Big Sales", ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels);

        progressDialog.setTitle("Welcome To ShopLoca ECommerce Application");
        progressDialog.setMessage("please wait.....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // Setting up OnClickListener for see all buttons
        newProductShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostActivity.class);
                intent.putExtra("isNewProducts", true);
                startActivity(intent);
            }
        });



        // Fetching new products from Firestore and populating New Products RecyclerView
        newProductRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        newProductsModelList = new ArrayList<>();
        newProductsAdapter = new NewProductsAdapter(getContext(),newProductsModelList);
        newProductRecyclerview.setAdapter(newProductsAdapter);
        db.collection("NewProduct")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                NewProductsModel newProductsModel = document.toObject(NewProductsModel.class);
                                newProductsModelList.add(newProductsModel);
                                newProductsAdapter.notifyDataSetChanged();
                                linearLayout.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Initializing Post RecyclerView and fetching popular products from Firestore
        postRecyclerview.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        postLists = new ArrayList<>();
        chewPostAdapter = new Chew_PostAdapter(getContext(), postLists);
        postRecyclerview.setAdapter(chewPostAdapter);
        db.collection("Products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PostModel postModel = document.toObject(PostModel.class);
                                postLists.add(postModel);
                                chewPostAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // Fetching following list from Realtime Database and reading posts
        checkFollowing();
        return view;
    }

    /**
     * Retrieves the list of users that the current user is following
     * from the Firebase Realtime Database and invokes the method to
     * read posts once the following list is obtained.
     */
    private void checkFollowing(){
        // Initialize the list to store user IDs that the current user is following
        followingList = new ArrayList<>();
        // Obtain a reference to the "following" collection in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference followingRef = db.collection("Follow").document(currentUserUid).collection("Following");
        // Add a snapshot listener to retrieve the list of users the current user is following
        followingRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    Log.e("checkFollowing", "Error retrieving following list", e);
                    return;
                }
                // Clear the existing list before populating it with new data
                followingList.clear();
                if (querySnapshot != null) {
                    // Iterate through each document snapshot to extract user IDs and add them to the list
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()){
                        followingList.add(snapshot.getId());
                    }
                    // Once the following list is obtained, invoke the method to read posts and stories
                    readPosts();
//                    readStory();
                }
            }
        });
    }


    // Method to retrieve and display posts from the database
    private void readPosts(){
        // Reference to the "Posts" node in the Firebase Realtime Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");
        // Adding a ValueEventListener to fetch data from the "Posts" node
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clearing the postLists to avoid duplication
                postLists.clear();
                // Iterating through each snapshot in the "Posts" node
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    // Retrieving PostModel object from the snapshot
                    PostModel post = snapshot.getValue(PostModel.class);
                    // Filtering posts based on the followingList
                    for (String id : followingList){
                        if (post.getProductName().equals(id)){
                            postLists.add(post);
                        }
                    }
                }
                // Notifying the adapter about the data change
                chewPostAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Database Error: " + error.getMessage());
            }
        });
    }


    // Method to retrieve and display user stories from the database
    private void readStory(){
        // Reference to the "Story" collection in Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Clear the existing storyList to avoid duplication
//        storyList.clear();
        // Adding a snapshot listener to fetch data from the "Story" collection
        db.collection("Story").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Handle error
                    Log.e("readStory", "Error retrieving stories", e);
                    return;
                }
                if (querySnapshot != null) {
                    // Get the current time
                    long timeCurrent = System.currentTimeMillis();

                    // Add an empty story for the current user
                    storyList.add(new StoryModel("", new Date(0), new Date(0), "", FirebaseAuth.getInstance().getCurrentUser().getUid()));

                    // Iterate through each document snapshot in the "Story" collection
                    for (DocumentSnapshot snapshot : querySnapshot.getDocuments()) {

                        // Retrieve the StoryModel object from the snapshot
                        StoryModel story = snapshot.toObject(StoryModel.class);
                        if (story != null) {

                            // Check if the current time is within the story duration
                            if (timeCurrent > story.getTimestartMillis() && timeCurrent < story.getTimeendMillis()) {

                                // Increment the story count for the current user
                                storyList.add(story);
                            }
                        }
                    }
                    // Notify the adapter about the data change
                    storyAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem logoutItem = menu.findItem(R.id.menu_logout);
        if (isHomeFragmentActive) {
            logoutItem.setVisible(false); // Hide logout icon in home fragment
        } else {
            logoutItem.setVisible(true); // Show logout icon in other fragments
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

}






























