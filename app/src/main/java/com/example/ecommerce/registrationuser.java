package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Activity.home;
import com.example.ecommerce.Helper.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class registrationuser extends AppCompatActivity {
    private FirebaseAuth mAuth; // Add this line to declare mAuth
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText phoneNumberEditText, usernameEditText, emailEditText;
    private Button registerButton;
    private Timestamp currentTimestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrationuser);


        mAuth = FirebaseAuth.getInstance(); // Add this line to initialize mAuth
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        phoneNumberEditText = findViewById(R.id.mobileNumberEditText);
        usernameEditText = findViewById(R.id.userIdEditText);
        emailEditText = findViewById(R.id.emailEditText);
        registerButton = findViewById(R.id.regis_but);

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userph_no = user.getPhoneNumber();
            if (userph_no != null) {
                phoneNumberEditText.setText(userph_no);
            } else {
                phoneNumberEditText.setText("");
            }
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserExistsAndRedirect();
    }

    private void registerUser() {
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        String userId = mAuth.getCurrentUser().getUid();

        // Move intent creation outside Firestore check
        Intent intent = new Intent(registrationuser.this, MainActivity.class);

        // Check if the user already exists in Firestore
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // User already exists, navigate to MainActivity
                                startActivity(intent);
                                finish();
                            } else {
                                // User does not exist, proceed with registration
                                UserModel user = new UserModel(phoneNumber, username, email, Timestamp.now());
                                storeUserDetails(userId, user);
                                Toast.makeText(registrationuser.this, "User details saved successfully", Toast.LENGTH_SHORT).show();
                                // Redirect to MainActivity after storing user details
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Log.w("RegisterUser", "Error checking user document", task.getException());
                        }
                    }
                });
    }

    private void checkUserExistsAndRedirect() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // User exists in Firestore, redirect to MainActivity
                                    navigateToMainActivity();
                                } else {
                                    // User does not exist, log and proceed
                                    Log.d("RegisterUser", "User does not exist in Firestore");
                                }
                            } else {
                                Log.w("RegisterUser", "Error checking user document", task.getException());
                            }
                        }
                    });
        }
    }

    private void storeUserDetails(String userId, UserModel user) {
        db.collection("users")
                .document(userId)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("RegisterUser", "User details stored in Firestore");
                        } else {
                            Log.w("RegisterUser", "Error adding document", task.getException());
                        }
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(registrationuser.this, home.class);
        startActivity(intent);
        finish();
    }
}