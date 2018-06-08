package com.example.user.tgifire;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AdminMainActivity extends AppCompatActivity {//implements NavigationView.OnNavigationItemSelectedListener {
    Context mContext = this;

    Building building;
    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_admin_main);

        Intent intent = getIntent();
        //building = (Building) intent.getSerializableExtra("building");

        //String[] items = new String[building.floorNumber];
        String[] items = new String[4];
        for (int i = 0; i < 4; i++) {
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
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toast.makeText(mContext, "Upload dasd...", Toast.LENGTH_SHORT).show();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Toast.makeText(mContext, "Upload fail...", Toast.LENGTH_SHORT).show();
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    protected class MyView extends View {

        public MyView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // TODO Auto-generated method stub
            super.onTouchEvent(event);

            if(event.getAction() == MotionEvent.ACTION_DOWN ){

                float x = event.getX();
                float y = event.getY();

                String msg = "노드 위치 : " +x+" / " +y;

                Toast. makeText(AdminMainActivity. this, msg, Toast.LENGTH_SHORT ).show();
                return true;
            }
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(AdminMainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.add_node, null);
            final EditText newNode = (EditText) mView.findViewById(R.id.new_node);
            Button mNode = (Button) mView.findViewById(R.id.btnNode);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            mNode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!newNode.getText().toString().isEmpty()){
                        Toast.makeText(AdminMainActivity.this,
                                R.string.success_node_msg,
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(),AdminMainActivity.class);
                        startActivityForResult(intent, 101);
                        dialog.dismiss();
                    }else{
                        Toast.makeText(AdminMainActivity.this,
                                R.string.error_node_msg,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        }
    }

    public void onButtonClick(View v){
        Toast.makeText(this, getString(R.string.add_node_toast), Toast.LENGTH_LONG).show();
        View view = new AdminMainActivity.MyView( this);
        setContentView(view);
    }
}