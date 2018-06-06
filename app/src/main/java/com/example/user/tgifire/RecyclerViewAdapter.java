package com.example.user.tgifire;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> implements View.OnClickListener, ItemTouchHelperListener {
    private ArrayList<BuildingInfoItem> mItems;
    Context mContext;

    // 버튼 클릭 이벤트를 위한 Listener 인터페이스 정의.
    public interface RecyclerButtonClickListener {
        void onRecyclerButtonClick(String type, int position);
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
        mItems.get(position).setIndex(position);
        holder.textFloor.setText(mItems.get(position).getText());
        if (mItems.get(position).getImageFloor() != null) {
            holder.imageFloor.setImageBitmap(mItems.get(position).getImageFloor());
        }
        else {
            holder.imageFloor.setImageBitmap(null);
        }

        // 이벤트처리 : 생성된 List 중 선택된 목록번호를 Toast로 출력
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, String.format("%d 선택", position + 1), Toast.LENGTH_SHORT).show();
            }
        });

        holder.buttonEditPicture.setTag(position);
        holder.buttonEditPicture.setOnClickListener(this);
        holder.buttonDeleteFloor.setTag(position + 1000);
        holder.buttonDeleteFloor.setOnClickListener(this);
    }

    // 필수 오버라이드 : 데이터 갯수 반환
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < 0 || fromPosition >= mItems.size() || toPosition < 0 || toPosition >= mItems.size()){
            return false;
        }

        BuildingInfoItem fromItem = mItems.get(fromPosition);
        mItems.remove(fromPosition);
        mItems.add(toPosition, fromItem);

        notifyItemMoved(fromPosition, toPosition);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < getItemCount(); i++) {
                    mItems.get(i).setIndex(i);
                }
                notifyDataSetChanged();
            }
        }, 500);

        return true;
    }

    @Override
    public void onItemRemove(int position) {
        mItems.remove(position);
        for (int i = 0; i < getItemCount(); i++) {
            mItems.get(i).setIndex(i);
        }
        notifyItemRemoved(position);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < getItemCount(); i++) {
                    mItems.get(i).setIndex(i);
                }
                notifyDataSetChanged();
            }
        }, 500);
    }

    // buttonDeleteFloor가 눌려졌을 때 실행되는 onClick함수.
    public void onClick(View v) {
        // ListBtnClickListener(MainActivity)의 onListBtnClick() 함수 호출.
        if (this.recyclerButtonClickListener != null) {
            if ((int) v.getTag() < 1000) {
                this.recyclerButtonClickListener.onRecyclerButtonClick("Edit", (int) v.getTag());
            }
            else {
                this.recyclerButtonClickListener.onRecyclerButtonClick("Delete", (int) v.getTag() - 1000);
            }
        }
    }
}