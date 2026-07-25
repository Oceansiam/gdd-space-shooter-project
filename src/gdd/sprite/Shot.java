package gdd.sprite;

import gdd.AudioPlayer;
import static gdd.Global.*;
import gdd.ImageUtil;

public class Shot extends Sprite {

    // Player sprite (rotated + scaled) is 30 wide x 45 tall.
    // Spawn the shot at the player's right edge, vertically centered.
    private static final int H_SPACE = 30;
    private static final int V_SPACE = 21;
    AudioPlayer audioPlayer;

    public Shot() {
    }

    public Shot(int x, int y) {

        initShot(x, y);
        try {
            audioPlayer = new AudioPlayer("src/audio/shot.wav", false);
            audioPlayer.play();
        } 
        catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
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