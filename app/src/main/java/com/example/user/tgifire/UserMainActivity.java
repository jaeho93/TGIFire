package com.example.user.tgifire;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
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
import java.util.ArrayList;

public class UserMainActivity extends AppCompatActivity {

    Context mContext = this;

    int currentFloor = 0;

    ListView listview;
    RelativeLayout mainView;

    // DB 관련
    int downloadCount = 0, floorIndex = 0;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://tgifire-cdf25.appspot.com/");
    StorageReference storageReference = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_admin_main);

        mainView = (RelativeLayout) findViewById(R.id.canvas_layout);
        currentFloor = 0;

        loadItemsFromDBFirst();
    }

    private void InitializeUI() {
        String[] items = new String[Building.getInstance().floorNumber];
        for (int i = 0; i < Building.getInstance().floorNumber; i++) {
            items[i] = Integer.toString(i + 1) + "층";
        }

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

        mainView.setBackgroundDrawable(FloorPicture.getInstance().floorPicture.get(currentFloor));
        drawNodes();

        // 노드 센서 정보를 DB에서 계속 수신
        databaseReference.child("BUILDING").child("bjp").addValueEventListener(new ValueEventListener() {
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            drawNodes();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
                if (Building.getInstance().nodes.get(i).state) {
                    newNode.setBackgroundResource(R.drawable.node_red); //버튼 이미지를 지정(int)
                }
                else {
                    newNode.setBackgroundResource(R.drawable.node_green); //버튼 이미지를 지정(int)
                }
                newNode.setTag(i);

                newNode.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(UserMainActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.node_info_uneditable, null);
                        final TextView nodeName = (TextView) mView.findViewById(R.id.textNodeName);
                        final TextView nodeState = (TextView) mView.findViewById(R.id.textNodeState);
                        Button buttonNotUse = (Button) mView.findViewById(R.id.buttonNotUse);
                        buttonNotUse.setTag((int) v.getTag());
                        Button buttonUserNodeExit = (Button) mView.findViewById(R.id.buttonUserNodeExit);

                        nodeName.setText(Building.getInstance().nodes.get((int) v.getTag()).name);
                        if (Building.getInstance().nodes.get((int) v.getTag()).state)
                            nodeState.setText("불법 적재물이 감지되었습니다.");
                        else
                            nodeState.setText("불법 적재물이 없습니다.");

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        buttonUserNodeExit.setOnClickListener(new Button.OnClickListener() {
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });

                mainView.addView(newNode); //지정된 뷰에 셋팅완료된 Button을 추가
            }
        }
    }

    public void loadItemsFromDBFirst() {
        Building.getInstance().Initialize();
        FloorPicture.getInstance().Initialize();

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
                }

                else {
                    // 건물이 없습니다.
                    return;
                }

                // 층별 사진 다운로드
                for (floorIndex = 0; floorIndex < Building.getInstance().floorNumber; floorIndex++) {
                    FloorPicture.getInstance().floorPicture.add(new BitmapDrawable());
                }
                downloadCount = 0;
                for (floorIndex = 0; floorIndex < Building.getInstance().floorNumber; floorIndex++) {
                    final long ONE_MEGABYTE = 1024 * 1024;
                    StorageReference spaceReference = storageReference.child("bjp/floor" + Integer.toString(floorIndex + 1) + ".png");
                    spaceReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        final int index = floorIndex;
                        @Override
                        public void onSuccess(byte[] bytes) {
                            FloorPicture.getInstance().floorPicture.set(index, new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length)));
                            downloadCount++;
                            if (downloadCount == Building.getInstance().floorNumber) {
                                InitializeUI();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /*
    AsyncTask<String, String, String> loadNodeTask = new AsyncTask<String, String, String>() {
        @Override
        protected String doInBackground(String... strings) {
            while (!this.isCancelled()) {
                try {
                    Thread.sleep(5000);
                    publishProgress("");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                drawNodes();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    };*/
}