package gdd.sprite;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * A projectile fired by an enemy, traveling left toward the player.
 *
 * Drawn procedurally (a small red bolt) instead of loaded from an image
 * file, so it doesn't depend on any art asset and is instantly visually
 * distinct from the player's own shots.
 */
public class EnemyShot extends Sprite {

    private static final int WIDTH = 18;
    private static final int HEIGHT = 6;
    public static final int SPEED = 10;

    public EnemyShot(int x, int y) {
        setImage(createBoltImage());
        setX(x);
        setY(y - HEIGHT / 2);
    }

    private static BufferedImage createBoltImage() {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(180, 20, 20));
        g.fillRoundRect(0, 0, WIDTH, HEIGHT, HEIGHT, HEIGHT);

        g.setColor(new Color(255, 210, 80));
        g.fillRoundRect(2, HEIGHT / 2 - 1, WIDTH - 4, 2, 2, 2);

        g.dispose();
        return img;
    }

    public void act() {
        x -= SPEED;
        if (x < -WIDTH) {
            die();
        }
    }
}
