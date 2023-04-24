package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyChatActivity extends AppCompatActivity {
    private ImageView back;
    private TextView name;
    Button fab;
    EditText message;
    private RecyclerView rvChat;
    String userName;
    String otherName;
    boolean notify = false;
    FirebaseDatabase database;
    DatabaseReference reference;

    MessageAdapter adapter;
    List<ModelClass> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chat);
        back = findViewById(R.id.imageViewBack);
        name = findViewById(R.id.tvName);
        fab = findViewById(R.id.btnSend);
        message = findViewById(R.id.etMsg);
        rvChat = findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        Intent i = getIntent();
        userName = i.getStringExtra("userName");
        otherName = i.getStringExtra("otherName");
        name.setText(otherName);
//        System.out.println("user"+userName);
//        System.out.println("other"+otherName);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MyChatActivity.this,MainActivity.class));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;

                System.out.println("this");
                String msg = message.getText().toString();System.out.println("this"+msg);
                if(!msg.equals(""))
                {
                    message.setText("");
                    sendMessege(msg);

                }
            }

        });
        getMessage();


    }

    public void sendMessege(String msg) {
        final String key = reference.child("messages").child(userName).child(otherName).push().getKey();
        final Map<String,Object> messageMap = new HashMap<>();
        messageMap.put("messege",msg);
        messageMap.put("from",userName);
        reference.child("messages").child(userName).child(otherName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    reference.child("messages").child(otherName).child(userName).child(key).setValue(messageMap);
                }
            }
        });
            }

            public void getMessage()
    {
        reference.child("messages").child(userName).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //ModelClass modelClass = new ModelClass(snapshot.getKey(),"abc");
                ModelClass modelClass =snapshot.getValue(ModelClass.class);
                System.out.println("val"+snapshot.getValue());
//                System.out.println("mc:"+previousChildName);
                list.add(modelClass);

                adapter.notifyDataSetChanged();
                rvChat.scrollToPosition(list.size()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new MessageAdapter(list,userName);
        rvChat.setAdapter(adapter);
    }
}