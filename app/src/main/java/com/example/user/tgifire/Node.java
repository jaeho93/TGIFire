package com.example.user.tgifire;

public class Node {
    public int x;
    public int y;
    public int floor;
    public String name;
    public String did;
    public boolean state;

    private Node() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Node(int x, int y, int floor, String name, String did, boolean state){
        this.x = x;
        this.y = y;
        this.floor = floor;
        this.name = name;
        this.did = did;
        this.state = state;
    }
}
