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

    // How fast a fired bomb travels (px/frame). Must be faster than the
    // enemy's own movement speed (1px/frame) or it never visibly separates
    // from the ship that fired it.
    public static final int BOMB_SPEED = 10;

    public static final int ALIEN_HEIGHT = 16 * SCALE_FACTOR; // matches scaled sprite (48)
    public static final int ALIEN_WIDTH = 16 * SCALE_FACTOR; // matches scaled sprite (48)

    // Stage 2's own enemies (Alien2/Alien3) are a bit bigger than Stage 1's -
    // kept separate from ALIEN_WIDTH/ALIEN_HEIGHT above so Stage 1 (Enemy2)
    // isn't affected. Scene2's shot-vs-enemy hitbox math uses these too, so
    // the hit zone stays matched to the bigger sprite.
    public static final int STAGE2_ALIEN_WIDTH = 22 * SCALE_FACTOR; // 66
    public static final int STAGE2_ALIEN_HEIGHT = 22 * SCALE_FACTOR; // 66
    public static final int ALIEN_INIT_X = 300; // Doubled from 150
    public static final int ALIEN_INIT_Y = 10; // Doubled from 5
    public static final int ALIEN_GAP = 30; // Gap between aliens

    public static final int GO_DOWN = 30; // Doubled from 15
    public static final int NUMBER_OF_ALIENS_TO_DESTROY = 50; // Stage 1 -> Stage 2, and Stage 2 -> boss

    // Odds (1 in N) that any given visible enemy fires at the player on a
    // given frame. Higher = rarer shots. Used by Scene2 (Stage 2/boss).
    public static final int ENEMY_FIRE_CHANCE = 150;
    // Stage 2's spawn schedule is generated across this many frames (5 min
    // at 60fps) - the actual stage-clear trigger is still the kill count
    // above, this just bounds how far out the generator lays out content.
    public static final int STAGE_DURATION_FRAMES = 300 * 60;
    // Widens the player-shot vs enemy hitbox on all sides (Stage 2).
    public static final int SHOT_HIT_MARGIN = 10;
    public static final int STAGE2_ENEMY_SHOT_SPEED = 11; // faster than EnemyShot.SPEED (7) for Stage 1
    public static final int CHANCE = 5;
    public static final int DELAY = 17;
    public static final int PLAYER_WIDTH = 30; // Doubled from 15
    public static final int PLAYER_HEIGHT = 20; // Doubled from 10

    // Images
    public static final String IMG_ENEMY = "src/images/metroid_enemy.png";

    // Which way IMG_ENEMY's art already faces - same idea as
    // PLAYER_IMAGE_ROTATION below. This sprite is left-right symmetric
    // (a top-down dome view), so 90 rotates it into a tall/narrow shape
    // instead of trying to make it "face" a direction it doesn't have.
    //   0 = no rotation, 90 = clockwise, 180 = upside down, 270 = counter-clockwise
    public static final int ENEMY_IMAGE_ROTATION = 90;

    // Frame-cycling animation for the enemy ship (pulsing/breathing effect
    // from the original sprite sheet - doesn't change facing, just brings
    // it to life). Files are frame_00.png .. frame_19.png.
    public static final String IMG_ENEMY_ANIM_FOLDER = "src/images/metroid_enemy_anim/";
    public static final int ENEMY_ANIM_FRAME_COUNT = 20;
    public static final int ENEMY_ANIM_SPEED = 6; // game ticks per animation frame
    public static final int ENEMY_ANIM_SCALE = 2; // native frames are tiny (45x12) - enlarge a bit

    // Enemies cross the whole board slowly, so a short spawn interval alone
    // still stacks several up on screen at once. Cap how many can be alive
    // at the same time - a scheduled spawn is just skipped past this limit
    // (there's plenty more later in the schedule, so nothing is lost).
    public static final int MAX_CONCURRENT_ENEMIES = 6;
    // Stage 2 should read as noticeably busier than Stage 1.
    public static final int STAGE2_MAX_CONCURRENT_ENEMIES = 9;
    public static final String IMG_ENEMY2 = "src/images/Sharp_X68000___Nemesis__94__Gradius_2____01_115x124.png";
    public static final String IMG_ENEMY3 = "src/images/Sharp_X68000___Nemesis__94__Gradius_2____03_108x76.png";

    // Stage 1's Enemy2 gets its own distinct ship art instead of reusing
    // the shared metroid_enemy_anim look every other Enemy has.
    public static final String IMG_ENEMY2_SHIP = "src/images/alien2.png";

    // Stage 2's third enemy type (Alien3): a 2-frame "power pulse"
    // animation crop, same idea as Alien1's frames but a different design.
    public static final String IMG_ALIEN3_FRAME1 = "src/images/alien1_frame1.png";
    public static final String IMG_ALIEN3_FRAME2 = "src/images/alien1_frame2.png";
    public static final String IMG_BOSS = "src/images/boss_placeholder.png";
    public static final String IMG_PLAYER = "src/images/Sega_Genesis___Zero_Wing___Playable_Char_03_32x27.png";

    // Player ship engine-thruster flicker animation (same ZIG-01 sheet,
    // adjacent frames with a different flame puff), using the shared
    // Sprite.setAnimationFrames()/advanceAnimation() system.
    public static final String[] IMG_PLAYER_FRAMES = {
        "src/images/player_frame1.png",
        "src/images/player_frame2.png",
        "src/images/player_frame3.png"
    };
    public static final int PLAYER_ANIM_SPEED = 8; // game ticks per animation frame

    // Boss idle "power pulse" animation: the real art frame plus a
    // programmatically tinted variant (see ImageUtil.tint), since only one
    // clean boss ship crop exists.
    public static final int BOSS_ANIM_SPEED = 20; // game ticks per animation frame

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
    public static final String IMG_POWERUP_MULTISHOT = "src/images/powerup-multishot.png";
    public static final String IMG_HEART = "src/images/heart.png";

    public static final int MAX_LIVES = 5;
    // Stage 2 (Scene2) keeps its own player-lives cap and heart-icon/boss
    // art, separate from Stage 1's MAX_LIVES/IMG_HEART above.
    public static final int PLAYER_MAX_LIVES = 3;
    public static final String IMG_BACKGROUND_STAGE2 = "src/images/background2.png";
    public static final String IMG_BOSS_MOTHER_HAWK = "src/images/boss_mother_hawk_ship.png";
    public static final int BOSS_MAX_HP = 60; // final boss - a multi-hit "bullet sponge" fight
    public static final String IMG_BOSS_EXPLOSION_PREFIX = "src/images/boss_explosion_";
    public static final int BOSS_EXPLOSION_FRAME_COUNT = 16;
    // Brief grace period after respawning where the player can't be hit
    // again immediately (classic "just got hit" invulnerability window).
    public static final int RESPAWN_INVULNERABLE_FRAMES = 90; // ~1.5s at 60fps
    public static final String IMG_BACKGROUND = "src/images/planet_background_level1.png"; //stage 1
    public static final String IMG_BACKGROUND2 = "src/images/background2.png"; // stage 2
    public static final String IMG_BACKGROUND3 = "src/images/Nebula_background.png"; // stage 1 diff map
    public static final String IMG_BACKGROUND4 = "src/images/background3.png"; // stage 2 dif map

    // Boss (Stage 2 finale). Art is a placeholder - swap IMG_BOSS (above,
    // near IMG_ENEMY2/IMG_ENEMY3) for something else whenever you're ready,
    // and adjust BOSS_IMAGE_ROTATION to match (0/90/180/270, same convention
    // as PLAYER_IMAGE_ROTATION and ENEMY_IMAGE_ROTATION).
    public static final int BOSS_IMAGE_ROTATION = 0;
    public static final int BOSS_MAX_HEALTH = 15; // hits required to destroy it
    public static final int BOSS_STOP_X = 620; // where it stops entering and starts fighting
    public static final int BOSS_BOB_SPEED = 2; // vertical px/frame while fighting
    public static final int BOSS_BOB_TOP = 40;
    public static final int BOSS_BOB_BOTTOM = 450;

    // Power-up progression
    public static final int PLAYER_BASE_SPEED = 2;
    public static final int SPEED_UP_STEP = 2; // pixels/frame added per Speed Up stage
    public static final int SPEED_UP_MAX_LEVEL = 2; // 2 stages

    public static final int SHOT_UP_MAX_LEVEL = 4; // 4 stages - up to 5 simultaneous bullets
}