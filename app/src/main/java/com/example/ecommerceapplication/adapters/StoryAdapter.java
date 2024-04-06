package com.example.ecommerceapplication.adapters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.ecommerceapplication.activities.AddStoryActivity;
import com.example.ecommerceapplication.models.StoryModel;
import com.example.ecommerceapplication.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private Context mContext;
    private List<StoryModel> mStory;

    public StoryAdapter(Context mContext, List<StoryModel> mStory) {
        this.mContext = mContext;
        this.mStory = mStory;
    }

    // Create view holder based on item view type
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        if (i == 0){
            // For the first item, inflate the layout for adding a story
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item, parent, false);
            return  new StoryAdapter.ViewHolder(view);
        }else {
            // For other items, inflate the regular story item layout
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item, parent, false);
            return  new StoryAdapter.ViewHolder(view);
        }
    }

    // Bind data to views
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        StoryModel story = mStory.get(position);
        userInfo(holder, story.getUserid(), position);

        if (holder.getAdapterPosition() != 0){
            seenStory(holder, story.getUserid());
        }
        if (holder.getAdapterPosition() == 0){
            myStory(holder.addstory_text, holder.story_plus, false);
        }

        // Click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == 0){
                    // If the clicked item is the first one (for adding story), handle accordingly
                    myStory(holder.addstory_text, holder.story_plus, true);
                }else {
                    // Handle click on other story items
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    // View holder class
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView story_photo, story_plus, story_photo_seen;
        public TextView story_username, addstory_text;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo = itemView.findViewById(R.id.story_photo);
            story_plus = itemView.findViewById(R.id.story_plus);
            story_photo_seen = itemView.findViewById(R.id.story_photo_seen);
            story_username = itemView.findViewById(R.id.story_username);
            addstory_text = itemView.findViewById(R.id.addstory_text);
        }
    }

    @Override
    public int getItemViewType(int position){
        if (position == 0){
            return 0 ;
        }
        return  1;
    }

    // Retrieve user information for the story
    private void userInfo(final ViewHolder viewHolder, final String userid, final int pos){
        // Get a reference to the Firestore document for the user
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userid);

        // Fetch user information from Firestore
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Convert Firestore document snapshot to UserModel object
                    UserModel user = documentSnapshot.toObject(UserModel.class);
                    if (user != null) {
                        // Load profile image using Glide
                        Glide.with(mContext).load(user.getProfileImg()).into(viewHolder.story_photo_seen);
                        if (pos != 0) {
                            // Load profile image and username for story
                            Glide.with(mContext).load(user.getProfileImg()).into(viewHolder.story_photo);
                            viewHolder.story_username.setText(user.getUsername());
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to retrieve user information from Firestore
                Log.e(TAG, "Firestore error: " + e.getMessage());
            }
        });
    }


    // Check if the story belongs to the current user
    private void myStory(TextView textView, ImageView imageView, final boolean click){
        // Get a reference to the Firestore collection for stories of the current user
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference storyRef = db.collection("Story").document(currentUserId).collection("UserStories");

        storyRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int count = 0;
                long timecurrent = System.currentTimeMillis();
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    StoryModel story = documentSnapshot.toObject(StoryModel.class);
                    if (timecurrent > story.getTimestart().getTime() && timecurrent < story.getTimeend().getTime()){
                        count++;
                    }
                }

                if (click) {
                    if (count > 0){
                        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Go to story
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }else {
                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(intent);
                    }

                }else {
                    if (count > 0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to retrieve story data from Firestore
                Log.e(TAG, "Firestore error: " + e.getMessage());
            }
        });
    }


    // Check if the story has been seen by the current user
    private void seenStory(ViewHolder viewHolder, String userid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference storiesRef = db.collection("Story").document(userid).collection("stories");

        storiesRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                long currentTimeMillis = System.currentTimeMillis();
                AtomicInteger unseenCount = new AtomicInteger(0); // AtomicInteger to track the count of unseen stories
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    DocumentReference storyRef = snapshot.getReference();
                    storyRef.collection("views")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (!documentSnapshot.exists()) {
                                        StoryModel story = snapshot.toObject(StoryModel.class);
                                        if (story != null && currentTimeMillis < story.getTimeend().getTime()) {
                                            unseenCount.incrementAndGet(); // Increment the count of unseen stories
                                        }
                                    }

                                    if (unseenCount.get() > 0) {
                                        viewHolder.story_photo.setVisibility(View.VISIBLE);
                                        viewHolder.story_photo_seen.setVisibility(View.GONE);
                                    } else {
                                        viewHolder.story_photo.setVisibility(View.GONE);
                                        viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to retrieve stories from Firestore
                Log.e(TAG, "Firestore error: " + e.getMessage());
            }
        });
    }





}
