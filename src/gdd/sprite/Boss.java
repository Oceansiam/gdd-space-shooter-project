package gdd.sprite;

import static gdd.Global.*;
import gdd.ImageUtil;

/**
 * A boss enemy: survives multiple hits instead of dying in one, and has its
 * own simple "enter, then hold position and fight" movement pattern instead
 * of just flying straight through like a regular Enemy.
 *
 * This is a starter scaffold, not a finished fight - the TODOs below are
 * the obvious next things to customize (art, attack pattern, movement).
 */
public class Boss extends Enemy {

    private int health;
    private final int maxHealth;
    private boolean fighting; // false while still entering from the right

    public Boss(int x, int y) {
        super(x, y);

        this.maxHealth = BOSS_MAX_HEALTH;
        this.health = maxHealth;

        // Overrides the regular-enemy animated sprite that super(x, y) just
        // set up - a boss needs its own art. Currently a single static
        // image; if you want it animated like the regular enemies, give it
        // its own frame set with setAnimationFrames(...) instead.
        // TODO: swap IMG_BOSS in Global.java for real boss art whenever
        // you've picked something (there are a few good candidates already
        // sitting in src/images/cropped-sprites/ from earlier - e.g. the
        // Darius, Gradius, or Silius boss sheets).
        setImage(ImageUtil.loadRotatedScaled(IMG_BOSS, 1, BOSS_IMAGE_ROTATION));
    }

    private int bobDirection = 1;

    @Override
    public void act(int direction) {
        if (!fighting) {
            // Entering: fly left like a normal enemy until it reaches its
            // stopping point, then switch into "fighting" mode.
            this.x--;
            if (this.x <= BOSS_STOP_X) {
                this.x = BOSS_STOP_X;
                fighting = true;
            }
        } else {
            // TODO: this is deliberately simple (just bob up and down) -
            // a real boss fight probably wants phases, faster movement as
            // health drops, brief pauses to attack, etc.
            this.y += BOSS_BOB_SPEED * bobDirection;
            if (this.y <= BOSS_BOB_TOP) {
                this.y = BOSS_BOB_TOP;
                bobDirection = 1;
            } else if (this.y >= BOSS_BOB_BOTTOM) {
                this.y = BOSS_BOB_BOTTOM;
                bobDirection = -1;
            }
        }
    }

    public boolean isFighting() {
        return fighting;
    }

    /**
     * Register one hit. Returns true exactly once, the moment the boss's
     * health reaches zero - use that as the "boss actually destroyed" signal
     * instead of relying on a single collision the way regular enemies do.
     */
    public boolean hit() {
        if (health <= 0) {
            return false; // already dead, ignore further hits this frame
        }
        health--;
        return health <= 0;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
}
