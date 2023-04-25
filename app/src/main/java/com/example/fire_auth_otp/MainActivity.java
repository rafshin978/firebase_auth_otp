package com.example.fire_auth_otp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.ktx.Firebase;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    EditText mob,otp;
    Button sub,ver;
    FirebaseAuth fauth;
    private String varificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mob = findViewById(R.id.mob);
        otp = findViewById(R.id.otp);
        sub = findViewById(R.id.sbmtbtn);
        ver = findViewById(R.id.vrfybtn);
        fauth = FirebaseAuth.getInstance();

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mob.getText().toString().isEmpty() && mob.getText().toString().length()==10){

                    String phoneNum = "+91" + mob.getText().toString().trim();
                    requestOTP(phoneNum);
                }
                else{
                    mob.setError("Phone Number is not valid");
                }
            }
        });
        ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!otp.getText().toString().isEmpty()){
                    String cod = otp.getText().toString().trim();
                    verifyCode(cod);
                }
            }
        });



    }

    private void requestOTP(String phoneNum) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fauth)
                        .setPhoneNumber(phoneNum)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(MainActivity.this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            varificationId = s ;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            final String code = phoneAuthCredential.getSmsCode();
            if (code!=null){
                otp.setText(code);
            }
            verifyCode(code);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {



        }
    };


    private void verifyCode(String cod) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(varificationId,cod);
        SignInWithcredentials(credential);
    }

    private void SignInWithcredentials(PhoneAuthCredential credential) {

        fauth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(getApplicationContext(),Home.class));
                }
            }
        });

    }


}