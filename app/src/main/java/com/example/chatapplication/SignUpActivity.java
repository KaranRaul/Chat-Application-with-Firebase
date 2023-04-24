package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {
    private CircleImageView image;
    TextInputEditText etEmail, etPass,etName;
    Boolean imageChoosen = false;
    private Button btnSignUp;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage storage;
    StorageReference storageReference;

    Uri imgUri;

    boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        image = findViewById(R.id.profile_image);
        etEmail = findViewById(R.id.textEmail);
        etPass = findViewById(R.id.textPass);
        etName = findViewById(R.id.textName);
        btnSignUp = findViewById(R.id.button);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference  = database.getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();
                String name = etName.getText().toString();

                if(!email.equals("") && !pass.equals("") && !name.equals(""))
                {
                    signUp(email,pass,name);
                }


            }
        });

    }

    public void signUp(String email, String pass, String name)
    {
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    reference.child("users").child(auth.getUid()).child("userName").setValue(name);
                    Toast.makeText(SignUpActivity.this, "REGISTERED SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                    if(imageChoosen)
                    {
                        UUID random = UUID.randomUUID();
                        final String imgName = "images/"+random+".jpg";
                         storageReference.child(imgName).putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                StorageReference myRef = storage.getReference(imgName);
                                myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        flag = true;
                                        String filePath = uri.toString();
                                        //System.out.println("path"+filePath);
                                        //reference.child("users").child(auth.getUid()).child("images").setValue("null");
                                        reference.child("users").child(auth.getUid()).child("images").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignUpActivity.this, "Write to database successful", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                reference.child("users").child(auth.getUid()).child("images").setValue("null");
                                                Toast.makeText(SignUpActivity.this, "Write to database is not successfully done", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                    if(!flag ) {
                        Toast.makeText(SignUpActivity.this, "IMAGE NOT SAVED", Toast.LENGTH_SHORT).show();
                        reference.child("users").child(auth.getUid()).child("images").setValue("null");
                    }

                    Intent i  = new Intent(SignUpActivity.this,MainActivity.class);
                    i.putExtra("name",name);
                    startActivity(i);
                    finish();
                }
                else
                    Toast.makeText(SignUpActivity.this, "NOT REGISTERED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void imageChooser()
    {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i,99);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 99 && resultCode == RESULT_OK && data != null)
        {
            imgUri = data.getData();
            Picasso.get().load(imgUri).into(image);
            imageChoosen = true;
        }
    }
}