package com.example.ecommerceapplication.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.UserModel;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {


    // Variable declarations
    private static final double SHIPPING_COST = 5.00;
    Toolbar toolbar;
    TextView subTotal, discount, shipping, total;
    Button paymentBtn;
    MaterialCardView bankCard;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Set up toolbar
        toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Back Pressed On Toolbar
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Initialize views
        subTotal = findViewById(R.id.sub_total);
        discount = findViewById(R.id.discount);
        shipping = findViewById(R.id.shipping);
        total = findViewById(R.id.total);
        paymentBtn = findViewById(R.id.pay_btn);
        bankCard = findViewById(R.id.bank_card);

        // Set click listener to navigate to CardPaymentActivity
        bankCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, CardPaymentActivity.class);
                startActivity(intent);
            }
        });

        // Retrieve the original amount from the intent
        double subTotalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);
        Log.d("PaymentActivity", "TotalAmount received: " + subTotalAmount);

        // Set sub-total
        subTotal.setText("RM " + String.format("%.2f", subTotalAmount));

        // Set shipping fee
        shipping.setText("RM " + String.format("%.2f", SHIPPING_COST));

        // Calculate total with shipping fee
        double totalAmount = subTotalAmount + SHIPPING_COST;
        total.setText("RM " + String.format("%.2f", totalAmount));

        // Set onClickListener for payment button
        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to initiate payment
                paymentMethod(totalAmount);
            }
        });

    }

    // Method to initiate payment
    private void paymentMethod(double amount) {
        final Activity activity = PaymentActivity.this;

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        if (firebaseUser != null) {
            // Retrieve the UserModel from Firestore
            DocumentReference userRef = firestore.collection("CurrentUser").document(firebaseUser.getUid());
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    UserModel userModel = documentSnapshot.toObject(UserModel.class);
                    if (userModel != null) {
                        String userEmail = userModel.getEmail();
                        String userContact = userModel.getPhone();
                        if (userEmail != null && userContact != null) {
                            try {
                                Checkout checkout = new Checkout();
                                checkout.setKeyID("rzp_test_1WsjV97GWo9w9D");
                                checkout.setImage(R.drawable.ic_baseline_loginout);

                                JSONObject options = new JSONObject();
                                // Set Company Name
                                options.put("name", "ShopLoca E-Commerce App");
                                // Reference number
                                options.put("description", "Reference No. #123456");
                                // Image to be displayed
                                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                                // Currency type
                                options.put("currency", "INR");
                                // Convert amount to cents
                                double paymentAmount = amount * 100; // In cents for Razorpay
                                // Set payment amount
                                options.put("amount", paymentAmount);

                                // Prefill email and contact information
                                JSONObject preFill = new JSONObject();
                                preFill.put("email", userEmail);
                                preFill.put("contact", userContact);
                                options.put("prefill", preFill);

                                // Open Razorpay Checkout activity
                                checkout.open(activity, options);
                            } catch (Exception e) {
                                Log.e("TAG", "Error in starting Razorpay Checkout", e);
                            }
                        }
                    }
                } else {
                    Log.e("TAG", "User document does not exist");
                }
            }).addOnFailureListener(e -> {
                Log.e("TAG", "Error retrieving user information: " + e.getMessage());
            });
        }
    }

    @Override
    public void onPaymentError(int errorCode, String errorDescription) {
        Toast.makeText(this, "Payment Error: " + errorDescription, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Success", Toast.LENGTH_SHORT).show();

    }
}