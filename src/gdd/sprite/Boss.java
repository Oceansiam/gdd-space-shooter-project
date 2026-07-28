package gdd.sprite;

import static gdd.Global.*;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Final-stage boss. Unlike regular Enemy sprites (which die in one hit),
 * the boss soaks up BOSS_MAX_HP shots before it goes down - Scene2 calls
 * takeDamage() directly instead of killing it on the first collision.
 */
public class Boss extends Sprite {

    private final int maxHp;
    private int hp;
    private final int holdX;
    private int tick = 0;

    public Boss(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.maxHp = BOSS_MAX_HP;
        this.hp = BOSS_MAX_HP;
        this.holdX = BOARD_WIDTH - 260;

        // Loaded synchronously to avoid the macOS getScaledInstance crash.
        // Only one clean boss ship crop exists, so the second "idle power
        // pulse" frame is a tinted copy of the same art rather than a
        // second sourced image - still a real two-frame animation via the
        // shared Sprite animation system.
        BufferedImage base = gdd.ImageUtil.loadScaledToFit(IMG_BOSS_MOTHER_HAWK, 260, 200);
        BufferedImage pulse = gdd.ImageUtil.tint(base, new Color(255, 90, 60), 0.35f);
        setAnimationFrames(new Image[]{base, pulse}, BOSS_ANIM_SPEED);
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    public boolean isDefeated() {
        return hp <= 0;
    }

    public void act() {
        advanceAnimation();

        // Flies in from the right, then holds position and hovers with a
        // gentle bob once it reaches its fighting spot.
        if (x > holdX) {
            x -= 3;
        } else {
            tick++;
            y += (int) Math.round(Math.sin(tick * 0.05) * 2);
        }
    }
}
