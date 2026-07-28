package gdd.sprite;

import static gdd.Global.*;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

/** A "speed assassin" type: flies faster than Alien1 and drifts gently up/down. */
public class Enemy2 extends Enemy {

    private static final int SPEED = 3; // faster than Alien1's 1px/frame
    private static final int DRIFT_RANGE = 15; // max pixels above/below its base Y
    private static final int ANIMATION_INTERVAL = 10; // ticks per pose - a quick engine-flare flicker

    private int baseY;
    private int tick = 0;

    public Enemy2(int x, int y) {
        super(x, y);
        this.baseY = y;
        // Replaces the metroid animation super(x, y) just set up with its
        // own distinct ship art. Only one clean crop of this ship exists,
        // so the second "engine flare" frame is a tinted copy of the same
        // art (see ImageUtil.tint) - still a real two-frame animation via
        // the shared Sprite animation system, not a static image.
        BufferedImage base = gdd.ImageUtil.loadScaledToFit(IMG_ENEMY2_SHIP, ALIEN_WIDTH, ALIEN_HEIGHT);
        BufferedImage flare = gdd.ImageUtil.tint(base, new Color(80, 200, 255), 0.4f);
        setAnimationFrames(new Image[]{base, flare}, ANIMATION_INTERVAL);
    }

    public void act(int direction) {
        this.x -= SPEED;

        tick++;
        this.y = baseY + (int) Math.round(Math.sin(tick * 0.08) * DRIFT_RANGE);
    }
}
