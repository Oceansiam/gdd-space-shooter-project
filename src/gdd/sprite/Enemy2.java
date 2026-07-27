package gdd.sprite;

public class Enemy2 extends Enemy {

    public Enemy2(int x, int y) {
        super(x, y);
    }

    public void act(int direction) {
        // TODO: give this a movement pattern different from Alien1's
        // straight-line "this.x--;" - e.g. a sine-wave bob on y, a faster
        // approach speed, moving in bursts, etc.
        this.x--;
    }
}
