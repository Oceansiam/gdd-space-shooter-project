package gdd.sprite;

import static gdd.Global.*;
import gdd.ImageUtil;
import java.awt.event.KeyEvent;

public class Player extends Sprite {

    // Player now sits on the left edge and moves up/down.
    private static final int START_X = 40;
    private int currentSpeed = 2;

    public Player() {
        initPlayer();
    }

    private void initPlayer() {
        // Original art points up; rotate clockwise so it faces right,
        // then scale. Loaded synchronously to avoid the macOS
        // getScaledInstance crash.
        var img = ImageUtil.loadRotatedScaled(IMG_PLAYER, SCALE_FACTOR);
        setImage(img);

        setX(START_X);
        setY(BOARD_HEIGHT / 2 - img.getHeight() / 2);
    }

    public int getSpeed() {
        return currentSpeed;
    }

    public int setSpeed(int speed) {
        if (speed < 1) {
            speed = 1; // Ensure speed is at least 1
        }
        this.currentSpeed = speed;
        return currentSpeed;
    }

    public void act() {
        y += dy;

        int height = getImage() != null ? getImage().getHeight(null) : 0;

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
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_UP) {
            dy = 0;
        }

        if (key == KeyEvent.VK_DOWN) {
            dy = 0;
        }
    }
}
