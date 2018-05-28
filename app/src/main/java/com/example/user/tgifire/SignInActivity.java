package com.example.user.tgifire;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.openid.appauth.AuthorizationService;

public class SignInActivity extends AppCompatActivity {
    AuthorizationService mAuthorizationService;
    AuthStateDAL mAuthStateDAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuthorizationService = new AuthorizationService(this);
        mAuthStateDAL = new AuthStateDAL(this);


    }
}
