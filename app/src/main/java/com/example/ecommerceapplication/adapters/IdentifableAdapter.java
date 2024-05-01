package com.example.ecommerceapplication.adapters;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.Identifiable;
import com.example.ecommerceapplication.models.SellerModel;
import com.example.ecommerceapplication.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

// Define the adapter class within or outside of FollowersActivity
public class IdentifableAdapter extends RecyclerView.Adapter<IdentifableAdapter.ViewHolder> {
    private List<Identifiable> items;
    private Context mcontext;


    private FirebaseUser firebaseUser;

    public IdentifableAdapter(List<Identifiable> items, Context mcontext) {
        this.items = items;
        this.mcontext = mcontext;
    }

    public IdentifableAdapter(List<Identifiable> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public IdentifableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout from XML and create a ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view); // Return the ViewHolder with the inflated layout
    }

    @Override
    public void onBindViewHolder(IdentifableAdapter.ViewHolder holder, int position) {
        Identifiable item = items.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (item instanceof UserModel) {
            UserModel user = (UserModel) item;

            holder.username.setText(user.getUsername());
            holder.fullname.setText(user.getUsername()); // Can replace with other attribute
            Glide.with(holder.itemView) // Use the ViewHolder's itemView context
                    .load(user.getProfileImg())
                    .into(holder.image_profile); // Glide loading into ImageView
            isFollowing(user.getId(), holder.btn_follow);

        } else if (item instanceof SellerModel) {
            SellerModel seller = (SellerModel) item;

            holder.username.setText(seller.getShopName());
            holder.fullname.setText(seller.getAddress()); // Can replace with other attribute
            Glide.with(holder.itemView) // Using itemView's context
                    .load(seller.getImagePath())
                    .into(holder.image_profile); // Glide loading into ImageView
            isFollowing(seller.getSellerID(), holder.btn_follow);
        }

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

    class ViewHolder extends RecyclerView.ViewHolder {
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
