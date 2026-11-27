package com.gravityflip.core;

import com.gravityflip.entities.Player;
import com.gravityflip.render.GameRenderer;
import com.gravityflip.world.CollisionSystem;
import com.gravityflip.world.LevelManager;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

public class GameEngine {

    private static final double MAX_DT = 0.05;
    private static final double PLAYER_START_X = 160.0;
    private static final double BG_PARALLAX_FACTOR = 0.35;

    private final Canvas canvas;
    private final InputHandler input;

    private final LevelManager levelManager;
    private final CollisionSystem collisionSystem;
    private final GameRenderer renderer;
    private final Player player;

    private GameState state = GameState.MENU;
    private long lastNanoTime = 0;
    private int bestScore = 0;
    private boolean isNewBest = false;
    private double bgOffset = 0.0;

    private final AnimationTimer loop;

    public GameEngine(Canvas canvas, InputHandler input) {
        this.canvas = canvas;
        this.input = input;

        this.levelManager = new LevelManager(canvas.getWidth(), canvas.getHeight());
        this.collisionSystem = new CollisionSystem();
        this.renderer = new GameRenderer(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight());
        this.player = new Player(PLAYER_START_X, levelManager.getFloorY() - Player.SIZE);

        this.loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastNanoTime == 0) {
                    lastNanoTime = now;
                    return;
                }
                double dt = Math.min((now - lastNanoTime) / 1_000_000_000.0, MAX_DT);
                lastNanoTime = now;

                update(dt);
                render();
            }
        };
    }

    public void start() {
        loop.start();
    }

    public void stop() {
        loop.stop();
    }

    private void update(double dt) {
        boolean flipPressed = input.consumeFlip();
        boolean restartPressed = input.consumeRestart();

        switch (state) {
            case MENU -> {
                if (flipPressed) {
                    resetRun();
                    state = GameState.PLAYING;
                }
            }
            case PLAYING -> {
                if (flipPressed) {
                    player.tryFlip();
                }

                double prevY = player.getY();
                player.update(dt);
                levelManager.update(dt);
                bgOffset += levelManager.getScrollSpeed() * BG_PARALLAX_FACTOR * dt;

                boolean dead = collisionSystem.resolve(
                        player,
                        prevY,
                        levelManager.getPlatforms(),
                        levelManager.getHazards(),
                        canvas.getHeight()
                );

                if (dead) {
                    int score = levelManager.getScore();
                    if (score > bestScore) {
                        bestScore = score;
                        isNewBest = true;
                    } else {
                        isNewBest = false;
                    }
                    state = GameState.GAME_OVER;
                } else if (restartPressed) {
                    resetRun();
                }
            }
            case GAME_OVER -> {
                if (flipPressed || restartPressed) {
                    resetRun();
                    state = GameState.PLAYING;
                }
            }
        }
    }

    private void resetRun() {
        levelManager.reset();
        player.reset(PLAYER_START_X, levelManager.getFloorY() - Player.SIZE);
        isNewBest = false;
        bgOffset = 0.0;
        input.clear();
    }

    private void render() {
        renderer.render(state, player, levelManager, bestScore, isNewBest, bgOffset);
    }
}