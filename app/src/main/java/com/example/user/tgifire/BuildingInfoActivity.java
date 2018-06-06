package com.example.user.tgifire;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

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

    RecyclerView listview ;
    ArrayList<BuildingInfoItem> items;// = new ArrayList<BuildingInfoItem>() ;
    RecyclerViewAdapter adapter;// = new ListViewButtonAdapter(this, R.layout.item_building_info, items, this) ;
    LinearLayoutManager mLayoutManager;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://tgifire-cdf25.appspot.com/");
    StorageReference storageReference = storage.getReference();

    int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_info);

        items = new ArrayList<BuildingInfoItem>() ;

        // items 로드.
        loadItemsFromDB();

        // 리스트뷰 참조
        listview = (RecyclerView) findViewById(R.id.listBuildingInfo);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        // LinearLayout으로 설정
        listview.setLayoutManager(mLayoutManager);

        // Adapter 생성 및 달기
        adapter = new RecyclerViewAdapter(items, this);
        listview.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
        itemTouchHelper.attachToRecyclerView(listview);

        runAnimation();

        Button buttonAddFloor = (Button) findViewById(R.id.buttonAddFloor);
        buttonAddFloor.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int n = adapter.getItemCount();
                items.add(n, new BuildingInfoItem(n));

                adapter.notifyDataSetChanged();
            }
        });

        Button buttonSaveBuildingInfo = (Button) findViewById(R.id.buttonSaveBuildingInfo);
        buttonSaveBuildingInfo.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                EditText editBuildingName = (EditText) findViewById(R.id.editBuildingName);

                // 건물 이름 업로드
                databaseReference.child("bjp").child("BUILDING").child("NAME").push().setValue(editBuildingName.getText().toString());
                // GPS 업로드
                GPSLocation GPS = new GPSLocation(mContext);
                double GPS_X = GPS.getGPS_X();
                double GPS_Y = GPS.getGPS_Y();
                databaseReference.child("bjp").child("BUILDING").child("GPS_X").push().setValue(Double.toString(GPS_X));
                databaseReference.child("bjp").child("BUILDING").child("GPS_Y").push().setValue(Double.toString(GPS_Y));
                databaseReference.child("bjp").child("BUILDING").child("ADDRESS").push().setValue(GPSLocation.getAddress(mContext, GPS_X, GPS_Y));
                // 층 개수 업로드
                databaseReference.child("bjp").child("BUILDING").child("FLOOR_NUMBER").push().setValue(Integer.toString(adapter.getItemCount()));

                // 층별 사진 업로드
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    if (items.get(i).getImageFloor() == null) continue;
                    // Get the data from an ImageView as bytes
                    Bitmap bitmap = items.get(i).getImageFloor();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    StorageReference spaceReference = storageReference.child("bjp/floor" + Integer.toString(i + 1) + ".jpg");
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
                            Toast.makeText(mContext, "Upload complete!", Toast.LENGTH_SHORT).show();
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        }
                    });
                }
            }
        });
    }

    public boolean loadItemsFromDB() {
        int i ;

        // 순서를 위한 i 값을 1로 초기화.
        i = 0;

        // 아이템 생성.
        items.add(new BuildingInfoItem(i));
        i++;
        items.add(new BuildingInfoItem(i));
        i++;
        items.add(new BuildingInfoItem(i));

        return true ;
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
            Bitmap bitmap=BitmapFactory.decodeFile(filePath, imgOptions);
            items.get(currentPosition).setImageFloor(bitmap);

            adapter.notifyDataSetChanged();
        }
    }
}
