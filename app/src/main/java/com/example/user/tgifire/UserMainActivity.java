package com.example.user.tgifire;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

import cloud.artik.model.User;

public class UserMainActivity extends AppCompatActivity {

    Context mContext = this;

    private GPSLocation GPS;
    private TextView txtX;
    private TextView txtY;
    private TextView txtAdd;

    private Building building;
    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_user_main);

        //txtX = (TextView)findViewById(R.id.TextView1);
        //txtY = (TextView)findViewById(R.id.TextView2);
        //txtAdd = (TextView)findViewById(R.id.TextView3);

        GPS = new GPSLocation(UserMainActivity.this);

        double GPS_X = GPS.getGPS_X();
        double GPS_Y = GPS.getGPS_Y();
        final String Address = GPS.getAddress(mContext, GPS_X, GPS_Y);

        //txtX.setText(String.valueOf(GPS_X));
        //txtY.setText(String.valueOf(GPS_Y));
        //
        // txtAdd.setText(Address);

        //building.floorNumber = 2;

        String[] items = new String[0];
        for (int i = 0; i < 0; i++) {
            items[i] = Integer.toString(i + 1) + "층";
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items) ;
        listview = (ListView) findViewById(R.id.drawer_menulist);
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.START) ;
            }
        });

        Button buttonReport = (Button) findViewById(R.id.buttonReport);
        buttonReport.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intentToReportActivity = new Intent(mContext, ReportActivity.class);
                intentToReportActivity.putExtra("address", Address);
                startActivity(intentToReportActivity);
            }
        });

    }
}