package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.HeartUp;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien2;
import gdd.sprite.Alien3;
import gdd.sprite.Boss;
import gdd.sprite.BossExplosion;
import gdd.sprite.Enemy;
import gdd.sprite.EnemyShot;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Stage 2: same mechanics as Scene1 (this game's Stage 1), but a distinct
 * background/pacing so it reads as a genuinely later stage, and it receives
 * the Player carried over from Scene1 (lives/power-up levels intact) instead
 * of starting fresh. TODO: the last stage needs a boss fight - that goes
 * where the ENEMIES_TO_CLEAR_STAGE check below currently just ends the game.
 */
public class Scene2 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private List<EnemyShot> enemyShots;
    private Player player;
    private Player carriedOverPlayer;
    private BufferedImage backgroundImage;
    private BufferedImage heartFullIcon;
    private BufferedImage heartEmptyIcon;

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    private int direction = -1;
    private int deaths = 0;
    private int score = 0;
    private static final int SCORE_PER_KILL = 100;
    private static final int MAX_ACTIVE_SHOTS = 20;
    private static final int ENEMIES_TO_CLEAR_STAGE = 60; // kills before the boss shows up
    private static final int BOSS_SCORE_BONUS = 5000;
    private static final int BOSS_FIRE_CHANCE = 45; // rarer = less frequent; boss fires more than grunts
    private static final int BOSS_DEATH_DELAY = 70; // frames to let the death explosions play before "Game won!"

    private Boss boss;
    private int bossDeathTimer = -1;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Rectangle playAgainButton = new Rectangle(
            BOARD_WIDTH / 2 - 110, BOARD_HEIGHT - 110, 220, 56);
    private boolean hoveringPlayAgain = false;

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    private final int[][] MAP = {
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}
    };

    private HashMap<Integer, SpawnDetails> spawnMap = new HashMap<>();
    private AudioPlayer audioPlayer;
    private AudioPlayer gameOverAudioPlayer;

    public Scene2(Game game) {
        this.game = game;
        loadSpawnDetails();
    }

    /** Set by Game.loadStage2() right before start(), so gameInit() picks it up. */
    public void setCarriedOverPlayer(Player player) {
        this.carriedOverPlayer = player;
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/4 - Burning Heat [Stage 1].wav";
            audioPlayer = new AudioPlayer(filePath, true);
            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error initializing audio player: " + e.getMessage());
        }
    }

    // Enemies are never actually removed from `enemies` once created (dead
    // ones just go invisible and sit there forever), so the spawn-cap check
    // below counts only the still-alive ones instead of enemies.size().
    private int countAliveEnemies() {
        int count = 0;
        for (Enemy e : enemies) {
            if (e.isVisible()) {
                count++;
            }
        }
        return count;
    }

    private void loadSpawnDetails() {
        // Same procedural approach as Scene1, but a tighter enemy cadence -
        // this stage should feel a notch harder than stage 1.
        int enemyFrame = 60;
        String[] enemyTypes = {"Alien2", "Alien3"};
        int enemyTypeIndex = 0;
        while (enemyFrame < STAGE_DURATION_FRAMES - 120) {
            int y = 60 + randomizer.nextInt(BOARD_HEIGHT - 120);
            spawnMap.put(enemyFrame, new SpawnDetails(enemyTypes[enemyTypeIndex % enemyTypes.length], BOARD_WIDTH, y));
            enemyTypeIndex++;
            enemyFrame += 30 + randomizer.nextInt(30); // next enemy in ~0.5-1s - denser than Stage 1
        }

        int powerUpFrame = 150;
        // SpeedUp goes first; HeartUp gets one extra slot in the cycle so
        // it shows up a bit more often than MultiShot/SpeedUp.
        String[] powerUpTypes = {"PowerUp-SpeedUp", "PowerUp-MultiShot", "PowerUp-HeartUp"};
        int powerUpTypeIndex = 0;
        while (powerUpFrame < STAGE_DURATION_FRAMES - 120) {
            while (spawnMap.containsKey(powerUpFrame)) {
                powerUpFrame++;
            }
            int y = 80 + randomizer.nextInt(BOARD_HEIGHT - 160);
            String type = powerUpTypes[powerUpTypeIndex % powerUpTypes.length];
            spawnMap.put(powerUpFrame, new SpawnDetails(type, BOARD_WIDTH, y));
            powerUpTypeIndex++;
            powerUpFrame += 1000 + randomizer.nextInt(600); // less frequent than before (~16.7-26.7s)
        }
    }

    public void start() {
        addKeyListener(new TAdapter());
        addMouseListener(new MAdapter());
        addMouseMotionListener(new MMAdapter());
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        gameInit();
        initAudio();
    }

    public void stop() {
        timer.stop();
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void gameInit() {

        enemies = new ArrayList<>();
        powerups = new ArrayList<>();
        explosions = new ArrayList<>();
        shots = new ArrayList<>();
        enemyShots = new ArrayList<>();

        // Loaded synchronously to avoid the macOS getScaledInstance crash.
        backgroundImage = gdd.ImageUtil.load(IMG_BACKGROUND_STAGE2);
        heartFullIcon = gdd.ImageUtil.createHeartIcon(18, new Color(230, 40, 60));
        heartEmptyIcon = gdd.ImageUtil.createHeartIcon(18, new Color(70, 70, 74));

        if (carriedOverPlayer != null) {
            // Keep lives/speed level/shot level from stage 1; just re-place
            // the ship at this stage's start position.
            player = carriedOverPlayer;
            player.resetPosition();
            carriedOverPlayer = null; // consume once - a later restart here starts fresh
        } else {
            player = new Player();
        }
    }

    private void drawBackground(Graphics g) {
        if (backgroundImage == null) {
            return;
        }

        int imgW = backgroundImage.getWidth();
        int imgH = backgroundImage.getHeight();
        int scrollX = frame / 4;

        int startTile = Math.floorDiv(scrollX, imgW) - 1;
        int endTile = Math.floorDiv(scrollX + BOARD_WIDTH, imgW) + 1;

        for (int tile = startTile; tile <= endTile; tile++) {
            int x = tile * imgW - scrollX;
            boolean flipped = Math.floorMod(tile, 2) != 0;

            if (flipped) {
                g.drawImage(backgroundImage, x + imgW, 0, x, imgH, 0, 0, imgW, imgH, null);
            } else {
                g.drawImage(backgroundImage, x, 0, x + imgW, imgH, 0, 0, imgW, imgH, null);
            }
        }
    }

    private void drawMap(Graphics g) {
        int scrollOffset = (frame) % BLOCKWIDTH;
        int baseCol = (frame) / BLOCKWIDTH;
        int colsNeeded = (BOARD_WIDTH / BLOCKWIDTH) + 2;

        for (int screenCol = 0; screenCol < colsNeeded; screenCol++) {
            int mapCol = (baseCol + screenCol) % MAP.length;
            int x = BOARD_WIDTH - ((screenCol * BLOCKWIDTH) - scrollOffset);

            if (x > BOARD_WIDTH || x < -BLOCKWIDTH) {
                continue;
            }

            for (int row = 0; row < MAP[mapCol].length; row++) {
                if (MAP[mapCol][row] == 1) {
                    int y = row * BLOCKHEIGHT;
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }
    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        g.fillOval(centerX - 20, centerY + 5, 1, 1);
        g.fillOval(centerX + 18, centerY - 15, 1, 1);
        g.fillOval(centerX - 5, centerY - 18, 1, 1);
        g.fillOval(centerX + 8, centerY + 20, 1, 1);
    }

    private void drawAliens(Graphics g) {
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                g.drawImage(enemy.getImage(), enemy.getX(), enemy.getY(), this);
            }
            if (enemy.isDying()) {
                enemy.die();
            }
        }
    }

    private void drawPowreUps(Graphics g) {
        for (PowerUp p : powerups) {
            if (p.isVisible()) {
                g.drawImage(p.getImage(), p.getX(), p.getY(), this);
            }
            if (p.isDying()) {
                p.die();
            }
        }
    }

    private void killPlayer() {
        if (player.isDying() || !player.isVisible()) {
            return;
        }
        player.setImage(gdd.ImageUtil.loadScaled(IMG_EXPLOSION, SCALE_FACTOR));
        player.setDying(true);
        message = "Game Over";

        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
            gameOverAudioPlayer = new AudioPlayer("src/audio/Game Over.wav", false);
            gameOverAudioPlayer.play();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    private void hitPlayer() {
        if (player.isDying() || !player.isVisible() || player.isInvulnerable()) {
            return;
        }

        player.loseLife();

        if (player.getLives() <= 0) {
            killPlayer();
            return;
        }

        player.startInvulnerability();
        try {
            new AudioPlayer("src/audio/shot.wav", false).play();
        } catch (Exception ex) {
            System.err.println("Error playing hit sound.");
        }
    }

    private void drawPlayer(Graphics g) {
        boolean shouldDraw = player.isVisible() && (!player.isInvulnerable() || (frame / 4) % 2 == 0);

        if (shouldDraw) {
            g.drawImage(player.getImage(), player.getX(), player.getY(), this);
        }

        if (player.isDying()) {
            player.die();
            inGame = false;
        }
    }

    private void drawShot(Graphics g) {
        for (Shot shot : shots) {
            if (shot.isVisible()) {
                g.drawImage(shot.getImage(), shot.getX(), shot.getY(), this);
            }
        }
    }

    private void drawEnemyShots(Graphics g) {
        for (EnemyShot enemyShot : enemyShots) {
            if (enemyShot.isVisible()) {
                g.drawImage(enemyShot.getImage(), enemyShot.getX(), enemyShot.getY(), this);
            }
        }
    }

    private void drawExplosions(Graphics g) {
        List<Explosion> toRemove = new ArrayList<>();

        for (Explosion explosion : explosions) {
            if (explosion.isVisible()) {
                g.drawImage(explosion.getImage(), explosion.getX(), explosion.getY(), this);
                explosion.visibleCountDown();
                if (!explosion.isVisible()) {
                    toRemove.add(explosion);
                }
            }
        }

        explosions.removeAll(toRemove);
    }

    private void drawBoss(Graphics g) {
        if (boss == null || !boss.isVisible()) {
            return;
        }

        g.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);

        // Health bar above the boss.
        int barWidth = 260;
        int barX = boss.getX();
        int barY = boss.getY() - 14;
        double ratio = boss.getHp() / (double) boss.getMaxHp();

        g.setColor(new Color(40, 0, 0));
        g.fillRect(barX, barY, barWidth, 8);
        g.setColor(new Color(220, 40, 40));
        g.fillRect(barX, barY, (int) (barWidth * ratio), 8);
        g.setColor(Color.white);
        g.drawRect(barX, barY, barWidth, 8);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        if (inGame) {
            drawBackground(g);
            drawMap(g);
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawBoss(g);
            drawPlayer(g);
            drawShot(g);
            drawEnemyShots(g);
            drawHUD(g);
        } else {
            if (timer.isRunning()) {
                timer.stop();
            }
            gameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawHUD(Graphics g) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, BOARD_WIDTH, 50);

        var hudFont = new Font("Monospaced", Font.BOLD, 16);
        g.setFont(hudFont);
        var fontMetrics = g.getFontMetrics(hudFont);

        g.setColor(Color.white);
        String scoreText = String.format("SCORE: %06d", score);
        g.drawString(scoreText, 12, 20);

        // Kills toward the boss - capped display at ENEMIES_TO_CLEAR_STAGE
        // since deaths keeps counting (harmlessly) once the boss is out.
        int progress = Math.min(deaths, ENEMIES_TO_CLEAR_STAGE);
        String progressText = progress + "/" + ENEMIES_TO_CLEAR_STAGE;
        int progressWidth = fontMetrics.stringWidth(progressText);
        g.drawString(progressText, (BOARD_WIDTH - progressWidth) / 2, 20);

        String speedText = "SPEED: " + player.getSpeed();
        int speedWidth = fontMetrics.stringWidth(speedText);
        g.drawString(speedText, BOARD_WIDTH - speedWidth - 12, 20);

        drawLivesRow(g);

        String stageText = "STAGE 2";
        int stageWidth = fontMetrics.stringWidth(stageText);
        g.drawString(stageText, BOARD_WIDTH - stageWidth - 12, 42);
    }

    private void drawLivesRow(Graphics g) {
        int size = 18;
        int gap = 4;
        int x = 12;
        int y = 28;

        for (int i = 0; i < PLAYER_MAX_LIVES; i++) {
            BufferedImage icon = i < player.getLives() ? heartFullIcon : heartEmptyIcon;
            g.drawImage(icon, x + i * (size + gap), y, this);
        }
    }

    private void gameOver(Graphics g) {
        drawBackground(g);
        drawMap(g);

        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        boolean won = "Game won!".equals(message);
        String headline = won ? "YOU WIN!" : "GAME OVER";

        var headlineFont = new Font("SansSerif", Font.BOLD, 48);
        g.setFont(headlineFont);
        var headlineFm = this.getFontMetrics(headlineFont);
        int headlineX = (BOARD_WIDTH - headlineFm.stringWidth(headline)) / 2;
        int headlineY = BOARD_HEIGHT / 2 - 100;

        g.setColor(Color.black);
        g.drawString(headline, headlineX + 3, headlineY + 3);
        g.setColor(won ? new Color(120, 230, 140) : new Color(230, 60, 50));
        g.drawString(headline, headlineX, headlineY);

        var scoreFont = new Font("Monospaced", Font.BOLD, 22);
        g.setFont(scoreFont);
        var scoreFm = this.getFontMetrics(scoreFont);
        String scoreText = String.format("FINAL SCORE: %06d", score);
        g.setColor(Color.white);
        g.drawString(scoreText, (BOARD_WIDTH - scoreFm.stringWidth(scoreText)) / 2, headlineY + 46);

        drawPlayAgainButton(g);
    }

    private void drawPlayAgainButton(Graphics g) {
        boolean pulseOn = (frame % 40) < 20;
        Color fill = hoveringPlayAgain ? new Color(255, 140, 40) : new Color(20, 20, 24, 230);
        Color border = pulseOn ? new Color(255, 140, 40) : Color.white;

        var g2 = (Graphics2D) g;
        g2.setColor(fill);
        g2.fillRoundRect(playAgainButton.x, playAgainButton.y,
                playAgainButton.width, playAgainButton.height, 14, 14);

        g2.setStroke(new BasicStroke(3));
        g2.setColor(border);
        g2.drawRoundRect(playAgainButton.x, playAgainButton.y,
                playAgainButton.width, playAgainButton.height, 14, 14);

        var buttonFont = new Font("SansSerif", Font.BOLD, 22);
        g.setFont(buttonFont);
        var buttonFm = this.getFontMetrics(buttonFont);
        String text = "PLAY AGAIN";
        int textX = playAgainButton.x + (playAgainButton.width - buttonFm.stringWidth(text)) / 2;
        int textY = playAgainButton.y + (playAgainButton.height + buttonFm.getAscent()) / 2 - 4;
        g.setColor(hoveringPlayAgain ? Color.black : Color.white);
        g.drawString(text, textX, textY);

        var hintFont = new Font("Monospaced", Font.PLAIN, 12);
        g.setFont(hintFont);
        var hintFm = this.getFontMetrics(hintFont);
        String hint = "click or press SPACE";
        g.setColor(Color.gray);
        g.drawString(hint, (BOARD_WIDTH - hintFm.stringWidth(hint)) / 2,
                playAgainButton.y + playAgainButton.height + 22);
    }

    private void restartGame() {
        try {
            if (gameOverAudioPlayer != null) {
                gameOverAudioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error stopping game-over audio player.");
        }

        frame = 0;
        deaths = 0;
        score = 0;
        message = "Game Over";
        inGame = true;
        boss = null;
        bossDeathTimer = -1;

        gameInit(); // carriedOverPlayer is already null by now, so this is a fresh Player
        initAudio();

        if (!timer.isRunning()) {
            timer.start();
        }

        repaint();
    }

    private void update() {

        // Once the boss shows up this becomes a dedicated boss room - stop
        // spawning more grunts/power-ups from the schedule.
        if (boss == null) {
            SpawnDetails sd = spawnMap.get(frame);
            if (sd != null) {
                switch (sd.type) {
                    // Enemies cross the board slowly, so a busy moment can
                    // already have several alive - skip a spawn rather than
                    // let the screen get too crowded.
                    case "Alien2":
                        if (countAliveEnemies() < STAGE2_MAX_CONCURRENT_ENEMIES) {
                            Enemy enemy2 = new Alien2(sd.x, sd.y);
                            enemies.add(enemy2);
                        }
                        break;
                    case "Alien3":
                        if (countAliveEnemies() < STAGE2_MAX_CONCURRENT_ENEMIES) {
                            Enemy enemy3 = new Alien3(sd.x, sd.y);
                            enemies.add(enemy3);
                        }
                        break;
                    case "PowerUp-SpeedUp":
                        PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                        powerups.add(speedUp);
                        break;
                    case "PowerUp-MultiShot":
                        PowerUp multiShot = new MultiShot(sd.x, sd.y);
                        powerups.add(multiShot);
                        break;
                    case "PowerUp-HeartUp":
                        PowerUp heartUp = new HeartUp(sd.x, sd.y);
                        powerups.add(heartUp);
                        break;
                    default:
                        System.out.println("Unknown enemy type: " + sd.type);
                        break;
                }
            }

            if (deaths >= ENEMIES_TO_CLEAR_STAGE) {
                boss = new Boss(BOARD_WIDTH, BOARD_HEIGHT / 2 - 100);
            }
        }

        // Let the boss's death explosions actually play out before cutting
        // to the win screen (which doesn't draw explosions).
        if (bossDeathTimer >= 0) {
            bossDeathTimer--;
            if (bossDeathTimer < 0) {
                inGame = false;
                timer.stop();
                message = "Game won!";
            }
        }

        player.act();

        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                }
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);
                enemy.advanceAnimation();

                // Flew past the player without dying - make it stop
                // counting as "alive" (see countAliveEnemies()) so it can't
                // permanently occupy one of the spawn-cap slots.
                if (enemy.getX() < -100) {
                    enemy.die();
                    continue;
                }

                if (player.isVisible() && player.collidesWith(enemy)) {
                    hitPlayer();
                    enemy.setDying(true);
                    explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                    deaths++;
                }

                if (player.isVisible() && randomizer.nextInt(ENEMY_FIRE_CHANCE) == 0) {
                    if (enemy instanceof Alien3) {
                        // The purple mecha fires two parallel rows instead
                        // of one, for a bit more bite than the other type.
                        enemyShots.add(new EnemyShot(enemy.getX(), enemy.getY() + STAGE2_ALIEN_HEIGHT / 3, STAGE2_ENEMY_SHOT_SPEED));
                        enemyShots.add(new EnemyShot(enemy.getX(), enemy.getY() + STAGE2_ALIEN_HEIGHT * 2 / 3, STAGE2_ENEMY_SHOT_SPEED));
                    } else {
                        enemyShots.add(new EnemyShot(enemy.getX(), enemy.getY() + STAGE2_ALIEN_HEIGHT / 2, STAGE2_ENEMY_SHOT_SPEED));
                    }
                }
            }
        }

        if (boss != null && boss.isVisible()) {
            boss.act();

            if (player.isVisible() && player.collidesWith(boss)) {
                hitPlayer();
            }

            if (player.isVisible() && randomizer.nextInt(BOSS_FIRE_CHANCE) == 0) {
                int bossHeight = boss.getImage() != null ? boss.getImage().getHeight(null) : 0;
                enemyShots.add(new EnemyShot(boss.getX(), boss.getY() + bossHeight / 2, STAGE2_ENEMY_SHOT_SPEED));
            }
        }

        List<EnemyShot> enemyShotsToRemove = new ArrayList<>();
        for (EnemyShot enemyShot : enemyShots) {
            enemyShot.act();

            if (!enemyShot.isVisible()) {
                enemyShotsToRemove.add(enemyShot);
            } else if (player.isVisible() && enemyShot.collidesWith(player)) {
                hitPlayer();
                enemyShot.die();
                enemyShotsToRemove.add(enemyShot);
            }
        }
        enemyShots.removeAll(enemyShotsToRemove);

        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (enemy.isVisible() && shot.isVisible()
                            && shotX >= (enemyX - SHOT_HIT_MARGIN)
                            && shotX <= (enemyX + STAGE2_ALIEN_WIDTH + SHOT_HIT_MARGIN)
                            && shotY >= (enemyY - SHOT_HIT_MARGIN)
                            && shotY <= (enemyY + STAGE2_ALIEN_HEIGHT + SHOT_HIT_MARGIN)) {

                        enemy.setImage(gdd.ImageUtil.loadScaled(IMG_EXPLOSION, SCALE_FACTOR));
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        deaths++;
                        score += SCORE_PER_KILL;
                        shot.die();
                        shotsToRemove.add(shot);
                    }
                }

                if (boss != null && boss.isVisible() && shot.isVisible() && shot.collidesWith(boss)) {
                    boss.takeDamage(1);
                    shot.die();
                    shotsToRemove.add(shot);

                    if (boss.isDefeated()) {
                        boss.die();
                        // Staggered, multi-frame explosions across the hull
                        // for a much bigger death than a grunt enemy's.
                        explosions.add(new BossExplosion(boss.getX(), boss.getY()));
                        explosions.add(new BossExplosion(boss.getX() + 90, boss.getY() + 40));
                        explosions.add(new BossExplosion(boss.getX() + 40, boss.getY() + 60));
                        score += BOSS_SCORE_BONUS;
                        // Don't cut to the win screen yet - bossDeathTimer
                        // (ticked down above) lets these explosions play out first.
                        bossDeathTimer = BOSS_DEATH_DELAY;
                    }
                }

                int x = shot.getX();
                x += 20;
                int y = shot.getY() + shot.getDy();

                if (x > BOARD_WIDTH || y < -20 || y > BOARD_HEIGHT + 20) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setX(x);
                    shot.setY(y);
                }
            }
        }
        shots.removeAll(shotsToRemove);
    }

    private void doGameCycle() {
        frame++;
        update();
        repaint();
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doGameCycle();
        }
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (!inGame) {
                if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ENTER) {
                    restartGame();
                }
                return;
            }

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            if (key == KeyEvent.VK_SPACE) {
                int bulletCount = player.getShotLevel() + 1;

                if (shots.size() + bulletCount <= MAX_ACTIVE_SHOTS) {
                    for (int i = 0; i < bulletCount; i++) {
                        double offset = i - (bulletCount - 1) / 2.0;
                        int shotY = y + (int) Math.round(offset * 10);
                        int dy = (int) Math.round(offset * 1.5);
                        shots.add(new Shot(x, shotY, dy));
                    }
                }
            }
        }
    }

    private class MAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!inGame && playAgainButton.contains(e.getPoint())) {
                restartGame();
            }
        }
    }

    private class MMAdapter extends MouseMotionAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            boolean nowHovering = !inGame && playAgainButton.contains(e.getPoint());
            if (nowHovering != hoveringPlayAgain) {
                hoveringPlayAgain = nowHovering;
                repaint();
            }
        }
    }
}
