package com.gravityflip.entities;

public class Hazard {

    private double x;
    private final double y;
    private final double width;
    private final double height;
    private final boolean pointingUp;

    public Hazard(double x, double y, double width, double height, boolean pointingUp) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pointingUp = pointingUp;
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
    public boolean isPointingUp() { return pointingUp; }
}