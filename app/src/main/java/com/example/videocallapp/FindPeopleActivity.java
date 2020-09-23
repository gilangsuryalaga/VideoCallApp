package com.example.videocallapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FindPeopleActivity extends AppCompatActivity {

    private RecyclerView findFriendList;
    private EditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        searchET = findViewById(R.id.search_user);
        findFriendList = findViewById(R.id.find_friend_list);
        findFriendList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView userNameTxt, userBioTxt;
        ImageView userProfile;
        Button videocallBtn;
        RelativeLayout cardView;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTxt = itemView.findViewById(R.id.name_contact);
            userBioTxt = itemView.findViewById(R.id.bio_contact);
            userProfile = itemView.findViewById(R.id.image_contact);
            videocallBtn = itemView.findViewById(R.id.vidcall_btn);
            cardView = itemView.findViewById(R.id.rl_design2);
        }
    }
}