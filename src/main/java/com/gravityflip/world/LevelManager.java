package com.gravityflip.world;

import com.gravityflip.entities.Hazard;
import com.gravityflip.entities.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LevelManager {

    public static final double CEILING_THICKNESS = 60.0;
    public static final double FLOOR_THICKNESS = 60.0;

    private static final double BASE_SPEED = 280.0;      // px/s
    private static final double MAX_SPEED = 520.0;       // px/s
    private static final double SPEED_RAMP = 6.0;        // px/s per second survived
    private static final double SPIKE_SIZE = 28.0;

    private final double screenWidth;
    private final double screenHeight;
    private final double floorY;

    private final List<Platform> platforms = new ArrayList<>();
    private final List<Hazard> hazards = new ArrayList<>();
    private final Random random = new Random();

    private double scrollSpeed = BASE_SPEED;
    private double nextSpawnX = 0.0;
    private double distanceTraveled = 0.0;

    public LevelManager(double screenWidth, double screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.floorY = screenHeight - FLOOR_THICKNESS;
        reset();
    }

    public void reset() {
        platforms.clear();
        hazards.clear();
        scrollSpeed = BASE_SPEED;
        distanceTraveled = 0.0;

        // Safe starting runway
        double startWidth = 900.0;
        platforms.add(new Platform(0, 0, startWidth, CEILING_THICKNESS));
        platforms.add(new Platform(0, floorY, startWidth, FLOOR_THICKNESS));
        nextSpawnX = startWidth;
    }

    public void update(double dt) {
        scrollSpeed = Math.min(MAX_SPEED, scrollSpeed + SPEED_RAMP * dt);
        double dx = scrollSpeed * dt;
        distanceTraveled += dx;

        for (Platform p : platforms) {
            p.scroll(dx);
        }
        for (Hazard h : hazards) {
            h.scroll(dx);
        }
        nextSpawnX -= dx;

        // Remove off-screen entities
        platforms.removeIf(p -> p.getRight() < -100);
        hazards.removeIf(h -> h.getRight() < -100);

        // Keep world generated ahead of the right edge
        while (nextSpawnX < screenWidth + 300) {
            spawnNextChunk();
        }
    }

    private void spawnNextChunk() {
        int pattern = random.nextInt(4);
        double chunkWidth = 260 + random.nextInt(140); // 260–400px

        switch (pattern) {
            case 0 -> {
                // Floor gap: solid ceiling, gap in the floor
                double gap = 120 + random.nextInt(60);
                platforms.add(new Platform(nextSpawnX, 0, chunkWidth, CEILING_THICKNESS));
                platforms.add(new Platform(nextSpawnX + gap, floorY, chunkWidth - gap, FLOOR_THICKNESS));
            }
            case 1 -> {
                // Ceiling gap: solid floor, gap in the ceiling
                double gap = 120 + random.nextInt(60);
                platforms.add(new Platform(nextSpawnX, floorY, chunkWidth, FLOOR_THICKNESS));
                platforms.add(new Platform(nextSpawnX + gap, 0, chunkWidth - gap, CEILING_THICKNESS));
            }
            case 2 -> {
                // Spike corridor: solid floor & ceiling with spikes on one side
                platforms.add(new Platform(nextSpawnX, 0, chunkWidth, CEILING_THICKNESS));
                platforms.add(new Platform(nextSpawnX, floorY, chunkWidth, FLOOR_THICKNESS));

                boolean onFloor = random.nextBoolean();
                int spikeCount = 2 + random.nextInt(2); // 2–3 spikes
                double startX = nextSpawnX + 80;
                for (int i = 0; i < spikeCount; i++) {
                    double sx = startX + i * SPIKE_SIZE;
                    double sy = onFloor ? floorY - SPIKE_SIZE : CEILING_THICKNESS;
                    hazards.add(new Hazard(sx, sy, SPIKE_SIZE, SPIKE_SIZE, onFloor));
                }
            }
            default -> {
                // Mid-air platform + opposite hazard
                platforms.add(new Platform(nextSpawnX, 0, chunkWidth, CEILING_THICKNESS));
                platforms.add(new Platform(nextSpawnX, floorY, chunkWidth, FLOOR_THICKNESS));

                double midY = CEILING_THICKNESS + 120 + random.nextInt(90);
                double midW = 130;
                platforms.add(new Platform(nextSpawnX + 70, midY, midW, 22));
            }
        }

        nextSpawnX += chunkWidth;
    }

    public List<Platform> getPlatforms() {
        return Collections.unmodifiableList(platforms);
    }

    public List<Hazard> getHazards() {
        return Collections.unmodifiableList(hazards);
    }

    public double getFloorY() { return floorY; }
    public double getScrollSpeed() { return scrollSpeed; }
    public int getScore() { return (int) (distanceTraveled / 15.0); }
}