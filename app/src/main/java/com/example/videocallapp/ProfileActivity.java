package com.example.videocallapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.jgabrielfreitas.core.BlurImageView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;

public class ProfileActivity extends AppCompatActivity {

    private  String receiverUserID = "", receiverUserImage = "", receiverUserName = "", receiverUserStatus = "";
    private BlurImageView backgroundIV;
    private CircleImageView background_profile_view;
    private TextView user_name,user_status;
    private Button add_friend, cancel_friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        background_profile_view = (CircleImageView) findViewById(R.id.background_profile_view);
        user_name = findViewById(R.id.name_profile);
        user_status = findViewById(R.id.status_profile);
        add_friend = findViewById(R.id.add_friend);
        cancel_friend = findViewById(R.id.decline_btn);
        backgroundIV = (BlurImageView) findViewById(R.id.background_image);


        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        receiverUserImage = getIntent().getExtras().get("profile_image").toString();
        receiverUserName = getIntent().getExtras().get("profile_name").toString();
        receiverUserStatus = getIntent().getExtras().get("profile_status").toString();

        Picasso.get().load(receiverUserImage).into(background_profile_view);
        Picasso.get().load(receiverUserImage).transform(new BlurTransformation(ProfileActivity.this,25,1)).into(backgroundIV);
        user_name.setText(receiverUserName);
        user_status.setText(receiverUserStatus);

        backgroundIV.setBlur(4);




    }
}