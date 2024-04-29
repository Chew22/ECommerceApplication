package com.example.ecommerceapplication.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.activities.Chew_ChatActivity;
import com.example.ecommerceapplication.models.ChatroomModel;
import com.example.ecommerceapplication.models.SellerModel;
import com.example.ecommerceapplication.utils.Chew_FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Adapter class for populating RecyclerView with recent chatroom data.
 * Responsible for binding chatroom data to ViewHolder and handling user interaction.
 */
public class Chew_RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, Chew_RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    // Context reference for handling UI operations
    Context context;
    FirestoreRecyclerOptions<ChatroomModel> options;

    public Chew_RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
        this.options = options;  // Move options initialization here
    }

    /**
     * Binds chatroom data to the ViewHolder.
     * @param holder The ViewHolder to bind the data to.
     * @param position The position of the item within the adapter's data set.
     * @param model The chatroom model containing data to be bound.
     */
    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        List<String> userIds = model.getUserIds(); // List of user IDs in the chatroom
        String myUserId = Chew_FirebaseUtil.currentUserId(); // Current user ID

        // Log the user IDs
        Log.d("ChatAdapter", "User IDs in chatroom: " + userIds.toString());
        Log.d("ChatAdapter", "Current user ID: " + myUserId);

        DocumentReference otherUserRef = Chew_FirebaseUtil.getOtherUserFromChatroom(model.getUserIds());
        Log.d("ChatAdapter", "Other user reference: " + otherUserRef);

        if (otherUserRef != null) {
            otherUserRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(Chew_FirebaseUtil.currentUserId());
                    SellerModel otherUserModel = task.getResult().toObject(SellerModel.class);

                    if (otherUserModel != null) {
                        holder.usernameText.setText(otherUserModel.getShopName());
                        if (lastMessageSentByMe) {
                            holder.lastMessageText.setText("You: " + model.getLastMessage());
                        } else {
                            holder.lastMessageText.setText(model.getLastMessage());
                        }
                        holder.lastMessageTime.setText(Chew_FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        if (otherUserModel.getImagePath() != null && !otherUserModel.getImagePath().isEmpty()) {
                            Glide.with(context)
                                    .load(otherUserModel.getImagePath())
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.profilePic);
                        }

                        holder.itemView.setOnClickListener(v -> {
                            FirebaseFirestore.getInstance().collection("seller")
                                    .whereEqualTo("shopName", otherUserModel.getShopName())
                                    .get()
                                    .addOnCompleteListener(sellerTask -> {
                                        if (sellerTask.isSuccessful() && !sellerTask.getResult().getDocuments().isEmpty()) {
                                            String sellerId = sellerTask.getResult().getDocuments().get(0).getId();
                                            Intent intent = new Intent(context, Chew_ChatActivity.class);
                                            intent.putExtra("shopName", otherUserModel.getShopName());
                                            intent.putExtra("address", otherUserModel.getAddress());
                                            intent.putExtra("sellerId", sellerId);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            context.startActivity(intent);
                                        } else {
                                            Log.e("ChatAdapter", "Could not find sellerId for shopName: " + otherUserModel.getShopName());
                                        }
                                    });
                        });


                    } else {
                        Log.e("ChatAdapter", "Other user model is null");
                    }
                }
            });
        } else {
            Log.e("ChatAdapter", "Other user reference is null");
        }

        // Delete confirmation
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Chat")
                    .setMessage("Are you sure you want to delete this chat?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteChat(position))
                    .setNegativeButton("Cancel", null) // Just dismisses the dialog
                    .show();
        });
    }

    public void deleteChat(int position) {
        getSnapshots().getSnapshot(position).getReference().delete()
                .addOnSuccessListener(aVoid -> Log.d("ChatAdapter", "Chat deleted successfully"))
                .addOnFailureListener(e -> {
                    Log.e("ChatAdapter", "Failed to delete chat", e);
                    Toast.makeText(context, "Failed to delete chat. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }



    /**
     * Inflates the layout for the ViewHolder.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chew_recent_chat_recycler_row, parent, false);
        return new ChatroomModelViewHolder(view);
    }

    /**
     * ViewHolder class for holding chatroom item views.
     */
    class ChatroomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;
        ImageButton btnDelete;

        /**
         * Constructor for the ViewHolder.
         * @param itemView The item view to be held by the ViewHolder.
         */
        public ChatroomModelViewHolder(@NonNull View itemView){
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}