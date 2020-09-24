package com.example.videocallapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class FindPeopleActivity extends AppCompatActivity {

    private RecyclerView findFriendList;
    private EditText searchET;
    private TextView titleSearch;
    private ImageView searchBtn,Backbtn;
    private String str = "";
    private Boolean cek = false;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        searchET = findViewById(R.id.search_user);
        titleSearch = findViewById(R.id.title_find);
        searchBtn = findViewById(R.id.search_btn);
        Backbtn = findViewById(R.id.back_btn);

        findFriendList = findViewById(R.id.find_friend_list);
        findFriendList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchET.getText().toString().equals("")){
                    Toasty.info(FindPeopleActivity.this,"Not working",Toasty.LENGTH_SHORT,true).show();
                }else{
                    str = s.toString();
                    onStart();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleSearch.setVisibility(View.GONE);
                searchET.setVisibility(View.VISIBLE);
                cek = true;
            }
        });

        TombolBack();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options = null;
        if (str.equals("")){
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(usersRef, Contacts.class)
                    .build();
        }else{
            options = new FirebaseRecyclerOptions.Builder<Contacts>()
                    .setQuery(usersRef
                            .orderByChild("name")
                                    .startAt(str)
                                    .endAt(str + "\uf8ff")
                            , Contacts.class)
                    .build();
        }

        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull final Contacts contacts) {
                holder.userNameTxt.setText(contacts.getName());
                Picasso.get().load(contacts.getImage()).into(holder.userProfile);
                holder.userBioTxt.setText(contacts.getStatus());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String visit_user_id = getRef(position).getKey();

                        Intent intent = new Intent(FindPeopleActivity.this, ProfileActivity.class);
                        intent.putExtra("visit_user_id", visit_user_id);
                        intent.putExtra("profile_image", contacts.getImage());
                        intent.putExtra("profile_name", contacts.getName());
                        intent.putExtra("profile_status", contacts.getStatus());

                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_people_design, parent ,false);
                FindFriendViewHolder viewHolder = new FindFriendViewHolder(view);
                return viewHolder;
            }
        };
        findFriendList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userNameTxt, userBioTxt;
        CircleImageView userProfile;
//        Button videocallBtn;
        RelativeLayout cardView;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTxt = itemView.findViewById(R.id.name_find);
            userBioTxt = itemView.findViewById(R.id.bio_find);
            userProfile = itemView.findViewById(R.id.image_find);
//            videocallBtn = itemView.findViewById(R.id.vidcall_btn);
            cardView = itemView.findViewById(R.id.rl_design_find);

//            videocallBtn.setVisibility(View.GONE);
        }
    }

    private void TombolBack(){
        Backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (cek == true){
            searchET.setVisibility(View.GONE);
            titleSearch.setVisibility(View.VISIBLE);

            cek = false;
        }else{
            Intent intent = new Intent(FindPeopleActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}