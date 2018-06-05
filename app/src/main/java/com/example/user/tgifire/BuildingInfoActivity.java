package com.example.user.tgifire;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class BuildingInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_info);
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
}
