package com.gravityflip;

import com.gravityflip.core.GameEngine;
import com.gravityflip.core.InputHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 450;

    private GameEngine engine;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        InputHandler input = new InputHandler(scene);
        engine = new GameEngine(canvas, input);

        stage.setTitle("Gravity Flip Arcade");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> engine.stop());
        stage.show();

        engine.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}