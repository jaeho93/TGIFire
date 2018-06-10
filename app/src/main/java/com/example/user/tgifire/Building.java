package com.example.user.tgifire;

import android.graphics.drawable.BitmapDrawable;

import java.io.Serializable;
import java.util.ArrayList;

public class Building{
    private static Building uniqueInstance;

    public String buildingName;
    public double GPS_X;
    public double GPS_Y;
    public String address;
    public int floorNumber;
    public ArrayList<Node> nodes;

    private Building() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public static Building getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new Building();
        }

        return uniqueInstance;

    }

    public void SetData(String buildingName, double GPS_X, double GPS_Y, String address, int floorNumber, ArrayList<Node> nodes) {
        this.buildingName = buildingName;
        this.GPS_X = GPS_X;
        this.GPS_Y = GPS_Y;
        this.address = address;
        this.floorNumber = floorNumber;
        this.nodes = nodes;
    }

    public void Initialize() {
        uniqueInstance = null;
    }
}
