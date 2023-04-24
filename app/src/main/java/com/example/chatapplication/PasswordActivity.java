package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class PasswordActivity extends AppCompatActivity {
    TextInputEditText email;
    Button reset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        email = findViewById(R.id.email);
        reset = findViewById(R.id.button2);
    }
}