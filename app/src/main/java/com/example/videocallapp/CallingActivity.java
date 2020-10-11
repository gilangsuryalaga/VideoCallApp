package com.example.videocallapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class CallingActivity extends AppCompatActivity {

    private TextView nameContact;
    private BlurImageView backgroundIV;
    private ImageView cancelbtn, makeCallBtn;

    private String receiverUserId = "", receiverUserImage = "", receiverUserName = "";
    private String senderId = "", senderUserImage = "", senderUserName = "";

    private DatabaseReference usersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        nameContact = findViewById(R.id.name_contact);
        backgroundIV = findViewById(R.id.background_image);
        cancelbtn = findViewById(R.id.cancel_call);
        makeCallBtn = findViewById(R.id.make_call);

        getAndSetuserProfileInfo();
    }

    private void getAndSetuserProfileInfo() {

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(receiverUserId).exists()) {
                    receiverUserImage = snapshot.child(receiverUserId).child("image").getValue().toString();
                    receiverUserName = snapshot.child(receiverUserId).child("name").getValue().toString();

                    nameContact.setText(receiverUserName);
                    Picasso.get().load(receiverUserImage).placeholder(R.drawable.profile_image).into(backgroundIV);
                }
                if (snapshot.child(senderId).exists()) {
                    senderUserImage = snapshot.child(senderId).child("image").getValue().toString();
                    senderUserName = snapshot.child(senderId).child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        usersRef.child(receiverUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.hasChild("Calling") && !snapshot.hasChild("Ringing")){
                            final HashMap<String, Object> callinginfo = new HashMap<>();
                            callinginfo.put("calling",receiverUserId);

                            usersRef.child(senderId).child("Calling")
                                    .updateChildren(callinginfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                final HashMap<String, Object> ringinginfo = new HashMap<>();
                                                ringinginfo.put("ringing",senderId);

                                                usersRef.child(receiverUserId)
                                                        .child("Ringing")
                                                        .updateChildren(ringinginfo);
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}