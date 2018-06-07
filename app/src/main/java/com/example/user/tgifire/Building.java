package com.example.user.tgifire;

import java.io.Serializable;
import java.util.ArrayList;

public class Building implements Serializable{

    public String buildingName;
    public double GPS_X;
    public double GPS_Y;
    public String address;
    public int floorNumber;
    public ArrayList<Node> nodes;

    public Building() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Building(String buildingName, double GPS_X, double GPS_Y, String address, int floorNumber, ArrayList<Node> nodes) {
        this.buildingName = buildingName;
        this.GPS_X = GPS_X;
        this.GPS_Y = GPS_Y;
        this.address = address;
        this.floorNumber = floorNumber;
        this.nodes = nodes;
    }

    public class Node {
        private int x;
        private int y;
        private int state;

        public Node(int x, int y, int state){
            this.x = x;
            this.y = y;
            this.state = state;
        }

        public void setState(int state) { this.state = state; }
        public int getState() { return this.state; }
    }
}
