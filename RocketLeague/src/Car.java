import java.awt.*;
import java.time.chrono.MinguoChronology;

import javax.swing.ImageIcon;

public class Car {
	Image carImage; // car photo
    int x, y; // position
    int speed = 6; // Movement speed
    int startx, starty;
    boolean left, right, up, down; // car movements
    Color color; // color
    int groundY; // ground (where car stays)
    int boost = 100;
    int minboost = 0;
    int boostcost = 10;
    double vx = 0;
    double vy = 0;
    boolean onGround = true;
    boolean jumping = false;
    GamePanel panel;
    boolean facingRight = true;

    /**
     * Constructor
     */
    public Car(int x, int y, int groundY, Color color, String imagePath, GamePanel panel) {
        this.x = x;
        this.y = y;
        this.groundY = groundY;
        this.color = color;
        this.panel = panel;
        this.startx = x;
        this.starty = y;

        carImage = new ImageIcon(imagePath).getImage();
    }

    /**
     * Update movement + ground physics
     */
    public void update() {

        if (left)  vx -= 0.5;
        if (right) vx += 0.5;

        vx *= 0.92;
        x += vx;

        double ground = panel.getFloorHeight(x);

        vy += 0.6;
        y += vy;

        if (y >= ground) {
            y = (int) ground;
            vy = 0;
            onGround = true;
        } else {
            onGround = false;
        }
        if (vx > 0.1) {
            facingRight = true;
        } else if (vx < -0.1) {
            facingRight = false;
        }
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
        vy = 0;
        onGround = true;
        jumping = false;
        boost = 100;
    }

    /**
     * Draw car
     */
    public void draw(Graphics2D g2) {
        int width = 120;
        int height = 70;
        if (facingRight) {
            g2.drawImage(carImage, x, y - 20, width, height, null);
        } else {
            g2.drawImage(carImage, x + width, y - 20, -width, height, null);
        }
    }

    public void jump() {
        if (onGround) {
            vy = -12;   // jump strength
            onGround = false;
            jumping = true;
        }
    }
    public void boost() {
    	if(boost > minboost) {
    		vx *= 1.25;
    		boost -= boostcost;
    	}
    }
}