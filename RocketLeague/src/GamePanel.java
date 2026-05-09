import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {

    Image bg;

    // ── Collision bounds (tune these to match the image) ──────────────────
    // These are the rectangles your ball and car should collide with

    public static final int FLOOR_X   = 120;   // where the floor starts (left)
    public static final int FLOOR_Y   = 490;   // how far down the floor is
    public static final int FLOOR_W   = 1060;  // width of the floor

    public static final int CEILING_X = 120;
    public static final int CEILING_Y = 110;   // how far down the ceiling is
    public static final int CEILING_W = 1060;

    public static final int WALL_L_X  = 120;   // left wall x
    public static final int WALL_R_X  = 1180;  // right wall x
    public static final int WALL_Y    = 110;   // top of walls
    public static final int WALL_H    = 380;   // height of walls

    // Goals are openings in the left and right walls
    public static final int GOAL_Y    = 320;   // top of goal opening
    public static final int GOAL_H    = 150;   // height of goal opening

    public GamePanel() {
        setPreferredSize(new Dimension(1300, 600));
        bg = new ImageIcon("arena.png").getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // DRAW THE BACKGROUND IMAGE
        g.drawImage(bg, 0, 0, 1300, 600, this);

        // DRAW COLLISION BOUNDS (comment these out once you're happy with placement)
        g.setColor(new Color(255, 0, 0, 120));   // red, semi-transparent

        // Floor
        g.fillRect(FLOOR_X, FLOOR_Y, FLOOR_W, 8);

        // Ceiling
        g.fillRect(CEILING_X, CEILING_Y, CEILING_W, 8);

        // Left wall (two pieces above and below the goal)
        g.fillRect(WALL_L_X, WALL_Y, 8, GOAL_Y - WALL_Y);
        g.fillRect(WALL_L_X, GOAL_Y + GOAL_H, 8, (WALL_Y + WALL_H) - (GOAL_Y + GOAL_H));

        // Right wall (two pieces above and below the goal)
        g.fillRect(WALL_R_X, WALL_Y, 8, GOAL_Y - WALL_Y);
        g.fillRect(WALL_R_X, GOAL_Y + GOAL_H, 8, (WALL_Y + WALL_H) - (GOAL_Y + GOAL_H));

        // BLUE GOAL outline
        g.setColor(new Color(0, 100, 255, 150));
        g.fillRect(WALL_L_X - 40, GOAL_Y, 40, GOAL_H);
        g.setColor(new Color(0, 150, 255));
        g.drawRect(WALL_L_X - 40, GOAL_Y, 40, GOAL_H);

        // ORANGE GOAL outline
        g.setColor(new Color(255, 120, 0, 150));
        g.fillRect(WALL_R_X, GOAL_Y, 40, GOAL_H);
        g.setColor(new Color(255, 160, 0));
        g.drawRect(WALL_R_X, GOAL_Y, 40, GOAL_H);

        // LABELS
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(100, 180, 255));
        g.drawString("BLUE", WALL_L_X - 38, GOAL_Y - 5);
        g.setColor(new Color(255, 160, 50));
        g.drawString("ORANGE", WALL_R_X + 2, GOAL_Y - 5);
    }
}