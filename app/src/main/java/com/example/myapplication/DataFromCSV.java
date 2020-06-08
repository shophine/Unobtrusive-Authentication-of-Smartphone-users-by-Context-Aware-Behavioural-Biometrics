package com.example.myapplication;

public class DataFromCSV {
    private float x,y;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "DataFromCSV{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
