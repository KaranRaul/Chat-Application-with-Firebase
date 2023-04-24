    package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

    public class LoginActivity extends AppCompatActivity {

     TextInputEditText etEmail, etPass;

    private Button btnSignIn, btnSignUp;
    TextView TVforgot;
    FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.itEmail);
        etPass = findViewById(R.id.itPass);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignUp = findViewById(R.id.btnSignup);

        auth = FirebaseAuth.getInstance();


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, pass;
                email = etEmail.getText().toString();
                pass = etPass.getText().toString();
                if(!(email.equals("") || pass.equals(""))) {
                    signIn(email, pass);

                }
                else
                {
                    Toast.makeText(LoginActivity.this, "ENTER THE DATA", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });


    }

        public void signIn(String email, String pass)
        {
            auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Intent i = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
                        Toast.makeText(LoginActivity.this,"Sign in Successful ",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Sign in is not Successful ",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
}