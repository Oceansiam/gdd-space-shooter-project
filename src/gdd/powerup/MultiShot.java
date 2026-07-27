package gdd.powerup;

import static gdd.Global.*;
import gdd.sprite.Player;

public class MultiShot extends PowerUp {

    public MultiShot(int x, int y) {
        super(x, y);
        // Loaded synchronously to avoid the macOS getScaledInstance crash.
        setImage(gdd.ImageUtil.loadScaled(IMG_POWERUP_MULTISHOT, SCALE_FACTOR));
    }

    public void act() {
        this.x -= 2; // Move left across the screen, same as SpeedUp
    }

    public void upgrade(Player player) {
        // 4-stage shot upgrade; no-ops once the player is already maxed out.
        player.upgradeShot();
        this.die(); // Remove the power-up after use
    }
}
