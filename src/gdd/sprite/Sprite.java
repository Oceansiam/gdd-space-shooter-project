package gdd.sprite;

import java.awt.Image;

abstract public class Sprite {

    protected boolean visible;
    protected Image image;
    protected boolean dying;
    protected int visibleFrames = 10;

    protected int x;
    protected int y;
    protected int dx;
    protected int dy;

    // Optional frame-cycling animation. A sprite that never calls
    // setAnimationFrames() behaves exactly as before (a single static
    // image) - this is purely additive.
    private Image[] animationFrames;
    private int currentFrameIndex;
    private int frameTickCounter;
    private int ticksPerFrame;

    public Sprite() {
        visible = true;
    }

    // Default no-op: not every sprite needs autonomous per-frame movement
    // (some, like Shot and Explosion, are driven directly by Scene1's update()).
    public void act() {
    }

    /**
     * Configure this sprite to cycle through a sequence of frames instead of
     * showing one static image. ticksPerFrame controls playback speed - how
     * many calls to advanceAnimation() happen before moving to the next
     * frame (higher = slower). Call advanceAnimation() once per game tick
     * (e.g. from Scene1's update loop) to actually play it.
     */
    public void setAnimationFrames(Image[] frames, int ticksPerFrame) {
        this.animationFrames = frames;
        this.ticksPerFrame = Math.max(1, ticksPerFrame);
        this.currentFrameIndex = 0;
        this.frameTickCounter = 0;
        if (frames != null && frames.length > 0) {
            this.image = frames[0];
        }
    }

    /** Advances the animation by one game tick. No-op if not animated. */
    public void advanceAnimation() {
        if (animationFrames == null || animationFrames.length < 2) {
            return;
        }
        frameTickCounter++;
        if (frameTickCounter >= ticksPerFrame) {
            frameTickCounter = 0;
            currentFrameIndex = (currentFrameIndex + 1) % animationFrames.length;
            this.image = animationFrames[currentFrameIndex];
        }
    }

    public boolean collidesWith(Sprite other) {
        if (other == null || !this.isVisible() || !other.isVisible()) {
            return false;
        }
        return this.getX() < other.getX() + other.getImage().getWidth(null)
                && this.getX() + this.getImage().getWidth(null) > other.getX()
                && this.getY() < other.getY() + other.getImage().getHeight(null)
                && this.getY() + this.getImage().getHeight(null) > other.getY();
    }

    public void die() {
        visible = false;
    }

    public boolean isVisible() {
        return visible;
    }

    public void visibleCountDown() {
        if (visibleFrames > 0) {
            visibleFrames--;
        } else {
            visible = false;
        }
    }

    protected void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setDying(boolean dying) {
        this.dying = dying;
    }

    public boolean isDying() {
        return this.dying;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }
}