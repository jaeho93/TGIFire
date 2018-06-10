package com.example.user.tgifire;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{
    public TextView textFloor;
    public Button buttonEditPicture;
    public Button buttonDeleteFloor;
    public Button buttonAddFloor;
    public ImageView imageFloor;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        textFloor = (TextView) itemView.findViewById(R.id.textFloor);
        buttonEditPicture = (Button) itemView.findViewById(R.id.buttonEditPicture);
        buttonDeleteFloor = (Button) itemView.findViewById(R.id.buttonDeleteFloor);
        buttonAddFloor = (Button) itemView.findViewById(R.id.buttonAddFloor);
        imageFloor = (ImageView) itemView.findViewById(R.id.imageFloor);
    }
}
