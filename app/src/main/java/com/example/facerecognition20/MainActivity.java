package com.example.facerecognition20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.facerecognition20.Fragments.RedirectToLoginFragment;
import com.example.facerecognition20.Fragments.homeFragment;
import com.example.facerecognition20.Fragments.scocialFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;
    boolean isLoggedIn;


    homeFragment homeFragment = new homeFragment();
    scocialFragment socialFragment = new scocialFragment();
    RedirectToLoginFragment redirectToLoginFragment = new RedirectToLoginFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNav);

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn){

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frameLayout, homeFragment, "homeFragment")
                    .add(R.id.frameLayout, socialFragment, "socialFragment")
                    .add(R.id.frameLayout, redirectToLoginFragment, "redirectToLoginFragment")
                    .hide(socialFragment)
                    .hide(redirectToLoginFragment)
                    .commit();

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();

                    if (itemId == R.id.nav_home) {
                        getSupportFragmentManager().beginTransaction().hide(socialFragment).hide(redirectToLoginFragment).show(homeFragment).commit();
                        return true;
                    } else if (itemId == R.id.nav_social && isLoggedIn) {
                        getSupportFragmentManager().beginTransaction().hide(homeFragment).hide(redirectToLoginFragment).show(socialFragment).commit();
                        return true;
                    } else {
                        getSupportFragmentManager().beginTransaction().hide(homeFragment).hide(socialFragment).show(redirectToLoginFragment).commit();
                        return true;
                    }
                }
            });

        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int itemId = item.getItemId();
                    // This is the boolean value that you mentioned.
                    if (itemId == R.id.nav_home) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, homeFragment).commit();
                        return true;
                    } else if (itemId == R.id.nav_social && isLoggedIn) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, socialFragment).commit();
                        return true;
                    }
                    else {
                        // The user is not logged in, so redirect to the login fragment.
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, redirectToLoginFragment).commit();
                        return true;
                    }
                    // Add more else if blocks for additional menu items
                }
            });
        }






    }

}