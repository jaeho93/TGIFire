package com.example.user.tgifire;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BuildingInfoActivity extends AppCompatActivity implements RecyclerViewAdapter.RecyclerButtonClickListener{
    RecyclerView listview ;
    ArrayList<BuildingInfoItem> items;// = new ArrayList<BuildingInfoItem>() ;
    RecyclerViewAdapter adapter;// = new ListViewButtonAdapter(this, R.layout.item_building_info, items, this) ;
    LinearLayoutManager mLayoutManager;

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
        // Animation Defualt 설정
        listview.setItemAnimator(new DefaultItemAnimator());

        // Adapter 생성 및 달기
        adapter = new RecyclerViewAdapter(items, this);
        listview.setAdapter(adapter);
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

    @Override
    public void onRecyclerButtonClick(int position) {
        Toast.makeText(this, Integer.toString(position+1) + " Item is selected..", Toast.LENGTH_SHORT).show();
        items.add(position + 1, new BuildingInfoItem(position + 1));

        for (int i = 0; i < adapter.getItemCount(); i++) {
            items.set(i, new BuildingInfoItem(i));
        }

        adapter.notifyDataSetChanged();
    }
}
