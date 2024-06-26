package com.example.ecommerceapplication.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.adapters.MyCartAdapter;
import com.example.ecommerceapplication.models.MyCartModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
public class CartActivity extends AppCompatActivity {

    // Context variable
    private Context context;

    // Firebase instances
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    // UI elements
    private Toolbar toolbar;
    private TextView overAllAmount;
    private RecyclerView recyclerView;
    private Button buyNowButton;
    private String userId;

    // Variables
    private double totalAmount = 0.0; // Updated total amount
    private List<MyCartModel> cartModelList;
    private MyCartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        checkCartEmpty(); // Check if cart is empty when activity starts

        // Initialize context variable
        context = this;

        // Set up toolbar
        toolbar = findViewById(R.id.my_cart_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize TextView for displaying total amount
        overAllAmount = findViewById(R.id.textView3);

        // Set up buy now button
        buyNowButton = findViewById(R.id.buy_now);
        buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveOrderToFirestore();

            }
        });

        // Back Pressed On Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set up RecyclerView
        recyclerView = findViewById(R.id.cart_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize cart model list and adapter
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(this, cartModelList, new MyCartAdapter.TotalAmountListener() {
            @Override
            public void onTotalAmountChanged(double totalAmount) {
                // Update the total amount TextView
                overAllAmount.setText("Total Amount: RM " + String.format("%.2f", totalAmount));

                // Save the updated total amount
                CartActivity.this.totalAmount = totalAmount;
            }
        });
        recyclerView.setAdapter(cartAdapter);

        // Fetch cart items from Firestore
        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("User")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                                // Get the document ID
                                String documentId = doc.getId();
                                // Convert the document to a MyCartModel object
                                MyCartModel myCartModel = doc.toObject(MyCartModel.class);
                                // Set the document ID for the MyCartModel
                                myCartModel.setDocumentId(documentId);

                                // Get the first image URL from the MyCartModel
                                String imageUrl = myCartModel.getProductImage();

                                // Add the MyCartModel to the cartModelList
                                cartModelList.add(myCartModel);

                                // Add the price of the current item to the total amount
                                totalAmount += myCartModel.getTotalPrice();
                            }
                            // Notify the adapter of data changes
                            cartAdapter.notifyDataSetChanged();
                            // Update the total amount TextView
                            overAllAmount.setText("Total Amount: RM " + String.format("%.2f", totalAmount));
                        }
                    }
                });
    }

    private void checkCartEmpty() {
        firestore.collection("AddToCart")
                .document(userId)
                .collection("User")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot document = task.getResult();
                        if (document.isEmpty()) {
                            buyNowButton.setVisibility(View.GONE); // Hide button if cart is empty
                        } else {
                            buyNowButton.setVisibility(View.VISIBLE); // Show button if cart has items
                        }
                    }
                });
    }

    // Method to save cart items to Order collection in Firestore
    private void saveOrderToFirestore() {
        // Check if the Order collection exists
        firestore.collection("Order").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Order collection exists, save cart items under the user's ID
                        saveCartItemsToOrder();
                    } else {
                        // Order collection does not exist, create it first
                        createOrderAndSaveCartItems();
                    }
                } else {
                    // Handle errors
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    // Method to create Order collection and save cart items under the user's ID
    private void createOrderAndSaveCartItems() {
        // Create a new Order collection with the user's ID
        firestore.collection("Order").document(auth.getCurrentUser().getUid()).set(new HashMap<>())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Order collection created successfully, save cart items under the user's ID
                            saveCartItemsToOrder();
                        } else {
                            // Handle errors
                            Log.d(TAG, "Error creating Order collection: ", task.getException());
                        }
                    }
                });
    }

    private void saveCartItemsToOrder() {
        // Define the order status
        final String orderStatus = "Order Placed";

        // Get the current date and time
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        final String saveCurrentTime = currentTime.format(calForDate.getTime());

        // Create a HashMap to store order details
        HashMap<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("orderStatus", orderStatus);
        orderDetails.put("orderDate", saveCurrentDate);
        orderDetails.put("orderTime", saveCurrentTime);
        orderDetails.put("orderTotal", totalAmount);

        // Save order details under the user's ID in the Order collection
        firestore.collection("Order")
                .document(auth.getCurrentUser().getUid())
                .collection("Orders")
                .add(orderDetails) // Add the order details as a document
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Get the ID of the newly created order document
                            String orderId = task.getResult().getId(); // Retrieve the generated document ID

                            // Add the orderId to the order details HashMap
                            orderDetails.put("orderId", orderId);

                            // Update the order document with the orderId
                            task.getResult().update("orderId", orderId)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Order ID added to order details: " + orderId);

                                            // Pass orderId to AddressActivity
                                            Intent addressIntent = new Intent(CartActivity.this, AddressActivity.class);
                                            addressIntent.putExtra("orderId", orderId); // Pass orderId to AddressActivity
                                            addressIntent.putExtra("totalAmount", totalAmount); // Pass totalAmount as well
                                            startActivity(addressIntent);

                                            // Create a HashSet to keep track of unique seller IDs
                                            HashSet<String> sellersToNotify = new HashSet<>();

                                            for (MyCartModel cartItem : cartModelList) {
                                                // Collect seller ID while saving cart items to "Items"
                                                sellersToNotify.add(cartItem.getSellerID());

                                                // Create a HashMap to store item details
                                                HashMap<String, Object> orderItem = new HashMap<>();
                                                orderItem.put("productId", cartItem.getProductId());
                                                orderItem.put("productName", cartItem.getProductName());
                                                orderItem.put("productImage", cartItem.getProductImage());
                                                orderItem.put("productPrice", cartItem.getProductPrice());
                                                orderItem.put("totalQuantity", cartItem.getTotalQuantity());
                                                orderItem.put("totalPrice", cartItem.getTotalPrice());
                                                orderItem.put("sellerID", cartItem.getSellerID());
                                                orderItem.put("selectedColor", cartItem.getSelectedColor());
                                                orderItem.put("selectedSize", cartItem.getSelectedSize());

                                                // Save the item under "Items" in the order document
                                                firestore.collection("Order")
                                                        .document(auth.getCurrentUser().getUid())
                                                        .collection("Orders")
                                                        .document(orderId) // Reference the newly created order document
                                                        .collection("Items") // Add subcollection "Items"
                                                        .add(orderItem) // Add the item details as a document
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                // Remove the item from the cart after it's been successfully saved
                                                                removeCartItemFromFirestore(cartItem.getDocumentId());
                                                            } else {
                                                                // Handle errors
                                                                Log.d(TAG, "Error saving cart item to Order: ", task.getException());
                                                            }
                                                        });
                                            }
                                            for (String sellerID : sellersToNotify) {
                                                createNotificationForSeller(sellerID, orderId, totalAmount);
                                            }

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure to add orderId to order details
                                            Log.e(TAG, "Failed to add Order ID to order details: ", e);
                                        }
                                    });
                        } else {
                            // Handle errors
                            Log.d(TAG, "Error saving order details: ", task.getException());
                        }
                    }
                });
    }

    // Method to create a notification in Firestore
    private void createNotificationForSeller(String sellerID, String orderId, double orderTotal) {
        // Create a HashMap to store notification data
        HashMap<String, Object> notificationData = new HashMap<>();
        notificationData.put("orderId", orderId);
        notificationData.put("orderTotal", orderTotal);
        notificationData.put("timestamp", FieldValue.serverTimestamp());
        notificationData.put("message", "You have a new order from a buyer");

        // Save the notification in the seller's Notifications collection
        firestore.collection("seller")
                .document(sellerID)
                .collection("Notifications")
                .add(notificationData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification created for seller: " + sellerID))
                .addOnFailureListener(e -> Log.e(TAG, "Error creating notification for seller: " + e.getMessage()));
    }


    // Push Notifications
    private void sendNotificationToSeller(String sellerID, String orderId) {
        // This method sends a notification to the seller with a given ID
        firestore.collection("seller").document(sellerID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot sellerDoc = task.getResult();
                if (sellerDoc.exists()) {
                    // Extract contact information for notification
                    String sellerEmail = sellerDoc.getString("email");
                    String sellerFCMToken = sellerDoc.getString("fcmToken");

                    if (sellerEmail != null) {
                        sendEmailNotification(sellerEmail, orderId);
                    }

                    // Or send a push notification via FCM
                    if (sellerFCMToken != null) {
                        sendPushNotification(sellerFCMToken, orderId);
                    }
                }
            }
        });
    }

    private void sendEmailNotification(String sellerEmail, String orderId) {
        // Code to send an email to the seller
        // You could use a service like SendGrid, Mailgun, etc.
    }

    private void sendPushNotification(String sellerFCMToken, String orderId) {
        // Code to send a push notification via FCM
        // This could involve a Firebase Cloud Function or a server-side script
    }

    // Method to remove cart item from Firestore
    private void removeCartItemFromFirestore(String documentId) {
        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User")
                .document(documentId)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Item removed from the cart
                            Log.d(TAG, "Cart item removed successfully: " + documentId);
                        } else {
                            // Handle errors
                            Log.d(TAG, "Error removing cart item: " + documentId, task.getException());
                        }
                    }
                });
    }

}
