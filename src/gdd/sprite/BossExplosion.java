package gdd.sprite;

import static gdd.Global.*;
import java.awt.Image;

/**
 * A bigger, multi-frame explosion used for the boss's death instead of the
 * plain single-image Explosion grunt enemies use. Overrides
 * visibleCountDown() (called every frame by the scene's existing explosion
 * drawing loop) to step through frames instead of a plain numeric
 * countdown, so it can be dropped into the same List<Explosion> with no
 * other changes needed anywhere else.
 */
public class BossExplosion extends Explosion {

    private static final int FRAME_HOLD = 4; // ticks per frame (~15fps at the 60fps game loop)

    private final Image[] frames;
    private int tick = 0;
    private int frameIndex = 0;

    public BossExplosion(int x, int y) {
        super(x, y);

        frames = new Image[BOSS_EXPLOSION_FRAME_COUNT];
        for (int i = 0; i < BOSS_EXPLOSION_FRAME_COUNT; i++) {
            String path = IMG_BOSS_EXPLOSION_PREFIX + String.format("%02d", i + 1) + ".png";
            // Loaded synchronously to avoid the macOS getScaledInstance crash.
            frames[i] = gdd.ImageUtil.loadScaledToFit(path, 160, 220);
        }
        setImage(frames[0]);
    }

    @Override
    public void visibleCountDown() {
        tick++;
        if (tick % FRAME_HOLD != 0) {
            return;
        }

        frameIndex++;
        if (frameIndex >= frames.length) {
            die();
        } else {
            setImage(frames[frameIndex]);
        }
    }
}
