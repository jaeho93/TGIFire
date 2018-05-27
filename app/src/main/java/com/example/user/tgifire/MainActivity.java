package com.example.user.tgifire;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonAdmin = findViewById(R.id.buttonAdmin);
        buttonAdmin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intentToSignInActivity = new Intent(mContext, SignInActivity.class);
                startActivity(intentToSignInActivity);
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
}
