package com.example.user.tgifire;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class BuildingInfoActivity extends AppCompatActivity implements ListViewButtonAdapter.ListBtnClickListener {
    ListView listview ;
    ArrayList<BuildingInfoItem> items;// = new ArrayList<BuildingInfoItem>() ;
    ListViewButtonAdapter adapter;// = new ListViewButtonAdapter(this, R.layout.item_building_info, items, this) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_info);


        ListView listview ;
        items = new ArrayList<BuildingInfoItem>() ;

        // items 로드.
        loadItemsFromDB(items);

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
        });

        // push button에 대한 이벤트 처리.
        Button buttonPushItemOnHead = (Button)findViewById(R.id.buttonPushItemOnHead) ;
        buttonPushItemOnHead.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // 아이템 추가.
                BuildingInfoItem item = new BuildingInfoItem() ;
                item.setText(Integer.toString(1) + "층") ;
                items.add(0, item);

                // listview 갱신
                adapter.notifyDataSetChanged();
            }
        });
        Button buttonPushItemOnTail = (Button)findViewById(R.id.buttonPushItemOnTail) ;
        buttonPushItemOnTail.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                int count;
                count = adapter.getCount();

                // 아이템 추가.
                BuildingInfoItem item = new BuildingInfoItem() ;
                item.setText(Integer.toString(count + 1) + "층") ;
                items.add(item);

                // listview 갱신
                adapter.notifyDataSetChanged();
            }
        });
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
        // 아이템 삭제
        items.remove(position) ;

        // listview 갱신.
        adapter.notifyDataSetChanged();
    }
}
