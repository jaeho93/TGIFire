package com.example.user.tgifire;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cloud.artik.model.Acknowledgement;
import cloud.artik.model.ActionOut;
import cloud.artik.model.MessageOut;
import cloud.artik.model.WebSocketError;
import cloud.artik.websocket.ArtikCloudWebSocketCallback;
import cloud.artik.websocket.FirehoseWebSocket;
import okhttp3.OkHttpClient;

public class AdminMainActivity extends AppCompatActivity {//implements NavigationView.OnNavigationItemSelectedListener {
    Context mContext = this;

    int currentFloor = 0;

    ListView listview;
    RelativeLayout mainView;

    // DB 관련
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    // Artik 관련
    int serverNodeIndex = 0;
    private ArrayList<FirehoseWebSocket> mFirehoseWebSocket = new ArrayList<FirehoseWebSocket>();

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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_floor, items);
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

        //AuthStateDAL authStateDAL = new AuthStateDAL(this);
        //mAccessToken = authStateDAL.readAuthState().getAccessToken();

        //setupArtikCloudApi();
        //getUserInfo();
        try {
            connectFirehoseWebSocket();
        } catch (Exception e) {
            e.printStackTrace();
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
            final EditText nodeDID = (EditText) mView.findViewById(R.id.editNewNodeDID);
            Button mNode = (Button) mView.findViewById(R.id.btnNode);
            Button exitBtn = (Button) mView.findViewById(R.id.plusNodeExit);

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
                        Building.getInstance().nodes.add(new Node((int)x, (int)y, currentFloor, nodeName.getText().toString(), nodeDID.getText().toString(), false));
                        // DB에 업로드
                        databaseReference.child("BUILDING").child(Auth.getInstance().userID).setValue(Building.getInstance());

                        dialog.dismiss();

                        Intent intent = new Intent(mContext, AdminMainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("currentFloor", currentFloor);
                        startActivity(intent);
                    }else{
                        Toast.makeText(AdminMainActivity.this,
                                R.string.error_node_msg,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            exitBtn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(mContext, AdminMainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("currentFloor", currentFloor);
                    startActivity(intent);
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
        plusButton.setAlpha(0.9f);
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
                if (Building.getInstance().nodes.get(i).state)
                    newNode.setBackgroundResource(R.drawable.node_red); //버튼 이미지를 지정(int)
                else
                    newNode.setBackgroundResource(R.drawable.node_green); //버튼 이미지를 지정(int)
                newNode.setTag(i);

                newNode.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AdminMainActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.node_info, null);
                        final EditText nodeName = (EditText) mView.findViewById(R.id.editNodeName);
                        final EditText nodeDID = (EditText) mView.findViewById(R.id.editNodeDID);
                        final TextView nodeState = (TextView) mView.findViewById(R.id.textNodeAdminState);
                        Button buttonRemoveNode = (Button) mView.findViewById(R.id.buttonRemoveNode);
                        buttonRemoveNode.setTag((int) v.getTag());
                        Button buttonNodeExit = (Button) mView.findViewById(R.id.buttonNodeExit);
                        buttonNodeExit.setTag((int) v.getTag());

                        nodeName.setText(Building.getInstance().nodes.get((int) v.getTag()).name);
                        nodeDID.setText(Building.getInstance().nodes.get((int) v.getTag()).did);
                        if (Building.getInstance().nodes.get((int) v.getTag()).state)
                            nodeState.setText("불법 적재물이 감지되었습니다.");
                        else
                            nodeState.setText("불법 적재물이 없습니다.");

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        buttonRemoveNode.setOnClickListener(new Button.OnClickListener() {
                            public void onClick(View v) {
                                Building.getInstance().nodes.remove((int) v.getTag());
                                // DB에 업로드
                                databaseReference.child("BUILDING").child(Auth.getInstance().userID).setValue(Building.getInstance());

                                dialog.dismiss();
                                drawNodes();
                            }
                        });
                        buttonNodeExit.setOnClickListener(new Button.OnClickListener() {
                            public void onClick(View v) {
                                Building.getInstance().nodes.get((int) v.getTag()).name = nodeName.getText().toString();
                                Building.getInstance().nodes.get((int) v.getTag()).did = nodeDID.getText().toString();
                                // DB에 업로드
                                databaseReference.child("BUILDING").child(Auth.getInstance().userID).setValue(Building.getInstance());

                                dialog.dismiss();
                            }
                        });
                    }
                });

                mainView.addView(newNode); //지정된 뷰에 셋팅완료된 Button을 추가
            }
        }
    }

    private void connectFirehoseWebSocket() throws Exception {
        OkHttpClient client;
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();

        // 센서 노드 별 메세지 리스너 등록
        for (serverNodeIndex = 0; serverNodeIndex < Building.getInstance().nodes.size(); serverNodeIndex++) {
            Log.d("DEVICE ID", Building.getInstance().nodes.get(serverNodeIndex).did);

            mFirehoseWebSocket.add(new FirehoseWebSocket(client, Auth.getInstance().mAccessToken, Building.getInstance().nodes.get(serverNodeIndex).did, null, null, Auth.getInstance().userID, new ArtikCloudWebSocketCallback() {
                final int currentIndex = serverNodeIndex;
                @Override
                public void onOpen(int httpStatus, String httpStatusMessage) {
                    Log.d("WebSocketOpen", httpStatusMessage);
                }

                @Override
                public void onMessage(MessageOut messageOut) {
                    Map<String, Object> data = messageOut.getData();
                    for (String key : data.keySet()) {
                        Log.d("WebSocketMsg", data.get(key).toString() + ", " + Integer.toString(currentIndex));
                        if (data.get(key).toString().equals("open")) {
                            Building.getInstance().nodes.get(currentIndex).state = true;
                            sendNoti();
                        }
                        if (data.get(key).toString().equals("closed")) {
                            Building.getInstance().nodes.get(currentIndex).state = false;
                        }
                    }

                    // DB에 업로드
                    databaseReference.child("BUILDING").child(Auth.getInstance().userID).setValue(Building.getInstance());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawNodes();
                        }
                    });
                }

                @Override
                public void onAction(ActionOut action) {
                }

                @Override
                public void onAck(Acknowledgement ack) {

                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                }

                @Override
                public void onError(WebSocketError error) {
                    Log.d("WebSocketError", error.toString());
                }

                @Override
                public void onPing(long timestamp) {
                    Log.d("WebSocketPing", "PING");
                }
            }));
            mFirehoseWebSocket.get(serverNodeIndex).connect();
        }
    }

    private void sendNoti () {

        Bitmap mLargeIconForNoti =
                BitmapFactory.decodeResource(getResources(), R.drawable.main_icon);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;

        if (Build.VERSION.SDK_INT >= 26) {

            String channelId = "one-channel";
            String channelName = "Channel One";
            String channelDescription = "My Channel One";
            NotificationChannel mchannel =
                    new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            mchannel.setDescription(channelDescription);
            mchannel.enableLights(true);
            mchannel.setLightColor(Color.RED);

            notificationManager.createNotificationChannel(mchannel);

            mBuilder = new NotificationCompat.Builder(AdminMainActivity.this, channelId);

        }

        else {

            mBuilder = new NotificationCompat.Builder(AdminMainActivity.this);

        }

                mBuilder.setSmallIcon(R.drawable.main_icon)
                .setContentTitle("TGIF")
                .setContentText("불법적재물이 감지되었습니다!")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setLargeIcon(mLargeIconForNoti)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

                notificationManager.notify(0, mBuilder.build());

    }

}