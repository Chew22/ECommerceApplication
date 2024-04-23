package com.example.ecommerceapplication.utils;

import android.util.Log;

import com.example.ecommerceapplication.models.SellerModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Utility class for interacting with Firebase services such as Authentication and Firestore.
 * Provides methods for retrieving current user ID, checking login status, accessing user details,
 * managing chatrooms and messages, and handling timestamps.
 */
public class FirebaseUtil {

    /**
     * Retrieves the current user's ID from Firebase Authentication.
     * @return The current user's ID, or null if the user is not authenticated.
     */
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    /**
     * Checks if a user is currently logged in.
     * @return True if the user is logged in, false otherwise.
     */
    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    /**
     * Retrieves the Firestore document reference for the current user's details.
     * @return Document reference for the current user's details.
     */
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("CurrentUser").document(currentUserId());
    }


    /**
     * Retrieves the Firestore collection reference for all users.
     * @return Collection reference for all users.
     */
    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("CurrentUser");
    }

    /**
     * Retrieves the Firestore document reference for a specific chatroom.
     * @param chatroomId The ID of the chatroom.
     * @return Document reference for the specified chatroom.
     */
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    /**
     * Retrieves the Firestore collection reference for messages within a specific chatroom.
     * @param chatroomId The ID of the chatroom.
     * @return Collection reference for messages within the specified chatroom.
     */
    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }
    /**
     * Generates a unique chatroom ID based on two user IDs.
     * @param userId1 The ID of the first user.
     * @param userId2 The ID of the second user.
     * @return Unique chatroom ID generated from the user IDs.
     */
    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.compareTo(userId2) < 0) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }


    /**
     * Retrieves the Firestore collection reference for all chatrooms.
     * @return Collection reference for all chatrooms.
     */
    public static CollectionReference allChatroomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static CollectionReference sellerCollectionReference() {
        return FirebaseFirestore.getInstance().collection("seller");
    }



    /**
     * Retrieves the Firestore document reference for the other user in a chatroom.
     * @param userIds List containing user IDs of both users in the chatroom.
     * @return Document reference for the other user in the chatroom.
     */
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            Log.w("FirebaseUtil", "User IDs list is empty or null");
            return null;
        }

        String currentUserId = currentUserId();
        if (currentUserId == null) {
            Log.w("FirebaseUtil", "Current user ID is null");
            return null;
        }

        // Determine the other user in the chatroom
        String otherUserId = userIds.get(0).equals(currentUserId) ? userIds.get(1) : userIds.get(0);
        if (otherUserId == null) {
            Log.w("FirebaseUtil", "Other user ID is null");
            return null;
        }

        return sellerCollectionReference().document(otherUserId);
    }



    /**
     * Converts a Firestore Timestamp object to a string representation.
     * @param timestamp The Firestore Timestamp object.
     * @return String representation of the timestamp in "HH:MM" format.
     */
    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static Task<String> getSellerDocumentId(SellerModel model) {
        return FirebaseFirestore.getInstance()
                .collection("seller")
                .whereEqualTo("email", model.getEmail()) // Assuming 'email' is a unique field to identify sellers
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                        return task.getResult().getDocuments().get(0).getId();
                    } else {
                        return null;
                    }
                });
    }

    public static DocumentReference getSellerReference(String sellerId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("seller").document(sellerId);
    }
}
