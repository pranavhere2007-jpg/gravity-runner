package com.gravityflip.world;

import com.gravityflip.entities.Hazard;
import com.gravityflip.entities.Platform;
import com.gravityflip.entities.Player;

import java.util.List;

public class CollisionSystem {

    private static final double HAZARD_INSET = 5.0; // Forgiving arcade spike hitbox

    /**
     * Resolves platform landings and checks for fatal collisions.
     *
     * @return true if the player died this frame, false otherwise.
     */
    public boolean resolve(Player player, double prevY, List<Platform> platforms,
                           List<Hazard> hazards, double screenHeight) {
        player.setOnSurface(false);

        double px = player.getX();
        double py = player.getY();
        double pw = player.getWidth();
        double ph = player.getHeight();

        // 1. Out-of-bounds check (fell through a floor or ceiling gap)
        if (py + ph < 0 || py > screenHeight) {
            return true;
        }

        // 2. Platform resolution
        for (Platform plat : platforms) {
            if (!overlaps(px, py, pw, ph, plat.getX(), plat.getY(), plat.getWidth(), plat.getHeight())) {
                continue;
            }

            // Landed on top of platform (normal gravity)
            if (prevY + ph <= plat.getY() + 4 && player.getVy() >= 0) {
                player.landOnFloor(plat.getY());
                py = player.getY();
            }
            // Landed on underside of platform (inverted gravity)
            else if (prevY >= plat.getBottom() - 4 && player.getVy() <= 0) {
                player.landOnCeiling(plat.getBottom());
                py = player.getY();
            }
            // Slammed into the front face of a platform
            else {
                return true;
            }
        }

        // 3. Hazard (spike) check
        for (Hazard h : hazards) {
            if (overlaps(
                    px, py, pw, ph,
                    h.getX() + HAZARD_INSET,
                    h.getY() + HAZARD_INSET,
                    h.getWidth() - HAZARD_INSET * 2,
                    h.getHeight() - HAZARD_INSET * 2)) {
                return true;
            }
        }

        return false;
    }

    private boolean overlaps(double ax, double ay, double aw, double ah,
                             double bx, double by, double bw, double bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }
}