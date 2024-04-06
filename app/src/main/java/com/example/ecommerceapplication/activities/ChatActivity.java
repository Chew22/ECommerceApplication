package com.example.ecommerceapplication.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.ChatRecyclerAdapter;
import com.example.ecommerceapplication.models.ChatMessageModel;
import com.example.ecommerceapplication.models.ChatroomModel;
import com.example.ecommerceapplication.models.UserModel;
import com.example.ecommerceapplication.utils.AndroidUtil;
import com.example.ecommerceapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
/**
 * This class represents the ChatActivity in the application, where users can
 * exchange messages with each other in a chatroom.
 */
public class ChatActivity extends AppCompatActivity {

    // Variables declaration
    String chatroomId;
    ChatroomModel chatroomModel;
    ChatRecyclerAdapter adapter;
    UserModel otherUser;
    EditText messageInput;
    ImageButton sendMessageBtn, backBtn;
    TextView otherUsername;
    RecyclerView recyclerView;

    /**
     * This method is called when the activity is first created.
     * It initializes the UI elements, retrieves necessary data, and sets up the chatroom.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get UserModel for the other user in the chat
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        // Generate unique chatroom ID based on user IDs
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.currentUserId(), otherUser.getId());

        // Initialize UI elements
        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recycler_view);

        // Set onClickListener for back button
        backBtn.setOnClickListener((v)->{
            finish();
        });

        // Set the username of the other user in the chat
        otherUsername.setText(otherUser.getUsername());

        // Set onClickListener for send button to send messages
        sendMessageBtn.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty())
                sendMessageToUser(message);
        });

        // Setup RecyclerView to display chat messages
        setupChatRecyclerView();

        // Retrieve or create chatroom model from Firestore
        getOrCreateChatroomModel();
    }

    /**
     * Sets up the RecyclerView to display chat messages.
     * Retrieves chat messages from Firestore and displays them in reverse chronological order.
     */
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

    /**
     * Sends a message to the other user in the chat.
     * Updates chatroom information and Firestore database accordingly.
     * @param message The message to be sent
     */
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
                        if (task.isSuccessful()){
                            messageInput.setText("");
                        }
                    }
                });
    }

    /**
     * Retrieves or creates the chatroom model from Firestore.
     * If the chatroom does not exist, it creates a new one.
     */
    void getOrCreateChatroomModel() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null){
                    // First Time Chat
                    chatroomModel = new ChatroomModel(
                            chatroomId,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getId()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }
            }
        });
    }
}
