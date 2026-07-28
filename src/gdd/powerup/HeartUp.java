package gdd.powerup;

import java.awt.Color;
import gdd.sprite.Player;

public class HeartUp extends PowerUp {

    private static final int ICON_SIZE = 34;

    public HeartUp(int x, int y) {
        super(x, y);
        // Drawn in code (see ImageUtil.createHeartIcon) rather than loaded
        // from a file - a plain heart shape needs no sourced art asset.
        setImage(gdd.ImageUtil.createHeartIcon(ICON_SIZE, new Color(230, 40, 60)));
    }

    public void act() {
        this.x -= 2; // Move left across the screen, same as the other power-ups
    }

    public void upgrade(Player player) {
        // Adds one heart; no-op once the player is already at full lives.
        player.addLife();
        this.die();
    }
}
