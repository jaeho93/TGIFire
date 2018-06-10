package com.example.user.tgifire;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import cloud.artik.model.User;

public class UserMainActivity extends AppCompatActivity {

    Context mContext = this;

    private GPSLocation GPS;

    private ListView listview;
    private RelativeLayout mainView;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://tgifire-cdf25.appspot.com/");
    StorageReference storageReference = storage.getReference();

    ArrayList<BuildingInfoItem> items2;

    int currentPosition;
    int floorIndex;
    int uploadCount, downloadCount;
    int currentFloor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_user_main);

        GPS = new GPSLocation(UserMainActivity.this);

        double GPS_X = GPS.getGPS_X();
        double GPS_Y = GPS.getGPS_Y();
        final String Address = GPS.getAddress(mContext, GPS_X, GPS_Y);

        items2 = new ArrayList<BuildingInfoItem>() ;

        //Building.getInstance().SetData(null, 0, 0, null, 0, null);/

        loadItemsFromDB();

        String[] items = new String[Building.getInstance().floorNumber];
        for (int i = 0; i < Building.getInstance().floorNumber; i++) {
            items[i] = Integer.toString(i + 1) + "층";
        }

        //if(Building.getInstance().floorNumber != 0) {

        currentFloor = 0;
        mainView = (RelativeLayout) findViewById(R.id.canvas_layout);
        //mainView.setBackgroundDrawable(FloorPicture.getInstance().floorPicture.get(currentFloor));
        //drawNodes();

        //}

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listview = (ListView) findViewById(R.id.drawer_menulist);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                currentFloor = position;
                mainView.setBackgroundDrawable(FloorPicture.getInstance().floorPicture.get(currentFloor));
                //drawNodes();
                Toast.makeText(mContext, Integer.toString(position+1), Toast.LENGTH_SHORT).show();

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

    public void loadItemsFromDB() {
        databaseReference.child("BUILDING").child("bjp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Building.class) != null) {
                    Building.getInstance().SetData(dataSnapshot.getValue(Building.class).buildingName,
                            dataSnapshot.getValue(Building.class).GPS_X,
                            dataSnapshot.getValue(Building.class).GPS_Y,
                            dataSnapshot.getValue(Building.class).address,
                            dataSnapshot.getValue(Building.class).floorNumber,
                            dataSnapshot.getValue(Building.class).nodes);
                    if (Building.getInstance().nodes == null) {
                        Building.getInstance().nodes = new ArrayList<Node>();
                    }
                } else {
                    Building.getInstance().SetData("", 0, 0, "", 0, null);
                    return;
                }

                for (floorIndex = 0; floorIndex < Building.getInstance().floorNumber; floorIndex++) {
                    final int index = floorIndex;
                    items2.add(new BuildingInfoItem(index));

                    StorageReference spaceReference = storageReference.child("bjp/floor" + Integer.toString(index + 1) + ".png");
                    final long ONE_MEGABYTE = 1024 * 1024;
                    spaceReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                            FloorPicture.getInstance().floorPicture.add(new BitmapDrawable(bitmap));
                            byte[] data = baos.toByteArray();
                            //items2.get(index).setImageFloor(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

}