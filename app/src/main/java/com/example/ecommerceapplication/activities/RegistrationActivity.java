package com.example.ecommerceapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, phone, email, password, reenterpassword;
    Button signUp;
    SharedPreferences sharedPreferences;
    ProgressBar progressBar;

    // Firebase authentication and Firestore instance
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Hide the action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        signUp = findViewById(R.id.button2);

//        // Check if the user is already logged in
//        if(auth.getCurrentUser() != null){
//            // If yes redirect to the MainActivity and close RegistrationActivity
//            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
//            finish();
//        }

        // Initialize EditText fields
        name = findViewById(R.id.name); // Store as "username" in "CurrentUser" Firebase
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        reenterpassword = findViewById(R.id.reenterpassword);

        // Initialize SharedPreferences for onboarding screen
        sharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);

        // Check if it's the first time the user is opening the app
        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);

        if(isFirstTime){
            // If it's the first time, show the onboarding activity
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
            Intent intent = new Intent(RegistrationActivity.this, OnBoardActivity.class);
            startActivity(intent);
            finish();
        }

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

    }



    // Method to handle user registration
    public void createUser() {

        // Retrieve user-entered data
        String userName = name.getText().toString();
        String userPhone = phone.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String userReenter = reenterpassword.getText().toString();

        // Progress Bar
        progressBar.setVisibility(View.VISIBLE);


        // Validate user data
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPhone)) {
            Toast.makeText(this, "Enter Phone", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!userEmail.contains("@")) {
            // Email does not contain "@" symbol
            Toast.makeText(this, "Email must contain '@' symbol", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(userPassword)) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userPassword.length() < 6) {
            Toast.makeText(this, "Password too short, enter minimum 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!userPassword.equals(userReenter)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the password contains at least one uppercase letter
        if (!Pattern.compile("[A-Z]").matcher(userPassword).find()) {
            Toast.makeText(this, "Password must contain at least one uppercase letter", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the password contains at least one lowercase letter
        if (!Pattern.compile("[a-z]").matcher(userPassword).find()) {
            Toast.makeText(this, "Password must contain at least one lowercase letter", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if the password contains at least one digit
        if (!Pattern.compile("\\d").matcher(userPassword).find()) {
            Toast.makeText(this, "Password must contain at least one digit", Toast.LENGTH_SHORT).show();
            return;
        }


        // Register the user with Firebase Authentication
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // If registration is successful, update user info to "CurrentUser" and display a success message and navigate to the on board activity
                            String id = task.getResult().getUser().getUid();
                            UserModel user = new UserModel(id,userName,userEmail,userPhone);

                            // Create a HashMap to store user data
                            HashMap<String, Object> userData = new HashMap<>();
                            userData.put("id", user.getId());
                            userData.put("username", user.getUsername());
                            userData.put("email", user.getEmail());
                            userData.put("phone", user.getPhone());

                            // Save user data to Firestore
                            firestore.collection("CurrentUser")
                                    .document(id)
                                    .set(userData)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // If data is saved successfully
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(RegistrationActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegistrationActivity.this, OnBoardActivity.class));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // If data save fails, display an error message
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(RegistrationActivity.this, "Failed to save user data to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // If registration fails, display an error message
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegistrationActivity.this, "Registration Failed" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    // Method to navigate to the login activity
    public void signin(View view) {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
    }
}