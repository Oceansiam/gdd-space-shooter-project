package gdd.scene;

import gdd.AudioPlayer;
import gdd.Game;
import static gdd.Global.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TitleScene extends JPanel {

    private static final String TITLE_TEXT = "SPACE SHOOTER";
    private static final String SUBTITLE_TEXT = "A SIDE-SCROLLING SHOOTER";
    private static final String[] TEAM_MEMBERS = {
        "Taian Chen - 6630027",
        "Kriidipas Kongsakul - 6640031"
    };
    // Warm ember accent to match the metallic/fiery title logo treatment.
    private static final Color ACCENT = new Color(255, 140, 40);

    private int frame = 0;
    private BufferedImage backgroundImage;
    private AudioPlayer audioPlayer;
    private final Dimension d = new Dimension(BOARD_WIDTH, BOARD_HEIGHT);
    private Timer timer;
    private final Game game;

    private final Rectangle startButton = new Rectangle(
            BOARD_WIDTH / 2 - 110, 420, 220, 64);
    private boolean hoveringStart = false;

    // Star positions/phases are precomputed once so the field twinkles in
    // place instead of re-randomizing (and jumping around) every frame.
    private final int[] starX;
    private final int[] starY;
    private final int[] starPhase;

    public TitleScene(Game game) {
        this.game = game;

        var randomizer = new Random(42);
        int starCount = 90;
        starX = new int[starCount];
        starY = new int[starCount];
        starPhase = new int[starCount];
        for (int i = 0; i < starCount; i++) {
            starX[i] = randomizer.nextInt(BOARD_WIDTH);
            starY[i] = randomizer.nextInt(BOARD_HEIGHT);
            starPhase[i] = randomizer.nextInt(60);
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

        initTitle();
        initAudio();
    }

    public void stop() {
        try {
            if (timer != null) {
                timer.stop();
            }

            if (audioPlayer != null) {
                audioPlayer.stop();
            }
        } catch (Exception e) {
            System.err.println("Error closing audio player.");
        }
    }

    private void initTitle() {
        // Loaded synchronously to avoid the macOS getScaledInstance crash.
        backgroundImage = gdd.ImageUtil.load(IMG_BACKGROUND);
    }

    private void initAudio() {
        try {
            String filePath = "src/audio/title.wav";
            audioPlayer = new AudioPlayer(filePath, true);

            audioPlayer.play();
        } catch (Exception e) {
            System.err.println("Error with playing sound.");
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }

    private void doDrawing(Graphics g) {
        var g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.black);
        g2.fillRect(0, 0, d.width, d.height);

        drawBackground(g2);
        drawStars(g2);
        drawTitle(g2);
        drawTeam(g2);
        drawStartButton(g2);

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawBackground(Graphics2D g) {
        // Same slow-scrolling nebula/planet art used in Scene1, so the
        // title screen reads as part of the same game.
        if (backgroundImage == null) {
            return;
        }

        int imgW = backgroundImage.getWidth();
        int imgH = backgroundImage.getHeight();
        int scrollX = frame / 6;

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

        // Dim overlay so the title/team/button text stays readable over the
        // bright planet art underneath.
        g.setColor(new Color(0, 0, 0, 110));
        g.fillRect(0, 0, d.width, d.height);
    }

    private void drawStars(Graphics2D g) {
        for (int i = 0; i < starX.length; i++) {
            int cycle = (frame + starPhase[i]) % 60;
            int brightness = cycle < 30 ? 255 - (cycle * 6) : 75 + (cycle - 30) * 6;
            g.setColor(new Color(brightness, brightness, brightness));
            g.fillOval(starX[i], starY[i], 2, 2);
        }
    }

    private void drawTitle(Graphics2D g) {
        // Built as an outlined+filled glyph shape (not a plain drawString) so
        // it can carry a thick black outline and a metallic gradient fill -
        // a chunky bevelled "game logo" look instead of flat HUD text.
        var titleFont = new Font("SansSerif", Font.BOLD, 60);
        GlyphVector gv = titleFont.createGlyphVector(g.getFontRenderContext(), TITLE_TEXT);
        Shape textOutline = gv.getOutline();
        Rectangle2D bounds = textOutline.getBounds2D();

        double baselineY = 175;
        double x = (d.width - bounds.getWidth()) / 2 - bounds.getX();
        Shape logo = AffineTransform.getTranslateInstance(x, baselineY).createTransformedShape(textOutline);

        // Fiery glow behind the logo.
        float glowCx = d.width / 2f;
        float glowCy = (float) baselineY - 20f;
        var glow = new RadialGradientPaint(
                glowCx, glowCy, 180f,
                new float[]{0f, 0.6f, 1f},
                new Color[]{
                    new Color(255, 200, 60, 170),
                    new Color(230, 80, 20, 80),
                    new Color(0, 0, 0, 0)
                });
        g.setPaint(glow);
        g.fillOval((int) (glowCx - 180), (int) (glowCy - 180), 360, 360);

        // Thick black outline, then a silver-to-gunmetal metallic fill.
        g.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(Color.black);
        g.draw(logo);

        var metal = new GradientPaint(
                0, (float) (baselineY + bounds.getY()), new Color(245, 245, 250),
                0, (float) (baselineY + bounds.getY() + bounds.getHeight()), new Color(110, 115, 125));
        g.setPaint(metal);
        g.fill(logo);

        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(200, 30, 20));
        g.draw(logo);

        var subFont = new Font("Monospaced", Font.BOLD, 16);
        g.setFont(subFont);
        FontMetrics subFm = g.getFontMetrics(subFont);
        g.setColor(Color.white);
        g.drawString(SUBTITLE_TEXT, (d.width - subFm.stringWidth(SUBTITLE_TEXT)) / 2, (int) baselineY + 46);
    }

    private void drawTeam(Graphics2D g) {
        var labelFont = new Font("Monospaced", Font.BOLD, 14);
        var nameFont = new Font("Monospaced", Font.PLAIN, 16);

        g.setFont(labelFont);
        FontMetrics labelFm = g.getFontMetrics(labelFont);
        String label = "TEAM";
        int labelY = 255;
        g.setColor(ACCENT);
        g.drawString(label, (d.width - labelFm.stringWidth(label)) / 2, labelY);

        g.setFont(nameFont);
        FontMetrics nameFm = g.getFontMetrics(nameFont);
        int nameY = labelY + 28;
        g.setColor(Color.white);
        for (String member : TEAM_MEMBERS) {
            g.drawString(member, (d.width - nameFm.stringWidth(member)) / 2, nameY);
            nameY += 22;
        }
    }

    private void drawStartButton(Graphics2D g) {
        boolean pulseOn = (frame % 40) < 20;

        Color fill = hoveringStart ? ACCENT : new Color(25, 25, 28, 220);
        Color border = pulseOn ? ACCENT : Color.white;

        g.setColor(fill);
        g.fillRoundRect(startButton.x, startButton.y, startButton.width, startButton.height, 16, 16);

        g.setStroke(new BasicStroke(3));
        g.setColor(border);
        g.drawRoundRect(startButton.x, startButton.y, startButton.width, startButton.height, 16, 16);

        var buttonFont = new Font("SansSerif", Font.BOLD, 26);
        g.setFont(buttonFont);
        FontMetrics fm = g.getFontMetrics(buttonFont);
        String text = "START";
        int textX = startButton.x + (startButton.width - fm.stringWidth(text)) / 2;
        int textY = startButton.y + (startButton.height + fm.getAscent()) / 2 - 4;

        g.setColor(hoveringStart ? Color.black : Color.white);
        g.drawString(text, textX, textY);

        var hintFont = new Font("Monospaced", Font.PLAIN, 12);
        g.setFont(hintFont);
        String hint = "click START or press SPACE";
        FontMetrics hintFm = g.getFontMetrics(hintFont);
        g.setColor(Color.gray);
        g.drawString(hint, (d.width - hintFm.stringWidth(hint)) / 2,
                startButton.y + startButton.height + 26);
    }

    private void startGame() {
        game.loadScene2();
    }

    private void update() {
        frame++;
    }

    private void doGameCycle() {
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

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            if (key == KeyEvent.VK_SPACE || key == KeyEvent.VK_ENTER) {
                startGame();
            }
        }
    }

    private class MAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (startButton.contains(e.getPoint())) {
                startGame();
            }
        }
    }

    private class MMAdapter extends MouseMotionAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            boolean nowHovering = startButton.contains(e.getPoint());
            if (nowHovering != hoveringStart) {
                hoveringStart = nowHovering;
                repaint();
            }
        }
    }
}
