package com.example.ecommerceapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.NewProductsModel;
import com.example.ecommerceapplication.models.PostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Activity to display detailed information about a product and add it to the cart or proceed to payment.
 */
public class DetailedActivity extends AppCompatActivity {

    // UI elements
    ImageView detailedImg;
    TextView rating, name, description, price, quantity;
    RatingBar ratingBar;
    Button addToCart, buyNow;
    ImageView addItems, removeItems;
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
        detailedImg = findViewById(R.id.detailed_img);
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

        // Retrieve the ("detailed") from the intent and assigns it to the variable obj
        final Object obj = getIntent().getSerializableExtra("detailed");

        // Check the type of product and assign it to the appropriate model
        if (obj instanceof NewProductsModel) {
            newProductsModel = (NewProductsModel) obj;
        } else if (obj instanceof PostModel) {
            postModel = (PostModel) obj;
        }

            // New Products
            if (newProductsModel != null) {
                Glide.with(getApplicationContext()).load(newProductsModel.getImg_url()).into(detailedImg); // Display image
                name.setText(newProductsModel.getName()); // Display name
                rating.setText(newProductsModel.getRating()); // Diasply rating
                description.setText(newProductsModel.getDescription()); // Display description
                price.setText(String.format("%.2f", newProductsModel.getPrice())); // Display price
                float ratingValue = Float.parseFloat(newProductsModel.getRating());
                ratingBar.setRating(ratingValue);

                totalPrice = newProductsModel.getPrice() * totalQuantity;
            }

            // Post Products
            if (postModel != null) {
                Glide.with(getApplicationContext()).load(postModel.getProductImages()).into(detailedImg);
                name.setText(postModel.getProductName());
                rating.setText(postModel.getRating());
                description.setText(postModel.getProductDescription());
                price.setText(String.format("%.2f", postModel.getPrice())); // Display price
                float ratingValue = Float.parseFloat(postModel.getRating());
                ratingBar.setRating(ratingValue);

                totalPrice = postModel.getPrice() * totalQuantity;
            }


        // Set up click listeners for buy now buttons
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start PaymentActivity with the selected product
                Intent intent = new Intent(DetailedActivity.this , AddressActivity.class);
                if (newProductsModel != null){
                    intent.putExtra("item", newProductsModel);
                }
                if (postModel != null){
                    intent.putExtra("item", postModel);
                }
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
            cartMap.put("productName",name.getText().toString());
        cartMap.put("productImage", newProductsModel != null ? newProductsModel.getImg_url() : postModel.getProductImages());
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
