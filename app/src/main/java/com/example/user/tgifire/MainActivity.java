package com.example.user.tgifire;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

import static com.example.user.tgifire.AuthHelper.INTENT_ARTIKCLOUD_AUTHORIZATION_RESPONSE;
import static com.example.user.tgifire.AuthHelper.USED_INTENT;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    private Context mContext = this;

    AuthorizationService mAuthorizationService;
    AuthStateDAL mAuthStateDAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 폰트 적용 */
        TextView textView=(TextView)findViewById(R.id.mainText);
        Typeface typeface=Typeface.createFromAsset(getAssets(), "Dekar.otf");
        textView.setTypeface(typeface);

        Button btn1=(Button)findViewById(R.id.buttonAdmin);
        Typeface btn_typeface=Typeface.createFromAsset(getAssets(), "a가로수.ttf");
        btn1.setTypeface(btn_typeface);
        Button btn2=(Button)findViewById(R.id.buttonUser);
        btn2.setTypeface(btn_typeface);

        /* 일부 글자 구간에만 색상 적용 */
        Spannable span;
        span = (Spannable)textView.getText();
        span.setSpan(new ForegroundColorSpan(0xFFFF0000), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        span.setSpan(new ForegroundColorSpan(0xFFFF0000), 7, 8, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        span.setSpan(new ForegroundColorSpan(0xFFFF0000), 14, 15, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        span.setSpan(new ForegroundColorSpan(0xFFFF0000), 24, 25, Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        mAuthorizationService = new AuthorizationService(this);
        mAuthStateDAL = new AuthStateDAL(this);

        Button buttonAdmin = findViewById(R.id.buttonAdmin);
        buttonAdmin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                doAuth();
            }
        });

        Button buttonUser = findViewById(R.id.buttonUser);
        buttonUser.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intentToUserMainActivity = new Intent(mContext, UserMainActivity.class);
                startActivity(intentToUserMainActivity);
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "Entering onStart ...");
        super.onStart();
        checkIntent(getIntent());
    }

    // File OAuth call with Authorization Code with PKCE method
    // https://developer.artik.cloud/documentation/getting-started/authentication.html#authorization-code-with-pkce-method
    private void doAuth() {
        AuthorizationRequest authorizationRequest = AuthHelper.createAuthorizationRequest();

        PendingIntent authorizationIntent = PendingIntent.getActivity(
                this,
                authorizationRequest.hashCode(),
                new Intent(INTENT_ARTIKCLOUD_AUTHORIZATION_RESPONSE, null, this, MainActivity.class),
                0);

        /* request sample with custom tabs */
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();

        mAuthorizationService.performAuthorizationRequest(authorizationRequest, authorizationIntent, customTabsIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        checkIntent(intent);
    }

    private void checkIntent(@Nullable Intent intent) {

        Log.d(TAG, "Entering checkIntent ...");
        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case INTENT_ARTIKCLOUD_AUTHORIZATION_RESPONSE:
                    Log.d(TAG, "checkIntent action = " + action
                            + " intent.hasExtra(USED_INTENT) = " + intent.hasExtra(USED_INTENT));
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    break;
                default:
                    Log.w(TAG, "checkIntent action = " + action);
                    // do nothing
            }
        } else {
            Log.w(TAG, "checkIntent intent is null!");
        }
    }

    private void handleAuthorizationResponse(@NonNull Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        Log.i(TAG, "Entering handleAuthorizationResponse with response from Intent = " + response.jsonSerialize().toString());

        if (response != null) {

            if (response.authorizationCode != null ) { // Authorization Code method: succeeded to get code

                final AuthState authState = new AuthState(response, error);
                Log.i(TAG, "Received code = " + response.authorizationCode + "\n make another call to get token ...");

                // File 2nd call to get the token
                mAuthorizationService.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                    @Override
                    public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            mAuthStateDAL.writeAuthState(authState); //store into persistent storage for use later
                            String text = String.format("Received token response [%s]", tokenResponse.jsonSerializeString());
                            Log.i(TAG, text);
                            startBuildingInfoActivity();
                        } else {
                            Context context = getApplicationContext();
                            Log.w(TAG, "Token Exchange failed", exception);
                            CharSequence text = "Token Exchange failed";
                            int duration = Toast.LENGTH_LONG;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
            } else { // come here w/o authorization code. For example, signup finish and user clicks "Back to login"
                Log.d(TAG, "additionalParameter = " + response.additionalParameters.toString());

                if (response.additionalParameters.get("status").equalsIgnoreCase("login_request")) {
                    // ARTIK Cloud instructs the app to display a sign-in form
                    doAuth();
                } else {
                    Log.d(TAG, response.jsonSerialize().toString());
                }
            }

        } else {
            Log.w(TAG, "Authorization Response is null ");
            Log.d(TAG, "Authorization Exception = " + error);
        }
    }

    private void startBuildingInfoActivity() {
        Intent buildingInfoActivityIntent = new Intent(mContext, BuildingInfoActivity.class);
        startActivity(buildingInfoActivityIntent);
    }
}
