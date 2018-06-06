package com.example.user.tgifire;

import android.graphics.Bitmap;
import android.util.Log;

public class BuildingInfoItem {
    private int index;
    private String textFloor;
    private Bitmap imageFloor = null;

    public BuildingInfoItem(int index) {
        this.index = index;
        this.textFloor = Integer.toString(index + 1) + "층";
    }

    public void setText(String text) { this.textFloor = text; }
    public void setIndex(int index)
    {
        this.index = index;
        this.textFloor = Integer.toString(index + 1) + "층";
    }
    public void setImageFloor(Bitmap bitmap) { imageFloor = bitmap; }

    public int getIndex() {
        return index;
    }
    public String getText(){
        return textFloor;
    }
    public Bitmap getImageFloor() { return imageFloor; }
}
