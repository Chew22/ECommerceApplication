package com.example.ecommerceapplication.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.AddressModel;
import com.example.ecommerceapplication.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    // UI elements
    CircleImageView image_profile;
    ImageView close;
    TextView save, tv_change;
    EditText profile_name, profile_email, profile_number, profile_address, profile_bio;

    // Firebase components
    FirebaseUser firebaseUser;
    FirebaseStorage storage;
    FirebaseDatabase database;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration userListener;


    // Variables for image upload
    private Uri mImageUri; // Uri for storing selected image URI
    private StorageTask uploadTask; // Storage task for uploading image
    StorageReference storageReference; // Reference to Firebase Storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize views
        close = findViewById(R.id.close);
        image_profile = findViewById(R.id.image_profile);
        save = findViewById(R.id.save);
        tv_change = findViewById(R.id.tv_change);
        profile_name = findViewById(R.id.profile_name);
        profile_number = findViewById(R.id.profile_number);
        profile_email = findViewById(R.id.profile_email);
        profile_address = findViewById(R.id.profile_address);
        profile_bio = findViewById(R.id.profile_bio);

        // Get current user and storage reference
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Reference to the "CurrentUser" document in Firestore
        DocumentReference docRef = db.collection("CurrentUser").document(FirebaseAuth.getInstance().getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Convert Firestore document snapshot to UserModel
                    UserModel user = documentSnapshot.toObject(UserModel.class);
                    if (user != null) {
                        // Set user data to views
                        profile_name.setText(user.getUsername());
                        profile_email.setText(user.getEmail());
                        profile_number.setText(user.getPhone());
                        profile_bio.setText(user.getBio());

                        // Access address subcollection
                        DocumentReference addressRef = docRef.collection("Addresses").document("Primary");
                        addressRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    // Convert Firestore document snapshot to AddressModel
                                    AddressModel address = documentSnapshot.toObject(AddressModel.class);
                                    if (address != null) {
                                        // Set address data to views
                                        profile_address.setText(address.getUserAddress());
                                    }
                                }
                            }
                        });

                        // Load user profile image using Glide
                        Glide.with(getApplicationContext()).load(user.getProfileImg()).into(image_profile);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to fetch user data
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image picker when the image is clicked
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,33);
            }
        });

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open image picker when the image is clicked
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,33);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Show "Saved" message
                Toast.makeText(EditProfileActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }


    private void updateProfile(String profile_name, String profile_address, String profile_number, String profile_bio, String profile_email, Uri profile_image_uri) {
        // Access Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "CurrentUser" document in Firestore
        DocumentReference docRef = db.collection("CurrentUser").document(FirebaseAuth.getInstance().getUid());

        // Update user data using a HashMap
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", profile_name);
        userData.put("phone", profile_number);
        userData.put("bio", profile_bio);
        userData.put("email", profile_email);

        // Add profile image URI if available
        if (profile_image_uri != null) {
            userData.put("profileImg", profile_image_uri.toString());
        }

        // Update user address data using a HashMap
        Map<String, Object> addressData = new HashMap<>();
        addressData.put("userAddress", profile_address);

        // Create a batch write for updating both user and address data
        WriteBatch batch = db.batch();

        // Update the user document
        batch.update(docRef, userData);

        // Reference to the user's address document within a subcollection named "Addresses"
        DocumentReference addressRef = docRef.collection("Addresses").document("Primary");

        // Update the address document
        batch.set(addressRef, addressData, SetOptions.merge());

        // Commit the batch write
        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Handle successful update
                Log.d(TAG, "Profile updated successfully");
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle update failure
                Log.e(TAG, "Error updating profile", e);
                Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        // Get file extension from URI
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if result is OK and data URI is not null
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected image from the data
            Uri uri = data.getData();

            // Set the selected image to the ImageView
            image_profile.setImageURI(uri);

            // Call updateProfile method with profile image URI
            updateProfile(profile_name.getText().toString(),
                    profile_address.getText().toString(),
                    profile_number.getText().toString(),
                    profile_bio.getText().toString(),
                    profile_email.getText().toString(),
                    uri);
        } else {
            // If result is not OK or data URI is null, show a toast message indicating no image selected
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}