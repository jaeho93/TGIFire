package com.example.user.tgifire;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cloud.artik.api.MessagesApi;
import cloud.artik.api.UsersApi;
import cloud.artik.client.ApiCallback;
import cloud.artik.client.ApiClient;
import cloud.artik.client.ApiException;
import cloud.artik.model.Action;
import cloud.artik.model.NormalizedAction;
import cloud.artik.model.NormalizedActionsEnvelope;
import cloud.artik.model.NormalizedMessagesEnvelope;

public class AdminMainActivity extends AppCompatActivity {//implements NavigationView.OnNavigationItemSelectedListener {
    Context mContext = this;

    int currentFloor = 0;

    ListView listview;
    RelativeLayout mainView;

    // DB 관련
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    // Artik 관련
    private MessagesApi mMessagesApi = null;
    private String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_admin_main);

        String[] items = new String[Building.getInstance().floorNumber];
        for (int i = 0; i < Building.getInstance().floorNumber; i++) {
            items[i] = Integer.toString(i + 1) + "층";
        }

        Intent intent = getIntent();
        currentFloor = intent.getExtras().getInt("currentFloor");

        mainView = (RelativeLayout) findViewById(R.id.canvas_layout);
        mainView.setBackgroundDrawable(FloorPicture.getInstance().floorPicture.get(currentFloor));
        drawNodes();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listview = (ListView) findViewById(R.id.drawer_menulist);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                currentFloor = position;
                mainView.setBackgroundDrawable(FloorPicture.getInstance().floorPicture.get(currentFloor));
                drawNodes();
                Toast.makeText(mContext, Integer.toString(position+1), Toast.LENGTH_SHORT).show();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(Gravity.START) ;
            }
        });

        AuthStateDAL authStateDAL = new AuthStateDAL(this);
        mAccessToken = authStateDAL.readAuthState().getAccessToken();

        setupArtikCloudApi();
        getLatestMsg();
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
            final EditText nodeName = (EditText) mView.findViewById(R.id.editNewNodeName);
            Button mNode = (Button) mView.findViewById(R.id.btnNode);

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            mNode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!nodeName.getText().toString().isEmpty()){
                        Toast.makeText(AdminMainActivity.this,
                                R.string.success_node_msg,
                                Toast.LENGTH_SHORT).show();

                        // 노드 추가
                        Building.getInstance().nodes.add(new Node((int)x, (int)y, currentFloor, nodeName.getText().toString(), 1));
                        // DB에 업로드
                        databaseReference.child("BUILDING").child("bjp").setValue(Building.getInstance());

                        dialog.dismiss();

                        Intent intent = new Intent(mContext, AdminMainActivity.class);
                        intent.putExtra("currentFloor", currentFloor);
                        startActivity(intent);
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

    private void drawNodes() {
        mainView.removeAllViewsInLayout();

        RelativeLayout.LayoutParams lp;

        // 노드 추가 버튼
        Button plusButton = new Button(this); //버튼을 선언

        lp = new RelativeLayout.LayoutParams(120, 120);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.rightMargin = 50;
        lp.bottomMargin = 50;
        plusButton.setLayoutParams(lp);
        plusButton.setAlpha(0.5f);
        plusButton.setBackgroundResource(R.drawable.plus); //버튼 이미지를 지정(int)
        plusButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(mContext, getString(R.string.add_node_toast), Toast.LENGTH_LONG).show();
                View view = new AdminMainActivity.MyView( mContext);
                view.setAlpha(0.5f);
                view.setBackgroundDrawable(mainView.getBackground());
                setContentView(view);
            }
        });
        mainView.addView(plusButton);

        // 노드들
        for (int i = 0; i < Building.getInstance().nodes.size(); i++) {
            if (Building.getInstance().nodes.get(i).floor == currentFloor) {
                Button newNode = new Button(this); //버튼을 선언

                lp = new RelativeLayout.LayoutParams(120, 120);
                lp.leftMargin = Building.getInstance().nodes.get(i).x - 60;
                lp.topMargin = Building.getInstance().nodes.get(i).y - 60;
                newNode.setLayoutParams(lp);
                newNode.setAlpha(0.75f);
                newNode.setBackgroundResource(R.drawable.node_green); //버튼 이미지를 지정(int)
                newNode.setTag(i);

                newNode.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AdminMainActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.node_info, null);
                        final EditText nodeName = (EditText) mView.findViewById(R.id.editNodeName);
                        Button buttonRemoveNode = (Button) mView.findViewById(R.id.buttonRemoveNode);
                        buttonRemoveNode.setTag((int) v.getTag());
                        Button buttonNodeExit = (Button) mView.findViewById(R.id.buttonNodeExit);
                        buttonNodeExit.setTag((int) v.getTag());

                        nodeName.setText(Building.getInstance().nodes.get((int) v.getTag()).name);

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        buttonRemoveNode.setOnClickListener(new Button.OnClickListener() {
                            public void onClick(View v) {
                                Building.getInstance().nodes.remove((int) v.getTag());
                                // DB에 업로드
                                databaseReference.child("BUILDING").child("bjp").setValue(Building.getInstance());

                                dialog.dismiss();

                                Intent intent = new Intent(mContext, AdminMainActivity.class);
                                intent.putExtra("currentFloor", currentFloor);
                                startActivity(intent);
                            }
                        });
                        buttonNodeExit.setOnClickListener(new Button.OnClickListener() {
                            public void onClick(View v) {
                                Building.getInstance().nodes.get((int) v.getTag()).name = nodeName.getText().toString();
                                // DB에 업로드
                                databaseReference.child("BUILDING").child("bjp").setValue(Building.getInstance());

                                dialog.dismiss();
                            }
                        });
                    }
                });

                mainView.addView(newNode); //지정된 뷰에 셋팅완료된 Button을 추가
            }
        }
    }

    private void setupArtikCloudApi() {
        ApiClient mApiClient = new ApiClient();
        mApiClient.setAccessToken(mAccessToken);

        mMessagesApi = new MessagesApi(mApiClient);
    }

    private void getLatestMsg() {
        final String tag = "MessagesAsync";
        try {
            int messageCount = 1;
            mMessagesApi.getLastNormalizedMessagesAsync(messageCount, Config.DEVICE_ID, null,
                    new ApiCallback<NormalizedMessagesEnvelope>() {
                        @Override
                        public void onFailure(ApiException exc, int i, Map<String, List<String>> stringListMap) {
                            processFailure(tag, exc);
                        }

                        @Override
                        public void onSuccess(NormalizedMessagesEnvelope result, int i, Map<String, List<String>> stringListMap) {
                            Log.v(tag, " onSuccess latestMessage = " + result.getData().toString());
                            String mid = "";
                            String data = "";
                            if (!result.getData().isEmpty()) {
                                mid = result.getData().get(0).getMid();
                                data = result.getData().get(0).getData().toString();
                            }
                            Log.v(tag, "DATA: " + data);
                        }

                        @Override
                        public void onUploadProgress(long bytes, long contentLen, boolean done) {
                        }

                        @Override
                        public void onDownloadProgress(long bytes, long contentLen, boolean done) {
                        }
                    });

        } catch (ApiException exc) {
            processFailure(tag, exc);
        }
    }

    private void processFailure(final String context, ApiException exc) {
        String errorDetail = " onFailure with exception" + exc;
        Log.w(context, errorDetail);
        exc.printStackTrace();
    }
}