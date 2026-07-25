package gdd;

public class Global {
    private Global() {
        // Prevent instantiation
    }

    public static final int SCALE_FACTOR = 3; // Scaling factor for sprites

    // Board is now landscape (wider than tall) for horizontal side-scrolling.
    public static final int BOARD_WIDTH = 900;
    public static final int BOARD_HEIGHT = 650;
    public static final int BORDER_RIGHT = 60; // Doubled from 30
    public static final int BORDER_LEFT = 10; // Doubled from 5

    public static final int GROUND = 580; // Doubled from 290
    public static final int BOMB_HEIGHT = 10; // Doubled from 5

    public static final int ALIEN_HEIGHT = 12 * SCALE_FACTOR; // matches scaled sprite (36)
    public static final int ALIEN_WIDTH = 12 * SCALE_FACTOR; // matches scaled sprite (36)
    public static final int ALIEN_INIT_X = 300; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
    public static final int NUMBER_OF_ALIENS_TO_DESTROY = 24;

    // Odds (1 in N) that any given visible enemy fires at the player on a
    // given frame. Higher = rarer shots.
    public static final int ENEMY_FIRE_CHANCE = 150;
    public static final int CHANCE = 5;
    public static final int DELAY = 17;
    public static final int PLAYER_WIDTH = 30; // Doubled from 15
    public static final int PLAYER_HEIGHT = 20; // Doubled from 10

    // Images
    public static final String IMG_ENEMY = "src/images/alien.png";
    public static final String IMG_PLAYER = "src/images/Sega_Genesis___Zero_Wing___Playable_Char_03_32x27.png";

    // Which way IMG_PLAYER's art already faces, so it can be turned to face
    // right (the direction the ship travels). Change this whenever you swap
    // IMG_PLAYER for a different sprite:
    //   0   = art already faces right (no rotation) - e.g. this ZIG-01 pose
    //   90  = art faces up                          - e.g. the original player.png
    //   180 = art faces left
    //   270 = art faces down
    public static final int PLAYER_IMAGE_ROTATION = 0;
    public static final String IMG_SHOT = "src/images/Sega_Genesis___Zero_Wing___Playable_Shot_23_20x16.png";
    public static final String IMG_EXPLOSION = "src/images/explosion.png";
    public static final String IMG_TITLE = "src/images/title.png";
    public static final String IMG_POWERUP_SPEEDUP = "src/images/powerup-s.png";
    public static final String IMG_BACKGROUND = "src/images/planet_background_level1.png";
}