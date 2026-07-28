package gdd.sprite;

import static gdd.Global.*;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Alien2 extends Enemy {

    private static final int ANIMATION_INTERVAL = 10; // ticks per pose - a quick engine-flare flicker

    private final int baseY;
    private int tick;

    public Alien2(int x, int y) {
        super(x, y);
        this.baseY = y;
        // Loaded synchronously to avoid the macOS getScaledInstance crash.
        // Scaled-to-fit (rather than loadScaled's integer factor) so this
        // differently-sized sprite-sheet crop still matches the
        // STAGE2_ALIEN_WIDTH x STAGE2_ALIEN_HEIGHT footprint Scene2's
        // collision math uses.
        //
        // Only one clean crop of this ship exists, so the second "engine
        // flare" frame is a tinted copy of the same art (see
        // ImageUtil.tint) - a real two-frame animation via the shared
        // Sprite animation system, not a static image.
        BufferedImage base = gdd.ImageUtil.loadScaledToFit(IMG_ENEMY2_SHIP, STAGE2_ALIEN_WIDTH, STAGE2_ALIEN_HEIGHT);
        BufferedImage flare = gdd.ImageUtil.tint(base, new Color(255, 220, 90), 0.4f);
        setAnimationFrames(new Image[]{base, flare}, ANIMATION_INTERVAL);
    }

    @Override
    public void act(int direction) {
        // Faster than Alien1, and weaves up/down in a sine wave as it
        // flies left - a distinct movement pattern, not just a reskin.
        this.x -= 2;
        tick++;
        this.y = baseY + (int) (Math.sin(tick * 0.1) * 20);
    }
}
