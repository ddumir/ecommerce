package com.example.ecommerce;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class otppage extends AppCompatActivity {
    Long timeoutseconds = 60L;
    private TextView Disp_no;
    private LinearLayout Otp_container;
    private String verificationId;
    private TextView Resentbt;
    private Button verifybt;
    private EditText oc1, oc2, oc3, oc4, oc5, oc6;
    private ProgressBar pBar;
    private OTP_Receiver otpReceiver;
    private ProgressBar progressBar2;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String phoneno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otppage);

        mAuth = FirebaseAuth.getInstance();

        Resentbt = findViewById(R.id.btnResend);
        verifybt = findViewById(R.id.verify_otp_button);
        oc1 = findViewById(R.id.otp_edit_box1);
        oc2 = findViewById(R.id.otp_edit_box2);
        oc3 = findViewById(R.id.otp_edit_box3);
        oc4 = findViewById(R.id.otp_edit_box4);
        oc5 = findViewById(R.id.otp_edit_box5);
        oc6 = findViewById(R.id.otp_edit_box6);
        pBar = findViewById(R.id.progressBar2);
        Disp_no = findViewById(R.id.disp_no);
        pBar.setVisibility(View.INVISIBLE);
        autoOtpReceiver();
        editTextinput();

        // Start the resend timer as soon as the activity is created
        startResenttimer();


        Disp_no.setText(String.format(
                "+91-%s", getIntent().getStringExtra("phone")
        ));
        verificationId = getIntent().getStringExtra("verificationId");
        Resentbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                againOtpsend();
            }
        });
        verifybt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pBar.setVisibility(View.VISIBLE);
                verifybt.setVisibility(View.INVISIBLE);
                if (oc1.getText().toString().trim().isEmpty() ||
                        oc2.getText().toString().trim().isEmpty() ||
                        oc3.getText().toString().trim().isEmpty() ||
                        oc4.getText().toString().trim().isEmpty() ||
                        oc5.getText().toString().trim().isEmpty() ||
                        oc6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(otppage.this, "OTP is Invalid", Toast.LENGTH_SHORT).show();
                    pBar.setVisibility(View.INVISIBLE);
                    verifybt.setVisibility(View.VISIBLE);
                    return;
                } else {
                    if (verificationId != null) {
                        String code = oc1.getText().toString().trim() +
                                oc2.getText().toString().trim() +
                                oc3.getText().toString().trim() +
                                oc4.getText().toString().trim() +
                                oc5.getText().toString().trim() +
                                oc6.getText().toString().trim();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        signInWithPhoneAuthCredential(credential);
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
//                                    pBar.setVisibility(View.VISIBLE);
//                                    verifybt.setVisibility(View.INVISIBLE);
                                    Toast.makeText(otppage.this, "Welcome....", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(otppage.this, registrationuser.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);

                                } else {
                                    pBar.setVisibility(View.GONE);
                                    verifybt.setVisibility(View.VISIBLE);
                                    //Toast.makeText(Otp_verify.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                                }
                                // Revert visibility changes
                                pBar.setVisibility(View.INVISIBLE);
                                verifybt.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }
            }
        });


    }

    private void autoOtpReceiver() {
        otpReceiver = new OTP_Receiver();
        this.registerReceiver(otpReceiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
        otpReceiver.initListener(new OTP_Receiver.OtpReceiverListener() {
            @Override
            public void onOtpsuccess(String otp) {
                int o1 = Character.getNumericValue(otp.charAt(0));
                int o2 = Character.getNumericValue(otp.charAt(1));
                int o3 = Character.getNumericValue(otp.charAt(2));
                int o4 = Character.getNumericValue(otp.charAt(3));
                int o5 = Character.getNumericValue(otp.charAt(4));
                int o6 = Character.getNumericValue(otp.charAt(5));

                oc1.setText(String.valueOf(o1));
                oc2.setText(String.valueOf(o2));
                oc3.setText(String.valueOf(o3));
                oc4.setText(String.valueOf(o4));
                oc5.setText(String.valueOf(o5));
                oc6.setText(String.valueOf(o6));
            }

            @Override
            public void onOtpTimeout() {
//            Toast.makeText(Otp_verify.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void againOtpsend() {
        startResenttimer();
        pBar.setVisibility(View.VISIBLE);
        verifybt.setVisibility(View.INVISIBLE);
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                pBar.setVisibility(View.GONE);
                verifybt.setVisibility(View.VISIBLE);
                Toast.makeText(otppage.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                pBar.setVisibility(View.GONE);
                verifybt.setVisibility(View.VISIBLE);
                Toast.makeText(otppage.this, "OTP Successfully send ", Toast.LENGTH_SHORT).show();

            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + getIntent().getStringExtra("phone").trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void startResenttimer() {
        Resentbt.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutseconds--;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Resentbt.setText("Resend OTP in " + timeoutseconds + " Seconds");
                    }
                });

                if (timeoutseconds <= 0) {
                    timeoutseconds = 60L;
                    timer.cancel();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Resentbt.setEnabled(true);
                        }
                    });
                }
            }
        }, 0, 1000);
    }


    private void editTextinput() {
        oc1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                oc2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        oc2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                oc3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        oc3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                oc4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        oc4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                oc5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        oc5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                oc6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (otpReceiver != null) {
            unregisterReceiver(otpReceiver);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Toast.makeText(Otp_verify.this, "Sign with C successful", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = task.getResult().getUser();
                            phoneno = getIntent().getStringExtra("phone");
                            Intent intent = new Intent(otppage.this, registrationuser.class);
                            startActivity(intent);
                            intent.putExtra("phone", phoneno);
                            finish();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(otppage.this, "Signin Failed", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}