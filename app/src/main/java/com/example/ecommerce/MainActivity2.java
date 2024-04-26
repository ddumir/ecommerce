package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
    public void to_reg(View vi){
        Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, registrationuser.class);
        startActivity(intent);
    }
}