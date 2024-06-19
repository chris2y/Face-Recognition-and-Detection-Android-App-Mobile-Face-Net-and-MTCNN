package com.example.facerecognition20;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.facerecognition20.Model.UserModel;
import com.example.facerecognition20.Util.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginAndSignupActivity extends AppCompatActivity {

    private TextView loginTextLol, signUpTextLol, signupTxt, loginTxt;
    private TextInputLayout emailEditText, passwordEditText;
    private Button loginButton,skipButton,signupButton;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ProgressBar progressBar;
    private FirebaseUser user;
    String uid;
    String email12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_signup);

        initiantialize();
        setUpButtonClicks();



    }

    private void setUpButtonClicks() {

        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable loginButton, forgotPassTxt, and signupTxt
                loginButton.setVisibility(View.GONE);
                //forgotPassTxt.setVisibility(View.GONE);
                signupTxt.setVisibility(View.GONE);
                signUpTextLol.setVisibility(View.GONE);


                loginTextLol.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.VISIBLE);
                loginTxt.setVisibility(View.VISIBLE);
            }
        });

        loginTxt.setOnClickListener(view -> {
            loginButton.setVisibility(View.VISIBLE);
            //forgotPassTxt.setVisibility(View.VISIBLE);
            signupTxt.setVisibility(View.VISIBLE);
            signUpTextLol.setVisibility(View.VISIBLE);

            loginTextLol.setVisibility(View.GONE);
            signupButton.setVisibility(View.GONE);
            loginTxt.setVisibility(View.GONE);
        });

        loginButton.setOnClickListener(v -> {

            String email = emailEditText.getEditText().getText().toString().trim();
            String password = passwordEditText.getEditText().getText().toString().trim();

            validateEditText(email,password);

            if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() == 8){
                FirebaseUtil.getAuthInstance().signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            user =  FirebaseUtil.getAuthInstance().getCurrentUser();
                            String email1 = user.getEmail();
                            String uid = user.getUid();

                            // Save the user's information to shared preferences

                            editor.putString("email", email1);
                            editor.putString("uid", uid);
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("authToken", user.getIdToken(false).getResult().getToken()); // Save authentication token
                            editor.apply();

                            Toast.makeText(LoginAndSignupActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginAndSignupActivity.this, MainActivity.class));

                            finish();
                        }).addOnFailureListener(e -> {

                            progressBar.setVisibility(View.GONE);
                            loginButton.setVisibility(View.VISIBLE);
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                emailEditText.setError("User not found");
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                passwordEditText.setError("Invalid password");

                            } else {
                                Toast.makeText(LoginAndSignupActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            }

                        });
            }
        });


        signupButton.setOnClickListener(view -> {

            String email = emailEditText.getEditText().getText().toString().trim();
            String password = passwordEditText.getEditText().getText().toString().trim();

            validateEditText(email,password);

             if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() == 8){
                FirebaseUtil.getAuthInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        user = FirebaseUtil.getAuthInstance().getCurrentUser();
                         email12 = user.getEmail();
                         uid = user.getUid();
                        // Save the user's information to shared preferences
                        /*editor.putString("Email", email12);
                        editor.putString("uid", uid);
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("authToken", user.getIdToken(false).getResult().getToken()); // Save authentication token
                        editor.apply();*/

                        saveToDatabase(uid,email12);

                    } else {
                        progressBar.setVisibility(View.GONE);
                        signupButton.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginAndSignupActivity.this, "SignUp failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        // Log the exception details
                        Log.d("SignUp", "Failed " +  task.getException().getMessage());
                    }
                });
            }
        });


        skipButton.setOnClickListener(view -> {
            editor.putString("skip", "true");
            editor.apply();
            startActivity(new Intent(LoginAndSignupActivity.this, MainActivity.class));
            finish();
        });

    }

    private void saveToDatabase(String uid,String email2) {

        UserModel userModel = new UserModel(email2,uid);
        FirebaseUtil.allUserCollection().document(uid)
                .set(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(LoginAndSignupActivity.this, EditProfileActivity.class);
                        intent.putExtra("Email", email2);
                        intent.putExtra("uid", uid);
                        intent.putExtra("authToken",user.getIdToken(false).getResult().getToken());

                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        signupButton.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginAndSignupActivity.this, "SignUp failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("SignUp", "Failed " +  e.getMessage());
                    }
                });
    }

    private void validateEditText(String email, String password) {
        emailEditText.setError(null);
        passwordEditText.setError(null);

        if (password.isEmpty() && email.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            emailEditText.setError("Email can't be empty");
            return;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email can't be empty");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email format");
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return;
        }

        if (password.length() != 8) {
            passwordEditText.setError("Password must contain 8 characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
        signupButton.setVisibility(View.GONE);
    }

    private void initiantialize() {
        loginTextLol = findViewById(R.id.txtLoginlol);
        signUpTextLol = findViewById(R.id.txtSignUplol);
        emailEditText = findViewById(R.id.txtEmail);
        passwordEditText = findViewById(R.id.txtPassword);
        loginButton = findViewById(R.id.btnLogin);
        skipButton = findViewById(R.id.btnSkip);
        signupButton = findViewById(R.id.btnSignUp);
        signupTxt = findViewById(R.id.txtSignup);
        //forgotPassTxt = findViewById(R.id.txtForgotPass);
        loginTxt = findViewById(R.id.txtLogIn);
        progressBar = findViewById(R.id.progressBarLogin);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
}