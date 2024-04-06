package com.example.ecommerceapplication.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
/**
 * Activity for adding a story. Allows users to select an image from their device,
 * crop it to the desired aspect ratio, and publish it as a story.
 */
public class AddStoryActivity extends AppCompatActivity {

    private Uri mImageUri;
    String myUrl = "";
    private StorageTask storageTask;
    StorageReference storageReference;

    private ActivityResultLauncher<Uri> cropImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        // Initialize Firebase storage reference
        storageReference = FirebaseStorage.getInstance().getReference("story");

        // Call startCrop() method to initiate cropping
        startCrop();

//        cropImageLauncher = registerForActivityResult(new CropImageContract(), new ActivityResultCallback<CropImageContract.ActivityResult>() {
//            @Override
//            public void onActivityResult(CropImageContract.ActivityResult result) {
//                if (result.isSuccessful()) {
//                    // Use the returned uri.
//                    mImageUri = result.getUriContent();
//                    // Publish the story after selecting and cropping the image
//                    publishStory();
//                } else {
//                    // An error occurred.
//                    Toast.makeText(AddStoryActivity.this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(AddStoryActivity.this, MainActivity.class));
//                    finish();
//                }
//            }
//        });

    }

    private void startCrop() {
        // Start picker to get image for cropping and then use the image in cropping activity.
        cropImageLauncher.launch(null);
    }


    // Publishes the story to the Firebase database after uploading the image to Firebase Storage.
    private void publishStory(){
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting");
        pd.show();

        if (mImageUri != null){
            StorageReference imageReference = storageReference.child(System.currentTimeMillis()
            + "." + getFileExtension(mImageUri));

            storageTask = imageReference.putFile(mImageUri);
            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return  imageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                                .child(myid);

                        String storyid = reference.push().getKey();
                        long timeend = System.currentTimeMillis() + 86400000;

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", myUrl);
                        hashMap.put("timestart", ServerValue.TIMESTAMP);
                        hashMap.put("timeend", timeend);
                        hashMap.put("storyid", storyid);
                        hashMap.put("userid", myid);

                        reference.child(storyid).setValue(hashMap);
                        pd.dismiss();

                        finish();
                    }else {
                        Toast.makeText(AddStoryActivity.this, "Post Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddStoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Retrieves the file extension of the provided URI.
     *
     * @param uri The URI of the file.
     * @return The file extension.
     */
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && requestCode == RESULT_OK){
//
//            CropImage.ActivityResult result = CropImage.getActivityResult(date);
//            mImageUri = result.getUri();
//            // Publish the story after selecting and cropping the image
//            publishStory();
//        }else {
//            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(AddStoryActivity.this, MainActivity.class));
//            finish();
//        }
//    }
}