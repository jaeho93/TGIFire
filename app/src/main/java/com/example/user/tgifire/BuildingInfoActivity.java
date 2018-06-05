package com.example.user.tgifire;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BuildingInfoActivity extends AppCompatActivity implements ListViewButtonAdapter.ListBtnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_info);

        ListView listview ;
        ListViewButtonAdapter adapter;
        ArrayList<BuildingInfoItem> items = new ArrayList<BuildingInfoItem>() ;

        // items 로드.
        loadItemsFromDB(items) ;

        // Adapter 생성
        adapter = new ListViewButtonAdapter(this, R.layout.item_building_info, items, this) ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listBuildingInfo);
        listview.setAdapter(adapter);

        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // TODO : item click
            }
        }) ;
    }

    public boolean loadItemsFromDB(ArrayList<BuildingInfoItem> list) {
        BuildingInfoItem item ;
        int i ;

        if (list == null) {
            list = new ArrayList<BuildingInfoItem>() ;
        }

        // 순서를 위한 i 값을 1로 초기화.
        i = 1 ;

        // 아이템 생성.
        item = new BuildingInfoItem() ;
        item.setText(Integer.toString(i) + "층") ;
        list.add(item) ;
        i++ ;
        item = new BuildingInfoItem() ;
        item.setText(Integer.toString(i) + "층") ;
        list.add(item) ;
        i++ ;
        item = new BuildingInfoItem() ;
        item.setText(Integer.toString(i) + "층") ;
        list.add(item) ;
        i++ ;
        item = new BuildingInfoItem() ;
        item.setText(Integer.toString(i) + "층") ;
        list.add(item) ;
        i++ ;

        return true ;
    }

    @Override
    public void onListBtnClick(int position) {
        Toast.makeText(this, Integer.toString(position+1) + " Item is selected..", Toast.LENGTH_SHORT).show() ;
    }
}
