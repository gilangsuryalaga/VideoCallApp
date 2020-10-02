package com.example.videocallapp;

import androidx.annotation.ColorLong;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID = "", receiverUserImage = "", receiverUserName = "", receiverUserStatus = "";
    private BlurImageView backgroundIV;
    private CircleImageView background_profile_view;
    private TextView user_name, user_status;
    private Button add_friend, decline_friend_request;

    private FirebaseAuth mAuth;
    private String senderUserId;
    private String currentState = "new";

    private DatabaseReference friendRequestRef, contactsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserId = mAuth.getCurrentUser().getUid();

        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        background_profile_view = (CircleImageView) findViewById(R.id.background_profile_view);
        user_name = findViewById(R.id.name_profile);
        user_status = findViewById(R.id.status_profile);
        add_friend = (Button) findViewById(R.id.add_friend);
        decline_friend_request =(Button) findViewById(R.id.decline_friend);
        backgroundIV = (BlurImageView) findViewById(R.id.background_image);


        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        receiverUserImage = getIntent().getExtras().get("profile_image").toString();
        receiverUserName = getIntent().getExtras().get("profile_name").toString();
        receiverUserStatus = getIntent().getExtras().get("profile_status").toString();
        manageClickEvenets();
        Picasso.get().load(receiverUserImage).into(background_profile_view);
        Picasso.get().load(receiverUserImage).transform(new BlurTransformation(ProfileActivity.this, 25, 1)).into(backgroundIV);
        user_name.setText(receiverUserName);
        user_status.setText(receiverUserStatus);

        backgroundIV.setBlur(4);


    }


    private void manageClickEvenets() {

        friendRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot snapshot) {
                        if (snapshot.hasChild(receiverUserID)) {
                            String requestType = snapshot.child(receiverUserID).child("request_type").getValue().toString();
                            if (requestType.equals("sent")) {

                                currentState = "request_sent";
                                add_friend.setText("Cancel Friend Request");
                                add_friend.setBackgroundResource(R.drawable.decline_btn);


                            } else if (requestType.equals("received")) {

                                currentState = "request_received";
                                add_friend.setText("Accept Friend Request");
//                                add_friend.setBackgroundColor(Color.GREEN);
                                add_friend.setBackgroundResource(R.drawable.accept_btn);

                                decline_friend_request.setVisibility(View.VISIBLE);
                                decline_friend_request.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelFriendRequest();
                                    }
                                });
                            } else {
                                contactsRef.child(senderUserId)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if (snapshot.hasChild(receiverUserID)) {
                                                    currentState = "friends";
                                                    add_friend.setText("Delete Contact");
                                                    add_friend.setBackgroundResource(R.drawable.decline_btn);

                                                } else {
                                                    currentState = "new";
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if (senderUserId.equals(receiverUserID)) {
            add_friend.setVisibility(View.GONE);
        } else {
            add_friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentState.equals("new")) {
                        SendFriendRequest();
                    }
                    if (currentState.equals("request_sent")) {
                        CancelFriendRequest();
                    }
                    if (currentState.equals("request_received")) {
                        AcceptFriendRequest();
                    }
                    if (currentState.equals("request_sent")) {
                        CancelFriendRequest();
                    }
                }
            });
        }
    }

    private void AcceptFriendRequest() {

        contactsRef.child(senderUserId).child(receiverUserID)
                .child("Contact").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactsRef.child(receiverUserID).child(senderUserId)
                                    .child("Contact").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                friendRequestRef.child(senderUserId).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    friendRequestRef.child(receiverUserID).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        currentState = "friends";
                                                                                        add_friend.setText("Delete Contact");
                                                                                        add_friend.setBackgroundResource(R.drawable.decline_btn);

                                                                                        decline_friend_request.setVisibility(View.GONE);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {
        friendRequestRef.child(senderUserId).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            friendRequestRef.child(receiverUserID).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                currentState = "new";
                                                add_friend.setText("Add Friend");
                                                add_friend.setBackgroundResource(R.drawable.accept_btn);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendFriendRequest() {
        friendRequestRef.child(senderUserId).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        friendRequestRef.child(receiverUserID).child(senderUserId)
                                .child("request_type").setValue("received")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            currentState = "request_sent";
                                            add_friend.setText("Cancel Friend request");
                                            add_friend.setBackgroundResource(R.drawable.decline_btn);
                                            FancyToast.makeText(ProfileActivity.this, "Friend request sent...", FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show();
                                        }
                                    }
                                });
                    }

                });
    }

}