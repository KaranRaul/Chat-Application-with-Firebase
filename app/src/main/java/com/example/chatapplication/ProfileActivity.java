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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView userImage;
     TextInputEditText text;
     Button btn;
    FirebaseDatabase database;
    FirebaseStorage storage;

    StorageReference storageReference;
    DatabaseReference reference;

    FirebaseAuth auth;
    FirebaseUser user;
    Boolean imageChoosen;
    String image;



    Uri imgUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userImage = findViewById(R.id.profile_image);
        btn = findViewById(R.id.button);
        text = findViewById(R.id.textName);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

            storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        getUserInfo();
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    public void updateProfile()
    {
        String userName = text.getText().toString();
        reference.child("users").child(user.getUid()).child("userName").setValue(userName);

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
                            String filePath = uri.toString();
                            reference.child("users").child(auth.getUid()).child("images").setValue(filePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ProfileActivity.this, "Write to database successful", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfileActivity.this, "Write to database is not successful", Toast.LENGTH_LONG    ).show();
                                }
                            });
                        }
                    });
                }
            });

        }
        else
        {
            reference.child("users").child(auth.getUid()).child("images").setValue(image);
        }
            Intent i  = new Intent(ProfileActivity.this,MainActivity.class);
           // i.putExtra("name",userName);
            startActivity(i);
        finish();
    }
    public void getUserInfo()
    {
        reference.child("users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("userName").getValue().toString();
                image = snapshot.child("images").getValue().toString();
                text.setText(name);

                if(image.equals("null"))
                {
                    userImage.setImageResource(R.drawable.account);
                }
                else
                {
                    Picasso.get().load(image).into(userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "CANT LOAD IMAGE", Toast.LENGTH_SHORT).show();
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

        if (requestCode == 99 && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();
            Picasso.get().load(imgUri).into(userImage);
            imageChoosen = true;
        }
    }
}