package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.MultiShot;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Enemy;
import gdd.sprite.Enemy2;
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

public class Scene1 extends JPanel {

    private int frame = 0;
    private List<PowerUp> powerups;
    private List<Enemy> enemies;
    private List<Explosion> explosions;
    private List<Shot> shots;
    private Player player;
    // private Shot shot;
    private BufferedImage backgroundImage;

    final int BLOCKHEIGHT = 50;
    final int BLOCKWIDTH = 50;

    final int BLOCKS_TO_DRAW = BOARD_HEIGHT / BLOCKHEIGHT;

    private int direction = -1;
    private int deaths = 0;
    private int score = 0;
    private static final int SCORE_PER_KILL = 100;
    private static final int MAX_ACTIVE_SHOTS = 20;

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

    private final Rectangle playAgainButton = new Rectangle(
            BOARD_WIDTH / 2 - 230, BOARD_HEIGHT - 110, 200, 56);
    private boolean hoveringPlayAgain = false;

    private final Rectangle quitButton = new Rectangle(
            BOARD_WIDTH / 2 + 30, BOARD_HEIGHT - 110, 200, 56);
    private boolean hoveringQuit = false;

    private boolean listenersAdded = false;

    private int currentRow = -1;
    // TODO load this map from a file
    private int mapOffset = 0;
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
    private volatile AudioPlayer audioPlayer;
    private volatile AudioPlayer gameOverAudioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;

    public Scene1(Game game) {
        this.game = game;
        // initBoard();
        // gameInit();
        loadSpawnDetails();
    }

    private void initAudio() {
        // Audio file/Clip I/O is blocking and can genuinely stall on real
        // audio hardware (driver quirks, limited concurrent lines). Doing
        // it on the EDT would freeze the entire window - input, rendering,
        // everything - for however long that stall lasts. Off-thread means
        // a slow or failing audio device degrades sound, not the game.
        new Thread(() -> {
            try {
                String filePath = "src/audio/4 - Burning Heat [Stage 1].wav";
                audioPlayer = new AudioPlayer(filePath, true);
                audioPlayer.play();
            } catch (Exception e) {
                System.err.println("Error initializing audio player: " + e.getMessage());
            }
        }).start();
    }

    private void loadSpawnDetails() {
        // TODO load this from a file
        // Enemies/power-ups now enter from the right edge (x = BOARD_WIDTH)
        // and their varying coordinate is the vertical (y) position.
        spawnMap.put(50, new SpawnDetails("PowerUp-SpeedUp", BOARD_WIDTH, 100));
        spawnMap.put(150, new SpawnDetails("PowerUp-MultiShot", BOARD_WIDTH, 450));
        spawnMap.put(200, new SpawnDetails("Alien1", BOARD_WIDTH, 200));
        spawnMap.put(220, new SpawnDetails("Enemy2", BOARD_WIDTH, 400));
        spawnMap.put(250, new SpawnDetails("PowerUp-MultiShot", BOARD_WIDTH, 150));
        spawnMap.put(300, new SpawnDetails("Alien1", BOARD_WIDTH, 300));
        spawnMap.put(320, new SpawnDetails("Enemy2", BOARD_WIDTH, 60));
        spawnMap.put(350, new SpawnDetails("PowerUp-MultiShot", BOARD_WIDTH, 300));
        spawnMap.put(380, new SpawnDetails("PowerUp-SpeedUp", BOARD_WIDTH, 500));

        spawnMap.put(400, new SpawnDetails("Alien1", BOARD_WIDTH, 10));
        spawnMap.put(401, new SpawnDetails("Alien1", BOARD_WIDTH, 90));
        spawnMap.put(402, new SpawnDetails("Alien1", BOARD_WIDTH, 170));
        spawnMap.put(403, new SpawnDetails("Alien1", BOARD_WIDTH, 250));

        spawnMap.put(420, new SpawnDetails("Enemy2", BOARD_WIDTH, 400));
        spawnMap.put(470, new SpawnDetails("Enemy2", BOARD_WIDTH, 60));
        spawnMap.put(480, new SpawnDetails("PowerUp-MultiShot", BOARD_WIDTH, 200));

        spawnMap.put(500, new SpawnDetails("Alien1", BOARD_WIDTH, 330));
        spawnMap.put(501, new SpawnDetails("Alien1", BOARD_WIDTH, 410));
        spawnMap.put(502, new SpawnDetails("Alien1", BOARD_WIDTH, 490));
        spawnMap.put(503, new SpawnDetails("Alien1", BOARD_WIDTH, 570));
    }

    private void initBoard() {

    }

    public void start() {
        if (!listenersAdded) {
            addKeyListener(new TAdapter());
            addMouseListener(new MAdapter());
            addMouseMotionListener(new MMAdapter());
            listenersAdded = true;
        }
        setFocusable(true);
        requestFocusInWindow();
        setBackground(Color.black);

        timer = new Timer(1000 / 60, new GameCycle());
        timer.start();

        resetGameState();
        initAudio();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        try {
            if (audioPlayer != null) {
                audioPlayer.stop();
            }
            if (gameOverAudioPlayer != null) {
                gameOverAudioPlayer.stop();
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

        // Loaded synchronously to avoid the macOS getScaledInstance crash.
        backgroundImage = gdd.ImageUtil.load(IMG_BACKGROUND);

        // for (int i = 0; i < 4; i++) {
        // for (int j = 0; j < 6; j++) {
        // var enemy = new Enemy(ALIEN_INIT_X + (ALIEN_WIDTH + ALIEN_GAP) * j,
        // ALIEN_INIT_Y + (ALIEN_HEIGHT + ALIEN_GAP) * i);
        // enemies.add(enemy);
        // }
        // }
        player = new Player();
        // shot = new Shot();
    }

    private void drawBackground(Graphics g) {
        // Slow-scrolling nebula image behind the dot starfield, for depth.
        if (backgroundImage == null) {
            return;
        }

        int imgW = backgroundImage.getWidth();
        int imgH = backgroundImage.getHeight();

        // Scrolls slower than the dot starfield (drawMap uses 1px/frame) -
        // the speed difference is what sells the sense of depth/parallax.
        int scrollX = frame / 4;

        // The image wasn't authored to tile with itself, so consecutive
        // copies are alternately mirrored. A mirrored copy shares the exact
        // same edge pixels as its neighbor, so the seam between repeats
        // disappears instead of showing a hard cut.
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
        // Draw scrolling starfield background, scrolling right-to-left

        // Calculate smooth scrolling offset (1 pixel per frame)
        int scrollOffset = (frame) % BLOCKWIDTH;

        // Calculate which columns to draw based on screen position
        int baseCol = (frame) / BLOCKWIDTH;
        int colsNeeded = (BOARD_WIDTH / BLOCKWIDTH) + 2; // +2 for smooth scrolling

        // Loop through columns that should be visible on screen
        for (int screenCol = 0; screenCol < colsNeeded; screenCol++) {
            // Calculate which MAP entry to use (with wrapping)
            int mapCol = (baseCol + screenCol) % MAP.length;

            // Calculate X position for this column (new stars enter from the right)
            int x = BOARD_WIDTH - ((screenCol * BLOCKWIDTH) - scrollOffset);

            // Skip if column is completely off-screen
            if (x > BOARD_WIDTH || x < -BLOCKWIDTH) {
                continue;
            }

            // Draw each row in this column
            for (int row = 0; row < MAP[mapCol].length; row++) {
                if (MAP[mapCol][row] == 1) {
                    // Calculate Y position
                    int y = row * BLOCKHEIGHT;

                    // Draw a cluster of stars
                    drawStarCluster(g, x, y, BLOCKWIDTH, BLOCKHEIGHT);
                }
            }
        }

    }

    private void drawStarCluster(Graphics g, int x, int y, int width, int height) {
        // Set star color to white
        g.setColor(Color.WHITE);

        // Draw multiple stars in a cluster pattern
        // Main star (larger)
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g.fillOval(centerX - 2, centerY - 2, 4, 4);

        // Smaller surrounding stars
        g.fillOval(centerX - 15, centerY - 10, 2, 2);
        g.fillOval(centerX + 12, centerY - 8, 2, 2);
        g.fillOval(centerX - 8, centerY + 12, 2, 2);
        g.fillOval(centerX + 10, centerY + 15, 2, 2);

        // Tiny stars for more detail
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
            return; // already handled this frame
        }
        player.setImage(gdd.ImageUtil.loadScaled(IMG_EXPLOSION, SCALE_FACTOR));
        player.setDying(true);
        message = "Game Over";

        try {
            if (audioPlayer != null) {
                audioPlayer.stop(); // stop the background gameplay music
            }
        } catch (Exception ex) {
            System.out.println("Error stopping background music.");
            ex.printStackTrace();
        }

        new Thread(() -> {
            try {
                gameOverAudioPlayer = new AudioPlayer("src/audio/Game Over.wav", false);
                gameOverAudioPlayer.play();
            } catch (Exception ex) {
                System.out.println("Error with playing sound.");
                ex.printStackTrace();
            }
        }).start();
    }

    private void drawPlayer(Graphics g) {

        if (player.isVisible()) {

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

    private void drawBombing(Graphics g) {

        for (Enemy e : enemies) {
            Enemy.Bomb b = e.getBomb();
            if (!b.isDestroyed()) {
                g.drawImage(b.getImage(), b.getX(), b.getY(), this);
            }
        }
    }

    private void drawHUD(Graphics g) {
        // Semi-transparent bar behind the HUD text so it stays readable
        // over the starfield/background regardless of what's under it.
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, 0, BOARD_WIDTH, 28);

        var hudFont = new Font("Monospaced", Font.BOLD, 16);
        g.setFont(hudFont);
        var fontMetrics = g.getFontMetrics(hudFont);

        g.setColor(Color.white);
        String scoreText = String.format("SCORE: %06d", score);
        g.drawString(scoreText, 12, 20);

        String shotText = "SHOT LV: " + player.getShotLevel() + "/" + SHOT_UP_MAX_LEVEL;
        int shotWidth = fontMetrics.stringWidth(shotText);
        g.drawString(shotText, (BOARD_WIDTH - shotWidth) / 2, 20);

        String speedText = "SPEED: " + player.getSpeed();
        int speedWidth = fontMetrics.stringWidth(speedText);
        g.drawString(speedText, BOARD_WIDTH - speedWidth - 12, 20);
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, d.width, d.height);

        g.setColor(Color.green);

        if (inGame) {

            drawBackground(g); // Nebula image, slow parallax layer
            drawMap(g);  // Draw background stars first
            drawExplosions(g);
            drawPowreUps(g);
            drawAliens(g);
            drawPlayer(g);
            drawShot(g);
            drawBombing(g);
            drawHUD(g);

        } else {

            if (timer.isRunning()) {
                timer.stop();
            }

            gameOver(g);
        }

        // Drawn last so the background image (which is fully opaque)
        // never paints over it. Sits below the HUD bar so they don't collide.
        g.setColor(Color.white);
        g.drawString("FRAME: " + frame, 10, 45);

        Toolkit.getDefaultToolkit().sync();
    }

    private void gameOver(Graphics g) {

        // Keep the starfield/planet visible under a dark overlay so the
        // results screen still reads as the same game as the title screen.
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

        String subtitle = won ? "Great flying, Commander!" : "Better luck next time!";
        var subtitleFont = new Font("SansSerif", Font.PLAIN, 20);
        g.setFont(subtitleFont);
        var subtitleFm = this.getFontMetrics(subtitleFont);
        g.setColor(new Color(220, 220, 220));
        g.drawString(subtitle, (BOARD_WIDTH - subtitleFm.stringWidth(subtitle)) / 2, headlineY + 32);

        var scoreFont = new Font("Monospaced", Font.BOLD, 22);
        g.setFont(scoreFont);
        var scoreFm = this.getFontMetrics(scoreFont);
        String scoreText = String.format("FINAL SCORE: %06d", score);
        g.setColor(Color.white);
        g.drawString(scoreText, (BOARD_WIDTH - scoreFm.stringWidth(scoreText)) / 2, headlineY + 68);

        drawPlayAgainButton(g);
    }

    private void drawButton(Graphics g, Rectangle rect, String label, String hint,
            boolean hovering, Color accent) {
        boolean pulseOn = (frame % 40) < 20;
        Color fill = hovering ? accent : new Color(20, 20, 24, 230);
        Color border = pulseOn ? accent : Color.white;

        var g2 = (Graphics2D) g;
        g2.setColor(fill);
        g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 14, 14);

        g2.setStroke(new BasicStroke(3));
        g2.setColor(border);
        g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 14, 14);

        var buttonFont = new Font("SansSerif", Font.BOLD, 20);
        g.setFont(buttonFont);
        var buttonFm = this.getFontMetrics(buttonFont);
        int textX = rect.x + (rect.width - buttonFm.stringWidth(label)) / 2;
        int textY = rect.y + (rect.height + buttonFm.getAscent()) / 2 - 4;
        g.setColor(hovering ? Color.black : Color.white);
        g.drawString(label, textX, textY);

        var hintFont = new Font("Monospaced", Font.PLAIN, 12);
        g.setFont(hintFont);
        var hintFm = this.getFontMetrics(hintFont);
        g.setColor(Color.gray);
        g.drawString(hint, rect.x + (rect.width - hintFm.stringWidth(hint)) / 2,
                rect.y + rect.height + 22);
    }

    private void drawPlayAgainButton(Graphics g) {
        drawButton(g, playAgainButton, "PLAY AGAIN", "click or SPACE",
                hoveringPlayAgain, new Color(255, 140, 40));
        drawButton(g, quitButton, "MAIN MENU", "click or ESC",
                hoveringQuit, new Color(90, 150, 230));
    }

    private void quitToMenu() {
        stop();
        game.loadTitle();
    }

    private void resetGameState() {
        frame = 0;
        deaths = 0;
        score = 0;
        message = "Game Over";
        inGame = true;

        gameInit();
    }

    private void restartGame() {
        try {
            if (gameOverAudioPlayer != null) {
                gameOverAudioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error stopping game-over audio player.");
        }

        resetGameState();
        initAudio();

        if (!timer.isRunning()) {
            timer.start();
        }

        repaint();
    }

    private void update() {


        // Check enemy spawn
        // TODO this approach can only spawn one enemy at a frame
        SpawnDetails sd = spawnMap.get(frame);
        if (sd != null) {
            // Create a new enemy based on the spawn details
            switch (sd.type) {
                case "Alien1":
                    Enemy enemy = new Alien1(sd.x, sd.y);
                    enemies.add(enemy);
                    break;
                // Add more cases for different enemy types if needed
                case "Enemy2":
                    Enemy enemy2 = new Enemy2(sd.x, sd.y);
                    enemies.add(enemy2);
                    break;
                case "PowerUp-SpeedUp":
                    // Handle speed up item spawn
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                case "PowerUp-MultiShot":
                    PowerUp multiShot = new MultiShot(sd.x, sd.y);
                    powerups.add(multiShot);
                    break;
                default:
                    System.out.println("Unknown enemy type: " + sd.type);
                    break;
            }
        }

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            // Stage 1 clear - hand off to Stage 2 instead of ending here.
            game.loadScene3();
            return;
        }

        // player
        player.act();

        // Power-ups
        for (PowerUp powerup : powerups) {
            if (powerup.isVisible()) {
                powerup.act();
                if (powerup.collidesWith(player)) {
                    powerup.upgrade(player);
                }
            }
        }

        // Enemies
        for (Enemy enemy : enemies) {
            if (enemy.isVisible()) {
                enemy.act(direction);
                enemy.advanceAnimation();

                // Ramming: touching an enemy ship kills the player.
                if (player.isVisible() && player.collidesWith(enemy)) {
                    killPlayer();
                    enemy.setDying(true);
                    explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                    deaths++;
                }

                // Bombs: each enemy owns one reusable Bomb (given codebase's
                // design). "Destroyed" means "not currently in flight, free
                // to drop again" - matching how the original template used it.
                // Only a *living* enemy can drop a new one.
                int chance = randomizer.nextInt(15);
                Enemy.Bomb bomb = enemy.getBomb();

                if (chance == CHANCE && bomb.isDestroyed()) {
                    bomb.setDestroyed(false);
                    bomb.setX(enemy.getX());
                    bomb.setY(enemy.getY() + enemy.getImage().getHeight(null) / 2);
                }
            }

            // A bomb already in flight must keep moving and stay eligible to
            // despawn (hit the player, or reach the left edge) even if the
            // enemy that fired it dies in the meantime. Otherwise it freezes
            // in place forever, since nothing else would ever clear it.
            Enemy.Bomb bomb = enemy.getBomb();
            if (!bomb.isDestroyed()) {
                // Original fell downward (bomb.setY(bomb.getY() + 1));
                // flies left instead to match the side-scroll conversion.
                // Faster than the enemy's own 1px/frame so it visibly
                // separates and reads as a fired projectile instead of
                // riding alongside the ship that fired it.
                bomb.setX(bomb.getX() - BOMB_SPEED);

                if (player.isVisible() && bomb.collidesWith(player)) {
                    killPlayer();
                    bomb.setDestroyed(true);
                } else if (bomb.getX() < 0) {
                    bomb.setDestroyed(true);
                }
            }
        }

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {

                for (Enemy enemy : enemies) {
                    // Collision detection: shot and enemy
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (shot.collidesWith(enemy)) {

                        enemy.setImage(gdd.ImageUtil.loadScaled(IMG_EXPLOSION, SCALE_FACTOR));
                        enemy.setDying(true);
                        explosions.add(new Explosion(enemyX, enemyY));
                        deaths++;
                        score += SCORE_PER_KILL;
                        shot.die();
                        shotsToRemove.add(shot);
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

        // enemies
        // for (Enemy enemy : enemies) {
        //     int x = enemy.getX();
        //     if (x >= BOARD_WIDTH - BORDER_RIGHT && direction != -1) {
        //         direction = -1;
        //         for (Enemy e2 : enemies) {
        //             e2.setY(e2.getY() + GO_DOWN);
        //         }
        //     }
        //     if (x <= BORDER_LEFT && direction != 1) {
        //         direction = 1;
        //         for (Enemy e : enemies) {
        //             e.setY(e.getY() + GO_DOWN);
        //         }
        //     }
        // }
        // for (Enemy enemy : enemies) {
        //     if (enemy.isVisible()) {
        //         int y = enemy.getY();
        //         if (y > GROUND - ALIEN_HEIGHT) {
        //             inGame = false;
        //             message = "Invasion!";
        //         }
        //         enemy.act(direction);
        //     }
        // }
        // bombs - collision detection
        // Bomb is with enemy, so it loops over enemies
        /*
        for (Enemy enemy : enemies) {

            int chance = randomizer.nextInt(15);
            Enemy.Bomb bomb = enemy.getBomb();

            if (chance == CHANCE && enemy.isVisible() && bomb.isDestroyed()) {

                bomb.setDestroyed(false);
                bomb.setX(enemy.getX());
                bomb.setY(enemy.getY());
            }

            int bombX = bomb.getX();
            int bombY = bomb.getY();
            int playerX = player.getX();
            int playerY = player.getY();

            if (player.isVisible() && !bomb.isDestroyed()
                    && bombX >= (playerX)
                    && bombX <= (playerX + PLAYER_WIDTH)
                    && bombY >= (playerY)
                    && bombY <= (playerY + PLAYER_HEIGHT)) {

                var ii = new ImageIcon(IMG_EXPLOSION);
                player.setImage(ii.getImage());
                player.setDying(true);
                bomb.setDestroyed(true);
            }

            if (!bomb.isDestroyed()) {
                bomb.setY(bomb.getY() + 1);
                if (bomb.getY() >= GROUND - BOMB_HEIGHT) {
                    bomb.setDestroyed(true);
                }
            }
        }
         */
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
                } else if (key == KeyEvent.VK_ESCAPE) {
                    quitToMenu();
                }
                return;
            }

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            if (key == KeyEvent.VK_SPACE) {
                // Multi-shot: shotLevel 0-4 fires 1-5 bullets in a fan spread
                // (also doubles as the optional multi-directional/3-way shot).
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
            } else if (!inGame && quitButton.contains(e.getPoint())) {
                quitToMenu();
            }
        }
    }

    private class MMAdapter extends MouseMotionAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            boolean nowHoveringPlay = !inGame && playAgainButton.contains(e.getPoint());
            boolean nowHoveringQuit = !inGame && quitButton.contains(e.getPoint());
            if (nowHoveringPlay != hoveringPlayAgain || nowHoveringQuit != hoveringQuit) {
                hoveringPlayAgain = nowHoveringPlay;
                hoveringQuit = nowHoveringQuit;
                repaint();
            }
        }
    }
}