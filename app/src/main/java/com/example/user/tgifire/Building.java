package com.example.user.tgifire;

public class Building {
    public String buildingName;
    public double GPS_X;
    public double GPS_Y;
    public String address;
    public int floorNumber;

    public Building() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Building(String buildingName, double GPS_X, double GPS_Y, String address, int floorNumber) {
        this.buildingName = buildingName;
        this.GPS_X = GPS_X;
        this.GPS_Y = GPS_Y;
        this.address = address;
        this.floorNumber = floorNumber;
    }
}
