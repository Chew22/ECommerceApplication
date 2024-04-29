package com.example.ecommerceapplication.adapters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.activities.MainActivity;
import com.example.ecommerceapplication.fragments.Chew_ProfileFragment;
import com.example.ecommerceapplication.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter class to populate a RecyclerView with user data.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    /**
     * Constructor to initialize the UserAdapter.
     * @param mcontext The context of the activity or fragment.
     * @param mUsers The list of UserModel objects containing user data.
     * @param isfragment Indicates whether the adapter is used in a fragment.
     */
    private Context mcontext;
    private List<UserModel> mUsers;
    private boolean isfragment;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mcontext, List<UserModel> mUsers, boolean isfragment) {
        this.mcontext = mcontext;
        this.mUsers = mUsers;
        this.isfragment = isfragment;
    }

    public UserAdapter(Context mcontext, List<UserModel> mUsers) {
        this.mcontext = mcontext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final UserModel user = mUsers.get(position);

        holder.btn_follow.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getUsername()); // Can replace with other attribute
        Glide.with(mcontext).load(user.getProfileImg()).into(holder.image_profile);
        isFollowing(user.getId(), holder.btn_follow);

        // Hide the follow button if the current user is viewing their own profile
        if (user != null && user.getId() != null && user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
        }

        // Set OnClickListener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the current context is a fragment
                if (isfragment) {
                    // If it's a fragment, navigate to the Chew_ProfileFragment
                    SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREPS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    ((FragmentActivity) mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new Chew_ProfileFragment()).commit();

                } else {
                    // If it's not a fragment, navigate to MainActivity
                    Intent intent = new Intent(mcontext, MainActivity.class);
                    intent.putExtra("publisherid", user.getId());
                    mcontext.startActivity(intent);
                }
            }
        });


        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Firestore instance
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Check if the button text is "Follow"
                if (holder.btn_follow.getText().toString().equals("Follow")) {
                    // Add the user to the following list of the current user
                    db.collection("Follow").document(firebaseUser.getUid())
                            .collection("following").document(user.getId())
                            .set(new HashMap<>()) // Using an empty HashMap as a placeholder
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Successfully added user to following list
                                    // Update UI or notify user
                                    Toast.makeText(mcontext, "User followed successfully!", Toast.LENGTH_SHORT).show();
                                    // Alternatively, you can update the button text or color to reflect the change
                                    holder.btn_follow.setText("Unfollow");
                                    addNotification(user.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure
                                    Toast.makeText(mcontext, "Failed to follow user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                    // Add the current user to the followers list of the other user
                    db.collection("Follow").document(user.getId())
                            .collection("followers").document(firebaseUser.getUid())
                            .set(new HashMap<>()) // Using an empty HashMap as a placeholder
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Successfully added current user to followers list
                                    // Update UI or notify user
                                    Toast.makeText(mcontext, "You are now following " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                    // Alternatively, you can update the button text or color to reflect the change
                                    holder.btn_follow.setText("Unfollow");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure
                                    Toast.makeText(mcontext, "Failed to follow " + user.getUsername() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Remove the user from the following list of the current user
                    db.collection("Follow").document(firebaseUser.getUid())
                            .collection("following").document(user.getId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Successfully removed user from following list
                                    // Update UI or notify user
                                    Toast.makeText(mcontext, "You have unfollowed " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                    // Alternatively, you can update the button text or color to reflect the change
                                    holder.btn_follow.setText("Follow");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure
                                    Toast.makeText(mcontext, "Failed to unfollow " + user.getUsername() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                    // Remove the current user from the followers list of the other user
                    db.collection("Follow").document(user.getId())
                            .collection("followers").document(firebaseUser.getUid())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Successfully removed current user from followers list
                                    // Update UI or notify user
                                    Toast.makeText(mcontext, "You have been successfully removed from followers list", Toast.LENGTH_SHORT).show();
                                    // Alternatively, you can update the button text or color to reflect the change
                                    // For example, if this action is performed from the user's profile, you may update the button to show "Follow" again
                                    // holder.btn_follow.setText("Follow");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure
                                    Toast.makeText(mcontext, "Failed to remove you from followers list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
    }

    private void addNotification(String userid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notificationsRef = db.collection("Notifications").document(userid).collection("notifications");

        // Create a HashMap to store the notification data
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userid", firebaseUser.getUid());
        notificationData.put("text", "started following you"); // Set the notification text as "started following you"
        notificationData.put("postid", "");
        notificationData.put("ispost", false);

        // Add the notification to the Firestore collection
        notificationsRef.add(notificationData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Notification added successfully
                        Log.d(TAG, "Notification added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to add notification
                        Log.e(TAG, "Failed to add notification: " + e.getMessage());
                    }
                });
    }


    private void isFollowing(String userid, Button button) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference followRef = db.collection("Follow")
                .document(firebaseUser.getUid())
                .collection("following")
                .document(userid);

        followRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    button.setText("Following");
                } else {
                    button.setText("Follow");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error checking follow status: " + e.getMessage());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView fullname; // Can replace with other attribute
        public ImageView image_profile;
        public Button btn_follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);

        }
    }
}
