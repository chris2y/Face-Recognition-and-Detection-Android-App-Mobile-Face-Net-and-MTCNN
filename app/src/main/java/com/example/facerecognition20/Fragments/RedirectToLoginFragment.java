package com.example.facerecognition20.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.facerecognition20.LoginAndSignupActivity;
import com.example.facerecognition20.R;


public class RedirectToLoginFragment extends Fragment {

    View rootView;
    Button redirectToLogin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_redirect_to_login, container, false);
        redirectToLogin = rootView.findViewById(R.id.redirectLoginButton);

        redirectToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), LoginAndSignupActivity.class));
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });

        return rootView;

    }
}