package com.example.videocallapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OtpTextView;

public class registration extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phoneText;
    private EditText codeText;
    //private OtpTextView codeText;
    private Button continueAndnextbtn;
    private String checker = "", phoneNumber = "";
    private RelativeLayout relativeLayout;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendtoken;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);

        phoneText = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        continueAndnextbtn = findViewById(R.id.continueNextButton);
        relativeLayout = findViewById(R.id.phoneAuth);

        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phoneText);

        continueAndnextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueAndnextbtn.getText().equals("SUBMIT") || checker.equals("Code Sent"))
                {
                    String verificationCode = codeText.getText().toString();
                    if (verificationCode.equals("")){
                        FancyToast.makeText(registration.this,"Wrong Code",FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                    }else{
                        loadingbar.setTitle("Code Verification");
                        loadingbar.setMessage("Please Wait...");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }else{
                    phoneNumber= ccp.getFullNumberWithPlus();
                    if(!phoneNumber.equals("")){
                        loadingbar.setTitle("Phone Number Verification");
                        loadingbar.setMessage("Please Wait...");
                        loadingbar.setCanceledOnTouchOutside(false);
                        loadingbar.show();



                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60,TimeUnit.SECONDS,registration.this, mCallbacks);
                    }else{
                        FancyToast.makeText(registration.this,"Please write valid phone number.",FancyToast.LENGTH_SHORT,FancyToast.INFO,true).show();
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                FancyToast.makeText(registration.this,"Invalid Phone Number", FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                loadingbar.dismiss();
                continueAndnextbtn.setText("CONTINUE");
                codeText.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerificationId = s;
                mResendtoken = forceResendingToken;

                checker = "Code Sent";
                continueAndnextbtn.setText("SUBMIT");
                codeText.setVisibility(View.VISIBLE);

                relativeLayout.setVisibility(View.GONE);

                loadingbar.dismiss();
            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingbar.dismiss();
                            sendUserToMainActivity();
                        } else {
                            loadingbar.dismiss();
                            String e = task.getException().toString();
                            FancyToast.makeText(registration.this, "Error: " +e,FancyToast.LENGTH_SHORT,FancyToast.ERROR,true).show();
                        }
                    }
                });

    }

    private void sendUserToMainActivity(){
        Intent intent = new Intent(registration.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}