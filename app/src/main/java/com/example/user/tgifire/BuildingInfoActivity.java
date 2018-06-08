package com.example.user.tgifire;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class BuildingInfoActivity extends AppCompatActivity implements RecyclerViewAdapter.RecyclerButtonClickListener{
    Context mContext = this;

    // 리스트 관련
    RecyclerView listview ;
    ArrayList<BuildingInfoItem> items;// = new ArrayList<BuildingInfoItem>() ;
    RecyclerViewAdapter adapter;// = new ListViewButtonAdapter(this, R.layout.item_building_info, items, this) ;
    LinearLayoutManager mLayoutManager;

    // DB 관련
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://tgifire-cdf25.appspot.com/");
    StorageReference storageReference = storage.getReference();

    int currentPosition;
    int floorIndex;
    int uploadCount, downloadCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_info);

        items = new ArrayList<BuildingInfoItem>() ;

        // 리스트뷰 참조
        listview = (RecyclerView) findViewById(R.id.listBuildingInfo);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // LinearLayout으로 설정
        listview.setLayoutManager(mLayoutManager);

        // Adapter 생성 및 달기
        adapter = new RecyclerViewAdapter(items, this);
        listview.setAdapter(adapter);

        // items 로드.
        loadItemsFromDB();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(listview);

        Button buttonAddFloor = (Button) findViewById(R.id.buttonAddFloor);
        buttonAddFloor.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int n = adapter.getItemCount();
                items.add(n, new BuildingInfoItem(n));

                listview.scrollToPosition(n);

                adapter.notifyDataSetChanged();
            }
        });

        // 확인 버튼 클릭
        Button buttonSaveBuildingInfo = (Button) findViewById(R.id.buttonSaveBuildingInfo);
        buttonSaveBuildingInfo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // 빌딩 정보 업로드
                EditText editBuildingName = (EditText) findViewById(R.id.editBuildingName);

                Building.getInstance().buildingName = editBuildingName.getText().toString();
                Building.getInstance().floorNumber = adapter.getItemCount();

                databaseReference.child("BUILDING").child("bjp").setValue(Building.getInstance());

                // 층별 사진 업로드
                uploadCount = 0;
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    if (items.get(i).getImageFloor() == null) continue;
                    // Get the data from an ImageView as bytes
                    Bitmap bitmap = items.get(i).getImageFloor();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    // 층별 사진 정보 저장
                    FloorPicture.getInstance().floorPicture.add(new BitmapDrawable(bitmap));

                    StorageReference spaceReference = storageReference.child("bjp/floor" + Integer.toString(i + 1) + ".png");
                    UploadTask uploadTask = spaceReference.putBytes(data);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Toast.makeText(mContext, "Upload fail...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            uploadCount++;
                            // 업로드 완료 시 AdminActivity로 이동
                            if (uploadCount == adapter.getItemCount()) {
                                Intent AdminMainActivityIntent = new Intent(mContext, AdminMainActivity.class);
                                AdminMainActivityIntent.putExtra("currentFloor", 0);
                                startActivity(AdminMainActivityIntent);
                            }
                        }
                    });
                }
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
                }

                else {
                    GPSLocation GPS = new GPSLocation(mContext);
                    double GPS_X = GPS.getGPS_X();
                    double GPS_Y = GPS.getGPS_Y();
                    Building.getInstance().SetData("",
                            GPS.getGPS_X(), GPS.getGPS_Y(), GPSLocation.getAddress(mContext, GPS_X, GPS_Y), 0, new ArrayList<Node>());
                    return;
                }

                EditText editBuildingName = (EditText) findViewById(R.id.editBuildingName);
                editBuildingName.setText(Building.getInstance().buildingName);

                // 층별 사진 다운로드
                downloadCount = 0;
                for (floorIndex = 0; floorIndex < Building.getInstance().floorNumber; floorIndex++) {
                    final int index = floorIndex;
                    items.add(new BuildingInfoItem(index));

                    StorageReference spaceReference = storageReference.child("bjp/floor" + Integer.toString(index + 1) + ".png");
                    final long ONE_MEGABYTE = 1024 * 1024;
                    spaceReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            items.get(index).setImageFloor(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            downloadCount++;
                            if (downloadCount == Building.getInstance().floorNumber) {
                                runAnimation();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        adapter.notifyDataSetChanged();
    }

    private void runAnimation() {
        Context context = listview.getContext();
        LayoutAnimationController controller = null;

        controller = AnimationUtils.loadLayoutAnimation(context, R.anim.anim_fall_down);

        listview.setLayoutAnimation(controller);
        adapter.notifyDataSetChanged();
        listview.scheduleLayoutAnimation();
    }

    @Override
    public void onRecyclerButtonClick(String type, int position) {
        currentPosition = position;

        if (type == "Edit") {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 20);
        }
        if (type == "Delete") {
            Toast.makeText(this, Integer.toString(position + 1) + " Item is selected..", Toast.LENGTH_SHORT).show();
            items.remove(position);

            for (int i = 0; i < adapter.getItemCount(); i++) {
                items.get(i).setIndex(i);
            }

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 20 && resultCode == RESULT_OK) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(data.getData(), projection, null, null, null);
            cursor.moveToFirst();
            String filePath = cursor.getString(0);
            insertImageView(filePath);
        }
    }

    private void insertImageView(String filePath) {
        if (!filePath.equals("")) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                InputStream in = new FileInputStream(filePath);
                BitmapFactory.decodeStream(in, null, options);
                in.close();
                in = null;
            } catch (Exception e) {
                e.printStackTrace();
            }

            int inSampleSize = 1;

            BitmapFactory.Options imgOptions=new BitmapFactory.Options();
            imgOptions.inSampleSize=inSampleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, imgOptions);
            items.get(currentPosition).setImageFloor(bitmap);

            adapter.notifyDataSetChanged();
        }
    }
}
