package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;

/** Stage 2's second enemy type: a 2-frame "power pulse" animated ship. */
public class Alien3 extends Enemy {

    private static final int ANIMATION_INTERVAL = 15; // ticks per pose (~4 flips/sec at 60fps)

    public Alien3(int x, int y) {
        super(x, y);
        // Loaded synchronously to avoid the macOS getScaledInstance crash.
        // Scaled-to-fit so this wide sprite-sheet crop still matches the
        // ALIEN_WIDTH x ALIEN_HEIGHT footprint Scene2's collision math uses.
        //
        // setAnimationFrames() replaces the metroid animation super(x, y)
        // just set up, so Scene2's per-frame advanceAnimation() call cycles
        // through these two frames instead of flickering back to the wrong
        // sprite.
        Image frame1 = gdd.ImageUtil.loadScaledToFit(IMG_ALIEN3_FRAME1, STAGE2_ALIEN_WIDTH, STAGE2_ALIEN_HEIGHT);
        Image frame2 = gdd.ImageUtil.loadScaledToFit(IMG_ALIEN3_FRAME2, STAGE2_ALIEN_WIDTH, STAGE2_ALIEN_HEIGHT);
        setAnimationFrames(new Image[]{frame1, frame2}, ANIMATION_INTERVAL);
    }

    @Override
    public void act(int direction) {
        // A bit faster than Alien1 - the animation (played via
        // advanceAnimation(), called by Scene2 for every enemy) is the main
        // distinguishing feature, movement stays a simple straight line.
        this.x -= 2;
    }
}
