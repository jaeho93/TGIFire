package com.example.user.tgifire;

import net.openid.appauth.AuthorizationService;

import cloud.artik.api.MessagesApi;
import cloud.artik.api.UsersApi;

public class Auth {
    private static Auth uniqueInstance;

    public AuthorizationService mAuthorizationService;
    public AuthStateDAL mAuthStateDAL;
    public UsersApi mUserApi = null;
    public MessagesApi mMessagesApi = null;
    public String userID;
    public String mAccessToken;

    private Auth() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public static Auth getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Auth();
        }

        return uniqueInstance;

    }
}
