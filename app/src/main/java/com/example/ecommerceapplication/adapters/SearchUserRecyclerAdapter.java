package com.example.ecommerceapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.activities.ChatActivity;
import com.example.ecommerceapplication.models.UserModel;
import com.example.ecommerceapplication.utils.AndroidUtil;
import com.example.ecommerceapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {

        holder.phoneText.setText(model.getPhone());
        String currentUserId = FirebaseUtil.currentUserId();
        if (currentUserId != null && currentUserId.equals(model.getId())) {
            holder.usernameText.setText(model.getUsername() + "(Me)");
        } else {
            holder.usernameText.setText(model.getUsername());
        }

        // Load profileImg using Glide
        if (model.getProfileImg() != null && !model.getProfileImg().isEmpty()) {
            Glide.with(context)
                    .load(model.getProfileImg())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.profilePic);
        }

        holder.itemView.setOnClickListener(v -> {
            // Navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }


    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    public class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView phoneText;
        ImageView profilePic;

        public UserModelViewHolder(@NonNull View itemView){
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            phoneText = itemView.findViewById(R.id.phone_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);

        }

    }
}
