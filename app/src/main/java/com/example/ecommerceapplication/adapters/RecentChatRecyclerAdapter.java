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
import com.example.ecommerceapplication.activities.ChatActivity;
import com.example.ecommerceapplication.models.ChatroomModel;
import com.example.ecommerceapplication.models.SellerModel;
import com.example.ecommerceapplication.utils.AndroidUtil;
import com.example.ecommerceapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

/**
 * Adapter class for populating RecyclerView with recent chatroom data.
 * Responsible for binding chatroom data to ViewHolder and handling user interaction.
 */
public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    // Context reference for handling UI operations
    Context context;

    /**
     * Constructor for the adapter.
     * @param options FirestoreRecyclerOptions containing chatroom data.
     * @param context The context in which the adapter is being used.
     */
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
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
        String myUserId = FirebaseUtil.currentUserId(); // Current user ID

        // Log the user IDs
        Log.d("ChatAdapter", "User IDs in chatroom: " + userIds.toString());

        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                        SellerModel otherUserModel = task.getResult().toObject(SellerModel.class);
                        holder.usernameText.setText(otherUserModel.getShopName());
                        if (lastMessageSentByMe) {
                            holder.lastMessageText.setText("You: " + model.getLastMessage());
                        } else {
                            holder.lastMessageText.setText(model.getLastMessage());
                        }
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        if (otherUserModel.getImagePath() != null && !otherUserModel.getImagePath().isEmpty()) {
                            Glide.with(context)
                                    .load(otherUserModel.getImagePath())
                                    .placeholder(R.drawable.placeholder)
                                    .into(holder.profilePic);
                        }

                        holder.itemView.setOnClickListener(v -> {
                            // Navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });
                    }
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
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
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
        }
    }
}