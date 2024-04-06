package com.example.ecommerceapplication.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapplication.R;
import com.example.ecommerceapplication.models.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    // UI elements
    EditText email, password;
    ProgressBar progressBar;
    TextView txtForgotPassword, google;

    // Firebase
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    GoogleSignInClient mGoogleSignInClient;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getSupportActionBar().hide();

        // Initialize Firebase instance
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        google = findViewById(R.id.google);

        // Reference EditText fields from layout file
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        // Progress Bar
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        // Progress Dialog
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("We are logging you in ");

        // Forgot Password
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });
    }

    int RC_SIGN_IN = 40;

    private void signin() {
        // Sign In with google
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){

            Task<GoogleSignInAccount>  task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuth(account.getIdToken());

            }catch (ApiException e){
                throw new RuntimeException(e);
            }
        }
    }


    // Method is invoke to handle sign-in process when user taps on the sign in button
    public void signin(View view) {

        // Retrieve user-entered email and password
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        // Validate email and password fields
        if (TextUtils.isEmpty(userEmail)) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
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

        // Sign in user with Firebase Authentication
        auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // Check if sign-in was successful
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        } else {
                            // Display error message if sign-in fails
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        // auth.signInWithEmailLink()
    }



    private void firebaseAuth(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseUser user = auth.getCurrentUser();

                            UserModel userModel = new UserModel();
                            userModel.setId(user.getUid());
                            userModel.setUsername(user.getDisplayName());
                            userModel.setProfileImg(user.getPhotoUrl().toString());

                            firestore.collection("CurrentUser")
                                    .document(user.getUid())
                                    .set(userModel);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to handle user registration
    public void signup(View view) {
        // Start registration activity when the user taps on the "sign up" button
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
    }
}
