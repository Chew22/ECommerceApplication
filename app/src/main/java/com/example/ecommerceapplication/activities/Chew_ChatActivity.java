package com.example.ecommerceapplication.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.Chew_ChatRecyclerAdapter;
import com.example.ecommerceapplication.models.ChatMessageModel;
import com.example.ecommerceapplication.models.ChatroomModel;
import com.example.ecommerceapplication.models.SellerModel;
import com.example.ecommerceapplication.utils.Chew_FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class Chew_ChatActivity extends AppCompatActivity {

    public static final String TAG = "ChatActivity";

    // Variables declaration
    String chatroomId;
    ChatroomModel chatroomModel;
    Chew_ChatRecyclerAdapter adapter;
    SellerModel seller;
    EditText messageInput;
    ImageButton sendMessageBtn, backBtn;
    TextView shopName;
    RecyclerView recyclerView;
    ImageView profile_pic_layout;
    // Flag to indicate if the chatroom is ready
    private boolean isChatroomInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chew_activity_chat);

        // Initialize UI elements
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        shopName = findViewById(R.id.shopName);
        recyclerView = findViewById(R.id.chat_recycler_view);
        profile_pic_layout = findViewById(R.id.profile_pic_layout);


        // Get seller ID from intent
        String sellerId = getIntent().getStringExtra("sellerId");
        Log.w(TAG, "Chat activity: sellerId from intent " + sellerId);

        Chew_FirebaseUtil.getSellerReference(sellerId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                seller = task.getResult().toObject(SellerModel.class);
                if (seller != null) {
                    shopName.setText(seller.getShopName());
                    Glide.with(this).load(seller.getImagePath()).into(profile_pic_layout);
                } else {
                    Log.w(TAG, "Seller data is null");
                }
            } else {
                Log.e(TAG, "Failed to retrieve seller data");
            }
        });


        // Generate unique chatroom ID based on user IDs
        chatroomId = Chew_FirebaseUtil.getChatroomId(Chew_FirebaseUtil.currentUserId(), sellerId);

        // Set onClickListener for back button
        backBtn.setOnClickListener((v) -> {
            finish();
        });


        // Setup RecyclerView to display chat messages
        setupChatRecyclerView();

        // Retrieve chatroom information and set flag when done
        Chew_FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ChatroomModel chatroom = task.getResult().toObject(ChatroomModel.class);

                if (chatroom == null) {
                    // If chatroom does not exist, create a new one
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(Chew_FirebaseUtil.currentUserId(), sellerId),
                            Timestamp.now(),
                            ""
                    );
                    Chew_FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                } else {
                    chatroomModel = chatroom; // If chatroom exists, use the existing one
                }

                isChatroomInitialized = true; // Set the flag to true once chatroom is initialized
            } else {
                Log.e(TAG, "Failed to retrieve chatroom data for ID: " + chatroomId);
            }
        });

        // Send message button with check for initialization
        sendMessageBtn.setOnClickListener(v -> {
            if (isChatroomInitialized) {
                String message = messageInput.getText().toString().trim();
                if (!message.isEmpty()) {
                    sendMessageToUser(message);
                }
            } else {
                Log.e(TAG, "Chatroom is not initialized, cannot send message");
                // Optionally, inform the user to wait or try again later
            }
        });
    }


    void sendMessageToUser(String message) {
        if (chatroomModel != null) {
            chatroomModel.setLastMessageTimestamp(Timestamp.now());
            chatroomModel.setLastMessageSenderId(Chew_FirebaseUtil.currentUserId());
            chatroomModel.setLastMessage(message);

            Chew_FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

            ChatMessageModel chatMessageModel = new ChatMessageModel(message, Chew_FirebaseUtil.currentUserId(), Timestamp.now());
            Chew_FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                messageInput.setText(""); // Clear input after successful send
                            }
                        }
                    });
        } else {
            Log.e(TAG, "chatroomModel is null, unable to send message");
        }
    }

    void setupChatRecyclerView() {
        Query query = Chew_FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new Chew_ChatRecyclerAdapter(options, getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        // Scroll RecyclerView to the top when new messages are added
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

}
