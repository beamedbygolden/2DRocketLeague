import java.awt.*;

public class Car {
    int x, y; // position
    int speed = 6; // Movement speed
    boolean left, right, up, down; // car movements
    Color color; // color
    int groundY; // ground (where car stays)

    /**
     * Constructor
     */
    public Car(int x, int y, int groundY, Color color) {
        this.x = x;
        this.y = y;
        this.groundY = groundY;
        this.color = color;
    }

    /**
     * UPDATE = movement + ground physics
     */
    public void update() {

        //Horizontal movement
        if (left)  x -= speed;
        if (right) x += speed;
        y = groundY; // locks car to ground
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

        
        // Boost flame (visual only for now)
        g2.setColor(Color.ORANGE);
        g2.fillPolygon(
                new int[]{x + 120, x + 150, x + 120},
                new int[]{y + 10, y + 17, y + 25},
                3
        );
    }
}