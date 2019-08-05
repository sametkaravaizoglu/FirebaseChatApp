package com.samet.firebasechatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email,password,name;
    private Button signin,signup;
    static FirebaseUser user;
    static String LoggedIn_User_Email;

    public void callsignin(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Testing","failed signin");

                        if(!task.isSuccessful()){

                        }
                        else{
                            Intent i = new Intent(MainActivity.this,SignIn.class);
                            finish();
                            startActivity(i);
                        }

                    }
                });
    }

    public void userProfile(){
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name.getText().toString().trim())
                    .build();

            user.updateProfile(profileUpdate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("TESTING","User profile updated.");
                            }
                        }
                    });
        }
    }
    public void callsignup(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Authentication succes.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();

                        } else {
                            userProfile();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);


        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        OneSignal.sendTag("User_ID", LoggedIn_User_Email);

        signin = (Button)findViewById(R.id.signin);
        signup = (Button)findViewById(R.id.signup);
        email = (EditText)findViewById(R.id.etEmail);
        password = (EditText)findViewById(R.id.etPassword);
        name = (EditText)findViewById(R.id.etName);

        //already logged in
        if(mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),SignIn.class));
        }

        if (user != null) {
            LoggedIn_User_Email = user.getEmail();
        }


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user = mAuth.getCurrentUser();


                String getEmail = email.getText().toString().trim();
                String getPassword = password.getText().toString().trim();
                callsignin(getEmail,getPassword);;

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getEmail = email.getText().toString().trim();
                String getPassword = password.getText().toString().trim();
                callsignup(getEmail,getPassword);

            }
        });


    }



}
