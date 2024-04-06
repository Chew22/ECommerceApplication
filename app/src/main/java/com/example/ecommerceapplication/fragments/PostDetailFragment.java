package com.example.ecommerceapplication.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.PostAdapter;
import com.example.ecommerceapplication.models.PostModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class PostDetailFragment extends Fragment {

    String postid;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<PostModel> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences preferences =getContext().getSharedPreferences("PREPS", Context.MODE_PRIVATE);
        postid = preferences.getString("postid", "none");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        readPost();



        return view;
    }

    private void readPost() {
        postList.clear();

        // Get reference to the specific document in the "Posts" collection in Firestore
        DocumentReference postRef = FirebaseFirestore.getInstance().collection("Posts").document(postid);

        // Get the document snapshot asynchronously
        postRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Convert the document snapshot to a PostModel object
                    PostModel post = documentSnapshot.toObject(PostModel.class);
                    // Add the post to the postList
                    postList.add(post);
                    // Notify the adapter about the data change
                    postAdapter.notifyDataSetChanged();
                } else {
                    // Handle case when the document doesn't exist
                    Log.d("readPost", "No such document");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failures
                Log.e("readPost", "Error getting document", e);
            }
        });
    }

}