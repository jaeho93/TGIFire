package com.example.user.tgifire;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.GridLayout.LayoutParams;

public class AdminMainActivity extends AppCompatActivity {//implements NavigationView.OnNavigationItemSelectedListener {
    Context mContext = this;

    int currentFloor = 0;

    Building building;
    ListView listview;
    RelativeLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_admin_main);

        Intent intent = getIntent();
        building = (Building) intent.getSerializableExtra("building");

        String[] items = new String[building.floorNumber];
        for (int i = 0; i < building.floorNumber; i++) {
            items[i] = Integer.toString(i + 1) + "층";
        }

        currentFloor = 0;

        mainView = (RelativeLayout) findViewById(R.id.canvas_layout);
        drawNodes();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listview = (ListView) findViewById(R.id.drawer_menulist);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                currentFloor = position;
                drawNodes();
                Toast.makeText(mContext, Integer.toString(position+1), Toast.LENGTH_SHORT).show();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.START) ;
            }
        });
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

            final float x = event.getX();
            final float y = event.getY();

            if(event.getAction() == MotionEvent.ACTION_DOWN ){
                String msg = "노드 위치 : " + x + " / " + y;

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

                        Intent intent = new Intent(mContext, AdminMainActivity.class);
                        intent.putExtra("building", building);
                        startActivity(intent);

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

    private void drawNodes() {
        mainView.removeAllViewsInLayout();

        RelativeLayout.LayoutParams lp;
        Button newNode = new Button(this); //버튼을 선언

        lp = new RelativeLayout.LayoutParams(120, 120);
        lp.leftMargin = currentFloor*150+50;
        lp.topMargin = currentFloor*150+50;
        newNode.setLayoutParams(lp);
        newNode.setAlpha(0.75f);

        newNode.setBackgroundResource(R.drawable.node_green); //버튼 이미지를 지정(int)
        mainView.addView(newNode); //지정된 뷰에 셋팅완료된 Button을 추가
    }
}