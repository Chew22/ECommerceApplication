package com.example.ecommerceapplication.adapters;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.fragments.PostDetailFragment;
import com.example.ecommerceapplication.fragments.ProfileFragment;
import com.example.ecommerceapplication.models.NotificationModel;
import com.example.ecommerceapplication.models.PostModel;
import com.example.ecommerceapplication.models.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Adapter class for displaying notifications in a RecyclerView.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {


    private Context mContext;
    private List<NotificationModel> mNotification;

    public NotificationAdapter(Context mContext, List<NotificationModel> mNotification) {
        this.mContext = mContext;
        this.mNotification = mNotification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent,false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NotificationModel notification = mNotification.get(position);
        holder.text.setText(notification.getText());

        // Check if userId is not null before calling getUserInfo()
        if (notification.getUserId() != null) {
            getUserInfo(holder.image_profile, holder.username, notification.getUserId());

            // Set post image visibility based on notification type (post or comment)
            if (notification.isPost()) {
                holder.post_image.setVisibility(View.VISIBLE);
                getPostImage(holder.post_image, notification.getPostId());
            } else {
                holder.post_image.setVisibility(View.GONE);
            }
        } else {
            // Handle the case where userId is null
            Log.e(TAG, "User ID is null for notification at position: " + position);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isPost()){

                    // It is a post, open PostDetailFragment with the post ID stored in SharedPreferences
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS", Context.MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostId());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();

                }else {
                    // Opens a ProfileFragment
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserId());
                    editor.apply();

                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image;
        public TextView username, text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherId) {
        // Get an instance of the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the document containing user information for the specified publisher ID
        db.collection("CurrentUser").document(publisherId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists
                    if (documentSnapshot.exists()) {
                        // Convert the document snapshot to a UserModel object
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        // If UserModel object is not null, set user's profile image and username
                        if (user != null) {
                            Glide.with(mContext).load(user.getProfileImg()).into(imageView);
                            username.setText(user.getUsername());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure when fetching user document
                    Log.e(TAG, "Error getting user document", e);
                });
    }


    // Fetch post image from Firestore.
    private void getPostImage(final ImageView imageView, final String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Products").document(postId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        PostModel post = documentSnapshot.toObject(PostModel.class);
                        if (post != null) {
                            Glide.with(mContext).load(post.getProductImages()).into(imageView);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e(TAG, "Error getting post document", e);
                });
    }

}
