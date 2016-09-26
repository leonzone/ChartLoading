package com.reiser.chartloading;

/**
 * Created by sunsharp on 16/9/6.
 */
public class Point {
    float x;
    float y;
    float r;
    int color;


    public Point(float x, float y, float r, int color) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.color = color;
    }

    public Point() {
    }

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

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
