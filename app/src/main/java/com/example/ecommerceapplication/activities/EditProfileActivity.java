package com.example.ecommerceapplication.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.AddressModel;
import com.example.ecommerceapplication.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 33;
    private static final String TAG = "EditProfileActivity";

    // UI elements
    CircleImageView image_profile;
    ImageView close;
    TextView save, tv_change;
    EditText profile_name, profile_email, profile_number, profile_address, profile_bio;

    // Firebase components
    FirebaseUser firebaseUser;
    FirebaseStorage storage;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    // Variables for image upload
    private Uri mImageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask; // Declare uploadTask here
    StorageReference storageReference;


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

        // Initialize Firebase components
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Retrieve user data from Firestore
        getUserData();

        // Set click listeners
        setClickListeners();
    }

    private void getUserData() {
        // Retrieve user data from Firestore and populate the views
        DocumentReference docRef = db.collection("CurrentUser").document(FirebaseAuth.getInstance().getUid());
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserModel user = documentSnapshot.toObject(UserModel.class);
                if (user != null) {
                    profile_name.setText(user.getUsername());
                    profile_email.setText(user.getEmail());
                    profile_number.setText(user.getPhone());
                    profile_bio.setText(user.getBio());
                    Glide.with(getApplicationContext()).load(user.getProfileImg()).into(image_profile);

                    // Retrieve address data
                    getAddressData(docRef);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle failure to fetch user data
            Log.e(TAG, "Error fetching user data", e);
        });
    }

    private void getAddressData(DocumentReference docRef) {
        // Retrieve address data from Firestore and populate the views
        DocumentReference addressRef = docRef.collection("Addresses").document("Primary");
        addressRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                AddressModel address = documentSnapshot.toObject(AddressModel.class);
                if (address != null) {
                    profile_address.setText(address.getUserAddress());
                }
            }
        }).addOnFailureListener(e -> {
            // Handle failure to fetch address data
            Log.e(TAG, "Error fetching address data", e);
        });
    }

    private void setClickListeners() {
        close.setOnClickListener(v -> finish());

        image_profile.setOnClickListener(v -> openImagePicker());

        tv_change.setOnClickListener(v -> openImagePicker());

        save.setOnClickListener(v -> {
            // Update profile
            updateProfile();

        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);

    }


    private void updateProfile() {
        String name = profile_name.getText().toString();
        String email = profile_email.getText().toString();
        String number = profile_number.getText().toString();
        String address = profile_address.getText().toString();
        String bio = profile_bio.getText().toString();

        // Update profile data in Firestore
        DocumentReference docRef = db.collection("CurrentUser").document(FirebaseAuth.getInstance().getUid());
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", name);
        userData.put("email", email);
        userData.put("phone", number);
        userData.put("bio", bio);
        docRef.update(userData).addOnSuccessListener(aVoid -> {
            // Update successful
            Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // Update failed
            Log.e(TAG, "Error updating profile", e);
            Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        });

        // Update address data in Firestore
        DocumentReference addressRef = docRef.collection("Addresses").document("Primary");
        Map<String, Object> addressData = new HashMap<>();
        addressData.put("userAddress", address);
        addressRef.set(addressData, SetOptions.merge()).addOnSuccessListener(aVoid -> {
            // Address update successful
            Log.d(TAG, "Address updated successfully");
        }).addOnFailureListener(e -> {
            // Address update failed
            Log.e(TAG, "Error updating address", e);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            image_profile.setImageURI(mImageUri);

            // Upload the image to Firebase Storage
            uploadImageToFirebaseStorage();
        }
    }

    private void uploadImageToFirebaseStorage() {
        if (mImageUri != null) {
            // Create a storage reference with a unique name
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));

            // Upload the image to Firebase Storage
            uploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload successful
                        Toast.makeText(EditProfileActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                        // Get the download URL of the uploaded image
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update the profile image URI in Firestore with the download URL
                            updateProfileImageUri(uri.toString());
                        }).addOnFailureListener(e -> {
                            // Handle failure to get download URL
                            Log.e(TAG, "Error getting download URL", e);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Image upload failed
                        Log.e(TAG, "Error uploading image", e);
                        Toast.makeText(EditProfileActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private String getFileExtension(Uri uri) {
        // Get file extension from URI
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void updateProfileImageUri(String downloadUrl) {
        // Update the profile image URI in Firestore with the download URL
        DocumentReference docRef = db.collection("CurrentUser").document(FirebaseAuth.getInstance().getUid());
        docRef.update("profileImg", downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    // Profile image URI update successful
                    Log.d(TAG, "Profile image URI updated successfully");
                })
                .addOnFailureListener(e -> {
                    // Profile image URI update failed
                    Log.e(TAG, "Error updating profile image URI", e);
                });
    }


}
