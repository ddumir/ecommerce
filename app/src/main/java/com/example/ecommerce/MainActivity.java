package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.Activity.home;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {
    private EditText Mobile_number;
    private Button Get_opt, Register_1;
    private FirebaseAuth mAuth;
    private ProgressBar pBar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, home.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // test_search = findViewById(R.id.button7);


        mAuth = FirebaseAuth.getInstance();

        Mobile_number = findViewById(R.id.editTextPhoneNumber);
        Get_opt = findViewById(R.id.Get_otp);
        //Register_1 = findViewById(R.id.button_re1);
        pBar = findViewById(R.id.progressBar1);
        pBar.setVisibility(View.INVISIBLE);


        //Mobile_number.setText(getIntent().getStringExtra("phone"));

//        Register_1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Otp_send.this, RegisterUser.class );
//                startActivity(intent);
//            }
//        });


        Get_opt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String phoneno = Mobile_number.getText().toString().trim();
                String mobileRegex = "[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(phoneno);

                if (phoneno.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(MainActivity.this, "Mobile Number is not Valid", Toast.LENGTH_SHORT).show();
                    return;
                } else if (phoneno.length() != 10) {
                    Toast.makeText(MainActivity.this, "Mobile Number is not Valid", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    otpsend();
                }
            }
        });

    }

    private void otpsend() {
        pBar.setVisibility(View.VISIBLE);
        Get_opt.setVisibility(View.INVISIBLE);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                pBar.setVisibility(View.GONE);
                Get_opt.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                pBar.setVisibility(View.GONE);
                Get_opt.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "OTP Successfully send ", Toast.LENGTH_SHORT).show();
                String phoneno = Mobile_number.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, otppage.class);
                intent.putExtra("phone", phoneno);
                intent.putExtra("verificationId", verificationId);
                startActivity(intent);
                finish();
            }
        };
        String phoneno = Mobile_number.getText().toString().trim();
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phoneno)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
