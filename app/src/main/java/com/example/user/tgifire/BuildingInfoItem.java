package com.example.user.tgifire;

public class BuildingInfoItem {
    private int index;
    private String textFloor;

    public BuildingInfoItem(int index) {
        this.index = index;
        this.textFloor = Integer.toString(index + 1) + "층";
    }

    public int getIndex() {
        return index;
    }
    public String getText(){
        return textFloor;
    }
    public void setText(String text) { this.textFloor = text; }
    public void setIndex(int index) {
        this.index = index;
    }

}
