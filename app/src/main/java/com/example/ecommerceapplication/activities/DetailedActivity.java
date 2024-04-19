package com.example.ecommerceapplication.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Activity to display detailed information about a product and add it to the cart or proceed to payment.
 */
public class DetailedActivity extends AppCompatActivity {

    // UI elements
    ImageSlider detailedImageSlider;
    TextView rating, name, description, price, quantity, productId, username;
    RatingBar ratingBar;
    Button addToCart, buyNow, message;
    ImageView addItems, removeItems, image_profile;
    ImageButton copy;
    Toolbar toolbar;

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
        } else if (obj instanceof PostModel) {
            postModel = (PostModel) obj;
            imageUrls = postModel.getProductImages();
            displaySellerInfo(postModel.getSellerID());
        }

        // Log image URLs
        Log.d("DetailedActivity", "Image URLs: " + imageUrls.toString());

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

        // Set up click listeners for buy now buttons
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start PaymentActivity with the updated total amount
                Intent intent = new Intent(DetailedActivity.this, PaymentActivity.class);
                double productPrice = 0.0; // Initialize product price variable
                if (newProductsModel != null) {
                    // Get the price of the current product
                    productPrice = newProductsModel.getPrice();
                }
                if (postModel != null) {
                    // Get the price of the current product
                    productPrice = postModel.getPrice();
                }
                // Pass the price to PaymentActivity
                intent.putExtra("totalAmount", productPrice);
                startActivity(intent);


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
                Intent intent = new Intent(DetailedActivity.this, ChatActivity.class);
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
            addItems.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if (totalQuantity < 100){
                        totalQuantity++;
                        quantity.setText(String.valueOf(totalQuantity));
                    }
                }
            });
             removeItems.setOnClickListener(new View.OnClickListener(){
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
                    }else {
                        // Handle the case where the minimum quantity is reached
                        Toast.makeText(DetailedActivity.this, "Minimum quantity reached", Toast.LENGTH_SHORT).show();
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

    // Method to add the current item to the user's cart
    private void addToCart() {

        // Get the current date and time
            String saveCurrentTime, saveCurrentDate;
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calForDate.getTime());


        // Create a HashMap to store cart item information
            final HashMap<String,Object> cartMap = new HashMap<>();
            cartMap.put("productId",productId.getText().toString());
            cartMap.put("productName",name.getText().toString());
            cartMap.put("productImage", newProductsModel != null ? newProductsModel.getProductImages() : postModel.getProductImages());
            cartMap.put("productPrice",price.getText().toString());
            cartMap.put("currentTime",saveCurrentTime);
            cartMap.put("currentDate",saveCurrentDate);
            cartMap.put("totalQuantity",quantity.getText().toString());
            cartMap.put("totalPrice",totalPrice);

        // Add the cart item to Firestore
            firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                    .collection("User").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                // Show a toast message indicating success
                                Toast.makeText(DetailedActivity.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                                // Finish the activity
                                finish();
                            } else {
                                // Show a toast message if there's an error
                                Toast.makeText(DetailedActivity.this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
