package com.example.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseRegistrar;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.viewHolder> {
    List<String> userList;
    String userName;
    Context mContext;
    FirebaseDatabase database;
    DatabaseReference reference;


    public userAdapter(List<String> userList, String userName, Context mContext) {
        this.userList = userList;
        this.userName = userName;
        this.mContext = mContext;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card,parent,false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        reference.child("users").child(userList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String otherName = snapshot.child("userName").getValue().toString();
                String imageURL = snapshot.child("images").getValue().toString();

                System.out.println(otherName);
                holder.textView.setText(otherName);

                if (!imageURL.equals("null")) {
                    Picasso.get().load(imageURL).into(holder.userImage);
                }

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(mContext,MyChatActivity.class);
                        i.putExtra("userName",userName);
                        i.putExtra("otherName",otherName);
                        mContext.startActivity(i);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder
    {
        private TextView textView;
        private CircleImageView userImage;
        private CardView cardView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            userImage = itemView.findViewById(R.id.imageViewUsers);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

}
