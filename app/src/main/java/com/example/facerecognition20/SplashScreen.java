package com.example.facerecognition20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.facerecognition20.Util.FirebaseUtil;

public class SplashScreen extends AppCompatActivity {
    boolean isLoggedIn;
    String skip;
    String authToken;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isUserAuthenticated()) {
            proceedToLogin();
            Log.d("Sharedprefvalue2", String.valueOf(isUserAuthenticated()));
        }
        else {
            //Toast.makeText(getApplicationContext(),String.valueOf(FirebaseUtil.getUserId()),Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> {

                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
                Log.d("Sharedprefvalue3", String.valueOf(isUserAuthenticated()));

                finish();
            }, 2000);

        }


    }
    private boolean isUserAuthenticated() {
        authToken = sharedPreferences.getString("authToken", null);
        skip = sharedPreferences.getString("skip", null);

        Log.d("Sharedprefvalue", authToken + skip);
        return (authToken == null || authToken.isEmpty()) && (skip == null || skip.isEmpty());
    }

    private void proceedToLogin() {
        startActivity(new Intent(SplashScreen.this, LoginAndSignupActivity.class));
        finish();
    }
}