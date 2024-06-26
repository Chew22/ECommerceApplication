package com.example.ecommerceapplication.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.activities.PostActivity;
import com.example.ecommerceapplication.adapters.CategoryAdapter;
import com.example.ecommerceapplication.adapters.Chew_PostAdapter;
import com.example.ecommerceapplication.models.CategoryModel;
import com.example.ecommerceapplication.models.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Chew_SearchFragment extends Fragment {

    TextView catShowAll;
    RecyclerView catRecyclerview;
    TextView bestMatchTextView;
    TextView priceTextView;

    // Category recyclerview
    CategoryAdapter categoryAdapter;
    List<CategoryModel> categoryModelList;

    private RecyclerView recyclerView;
    private Chew_PostAdapter Chew_PostAdapter;
    private List<PostModel> mPosts;

    EditText search_bar;

    ProgressBar progressBar;
    ProgressDialog progressDialog;
    LinearLayout linearLayout;

    // FireStore
    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chew_fragment_search, container, false);
        // Inflate the layout for this fragment

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        catRecyclerview = view.findViewById(R.id.rec_category);
        catShowAll = view.findViewById(R.id.category_see_all);

        search_bar = view.findViewById(R.id.search_bar);

        bestMatchTextView = view.findViewById(R.id.bestMatch);
        priceTextView = view.findViewById(R.id.price);

        mPosts = new ArrayList<>();
        Chew_PostAdapter = new Chew_PostAdapter(getContext(), mPosts);
        recyclerView.setAdapter(Chew_PostAdapter);

        // Initializing views and progress dialog
        progressDialog = new ProgressDialog(getActivity());

        db = FirebaseFirestore.getInstance();

        readposts();

        priceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the text color to indicate sorting is applied
                priceTextView.setTextColor(getResources().getColor(R.color.theme_orange));
                bestMatchTextView.setTextColor(getResources().getColor(android.R.color.black));

                // Call method to sort posts by price
                sortPostsByPrice();
            }
        });

        bestMatchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the text color to indicate sorting is applied
                priceTextView.setTextColor(getResources().getColor(R.color.black));
                bestMatchTextView.setTextColor(getResources().getColor(R.color.theme_orange));


                readposts();
            }
        });

        catShowAll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostActivity.class);
                startActivity(intent);
            }
        });

        // Fetching categories from Firestore and populating Category RecyclerView
        catRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        categoryModelList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), categoryModelList);
        catRecyclerview.setAdapter(categoryAdapter);
        db.collection("Category")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                CategoryModel categoryModel = document.toObject(CategoryModel.class);
                                categoryModelList.add(categoryModel);
                                categoryAdapter.notifyDataSetChanged();
                                progressDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(getActivity(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        catRecyclerview.addOnItemTouchListener(new RecyclerTouchListener(getContext(), catRecyclerview, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Get the clicked category
                String categoryName = categoryModelList.get(position).getName();

                // Log the selected category name
                Log.d("Chew_SearchFragment", "Selected Category: " + categoryName);

                // Call method to retrieve posts by category
                readPostsByCategory(categoryName);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the search query is empty
                if (s.toString().isEmpty()) {
                    // Clear the search results
                    mPosts.clear();
                    // Notify the adapter about the data change
                    Chew_PostAdapter.notifyDataSetChanged();
                } else {
                    // Perform search with the non-empty query
                    searchposts(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Add OnTouchListener to clear search bar text when the user taps on the drawable
        search_bar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                priceTextView.setTextColor(getResources().getColor(R.color.black));
                bestMatchTextView.setTextColor(getResources().getColor(R.color.theme_orange));


                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (search_bar.getRight() - search_bar.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // Clear the text in the EditText
                        search_bar.setText("");
                        return true;
                    }
                }
                return false;
            }
        });


        return view;
    }

    private void sortPostsByPrice() {
        // Clear the existing post list
        mPosts.clear();

        // Construct a query to retrieve posts sorted by price
        FirebaseFirestore.getInstance().collection("Products")
                .orderBy("price")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert the document snapshot to a PostModel object
                                PostModel post = document.toObject(PostModel.class);
                                // Add the post to the post list
                                mPosts.add(post);
                            }
                            // Notify the adapter about the data change
                            Chew_PostAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("Chew_SearchFragment", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void searchposts(String s) {
        // Check if the search string is empty
        if (s.isEmpty()) {
            // Clear the existing post list
            mPosts.clear();
            // Notify the adapter about the data change
            Chew_PostAdapter.notifyDataSetChanged();
            return; // Exit the method
        }

        // Construct a query to search for postnames in Firestore
        FirebaseFirestore.getInstance().collection("Products")
                .orderBy("productName")
                .startAt(s)
                .endAt(s + "\uf8ff")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Chew_SearchFragment", "Error getting posts: ", e);
                            return;
                        }

                        Log.d("Chew_SearchFragment", "searchPosts onEvent called");

                        // Clear the existing post list
                        mPosts.clear();

                        // Iterate through each document snapshot in the query result
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Convert the document snapshot to a PostModel object
                            PostModel post = document.toObject(PostModel.class);
                            // Add the post to the post list
                            mPosts.add(post);
                        }

                        // Notify the adapter about the data change
                        Chew_PostAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void readPostsByCategory(String categoryName) {
        CollectionReference postsRef = FirebaseFirestore.getInstance().collection("Products");

        postsRef.whereEqualTo("productCategory", categoryName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        Log.d("Chew_SearchFragment", "Querying posts for category: " + categoryName);

                        if (e != null) {
                            Log.e("Chew_SearchFragment", "readPostsByCategory onEvent: Error querying posts", e);
                            return;
                        }

                        mPosts.clear();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                PostModel post = document.toObject(PostModel.class);
                                long timestamp = document.getLong("timestamp");
                                post.setTimestamp(timestamp);
                                mPosts.add(post);
                            }
                            Chew_PostAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }



    private void readposts(){
        // Get reference to the "posts" collection in Firestore
        CollectionReference postsRef = FirebaseFirestore.getInstance().collection("Products");

        // Add a snapshot listener to listen for changes in the data
        postsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                // Check for errors
                if (e != null) {
                    Log.e("Chew_SearchFragment", "readposts onEvent: Error querying posts", e);
                    return;
                }

                // Clear the existing post list
                mPosts.clear();

                // Check if the query snapshot is not null and contains documents
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        // Convert the document snapshot to a PostModel object
                        PostModel post = document.toObject(PostModel.class);
                        // Add the post to the post list
                        mPosts.add(post);
                    }
                    // Notify the adapter about the data change
                    Chew_PostAdapter.notifyDataSetChanged();
                }
            }
        });
    }

}