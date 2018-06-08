package com.example.user.tgifire;

import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;

public class FloorPicture {
    private static FloorPicture uniqueInstance;

    public ArrayList<BitmapDrawable> floorPicture;

    private FloorPicture() {
    }

    public static FloorPicture getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new FloorPicture();
            uniqueInstance.floorPicture = new ArrayList<BitmapDrawable>();
        }

        return uniqueInstance;

    }
}
