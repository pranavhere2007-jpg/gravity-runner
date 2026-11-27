package com.gravityflip.entities;

public class Player {

    public static final double SIZE = 32.0;
    private static final double GRAVITY = 2200.0;        // px/s^2
    private static final double MAX_FALL_SPEED = 900.0;  // px/s
    private static final double FLIP_IMPULSE = 140.0;    // instant kick on flip

    private double x;
    private double y;
    private final double width = SIZE;
    private final double height = SIZE;

    private double vy = 0.0;
    private int gravitySign = 1; // 1 = down, -1 = up
    private boolean onSurface = false;

    public Player(double startX, double startY) {
        reset(startX, startY);
    }

    public void reset(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.vy = 0.0;
        this.gravitySign = 1;
        this.onSurface = true;
    }

    /**
     * Flips gravity only when the player is grounded on a floor or ceiling.
     */
    public boolean tryFlip() {
        if (!onSurface) {
            return false;
        }
        gravitySign = -gravitySign;
        vy = gravitySign * FLIP_IMPULSE;
        onSurface = false;
        return true;
    }

    public void update(double dt) {
        vy += gravitySign * GRAVITY * dt;
        vy = Math.max(-MAX_FALL_SPEED, Math.min(MAX_FALL_SPEED, vy));
        y += vy * dt;
    }

    public void landOnFloor(double floorY) {
        this.y = floorY - height;
        this.vy = 0.0;
        this.onSurface = true;
    }

    public void landOnCeiling(double ceilingY) {
        this.y = ceilingY;
        this.vy = 0.0;
        this.onSurface = true;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getVy() { return vy; }
    public int getGravitySign() { return gravitySign; }
    public boolean isOnSurface() { return onSurface; }

    public void setY(double y) { this.y = y; }
    public void setVy(double vy) { this.vy = vy; }
    public void setOnSurface(boolean onSurface) { this.onSurface = onSurface; }
}