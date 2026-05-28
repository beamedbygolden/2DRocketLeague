import java.awt.*;

public class Car {
    int x, y; // position
    int speed = 6; // Movement speed
    int startx, starty;
    boolean left, right, up, down; // car movements
    Color color; // color
    int groundY; // ground (where car stays)
    int boost = 100;
    int boostcost = 10;
    double vx = 0;

    /**
     * Constructor
     */
    public Car(int x, int y, int groundY, Color color) {
        this.x = x;
        this.y = y;
        this.groundY = groundY;
        this.color = color;
        this.startx = x;
        this.starty = y;
    }

    /**
     * Update movement + ground physics
     */
    public void update() {

        // acceleration
        if (left)  vx -= 0.5;
        if (right) vx += 0.5;

        // friction
        vx *= 0.92;

        // apply velocity
        x += vx;

        // keep on ground
        y = groundY;
    }
    /**
     * resets car value 
     */
    public void reset() {
        x = startx;
        y = starty;
        vx = 0; 
        left = false;
        right = false;
        up = false;
        down = false;
    }

    /**
     * Draw car
     */
    public void draw(Graphics2D g2) {
        // Body
        g2.setColor(color);
        g2.fillRoundRect(x, y, 120, 35, 15, 15);

        // Roof
        g2.setColor(color.darker());
        g2.fillRoundRect(x + 25, y - 20, 60, 25, 10, 10);

        // Windows
        g2.setColor(new Color(200, 240, 255));
        g2.fillRoundRect(x + 30, y - 18, 20, 15, 5, 5);
        g2.fillRoundRect(x + 55, y - 18, 20, 15, 5, 5);

        // Wheels
        g2.setColor(Color.DARK_GRAY);
        g2.fillOval(x + 10, y + 25, 25, 25);
        g2.fillOval(x + 85, y + 25, 25, 25);

        // wheel centers
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillOval(x + 17, y + 32, 12, 12);
        g2.fillOval(x + 92, y + 32, 12, 12);

        
        //boost flame
        g2.setColor(Color.ORANGE);
        // moving right
        if (vx > 1) {

            g2.fillPolygon(
                new int[]{x, x - 30, x},
                new int[]{y + 10, y + 17, y + 25},
                3
            );
        }

        // moving left
        if (vx < -1) {

            g2.fillPolygon(
                new int[]{x + 120, x + 150, x + 120},
                new int[]{y + 10, y + 17, y + 25},
                3
            );
        }
    }
}