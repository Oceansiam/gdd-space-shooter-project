package gdd.sprite;

import gdd.AudioPlayer;
import static gdd.Global.*;
import gdd.ImageUtil;

public class Shot extends Sprite {

    // Player sprite (rotated + scaled) is 30 wide x 45 tall.
    // Spawn the shot at the player's right edge, vertically centered.
    private static final int H_SPACE = 30;
    private static final int V_SPACE = 21;
    volatile AudioPlayer audioPlayer;

    public Shot() {
    }

    public Shot(int x, int y) {
        this(x, y, 0);
    }

    /** dy: vertical drift per frame, used to fan multi-shot bullets out. */
    public Shot(int x, int y, int dy) {

        initShot(x, y);
        setDy(dy);
        // Off-thread: opening a new audio Clip is blocking I/O, and this
        // constructor can run many times per second (rapid-fire, multi-shot
        // firing several bullets per keypress). Doing it on the EDT risks
        // freezing the whole window if the audio subsystem stalls or runs
        // low on available lines; off-thread, a slow/failing sound just
        // means a missed "pew" instead of a frozen game.
        new Thread(() -> {
            try {
                AudioPlayer player = new AudioPlayer("src/audio/shot.wav", false);
                audioPlayer = player;
                player.play();
            } catch (Exception ex) {
                System.out.println("Error with playing sound.");
                ex.printStackTrace();
            }
        }).start();
    }

    private void initShot(int x, int y) {

        // Original art is a vertical beam; rotate clockwise so it is
        // horizontal, then scale. Loaded synchronously to avoid the
        // macOS getScaledInstance crash.
        setImage(ImageUtil.loadRotatedScaled(IMG_SHOT, SCALE_FACTOR));

        setX(x + H_SPACE);
        setY(y + V_SPACE);
    }
}