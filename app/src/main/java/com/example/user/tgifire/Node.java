package com.example.user.tgifire;

public class Node {
    public int x;
    public int y;
    public boolean state;
    public int floor;
    public String name;

    private Node() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Node(int x, int y, int floor, String name, boolean state){
        this.x = x;
        this.y = y;
        this.floor = floor;
        this.name = name;
        this.state = state;
    }
}
