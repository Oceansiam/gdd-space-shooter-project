package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import gdd.SpawnDetails;
import gdd.powerup.PowerUp;
import gdd.powerup.SpeedUp;
import gdd.sprite.Alien1;
import gdd.sprite.Enemy;
import gdd.sprite.EnemyShot;
import gdd.sprite.Explosion;
import gdd.sprite.Player;
import gdd.sprite.Shot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
    private List<EnemyShot> enemyShots;
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

    private boolean inGame = true;
    private String message = "Game Over";

    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private final Random randomizer = new Random();

    private Timer timer;
    private final Game game;

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
    private AudioPlayer audioPlayer;
    private AudioPlayer gameOverAudioPlayer;
    private int lastRowToShow;
    private int firstRowToShow;

    public Scene1(Game game) {
        this.game = game;
        // initBoard();
        // gameInit();
        loadSpawnDetails();
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

    private void loadSpawnDetails() {
        // TODO load this from a file
        // Enemies/power-ups now enter from the right edge (x = BOARD_WIDTH)
        // and their varying coordinate is the vertical (y) position.
        spawnMap.put(50, new SpawnDetails("PowerUp-SpeedUp", BOARD_WIDTH, 100));
        spawnMap.put(200, new SpawnDetails("Alien1", BOARD_WIDTH, 200));
        spawnMap.put(300, new SpawnDetails("Alien1", BOARD_WIDTH, 300));

        spawnMap.put(400, new SpawnDetails("Alien1", BOARD_WIDTH, 400));
        spawnMap.put(401, new SpawnDetails("Alien1", BOARD_WIDTH, 450));
        spawnMap.put(402, new SpawnDetails("Alien1", BOARD_WIDTH, 500));
        spawnMap.put(403, new SpawnDetails("Alien1", BOARD_WIDTH, 550));

        spawnMap.put(500, new SpawnDetails("Alien1", BOARD_WIDTH, 100));
        spawnMap.put(501, new SpawnDetails("Alien1", BOARD_WIDTH, 150));
        spawnMap.put(502, new SpawnDetails("Alien1", BOARD_WIDTH, 200));
        spawnMap.put(503, new SpawnDetails("Alien1", BOARD_WIDTH, 350));
    }

    private void initBoard() {

    }

    public void start() {
        addKeyListener(new TAdapter());
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
            gameOverAudioPlayer = new AudioPlayer("src/audio/Game Over.wav", false);
            gameOverAudioPlayer.play();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
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

    private void drawEnemyShots(Graphics g) {

        for (EnemyShot enemyShot : enemyShots) {

            if (enemyShot.isVisible()) {
                g.drawImage(enemyShot.getImage(), enemyShot.getX(), enemyShot.getY(), this);
            }
        }
    }

    private void drawBombing(Graphics g) {

        // for (Enemy e : enemies) {
        //     Enemy.Bomb b = e.getBomb();
        //     if (!b.isDestroyed()) {
        //         g.drawImage(b.getImage(), b.getX(), b.getY(), this);
        //     }
        // }
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

        String speedText = "SPEED: " + player.getSpeed();
        int speedWidth = fontMetrics.stringWidth(speedText);
        g.drawString(speedText, BOARD_WIDTH - speedWidth - 12, 20);
    }

    private void gameOver(Graphics g) {

        g.setColor(Color.black);
        g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(new Color(0, 32, 48));
        g.fillRect(50, BOARD_HEIGHT / 2 - 30, BOARD_WIDTH - 100, 50);
        g.setColor(Color.white);
        g.drawRect(50, BOARD_HEIGHT / 2 - 30, BOARD_WIDTH - 100, 50);

        var small = new Font("Helvetica", Font.BOLD, 14);
        var fontMetrics = this.getFontMetrics(small);

        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(message, (BOARD_WIDTH - fontMetrics.stringWidth(message)) / 2,
                BOARD_HEIGHT / 2);
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
                case "Alien2":
                    // Enemy enemy2 = new Alien2(sd.x, sd.y);
                    // enemies.add(enemy2);
                    break;
                case "PowerUp-SpeedUp":
                    // Handle speed up item spawn
                    PowerUp speedUp = new SpeedUp(sd.x, sd.y);
                    powerups.add(speedUp);
                    break;
                default:
                    System.out.println("Unknown enemy type: " + sd.type);
                    break;
            }
        }

        if (deaths == NUMBER_OF_ALIENS_TO_DESTROY) {
            inGame = false;
            timer.stop();
            message = "Game won!";
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

                // Ramming: touching an enemy ship kills the player.
                if (player.isVisible() && player.collidesWith(enemy)) {
                    killPlayer();
                    enemy.setDying(true);
                    explosions.add(new Explosion(enemy.getX(), enemy.getY()));
                    deaths++;
                }

                // Enemies occasionally fire back toward the player.
                if (player.isVisible() && randomizer.nextInt(ENEMY_FIRE_CHANCE) == 0) {
                    enemyShots.add(new EnemyShot(enemy.getX(), enemy.getY() + ALIEN_HEIGHT / 2));
                }
            }
        }

        // Enemy shots: move them, remove off-screen ones, check player hits.
        List<EnemyShot> enemyShotsToRemove = new ArrayList<>();
        for (EnemyShot enemyShot : enemyShots) {
            enemyShot.act();

            if (!enemyShot.isVisible()) {
                enemyShotsToRemove.add(enemyShot);
            } else if (player.isVisible() && enemyShot.collidesWith(player)) {
                killPlayer();
                enemyShot.die();
                enemyShotsToRemove.add(enemyShot);
            }
        }
        enemyShots.removeAll(enemyShotsToRemove);

        // shot
        List<Shot> shotsToRemove = new ArrayList<>();
        for (Shot shot : shots) {

            if (shot.isVisible()) {
                int shotX = shot.getX();
                int shotY = shot.getY();

                for (Enemy enemy : enemies) {
                    // Collision detection: shot and enemy
                    int enemyX = enemy.getX();
                    int enemyY = enemy.getY();

                    if (enemy.isVisible() && shot.isVisible()
                            && shotX >= (enemyX)
                            && shotX <= (enemyX + ALIEN_WIDTH)
                            && shotY >= (enemyY)
                            && shotY <= (enemyY + ALIEN_HEIGHT)) {

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

                if (x > BOARD_WIDTH) {
                    shot.die();
                    shotsToRemove.add(shot);
                } else {
                    shot.setX(x);
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
            System.out.println("Scene2.keyPressed: " + e.getKeyCode());

            player.keyPressed(e);

            int x = player.getX();
            int y = player.getY();

            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE && inGame) {
                System.out.println("Shots: " + shots.size());
                if (shots.size() < 4) {
                    // Create a new shot and add it to the list
                    Shot shot = new Shot(x, y);
                    shots.add(shot);
                }
            }

        }
    }
}