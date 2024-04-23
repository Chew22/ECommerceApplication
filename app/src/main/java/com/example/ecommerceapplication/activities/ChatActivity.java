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
import com.example.ecommerceapplication.adapters.ChatRecyclerAdapter;
import com.example.ecommerceapplication.models.ChatMessageModel;
import com.example.ecommerceapplication.models.ChatroomModel;
import com.example.ecommerceapplication.models.SellerModel;
import com.example.ecommerceapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "ChatActivity";

    // Variables declaration
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    SellerModel seller;
    EditText messageInput;
    ImageButton sendMessageBtn, backBtn;
    TextView shopName;
    RecyclerView recyclerView;
    ImageView profile_pic_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize UI elements
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        shopName = findViewById(R.id.shopName);
        recyclerView = findViewById(R.id.chat_recycler_view);
        profile_pic_layout = findViewById(R.id.profile_pic_layout);


        // Get seller ID from intent
        String sellerId = getIntent().getStringExtra("sellerId");

        FirebaseUtil.getSellerReference(sellerId).get().addOnCompleteListener(task -> {
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
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), sellerId);

        // Set onClickListener for back button
        backBtn.setOnClickListener((v) -> {
            finish();
        });

        // Set onClickListener for send button to send messages
        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty())
                sendMessageToUser(message);
        });

        // Setup RecyclerView to display chat messages
        setupChatRecyclerView();

        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                ChatroomModel chatroom = task.getResult().toObject(ChatroomModel.class);
                if (chatroom == null) {
                    Log.w(TAG, "Chatroom with ID " + chatroomId + " does not exist");
                } else {
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), sellerId),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            } else {
                Log.e(TAG, "Failed to retrieve chatroom data" + chatroomId);
            }
        });

    }




    void setupChatRecyclerView() {
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());
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

    void sendMessageToUser(String message) {
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroomModel.setLastMessage(message);

        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            messageInput.setText("");
                        }
                    }
                });
    }


}
