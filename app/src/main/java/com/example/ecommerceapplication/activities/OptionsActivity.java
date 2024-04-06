package com.example.ecommerceapplication.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ecommerceapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mahfa.dnswitch.DayNightSwitch;
import com.mahfa.dnswitch.DayNightSwitchListener;

public class OptionsActivity extends AppCompatActivity {

    private DayNightSwitch dayNightSwitch;
    private boolean nightModeEnabled = false;

    AlertDialog.Builder reset_alert;
    LayoutInflater inflater;

    FirebaseAuth firebaseAuth;

    private TextView logout, updateEmailBtn, deleteAccountBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        firebaseAuth = FirebaseAuth.getInstance();

        reset_alert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        // Setting up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Options");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initializing TextViews for logout and settings options
        logout = findViewById(R.id.logout);
        updateEmailBtn = findViewById(R.id.updateEmailMenu);
        deleteAccountBtn = findViewById(R.id.delete_account_menu);

        // Initializing day/night mode switch
        dayNightSwitch = (DayNightSwitch)findViewById(R.id.dayNight);
        dayNightSwitch.setDuration(450);

        dayNightSwitch.setListener(new DayNightSwitchListener() {
            @Override
            public void onSwitch(boolean isNight) {
                // Toggle the day/night mode based on the state of the Switch
                if (isNight) {
                    // Apply night theme
                    Toast.makeText(OptionsActivity.this, "Night mode selected", Toast.LENGTH_SHORT).show();
                    setTheme(R.style.AppThemeNight);
                } else {
                    // Apply day theme
                    Toast.makeText(OptionsActivity.this, "Day mode selected", Toast.LENGTH_SHORT).show();
                    setTheme(R.style.AppThemeDay);
                }

            }
        });

        // Handling toolbar navigation button click
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Click listener for logout option
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Signing out the user and redirecting to the login activity
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(OptionsActivity.this, LoginActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        updateEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                View view = inflater.inflate(R.layout.reset_pop, null);
                reset_alert.setTitle("Update Email? ")
                        .setMessage("Enter Your New Email ")
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText email = view.findViewById(R.id.reset_email_pop);
                                if (email.getText().toString().isEmpty()){
                                    email.setError("Required Field");
                                    return;
                                }

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String newEmail = email.getText().toString();

                                ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                                        .setUrl("https://www.example.com/?email=" + newEmail)
                                        .setHandleCodeInApp(true)
                                        .setIOSBundleId("com.example.ios")
                                        .setAndroidPackageName(
                                                "com.example.android",
                                                true, /* installIfNotAvailable */
                                                "12" /* minimumVersion */)
                                        .build();

                                user.verifyBeforeUpdateEmail(newEmail, actionCodeSettings)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                // Verification email sent successfully
                                                Toast.makeText(OptionsActivity.this, "Verification email sent to new address", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Failed to send verification email
                                                Toast.makeText(OptionsActivity.this, "Failed to send verification email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }).setNegativeButton("Cancel", null)
                        .setView(view)
                        .create().show();

            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a confirmation dialog
                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(OptionsActivity.this);
                confirmationDialog.setTitle("Confirm Account Deletion");
                confirmationDialog.setMessage("Are you sure you want to delete your account?");
                confirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User confirmed account deletion, proceed with deletion
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            // Attempt to delete the user account
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // User account successfully deleted
                                        Log.d(TAG, "User account deleted.");
                                        // Optionally, navigate to a different screen or perform additional actions
                                        // For example, you might navigate back to the login screen
                                        startActivity(new Intent(OptionsActivity.this, LoginActivity.class));
                                        finish(); // Finish the current activity to prevent the user from going back
                                    } else {
                                        // Failed to delete user account
                                        Log.w(TAG, "Failed to delete user account.", task.getException());
                                        // Display an error message to the user
                                        Toast.makeText(OptionsActivity.this, "Failed to delete user account.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
                confirmationDialog.setNegativeButton("No", null); // If user cancels, do nothing
                confirmationDialog.show(); // Display the confirmation dialog
            }
        });

    }

}
