package com.example.videocallapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationFragment extends Fragment {
    private DatabaseReference friendRequestRef, contactsRef, usersRef;
    private RecyclerView notification_list;
    private FirebaseAuth mAuth;
    private String currentUserId;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        notification_list = view.findViewById(R.id.notification_list);
        notification_list.setLayoutManager(new LinearLayoutManager(view.getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        friendRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");


        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(friendRequestRef.child(currentUserId), Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, NotificationViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contacts, NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationViewHolder holder, int i, @NonNull Contacts contacts) {
                holder.acceptBtn.setVisibility(View.VISIBLE);
                holder.declineBtn.setVisibility(View.VISIBLE);

                final String listUserId = getRef(i).getKey();
                DatabaseReference requestTypeRef = getRef(i).child("request_type").getRef();
                requestTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String type = snapshot.getValue().toString();
                            if (type.equals("received")) {
                                holder.cardView.setVisibility(View.VISIBLE);
                                usersRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("image")){
                                            final String imageStr = snapshot.child("image").getValue().toString();

                                            Picasso.get().load(imageStr).into(holder.userProfile);
                                        }
                                        final String nameStr = snapshot.child("name").getValue().toString();
                                        final String bioStr = snapshot.child("status").getValue().toString();
                                        holder.userNameTxt.setText(nameStr);
                                        holder.userBioTxt.setText(bioStr);
                                        Log.d("cek", "input" +nameStr);
                                        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                {

                                                    contactsRef.child(currentUserId).child(listUserId)
                                                            .child("Contact").setValue("Saved")
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        contactsRef.child(listUserId).child(currentUserId)
                                                                                .child("Contact").setValue("Saved")
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            friendRequestRef.child(currentUserId).child(listUserId)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                friendRequestRef.child(listUserId).child(currentUserId)
                                                                                                                        .removeValue()
                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                            @Override
                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                    FancyToast.makeText(getView().getContext(),"Contact Saved..", FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
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
                                            }
                                        });
                                        holder.declineBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                {
                                                    friendRequestRef.child(currentUserId).child(listUserId)
                                                            .removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        friendRequestRef.child(listUserId).child(currentUserId)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            FancyToast.makeText(getView().getContext(),"Friend request cancelled..", FancyToast.LENGTH_SHORT,FancyToast.SUCCESS,false).show();
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

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                holder.cardView.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_design, parent, false);
                NotificationViewHolder viewHolder = new NotificationViewHolder(view);
                return viewHolder;
            }
        };
        notification_list.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTxt, userBioTxt;
        CircleImageView userProfile;
        Button acceptBtn, declineBtn;
        RelativeLayout cardView;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTxt = itemView.findViewById(R.id.name_notification);
            userBioTxt = itemView.findViewById(R.id.bio_notification);
            userProfile = itemView.findViewById(R.id.image_notification);
            acceptBtn = itemView.findViewById(R.id.accept_btn);
            declineBtn = itemView.findViewById(R.id.decline_btn);
            cardView = itemView.findViewById(R.id.rl_design);
        }
    }

}

