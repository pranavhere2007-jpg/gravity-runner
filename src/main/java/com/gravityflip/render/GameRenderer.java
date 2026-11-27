package com.gravityflip.render;

import com.gravityflip.core.GameState;
import com.gravityflip.entities.Hazard;
import com.gravityflip.entities.Platform;
import com.gravityflip.entities.Player;
import com.gravityflip.world.LevelManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class GameRenderer {

    private final GraphicsContext gc;
    private final double width;
    private final double height;

    private static final double GRID_SPACING = 40.0;

    public GameRenderer(GraphicsContext gc, double width, double height) {
        this.gc = gc;
        this.width = width;
        this.height = height;
    }

    public void render(GameState state, Player player, LevelManager levelManager,
                       int bestScore, boolean isNewBest, double bgOffset) {
        drawBackground(bgOffset);
        drawPlatforms(levelManager);
        drawHazards(levelManager);
        drawPlayer(player);
        drawHud(levelManager, player, bestScore);

        if (state == GameState.MENU) {
            drawMenuOverlay();
        } else if (state == GameState.GAME_OVER) {
            drawGameOverOverlay(levelManager.getScore(), bestScore, isNewBest);
        }
    }

    private void drawBackground(double bgOffset) {
        gc.setFill(Color.web("#0f172a"));
        gc.fillRect(0, 0, width, height);

        // Subtle scrolling parallax grid
        gc.setStroke(Color.web("#1e293b"));
        gc.setLineWidth(1);
        double offsetX = -(bgOffset % GRID_SPACING);
        for (double x = offsetX; x < width; x += GRID_SPACING) {
            gc.strokeLine(x, 0, x, height);
        }
        for (double y = 0; y < height; y += GRID_SPACING) {
            gc.strokeLine(0, y, width, y);
        }
    }

    private void drawPlatforms(LevelManager levelManager) {
        for (Platform p : levelManager.getPlatforms()) {
            gc.setFill(Color.web("#1e293b"));
            gc.fillRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());

            gc.setStroke(Color.web("#475569"));
            gc.setLineWidth(2);
            gc.strokeRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());
        }
    }

    private void drawHazards(LevelManager levelManager) {
        for (Hazard hz : levelManager.getHazards()) {
            double x = hz.getX();
            double y = hz.getY();
            double hw = hz.getWidth();
            double hh = hz.getHeight();

            double[] xs = { x, x + hw / 2.0, x + hw };
            double[] ys = hz.isPointingUp()
                    ? new double[]{ y + hh, y, y + hh }
                    : new double[]{ y, y + hh, y };

            gc.setFill(Color.web("#ef4444"));
            gc.fillPolygon(xs, ys, 3);

            gc.setStroke(Color.web("#fca5a5"));
            gc.setLineWidth(1.5);
            gc.strokePolygon(xs, ys, 3);
        }
    }

    private void drawPlayer(Player player) {
        boolean gravityDown = player.getGravitySign() == 1;
        Color bodyColor = gravityDown ? Color.web("#38bdf8") : Color.web("#f472b6");

        // Player body
        gc.setFill(bodyColor);
        gc.fillRoundRect(player.getX(), player.getY(), player.getWidth(), player.getHeight(), 6, 6);

        // Active landing-edge strip
        gc.setFill(Color.WHITE);
        double stripY = gravityDown
                ? player.getY() + player.getHeight() - 5
                : player.getY() + 1;
        gc.fillRect(player.getX() + 4, stripY, player.getWidth() - 8, 4);

        // Gravity direction chevron inside player
        gc.setFill(Color.web("#0f172a"));
        double cx = player.getX() + player.getWidth() / 2.0;
        double cy = player.getY() + player.getHeight() / 2.0;
        double[] ax = { cx - 5, cx + 5, cx };
        double[] ay = gravityDown
                ? new double[]{ cy - 3, cy - 3, cy + 4 }
                : new double[]{ cy + 3, cy + 3, cy - 4 };
        gc.fillPolygon(ax, ay, 3);
    }

    private void drawHud(LevelManager levelManager, Player player, int bestScore) {
        gc.setFill(Color.web("#f8fafc"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("SCORE: " + levelManager.getScore(), 16, 36);

        gc.setFill(Color.web("#94a3b8"));
        gc.fillText("BEST: " + bestScore, 150, 36);

        // Gravity cue badge on top-right
        boolean gravityDown = player.getGravitySign() == 1;
        gc.setFill(gravityDown ? Color.web("#38bdf8") : Color.web("#f472b6"));
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText(gravityDown ? "GRAVITY: DOWN" : "GRAVITY: UP", width - 16, 36);
    }

    private void drawMenuOverlay() {
        gc.setFill(Color.rgb(15, 23, 42, 0.78));
        gc.fillRect(0, 0, width, height);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.web("#38bdf8"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 36));
        gc.fillText("GRAVITY FLIP ARCADE", width / 2.0, height / 2.0 - 45);

        gc.setFill(Color.web("#cbd5e1"));
        gc.setFont(Font.font("Monospaced", 16));
        gc.fillText("Flip gravity to dodge spikes and cross gaps", width / 2.0, height / 2.0 - 5);

        gc.setFill(Color.web("#f8fafc"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 18));
        gc.fillText("PRESS SPACE / W / UP TO START", width / 2.0, height / 2.0 + 45);
    }

    private void drawGameOverOverlay(int score, int bestScore, boolean isNewBest) {
        gc.setFill(Color.rgb(15, 23, 42, 0.82));
        gc.fillRect(0, 0, width, height);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.web("#ef4444"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 36));
        gc.fillText("GAME OVER", width / 2.0, height / 2.0 - 55);

        gc.setFill(Color.web("#f8fafc"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 20));
        gc.fillText("SCORE: " + score + "   |   BEST: " + bestScore, width / 2.0, height / 2.0 - 10);

        if (isNewBest && score > 0) {
            gc.setFill(Color.web("#facc15"));
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 16));
            gc.fillText("NEW HIGH SCORE!", width / 2.0, height / 2.0 + 20);
        }

        gc.setFill(Color.web("#cbd5e1"));
        gc.setFont(Font.font("Monospaced", 16));
        gc.fillText("PRESS SPACE OR R TO RETRY", width / 2.0, height / 2.0 + 60);
    }
}