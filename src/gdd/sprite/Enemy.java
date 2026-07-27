package gdd.sprite;

import static gdd.Global.*;
import gdd.ImageUtil;
import java.awt.Image;

public class Enemy extends Sprite {

    private Bomb bomb;

    // Shared across every Enemy instance - loaded from disk once, not once
    // per spawn, since every enemy plays the same animation.
    private static Image[] sharedAnimationFrames;

    private static Image[] loadAnimationFrames() {
        if (sharedAnimationFrames == null) {
            sharedAnimationFrames = new Image[ENEMY_ANIM_FRAME_COUNT];
            for (int i = 0; i < ENEMY_ANIM_FRAME_COUNT; i++) {
                String path = String.format("%sframe_%02d.png", IMG_ENEMY_ANIM_FOLDER, i);
                sharedAnimationFrames[i] = ImageUtil.loadRotatedScaled(path, 1, ENEMY_IMAGE_ROTATION);
            }
        }
        return sharedAnimationFrames;
    }

    public Enemy(int x, int y) {

        initEnemy(x, y);
    }

    private void initEnemy(int x, int y) {

        this.x = x;
        this.y = y;

        bomb = new Bomb(x, y);

        // Loaded synchronously to avoid the macOS getScaledInstance crash.
        setAnimationFrames(loadAnimationFrames(), ENEMY_ANIM_SPEED);
    }

    public void act(int direction) {

        this.x += direction;
    }

    public Bomb getBomb() {

        return bomb;
    }

    public class Bomb extends Sprite {

        private boolean destroyed;

        public Bomb(int x, int y) {

            initBomb(x, y);
        }

        private void initBomb(int x, int y) {

            setDestroyed(true);

            this.x = x;
            this.y = y;

            // Original art is a small vertical drop; rotated 90 degrees so
            // it flies left instead of falling down, same treatment every
            // other sprite got in the side-scroll conversion. Loaded
            // synchronously to avoid the macOS getScaledInstance crash.
            setImage(ImageUtil.loadRotatedScaled("src/images/bomb.png", SCALE_FACTOR, 90));
        }

        public void setDestroyed(boolean destroyed) {

            this.destroyed = destroyed;
        }

        public boolean isDestroyed() {

            return destroyed;
        }
    }
}