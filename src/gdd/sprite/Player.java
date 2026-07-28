package gdd.sprite;

import static gdd.Global.*;
import gdd.ImageUtil;
import java.awt.event.KeyEvent;

public class Player extends Sprite {

    // Player now sits on the left edge and moves up/down.
    private static final int START_X = 40;
    private static final int INVULNERABLE_DURATION = 90; // ~1.5s at 60fps of post-hit invincibility, used by Scene2

    private int currentSpeed = PLAYER_BASE_SPEED;
    private int speedLevel = 0;
    private int shotLevel = 0;
    private int lives = PLAYER_MAX_LIVES; // Scene2 (Stage 2) lives; Scene1 tracks its own separately
    private int invulnerableFrames = 0;

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        // Rotates by whatever PLAYER_IMAGE_ROTATION is set to in Global.java,
        // so swapping IMG_PLAYER_FRAMES for different sprites is a
        // one-constant change instead of a code change. An engine-thruster
        // flicker animation (via the shared Sprite animation system) rather
        // than one static pose. Loaded synchronously to avoid the macOS
        // getScaledInstance crash.
        var frames = new java.awt.Image[IMG_PLAYER_FRAMES.length];
        for (int i = 0; i < IMG_PLAYER_FRAMES.length; i++) {
            frames[i] = ImageUtil.loadRotatedScaled(IMG_PLAYER_FRAMES[i], SCALE_FACTOR, PLAYER_IMAGE_ROTATION);
        }
        setAnimationFrames(frames, PLAYER_ANIM_SPEED);

        resetPosition();
    }

    /** Moves the player back to its starting spot - used both at game start
     * and to respawn after losing a life (with lives remaining). */
    public void resetPosition() {
        setX(START_X);
        setY(BOARD_HEIGHT / 2 - getImage().getHeight(null) / 2);
        dx = 0;
        dy = 0;
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int getSpeedLevel() {
        return speedLevel;
    }

    /** Speed Up power-up: 2 stages, each adding SPEED_UP_STEP to the base speed. */
    public void upgradeSpeed() {
        if (speedLevel < SPEED_UP_MAX_LEVEL) {
            speedLevel++;
            currentSpeed = PLAYER_BASE_SPEED + speedLevel * SPEED_UP_STEP;
        }
    }

    public int getShotLevel() {
        return shotLevel;
    }

    /** Multi-shot power-up: 4 stages, each adding one more simultaneous bullet. */
    public void upgradeShot() {
        if (shotLevel < SHOT_UP_MAX_LEVEL) {
            shotLevel++;
        }
    }

    public int getLives() {
        return lives;
    }

    /** One hit removes one heart; never drops below 0. Used by Scene2. */
    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    /** Heart Up power-up: adds one heart, capped at PLAYER_MAX_LIVES. */
    public void addLife() {
        if (lives < PLAYER_MAX_LIVES) {
            lives++;
        }
    }

    public boolean isInvulnerable() {
        return invulnerableFrames > 0;
    }

    /** Brief post-hit invincibility so one collision can't drain every heart at once. */
    public void startInvulnerability() {
        invulnerableFrames = INVULNERABLE_DURATION;
    }

    public void act() {
        if (invulnerableFrames > 0) {
            invulnerableFrames--;
        }

        advanceAnimation();

        x += dx;
        y += dy;

        int width = getImage() != null ? getImage().getWidth(null) : 0;
        int height = getImage() != null ? getImage().getHeight(null) : 0;

        if (x <= 2) {
            x = 2;
        }

        if (x >= BOARD_WIDTH - width) {
            x = BOARD_WIDTH - width;
        }

        if (y <= 2) {
            y = 2;
        }

        if (y >= BOARD_HEIGHT - height) {
            y = BOARD_HEIGHT - height;
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP) {
            dy = -currentSpeed;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = currentSpeed;
        }

        if (key == KeyEvent.VK_LEFT) {
            dx = -currentSpeed;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = currentSpeed;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP) {
            dy = 0;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = 0;
        }

        if (key == KeyEvent.VK_LEFT) {
            dx = 0;
        }

        if (key == KeyEvent.VK_RIGHT) {
            dx = 0;
        }
    }
}