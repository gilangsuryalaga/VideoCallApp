package com.example.videocallapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class SettingProfileActivity extends AppCompatActivity {
    private Button saveBtn;
    private EditText usernameET, userbioET;
    private ImageView profileIV;
    private FloatingActionButton fabEdit;

    private static int GalleryPick = 1;
    private Uri ImageUri;

    private StorageReference userProfileImagereference;
    private String downloadUrl;
    private DatabaseReference userRef;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_profile);

        userProfileImagereference = FirebaseStorage.getInstance().getReference().child("Profile Images");
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        saveBtn = findViewById(R.id.save_settings_btn);
        usernameET = findViewById(R.id.username_settings);
        userbioET = findViewById(R.id.bio_settings);
        profileIV = findViewById(R.id.profile_image);
        fabEdit = (FloatingActionButton) findViewById(R.id.fab);
        progressDialog = new ProgressDialog(this);



        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent galleryIntent = new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent, GalleryPick);
                CropImage.activity(ImageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingProfileActivity.this);

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserData();
            }
        });

        retriveUserInfo();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            ImageUri = result.getUri();
            profileIV.setImageURI(ImageUri);
        }
    }

    private void saveUserData() {
        final String getUserName = usernameET.getText().toString();
        final String getUserStatus = userbioET.getText().toString();

        if (ImageUri == null) {
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).hasChild("image")) {
                        saveinfoOnlyWithoutImage();
                    } else {
                        FancyToast.makeText(SettingProfileActivity.this, "Please select profile image", FancyToast.LENGTH_SHORT, FancyToast.ERROR, true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (getUserName.equals("")) {
            FancyToast.makeText(this, "Username is empty", Toast.LENGTH_SHORT, FancyToast.ERROR, true);
        } else if (getUserStatus.equals("")) {
            FancyToast.makeText(this, "Status is empty", Toast.LENGTH_SHORT, FancyToast.ERROR, true);
        } else {
            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            final StorageReference filePath = userProfileImagereference.child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid());

            final UploadTask uploadTask = filePath.putFile(ImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isComplete()) {
                        throw task.getException();
                    }
                    downloadUrl = filePath.getDownloadUrl().toString();
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUrl = task.getResult().toString();

                        HashMap<String, Object> profileMap = new HashMap<>();
                        profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        profileMap.put("name", getUserName);
                        profileMap.put("status", getUserStatus);
                        profileMap.put("image", downloadUrl);

                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    sendUserToMainActivity();
                                    progressDialog.dismiss();
                                    FancyToast.makeText(SettingProfileActivity.this, "Profile has been updated", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void saveinfoOnlyWithoutImage() {
        final String getUserName = usernameET.getText().toString();
        final String getUserStatus = userbioET.getText().toString();


        if (getUserName.equals("")) {
            FancyToast.makeText(this, "Username is empty", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
        } else if (getUserStatus.equals("")) {
            FancyToast.makeText(this, "Status is empty", Toast.LENGTH_SHORT, FancyToast.ERROR, true).show();
        } else {
            progressDialog.setTitle("Account Settings");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();

            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            profileMap.put("name", getUserName);
            profileMap.put("status", getUserStatus);

            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                        FancyToast.makeText(SettingProfileActivity.this, "Profile has been updated", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                    }
                }
            });
        }


    }

    private void retriveUserInfo(){
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String imageDb = snapshot.child("image").getValue().toString();
                            String nameDb = snapshot.child("name").getValue().toString();
                            String statusDb = snapshot.child("status").getValue().toString();

                            usernameET.setText(nameDb);
                            userbioET.setText(statusDb);
                            Picasso.get().load(imageDb).placeholder(R.drawable.profile_image).into(profileIV);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void sendUserToMainActivity(){
        FancyToast.makeText(SettingProfileActivity.this, "Profile has been updated", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();

        Intent intent = new Intent(SettingProfileActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}