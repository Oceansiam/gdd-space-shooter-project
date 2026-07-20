package gdd.sprite;

import static gdd.Global.*;


public class Explosion extends Sprite {


    public Explosion(int x, int y) {

        initExplosion(x, y);
    }

    private void initExplosion(int x, int y) {

        this.x = x;
        this.y = y;

        // Loaded synchronously to avoid the macOS getScaledInstance crash.
        setImage(gdd.ImageUtil.loadScaled(IMG_EXPLOSION, SCALE_FACTOR));
    }

    public void act(int direction) {

        // this.x += direction;
    }


}
