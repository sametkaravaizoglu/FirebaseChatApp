package com.samet.firebasechatapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SignIn extends AppCompatActivity {

    Button signout,notification,chat_room_button;
    private FirebaseAuth mAuth;
    TextView username;
    static String LoggedIn_User_Email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);


        mAuth = FirebaseAuth.getInstance();

        signout = (Button)findViewById(R.id.signout);
        username = (TextView) findViewById(R.id.tvName);
        notification =(Button)findViewById(R.id.notification);
        chat_room_button = (Button)findViewById(R.id.chat_room_button);


        if(mAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        //username fetch
        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null){
            username.setText("welcome  "+user.getEmail());
            LoggedIn_User_Email =user.getEmail();
        }


        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        chat_room_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignIn.this, ChatRoomLayoutActivity.class);
                startActivity(i);
            }
        });



    }
    private void sendNotification()
    {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    String send_email;
                    LoggedIn_User_Email = MainActivity.LoggedIn_User_Email;
                    OneSignal.sendTag("User_ID", LoggedIn_User_Email);

                    send_email = "example@test.com";

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic OWEyYjAxMTEtNDNkYi00ZDFhLTlhZGQtY2NiYmM1NTZkNjIz");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                +   "\"app_id\": \"ONESİGNAL_APP_ID\","
                                +   "\"data\": {\"foo\": \"bar\"},"
                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"
                                +   "\"contents\": {\"en\": \"Yeni bir mesajiniz var\"}"
                                + "}";

                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (  httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch(Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }
}
