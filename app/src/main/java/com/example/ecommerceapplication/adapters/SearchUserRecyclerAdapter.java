package com.example.ecommerceapplication.adapters;

import static com.example.ecommerceapplication.utils.FirebaseUtil.getSellerDocumentId;

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
import com.example.ecommerceapplication.activities.ChatActivity;
import com.example.ecommerceapplication.models.SellerModel;
import com.example.ecommerceapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;


public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<SellerModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

    Context context;

    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<SellerModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull SellerModel model) {

        holder.address.setText(model.getAddress());
        String currentUserId = FirebaseUtil.currentUserId();
        if (currentUserId != null && currentUserId.equals(getSellerDocumentId(model))) {
            holder.shopName.setText(model.getShopName() + "(Me)");
        } else {
            holder.shopName.setText(model.getShopName());
        }

        // Load profileImg using Glide
        if (model.getImagePath() != null && !model.getImagePath().isEmpty()) {
            Glide.with(context)
                    .load(model.getImagePath())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imagePath);
        }

        holder.itemView.setOnClickListener(v -> {
            // Navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("shopName", model.getShopName());
            intent.putExtra("address", model.getAddress());

            // Retrieve the document ID of the SellerModel
            FirebaseFirestore.getInstance().collection("seller")
                    .whereEqualTo("shopName", model.getShopName())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                            String sellerId = task.getResult().getDocuments().get(0).getId();
                            intent.putExtra("sellerId", sellerId);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent); // Only start activity after sellerId is obtained
                        } else {
                            Log.e("ChatAdapter", "Could not find sellerId for shopName: " + model.getShopName());
                        }
                    });
        });

    }


    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModelViewHolder(view);
    }

    public class UserModelViewHolder extends RecyclerView.ViewHolder {
        TextView shopName;
        TextView address;
        ImageView imagePath;

        public UserModelViewHolder(@NonNull View itemView){
            super(itemView);
            shopName = itemView.findViewById(R.id.shopName);
            address = itemView.findViewById(R.id.address);
            imagePath = itemView.findViewById(R.id.imagePath);

        }

    }
}
