package com.example.user.tgifire;

import android.net.Uri;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

public class AuthHelper {
    private static final String ARTIKCLOUD_AUTHORIZE_URI = "https://accounts.artik.cloud/signin";
    private static final String ARTIKCLOUD_TOKEN_URI = "https://accounts.artik.cloud/token";

    static final String INTENT_ARTIKCLOUD_AUTHORIZATION_RESPONSE
            = "cloud.artik.example.oauth.ARTIKCLOUD_AUTHORIZATION_RESPONSE";
    static final String USED_INTENT = "USED_INTENT";


    static AuthorizationRequest createAuthorizationRequest() {

        AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse(ARTIKCLOUD_AUTHORIZE_URI),
                Uri.parse(ARTIKCLOUD_TOKEN_URI),
                null
        );

        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfiguration,
                Config.CLIENT_ID,
                ResponseTypeValues.CODE,
                Uri.parse(Config.REDIRECT_URI)
        );

        return builder.build();

    }
}
