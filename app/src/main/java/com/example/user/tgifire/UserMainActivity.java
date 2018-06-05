package com.example.user.tgifire;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserMainActivity extends AppCompatActivity {

    private final Context mContext = this;

    private GPSLocation GPS;
    private TextView txtX;
    private TextView txtY;
    private TextView txtAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        txtX = (TextView)findViewById(R.id.TextView1);
        txtY = (TextView)findViewById(R.id.TextView2);
        txtAdd = (TextView)findViewById(R.id.TextView3);

        GPS = new GPSLocation(UserMainActivity.this);

        double GPS_X = GPS.getGPS_X();
        double GPS_Y = GPS.getGPS_Y();
        String Address = GPS.getAddress(mContext, GPS_X, GPS_Y);

        txtX.setText(String.valueOf(GPS_X));
        txtY.setText(String.valueOf(GPS_Y));
        txtAdd.setText(Address);

    }
}