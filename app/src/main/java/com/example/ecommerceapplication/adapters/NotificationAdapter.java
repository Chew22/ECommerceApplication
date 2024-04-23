package com.example.ecommerceapplication.adapters;

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
import com.example.ecommerceapplication.models.SellerModel;
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
    // Declare a TAG for logging
    private static final String TAG = "NotificationAdapter";
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final NotificationModel notification = mNotification.get(position);

        // Log position and notification type
        Log.d(TAG, "onBindViewHolder: Position - " + position + ", Notification Type - " + (notification.isIspost() ? "Post" : "Comment"));

        // Check if userId is null
        if (notification.getUserid() == null) {
            // Log and handle the null userId
            Log.e(TAG, "User ID is null for notification at position: " + position);

            // Set default values to prevent crashes
            holder.username.setText("Unknown");
            holder.image_profile.setImageResource(R.drawable.placeholder); // Example default image
            holder.post_image.setVisibility(View.GONE); // Hide post image if userId is null
            return; // No further action required for a null userId
        }

        // If userId is not null, proceed as usual
        getUserInfo(holder.image_profile, holder.username, notification.getUserid());

        if (notification.isIspost()) {
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image, notification.getPostid());
        } else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification.isIspost()) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS", Context.MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostid());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new PostDetailFragment())
                            .commit();
                } else {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREPS", Context.MODE_PRIVATE).edit();
                    editor.putString("userid", notification.getUserid());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment())
                            .commit();
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
        db.collection("seller").document(publisherId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists
                    if (documentSnapshot.exists()) {
                        // Convert the document snapshot to a UserModel object
                        SellerModel user = documentSnapshot.toObject(SellerModel.class);
                        // If UserModel object is not null, set user's profile image and username
                        if (user != null) {
                            Glide.with(mContext).load(user.getImagePath()).into(imageView);
                            username.setText(user.getShopName());
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
