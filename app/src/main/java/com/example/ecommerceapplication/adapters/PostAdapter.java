/*
This adapter is responsible for populating the RecyclerView with popular products in the home screen.
*/

package com.example.ecommerceapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.activities.CommentsActivity;
import com.example.ecommerceapplication.activities.DetailedActivity;
import com.example.ecommerceapplication.activities.FollowersActivity;
import com.example.ecommerceapplication.fragments.ProfileFragment;
import com.example.ecommerceapplication.models.PostModel;
import com.example.ecommerceapplication.models.SellerModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public static final String TAG = "PostAdapter";

    private Context context;
    private List<PostModel> list;
    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;


    public PostAdapter(Context context, List<PostModel> list) {
        this.context = context;
        this.list = list;
        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    // Creating view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    // Binding data to the views
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PostModel post = list.get(position);

        // Check if the current user is following the seller
        checkFollowStatus(holder.post_follow_button, post.getSellerID());

        // Set up RecyclerView for image slider
        if (post.getProductImages() != null && !post.getProductImages().isEmpty()) {
            List<SlideModel> imageList = new ArrayList<>();
            for (String imageUrl : post.getProductImages()) {
                imageList.add(new SlideModel(imageUrl, "", ScaleTypes.CENTER_CROP));
            }

            holder.imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP);
        }

        holder.product_name.setText(post.getProductName());
        holder.description.setText(post.getProductDescription());
        holder.price.setText(String.format("%.2f", post.getPrice()));

        if (post.getProductDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getProductDescription());
        }

        publisherInfo(holder.image_profile, holder.shopName, post.getSellerID());
        isLiked(post.getProductId(), holder.like);
        nrLikes(holder.likes, post.getProductId());
        getComments(post.getProductId(), holder.comments);
        isSaved(post.getProductId(), holder.save);

        holder.post_follow_button.setOnClickListener(view -> {
            Button button = (Button) view;
            if ("Follow".equals(button.getText().toString())) {
                followSeller(post.getSellerID(), button);
            } else {
                unfollowSeller(post.getSellerID(), button);
            }
        });


        holder.image_profile.setOnClickListener(v -> {
            // Navigate to the seller profile
            ProfileFragment profileFragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putString("sellerId", post.getSellerID());
            profileFragment.setArguments(bundle);

            // Use the context to access getSupportFragmentManager
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, profileFragment)
                    .addToBackStack(null)
                    .commit();
        });



        holder.imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int i) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detailed", post);
                context.startActivity(intent);
            }

            @Override
            public void doubleClick(int i) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailedActivity.class);
                intent.putExtra("detailed", post);
                context.startActivity(intent);

            }
        });

        holder.save.setOnClickListener(v -> {
            ImageView saveIcon = (ImageView) v;
            if (saveIcon.getTag().equals("save")) {
                savePost(post.getProductId(), saveIcon);
            } else {
                unsavePost(post.getProductId(), saveIcon);
            }
        });

        holder.like.setOnClickListener(v -> {
            ImageView likeIcon = (ImageView) v;
            if (likeIcon.getTag().equals("like")) {
                likePost(post.getProductId(), likeIcon, holder.likes);
                addNotifications(post.getSellerID(), post.getProductId());
            } else {
                unlikePost(post.getProductId(), likeIcon, holder.likes);
            }
        });


        holder.comment.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("postid", post.getProductId());
            intent.putExtra("publisherid", post.getSellerID());
            context.startActivity(intent);
        });

        holder.comments.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentsActivity.class);
            intent.putExtra("postid", post.getProductId());
            intent.putExtra("publisherid", post.getSellerID());
            context.startActivity(intent);
        });

        holder.likes.setOnClickListener(v -> {
            Intent intent = new Intent(context, FollowersActivity.class);
            intent.putExtra("id", post.getProductId());
            intent.putExtra("title", "Likes");
            context.startActivity(intent);
        });
    }

    // Getting item count
    @Override
    public int getItemCount() {
        return list.size();
    }

    // View holder class
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image_profile, like, comment, save;
        TextView shopName, likes, product_name, description, comments, price;
        ImageSlider imageSlider;
        Button post_follow_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            shopName = itemView.findViewById(R.id.username);
            likes = itemView.findViewById(R.id.likes);
            product_name = itemView.findViewById(R.id.product_name);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            price = itemView.findViewById(R.id.all_price);
            imageSlider = itemView.findViewById(R.id.imageSlider);
            post_follow_button = itemView.findViewById(R.id.post_follow_button);

        }
    }

    // Check if the user is following the seller
    private void checkFollowStatus(Button followButton, String sellerId) {
        DocumentReference followRef = db.collection("Follow")
                .document(firebaseUser.getUid()).collection("following").document(sellerId);

        followRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                followButton.setText("Following");
            } else {
                followButton.setText("Follow");
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Error checking follow status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Follow a seller
    private void followSeller(String sellerId, Button followButton) {
        DocumentReference followingRef = db.collection("Follow")
                .document(firebaseUser.getUid()).collection("following").document(sellerId);

        DocumentReference followerRef = db.collection("Follow")
                .document(sellerId).collection("followers").document(firebaseUser.getUid());

        HashMap<String, Object> data = new HashMap<>();
        data.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());

        followingRef.set(data)
                .addOnSuccessListener(aVoid -> {
                    followButton.setText("Following");
                    addFollowNotification(sellerId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to follow: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        followerRef.set(data);
    }

    // Unfollow a seller
    private void unfollowSeller(String sellerId, Button followButton) {
        DocumentReference followingRef = db.collection("Follow")
                .document(firebaseUser.getUid()).collection("following").document(sellerId);

        DocumentReference followerRef = db.collection("Follow")
                .document(sellerId).collection("followers").document(firebaseUser.getUid());

        followingRef.delete()
                .addOnSuccessListener(aVoid -> {
                    followButton.setText("Follow");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to unfollow: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        followerRef.delete();
    }

    // Add follow notification
    private void addFollowNotification(String sellerId) {
        HashMap<String, Object> notificationData = new HashMap<>();
        notificationData.put("userid", firebaseUser.getUid());
        notificationData.put("text", "started following you");

        db.collection("Notifications").document(sellerId).collection("Notifications")
                .add(notificationData);
    }
    // Retrieve and display publisher information
    private void publisherInfo(ImageView image_profile, TextView username, String userid) {
        db.collection("seller").document(userid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        SellerModel user = documentSnapshot.toObject(SellerModel.class);
                        Glide.with(context).load(user.getImagePath()).into(image_profile);
                        username.setText(user.getShopName());

                    }
                }).addOnFailureListener(e -> Log.e("PostAdapter", "Error getting user info: " + e.getMessage()));
    }

    // Check if the post is liked by the current user
    private void isLiked(String postid, final ImageView imageView) {
        db.collection("Likes").document(postid).collection("Likes").document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        imageView.setImageResource(R.drawable.ic_liked);
                        imageView.setTag("liked");
                    } else {
                        imageView.setImageResource(R.drawable.ic_like);
                        imageView.setTag("like");
                    }
                }).addOnFailureListener(e -> Log.e("PostAdapter", "Error checking like status: " + e.getMessage()));
    }

    /**
     * Adds a notification to the Firestore database.
     *
     * @param userId The ID of the user who will receive the notification.
     * @param postId The ID of the post related to the notification.
     */
    private void addNotifications(String userId, String postId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("Notifications").document(userId).collection("Notifications");

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", firebaseUser.getUid()); // ID of the user who triggered the notification
        notificationData.put("text", "liked your post");
        notificationData.put("postId", postId);
        notificationData.put("isPost", true); // Indicates if the notification is related to a post

        notificationsRef.add(notificationData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Notification added successfully");
                    // Handle success if needed
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding notification", e);
                    // Handle failure
                });
    }


    // Retrieve and display the number of likes for a post
    private void nrLikes(final TextView likes, String postid) {
        db.collection("Likes").document(postid).collection("Likes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    likes.setText(queryDocumentSnapshots.size() + " likes");
                }).addOnFailureListener(e -> Log.e("PostAdapter", "Error getting number of likes: " + e.getMessage()));
    }

    // Retrieve the number of comments for a post
    private void getComments(String postid, TextView comments) {
        db.collection("Comments").document(postid).collection("Comments")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    comments.setText("View All " + queryDocumentSnapshots.size() + " Comments");
                }).addOnFailureListener(e -> Log.e("PostAdapter", "Error getting number of comments: " + e.getMessage()));
    }

    // Check if the post is saved by the current user
    private void isSaved(String postid, ImageView imageView) {
        db.collection("Saves").document(firebaseUser.getUid()).collection("SavedPosts").document(postid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // If the post is saved, set the icon to the saved icon
                        imageView.setImageResource(R.drawable.ic_saved);
                        imageView.setTag("saved");
                    } else {
                        // If the post is not saved, set the icon to the unsaved icon
                        imageView.setImageResource(R.drawable.ic_save);
                        imageView.setTag("save");
                    }
                }).addOnFailureListener(e -> Log.e("PostAdapter", "Error checking save status: " + e.getMessage()));
    }

    // Save a post
    private void savePost(String postid, ImageView saveIcon) {
        Map<String, Object> saveData = new HashMap<>();
        saveData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("Saves").document(firebaseUser.getUid()).collection("SavedPosts").document(postid)
                .set(saveData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PostAdapter", "Post saved successfully");
                    // Update the icon to the saved icon
                    saveIcon.setImageResource(R.drawable.ic_saved);
                    saveIcon.setTag("saved");
                })
                .addOnFailureListener(e -> Log.e("PostAdapter", "Error saving post: " + e.getMessage()));
    }

    // Unsave a post
    private void unsavePost(String postid, ImageView saveIcon) {
        db.collection("Saves").document(firebaseUser.getUid()).collection("SavedPosts").document(postid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("PostAdapter", "Post unsaved successfully");
                    // Update the icon to the unsaved icon
                    saveIcon.setImageResource(R.drawable.ic_save);
                    saveIcon.setTag("save");
                })
                .addOnFailureListener(e -> Log.e("PostAdapter", "Error unsaving post: " + e.getMessage()));
    }

    // Like a post
    private void likePost(String postid, ImageView likeIcon, TextView likesText) {
        Map<String, Object> likeData = new HashMap<>();
        likeData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("Likes").document(postid).collection("Likes").document(firebaseUser.getUid())
                .set(likeData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PostAdapter", "Post liked successfully");
                    // Update the icon to the liked icon
                    likeIcon.setImageResource(R.drawable.ic_liked);
                    likeIcon.setTag("liked");

                    // Update the number of likes
                    nrLikes(likesText, postid);
                })
                .addOnFailureListener(e -> Log.e("PostAdapter", "Error liking post: " + e.getMessage()));
    }

    // Unlike a post
    private void unlikePost(String postid, ImageView likeIcon, TextView likesText) {
        db.collection("Likes").document(postid).collection("Likes").document(firebaseUser.getUid())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("PostAdapter", "Post unliked successfully");
                    // Update the icon to the unliked icon
                    likeIcon.setImageResource(R.drawable.ic_like);
                    likeIcon.setTag("like");

                    // Update the number of likes
                    nrLikes(likesText, postid);
                })
                .addOnFailureListener(e -> Log.e("PostAdapter", "Error unliking post: " + e.getMessage()));
    }


}
