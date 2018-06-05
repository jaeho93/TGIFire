package com.example.user.tgifire;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> implements View.OnClickListener{
    private ArrayList<BuildingInfoItem> mItems;
    Context mContext;

    // 버튼 클릭 이벤트를 위한 Listener 인터페이스 정의.
    public interface RecyclerButtonClickListener {
        void onRecyclerButtonClick(int position) ;
    }
    // 생성자로부터 전달된 ListBtnClickListener  저장.
    private RecyclerButtonClickListener recyclerButtonClickListener;

    public RecyclerViewAdapter(ArrayList itemList, RecyclerButtonClickListener clickListener) {
        mItems = itemList;
        this.recyclerButtonClickListener = clickListener;
    }

    // 필수 오버라이드 : View 생성, ViewHolder 호출
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_building_info, parent, false);
        mContext = parent.getContext();
        RecyclerViewHolder holder = new RecyclerViewHolder(v);
        return holder;
    }
    // 필수 오버라이드 : 재활용되는 View 가 호출, Adapter 가 해당 position 에 해당하는 데이터를 결합
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        // 해당 position 에 해당하는 데이터 결합
        //holder.textFloor.setText(Integer.toString(mItems.get(position + 1).getIndex()) + "층");
        holder.textFloor.setText(mItems.get(position).getText());

        // 이벤트처리 : 생성된 List 중 선택된 목록번호를 Toast로 출력
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, String.format("%d 선택", position + 1), Toast.LENGTH_SHORT).show();
            }
        });

        holder.buttonAddFloor.setTag(position);
        holder.buttonAddFloor.setOnClickListener(this);
    }
    // 필수 오버라이드 : 데이터 갯수 반환
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    // button2가 눌려졌을 때 실행되는 onClick함수.
    public void onClick(View v) {
        // ListBtnClickListener(MainActivity)의 onListBtnClick() 함수 호출.
        if (this.recyclerButtonClickListener != null) {
            this.recyclerButtonClickListener.onRecyclerButtonClick((int)v.getTag());
        }
    }
}
