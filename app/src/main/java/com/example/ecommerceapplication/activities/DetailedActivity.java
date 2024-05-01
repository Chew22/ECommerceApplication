package com.example.ecommerceapplication.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.NewProductsModel;
import com.example.ecommerceapplication.models.PostModel;
import com.example.ecommerceapplication.models.ProductModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Activity to display detailed information about a product and add it to the cart or proceed to payment.
 */
public class DetailedActivity extends AppCompatActivity {

    // UI elements
    ImageSlider detailedImageSlider;
    TextView rating, name, description, price, quantity, productId, username, tvColorsTitle, tvSizesTitle;
    RatingBar ratingBar;
    Button addToCart, buyNow, message, lastSelectedButton;
    ImageView addItems, removeItems, image_profile;
    ImageButton copy;
    Toolbar toolbar;
    RadioGroup radioGroupSizes;
    LinearLayout layoutSelectedColors;


    // Variables to track quantity and total price
    int totalQuantity = 1;
    double totalPrice = 0.0;

    //Product models
    NewProductsModel newProductsModel = null;
    PostModel postModel = null;

    // Firebase authentication and Firestore instance
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    // Declare imageUrls at the class level
    List<String> imageUrls = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        // Set up the toolbar
        toolbar = findViewById(R.id.detailed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Back Pressed On Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize Firebase instances
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        radioGroupSizes = findViewById(R.id.edSizes);
        tvSizesTitle = findViewById(R.id.tvSizesTitle);
        layoutSelectedColors = findViewById(R.id.layoutSelectedColors);
        tvColorsTitle = findViewById(R.id.tvColorsTitle);
        lastSelectedButton = null;

        detailedImageSlider = findViewById(R.id.detailedImageSlider);
        image_profile = findViewById(R.id.image_profile);
        username = findViewById(R.id.username);
        productId = findViewById(R.id.productId);
        quantity = findViewById(R.id.quantity);
        name = findViewById(R.id.detailed_name);
        rating = findViewById(R.id.rating);
        ratingBar = findViewById(R.id.my_rating);
        description = findViewById(R.id.detailed_desc);
        price = findViewById(R.id.detailed_price);
        addToCart = findViewById(R.id.add_to_cart);
        buyNow = findViewById(R.id.buy_now);
        addItems = findViewById(R.id.add_item);
        removeItems = findViewById(R.id.remove_item);
        message = findViewById(R.id.btn_message);
        copy = findViewById(R.id.copy);

        // Retrieve the ("detailed") from the intent and assigns it to the variable obj
        final Object obj = getIntent().getSerializableExtra("detailed");

        // Check the type of product and assign it to the appropriate model
        if (obj instanceof NewProductsModel) {
            newProductsModel = (NewProductsModel) obj;
            imageUrls = newProductsModel.getProductImages();
            displaySellerInfo(newProductsModel.getSellerID());
            // Update layout with colors and sizes for PostModel
            // This initializes the colors and sizes UI
            updateLayout(newProductsModel);

        } else if (obj instanceof PostModel) {
            postModel = (PostModel) obj;
            imageUrls = postModel.getProductImages();
            displaySellerInfo(postModel.getSellerID());

            updateLayout(postModel);
        }

        // Create SlideModel list
        List<SlideModel> slideModels = new ArrayList<>();
        for (String imageUrl : imageUrls) {
            slideModels.add(new SlideModel(imageUrl, "", ScaleTypes.CENTER_CROP));
        }

        // Set image list to ImageSlider
        detailedImageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);


        if (newProductsModel != null) {
            productId.setText(newProductsModel.getProductId());
            name.setText(newProductsModel.getProductName());
            rating.setText(newProductsModel.getRating() != null ? newProductsModel.getRating() : "0"); // Check for null rating
            description.setText(newProductsModel.getProductDescription());
            price.setText(String.format("%.2f", newProductsModel.getPrice()));
            float ratingValue = Float.parseFloat(newProductsModel.getRating() != null ? newProductsModel.getRating() : "0"); // Check for null rating
            ratingBar.setRating(ratingValue);

            totalPrice = newProductsModel.getPrice() * totalQuantity;
        }

        if (postModel != null) {
            productId.setText(postModel.getProductId());
            name.setText(postModel.getProductName());
            rating.setText(postModel.getRating() != null ? postModel.getRating() : "0"); // Check for null rating
            description.setText(postModel.getProductDescription());
            price.setText(String.format("%.2f", postModel.getPrice()));
            float ratingValue = Float.parseFloat(postModel.getRating() != null ? postModel.getRating() : "0"); // Check for null rating
            ratingBar.setRating(ratingValue);

            totalPrice = postModel.getPrice() * totalQuantity;
        }

        // Set OnClickListener for copy ImageButton
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the product name from TextView
                String productName = name.getText().toString();

                // Get the clipboard manager
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

                // Create a ClipData object to store the product name
                ClipData clip = ClipData.newPlainText("Product Name", productName);

                // Set the ClipData to clipboard
                clipboard.setPrimaryClip(clip);

                // Show a toast message to indicate the product name has been copied
                Toast.makeText(DetailedActivity.this, "Product name copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up click listener for "Buy Now" button
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrderToFirestore();
                // Initialize product price and product ID variables
                double productPrice = 0.0;

                // Get the product price and ID depending on the type of product
                if (newProductsModel != null) {
                    productPrice = newProductsModel.getPrice();
                } else if (postModel != null) {
                    productPrice = postModel.getPrice();
                }


            }
        });


        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check if the seller ID is available
                String sellerId = "";
                if (obj instanceof NewProductsModel) {
                    sellerId = ((NewProductsModel) obj).getSellerID();
                } else if (obj instanceof PostModel) {
                    sellerId = ((PostModel) obj).getSellerID();
                }

                // Start the ChatActivity with the seller ID
                Intent intent = new Intent(DetailedActivity.this, Chew_ChatActivity.class);
                intent.putExtra("sellerId", sellerId);
                startActivity(intent);

            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sellerId = "";
                if (obj instanceof NewProductsModel) {
                    sellerId = ((NewProductsModel) obj).getSellerID();
                } else if (obj instanceof PostModel) {
                    sellerId = ((PostModel) obj).getSellerID();
                }

                // Start the ProfileActivity with the seller ID
                Intent intent = new Intent(DetailedActivity.this, ProfileActivity.class);
                intent.putExtra("sellerId", sellerId);
                startActivity(intent);
            }
        });


        // Set up the "Add to Cart" button click listener
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        // Set click listeners for adding and removing items
        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity < 100) {
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });
        removeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity > 1) {
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                    if (newProductsModel != null) {
                        totalPrice = newProductsModel.getPrice() * totalQuantity;
                    }
                    if (postModel != null) {
                        totalPrice = postModel.getPrice() * totalQuantity;
                    }
                } else {
                    // Handle the case where the minimum quantity is reached
                    Toast.makeText(DetailedActivity.this, "Minimum quantity reached", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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

    private void saveOrderToFirestore() {
        // Get the current user ID
        final String userId = auth.getCurrentUser().getUid();

        // Get the current date and time
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        final String saveCurrentTime = currentTime.format(calForDate.getTime());



        // Product-related variables
        final String selectedProductId;
        final String productName;
        final double productPrice;
        final List<String> productImages;

        if (newProductsModel != null) {
            selectedProductId = newProductsModel.getProductId();
            productName = newProductsModel.getProductName();
            productPrice = newProductsModel.getPrice();
            productImages = new ArrayList<>(newProductsModel.getProductImages()); // Copying the list to ensure immutability
        } else if (postModel != null) {
            selectedProductId = postModel.getProductId();
            productName = postModel.getProductName();
            productPrice = postModel.getPrice();
            productImages = new ArrayList<>(postModel.getProductImages());
        } else {
            throw new IllegalStateException("Neither newProductsModel nor postModel is initialized.");
        }

        // Prepare the order details
        final Map<String, Object> orderDetails = new HashMap<>();
        orderDetails.put("orderStatus", "Order Placed");
        orderDetails.put("orderDate", saveCurrentDate);
        orderDetails.put("orderTime", saveCurrentTime);
        orderDetails.put("orderTotal", totalPrice);

        firestore.collection("Order")
                .document(userId)
                .collection("Orders")
                .add(orderDetails)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Get the ID of the newly created order document
                            String orderId = task.getResult().getId();
                            // Add the orderId to the order details HashMap
                            orderDetails.put("orderId", orderId);
                            task.getResult().update("orderId", orderId);

                            // Navigate to AddressActivity with additional information
                            Intent addressIntent = new Intent(DetailedActivity.this, AddressActivity.class);
                            addressIntent.putExtra("totalAmount", productPrice);
                            addressIntent.putExtra("orderId", orderId);
                            startActivity(addressIntent);

                            // Create the order item
                            final Map<String, Object> orderItem = new HashMap<>();
                            orderItem.put("productId", selectedProductId);
                            orderItem.put("productName", productName);
                            orderItem.put("productImage", productImages.get(0));
                            orderItem.put("productPrice", productPrice);
                            orderItem.put("totalQuantity", totalQuantity);
                            orderItem.put("totalPrice", totalPrice);
                            orderItem.put("sellerID", getSellerId());
                            orderItem.put("selectedColor", getSelectedColor());
                            orderItem.put("selectedSize", getSelectedSize());

                            firestore.collection("Order")
                                    .document(userId)
                                    .collection("Orders")
                                    .document(orderId)
                                    .collection("Items")
                                    .add(orderItem)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> itemTask) {
                                            if (itemTask.isSuccessful()) {
                                                Toast.makeText(DetailedActivity.this, "Order saved successfully.", Toast.LENGTH_SHORT).show();
                                                createNotificationForSeller(getSellerId(), orderId, totalPrice);

                                            } else {
                                                Log.d(TAG, "Error saving order item: ", itemTask.getException());
                                            }
                                        }
                                    });

                            sendNotificationToSeller(getSellerId(), orderId);
                        } else {
                            Log.e(TAG, "Error creating order: ", task.getException());
                        }
                    }
                });
    }



    // Method to fetch and display seller info
    private void displaySellerInfo(String sellerId) {
        Log.d("DetailedActivity", "Fetching seller information for seller ID: " + sellerId);
        firestore.collection("seller").document(sellerId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String name = document.getString("shopName");
                                // Set seller info to TextViews
                                username.setText(name);
                                Log.d("DetailedActivity", "Seller information retrieved successfully. Seller name: " + name);
                            } else {
                                // Seller document does not exist
                                Log.d("DetailedActivity", "Seller document does not exist for seller ID: " + sellerId);
                                Toast.makeText(DetailedActivity.this, "Seller information not found", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }
    public void updateLayout(ProductModel productModel) {
        // Handling nullable sizes
        if (productModel.getSizes() == null || productModel.getSizes().isEmpty()) {
            radioGroupSizes.setVisibility(View.GONE);
            tvSizesTitle.setVisibility(View.GONE);
        } else {
            radioGroupSizes.setVisibility(View.VISIBLE);
            tvSizesTitle.setVisibility(View.VISIBLE);
            radioGroupSizes.removeAllViews();
            for (String size : productModel.getSizes()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(size);
                radioGroupSizes.addView(radioButton);
            }
        }

        // Handling nullable colors
        if (productModel.getColors() == null || productModel.getColors().isEmpty()) {
            layoutSelectedColors.setVisibility(View.GONE);
            tvColorsTitle.setVisibility(View.GONE);
        } else {
            boolean hasValidColor = false;
            layoutSelectedColors.setVisibility(View.VISIBLE);
            tvColorsTitle.setVisibility(View.VISIBLE);
            layoutSelectedColors.removeAllViews();

            // Check if there are valid colors (i.e., not -1)
            for (Integer color : productModel.getColors()) {
                if (color != null && color != -1) {
                    hasValidColor = true;
                    Button colorButton = new Button(this);
                    colorButton.setBackgroundColor(color);
                    colorButton.setOnClickListener(this::onColorOptionSelected);
                    layoutSelectedColors.addView(colorButton);
                }
            }

            // If no valid colors, hide the layout and the title
            if (!hasValidColor) {
                layoutSelectedColors.setVisibility(View.GONE);
                tvColorsTitle.setVisibility(View.GONE);
            }
        }

    }

    private void addToCart() {
        // Get the current user ID
        String userId = auth.getCurrentUser().getUid();

        // Get current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());


        // Retrieve existing cart items
        firestore.collection("AddToCart").document(userId)
                .collection("User")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> cartItems = task.getResult().getDocuments();
                        boolean itemExists = false;

                        // Information for the new item
                        String newProductId = productId.getText().toString();
                        String newProductName = name.getText().toString();
                        String newProductImage = newProductsModel != null ?
                                newProductsModel.getProductImages().get(0) :
                                postModel.getProductImages().get(0);
                        String newProductPrice = price.getText().toString();
                        String newSelectedSize = radioGroupSizes.getVisibility() == View.VISIBLE
                                ? getSelectedSize() : "none";
                        String newSelectedColor = layoutSelectedColors.getVisibility() == View.VISIBLE
                                ? getSelectedColor() : "0";
                        int newTotalQuantity = totalQuantity;
                        double newTotalPrice = Double.parseDouble(newProductPrice) * newTotalQuantity;

                        // Compare against existing items
                        for (DocumentSnapshot doc : cartItems) {
                            String existingProductId = doc.getString("productId");
                            String existingSize = doc.getString("selectedSize");
                            String existingColor = doc.getString("selectedColor");
                            int existingQuantity = Integer.parseInt(doc.getString("totalQuantity"));

                            // Check for matching product ID, color, and size
                            if (existingProductId.equals(newProductId) &&
                                    (newSelectedSize.equals(existingSize) || existingSize.equals("none")) &&
                                    (newSelectedColor.equals(existingColor) || existingColor.equals("0"))) {

                                // If they match, update the quantity
                                int updatedQuantity = existingQuantity + newTotalQuantity;
                                doc.getReference().update("totalQuantity", String.valueOf(updatedQuantity));
                                Toast.makeText(DetailedActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();


                                itemExists = true; // Indicate that the item exists
                                break; // No need to continue checking
                            }
                        }

                        if (!itemExists) {
                            // Add new item to the cart
                            Map<String, Object> cartMap = new HashMap<>();
                            cartMap.put("productId", newProductId);
                            cartMap.put("productName", newProductName);
                            cartMap.put("productImage", newProductImage);
                            cartMap.put("productPrice", newProductPrice);
                            cartMap.put("selectedSize", newSelectedSize);
                            cartMap.put("selectedColor", newSelectedColor);
                            cartMap.put("currentDate", currentDate);
                            cartMap.put("currentTime", currentTime);
                            cartMap.put("totalQuantity", String.valueOf(newTotalQuantity));
                            cartMap.put("totalPrice", newTotalPrice);
                            cartMap.put("sellerID", getSellerId()); // Assuming a method to get seller ID

                            firestore.collection("AddToCart").document(userId)
                                    .collection("User")
                                    .add(cartMap)
                                    .addOnCompleteListener(addTask -> {
                                        if (addTask.isSuccessful()) {
                                            Toast.makeText(DetailedActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(DetailedActivity.this, "Failed to add to Cart", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Handle error fetching cart items
                        Toast.makeText(DetailedActivity.this, "Error retrieving cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getSelectedSize() {
        int selectedSizeId = radioGroupSizes.getCheckedRadioButtonId();
        if (selectedSizeId != -1) {
            RadioButton selectedRadioButton = radioGroupSizes.findViewById(selectedSizeId);
            return selectedRadioButton.getText().toString();
        }
        return "none";
    }

    private String getSelectedColor() {
        return lastSelectedButton != null ? lastSelectedButton.getTag().toString() : "0";
    }

    private String getSellerId() {
        if (newProductsModel != null) {
            return newProductsModel.getSellerID();
        } else if (postModel != null) {
            return postModel.getSellerID();
        }
        return "";
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


    // Variable to store the original background color
    private Map<Button, Integer> buttonOriginalColors = new HashMap<>();

    public void onColorOptionSelected(View view) {
        // If there's a previously selected button, reset its background
        if (lastSelectedButton != null && lastSelectedButton != view) {
            // Restore the original background color from the map
            if (buttonOriginalColors.containsKey(lastSelectedButton)) {
                lastSelectedButton.setBackgroundColor(buttonOriginalColors.get(lastSelectedButton));
            }
        }
        // Store the current button as the last selected
        lastSelectedButton = (Button) view;

        // If the button is not in the map, add its original color
        if (!buttonOriginalColors.containsKey(lastSelectedButton)) {
            ColorDrawable bgDrawable = (ColorDrawable) view.getBackground();
            buttonOriginalColors.put(lastSelectedButton, bgDrawable.getColor());
        }

        // Add a border to the selected button
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setStroke(3, Color.BLACK); // Border width and color
        borderDrawable.setColor(buttonOriginalColors.get(lastSelectedButton)); // Keep the original color with border
        Integer originalColor = buttonOriginalColors.get(lastSelectedButton);
        if (originalColor != null) {
            borderDrawable.setColor(originalColor);
        }
        lastSelectedButton.setBackground(borderDrawable);

        // Log the tag if it's not null
        Object tag = lastSelectedButton.getTag();
        if (tag != null) {
            Log.d("DetailedActivity", "Last selected color: " + tag.toString());
        } else {
            Log.d("DetailedActivity", "No color tag found on the last selected button.");
        }
    }

}
