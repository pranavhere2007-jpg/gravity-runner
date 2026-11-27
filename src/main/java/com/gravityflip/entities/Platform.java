package com.gravityflip.entities;

public class Platform {

    private double x;
    private final double y;
    private final double width;
    private final double height;

    public Platform(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void scroll(double dx) {
        this.x -= dx;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getRight() { return x + width; }
    public double getBottom() { return y + height; }
}