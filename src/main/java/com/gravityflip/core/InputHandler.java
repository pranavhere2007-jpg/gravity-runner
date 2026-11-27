package com.gravityflip.core;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

public class InputHandler {

    private final Set<KeyCode> heldKeys = new HashSet<>();
    private boolean flipQueued = false;
    private boolean restartQueued = false;

    public InputHandler(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            // Ignore OS key-repeat events while a key is already held
            if (heldKeys.add(code)) {
                if (code == KeyCode.SPACE || code == KeyCode.W || code == KeyCode.UP) {
                    flipQueued = true;
                } else if (code == KeyCode.R) {
                    restartQueued = true;
                }
            }
        });

        scene.setOnKeyReleased(event -> heldKeys.remove(event.getCode()));
    }

    public boolean consumeFlip() {
        if (flipQueued) {
            flipQueued = false;
            return true;
        }
        return false;
    }

    public boolean consumeRestart() {
        if (restartQueued) {
            restartQueued = false;
            return true;
        }
        return false;
    }

    public void clear() {
        flipQueued = false;
        restartQueued = false;
    }
}