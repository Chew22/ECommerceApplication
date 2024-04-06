package com.example.ecommerceapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.activities.MainActivity;
import com.example.ecommerceapplication.fragments.ProfileFragment;
import com.example.ecommerceapplication.models.CommentModel;
import com.example.ecommerceapplication.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Adapter class to bind comments to a RecyclerView.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mcontext;
    private List<CommentModel> mComment;
    private FirebaseUser firebaseUser;

    public CommentAdapter(Context mcontext, List<CommentModel> mComment) {
        this.mcontext = mcontext;
        this.mComment = mComment;
    }

    // Method to create ViewHolder instances
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mcontext).inflate(R.layout.comment_item, viewGroup, false);
        return new CommentAdapter.ViewHolder(view);
    }

    // Method to bind data to each ViewHolder.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final CommentModel comment = mComment.get(i);

        viewHolder.comment.setText(comment.getComment());
        getUserInfo(viewHolder.image_profile, viewHolder.username, comment.getPublisherid());

        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mcontext, MainActivity.class);
                intent.putExtra("publisherid", comment.getPublisherid());
                mcontext.startActivity(intent);
            }
        });

        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext, ProfileFragment.class);
                intent.putExtra("publisherid", comment.getPublisherid());
                mcontext.startActivity(intent);
            }
        });


    }

    // Method to return the number of comments
    @Override
    public int getItemCount() {
        return mComment.size();
    }

    // ViewHolder class to hold references to views.
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image_profile;
        TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid) {
        // Get reference to the document in the "Users" collection with the specified user ID
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("CurrentUser").document(publisherid);

        // Retrieve user information from Firestore
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserModel user = documentSnapshot.toObject(UserModel.class);
                if (user != null) {
                    // Load user profile image using Glide
                    Glide.with(mcontext).load(user.getProfileImg()).into(imageView);
                    // Set username text
                    username.setText(user.getUsername());
                }
            } else {
                // Handle case where user document doesn't exist
                Log.e("getUserInfo", "User document does not exist");
            }
        }).addOnFailureListener(e -> {
            // Handle errors
            Log.e("getUserInfo", "Error retrieving user information: " + e.getMessage());
        });
    }



}
