package gdd;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Image loading helpers.
 *
 * The old code used ImageIcon + Image.getScaledInstance(SCALE_SMOOTH),
 * which loads images asynchronously and hits a known JDK bug
 * (ClassCastException: [I cannot be cast to [B) on some platforms,
 * macOS in particular. Loading synchronously through ImageIO into a
 * BufferedImage and scaling with Graphics2D avoids that entirely, and
 * nearest-neighbor scaling keeps the pixel art crisp.
 */
public final class ImageUtil {

    private ImageUtil() {
        // Prevent instantiation
    }

    /** Load an image file synchronously as a BufferedImage. */
    public static BufferedImage load(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            System.err.println("Failed to load image: " + path + " (" + e.getMessage() + ")");
            // Return a small magenta placeholder so the game keeps running
            BufferedImage placeholder = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = placeholder.createGraphics();
            g.setColor(java.awt.Color.MAGENTA);
            g.fillRect(0, 0, 12, 12);
            g.dispose();
            return placeholder;
        }
    }

    /** Scale an image by an integer factor using crisp nearest-neighbor sampling. */
    public static BufferedImage scale(BufferedImage src, int factor) {
        int w = src.getWidth() * factor;
        int h = src.getHeight() * factor;
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src, 0, 0, w, h, null);
        g.dispose();
        return out;
    }

    /** Rotate an image 90 degrees clockwise (e.g. up-facing art becomes right-facing). */
    public static BufferedImage rotateClockwise(BufferedImage src) {
        return rotate(src, 90);
    }

    /**
     * Rotate an image by a multiple of 90 degrees: 0 (none), 90 (clockwise),
     * 180, or 270 (= 90 counter-clockwise). Use this when swapping in a new
     * sprite: check which way the source art is already facing and pick
     * whichever angle turns it to face right (the direction ships/bullets
     * travel in this game).
     *   - art already faces right  -> 0
     *   - art faces up             -> 90
     *   - art faces left           -> 180
     *   - art faces down           -> 270
     */
    public static BufferedImage rotate(BufferedImage src, int degrees) {
        degrees = ((degrees % 360) + 360) % 360; // normalize to 0-270

        if (degrees == 0) {
            return src;
        }

        int w = src.getWidth();
        int h = src.getHeight();
        boolean swapDimensions = (degrees == 90 || degrees == 270);
        BufferedImage out = new BufferedImage(
                swapDimensions ? h : w,
                swapDimensions ? w : h,
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = out.createGraphics();
        switch (degrees) {
            case 90:
                g.translate(h, 0);
                break;
            case 180:
                g.translate(w, h);
                break;
            case 270:
                g.translate(0, w);
                break;
        }
        g.rotate(Math.toRadians(degrees));
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }

    /** Convenience: load, rotate clockwise, then scale. */
    public static BufferedImage loadRotatedScaled(String path, int factor) {
        return scale(rotateClockwise(load(path)), factor);
    }

    /** Convenience: load, rotate by the given angle (0/90/180/270), then scale. */
    public static BufferedImage loadRotatedScaled(String path, int factor, int degrees) {
        return scale(rotate(load(path), degrees), factor);
    }

    /** Convenience: load then scale. */
    public static BufferedImage loadScaled(String path, int factor) {
        return scale(load(path), factor);
    }
}
