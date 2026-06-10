import java.awt.*;
import java.time.chrono.MinguoChronology;

import javax.swing.ImageIcon;

public class Car {
	Image carImage; // car photo
	Image boostImage; // boost image
	Image goalexplosion;
    int x, y; // position
    int speed = 6; // Movement speed
    int startx, starty;
    boolean left, right, up, down; // car movements
    Color color; // color
    int groundY; // ground (where car stays)
    int boost = 100;
    int minboost = 0;
    int boostcost = 1;
    double vx = 0;
    double vy = 0;
    boolean onGround = true;
    boolean jumping = false;
    GamePanel panel;
    boolean facingRight = true;
    boolean boosting = false;
    double angle = 0;
    /**
     * Constructor
     */
    public Car(int x, int y, int groundY, Color color,String boostimg, String imagePath,String goalexplo, GamePanel panel) {
        this.x = x;
        this.y = y;
        this.groundY = groundY;
        this.color = color;
        this.panel = panel;
        this.startx = x;
        this.starty = y;
        
        boostImage = new ImageIcon(boostimg).getImage();
        carImage = new ImageIcon(imagePath).getImage();
        goalexplosion = new ImageIcon(goalexplo).getImage();
    }
    
    public void tilt(double stickX1) {
        if (!onGround) {
            // free rotation in the air like rocket league
            angle += stickX1 * 3.0;  
            angle = Math.max(-90, Math.min(90, angle));
        } else {
            // on ground just tilt slightly, snap back to flat
            angle += stickX1 * 1.5;
            angle *= 0.85; // spring back toward 0
            angle = Math.max(-30, Math.min(30, angle));
        }
    }

    public void update() {
        // keyboard/stick horizontal movement
        if (left)  vx -= 0.8;
        if (right) vx += 0.8;

        vx *= 0.92;
        x += (int) vx;

        double ground = panel.getFloorHeight(x);

        vy += 0.6;
        y += vy;

        if (y >= ground) {
            y = (int) ground;
            vy = 0;
            onGround = true;
            angle *= 0.7; 
        } else {
            onGround = false;
        }

        // recharge boost
        if (!boosting && boost < 100 && onGround) {
            boost += 10; // change value depending on how competitive you want the game.
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

        var saved = g2.getTransform();

        // pivot rotation around the center of the car
        double pivotX = x + width / 2.0;
        double pivotY = y;
        g2.rotate(Math.toRadians(angle), pivotX, pivotY);
        if (facingRight) {
            g2.drawImage(carImage, x, y - 20, width, height, null);
            if (isBoosting()) {
                g2.drawImage(boostImage, x - 40, y - 10, 50, 40, null);
            }
        } else {
            g2.drawImage(carImage, x + width, y - 20, -width, height, null);
            if (isBoosting()) {
                g2.drawImage(boostImage, x + width, y - 10, 50, 40, null);
            }
        }

        g2.setTransform(saved); // restore so nothing else rotates
    }

    public void driveForward(double intensity) {
        double rad = Math.toRadians(angle);
        double thrust = intensity * 0.4;
        if (facingRight) {
            vx += Math.cos(rad) * thrust;
        } else {
            vx -= Math.cos(rad) * thrust;
        }
        vy += Math.sin(rad) * thrust;
    }

    public void brake(double intensity) {
        vx *= (1.0 + intensity * 0.1); // intensity is negative, so this slows vx
        if (onGround) vy = 0;
    }

    public void setAngle(double d) {
        this.angle = Math.max(-90, Math.min(90, d)); // clamp so you can't flip upside down only 90 degrees!
    }

    public void jump() {
        if (onGround) {
            vy = -12;   // jump strength
            onGround = false;
            jumping = true;
        }
    }
    
    // tells state of boosting
    public boolean isBoosting() {
        return boosting;
    }
    
   // public void drawexplo(Image ) {
    	
   // }
    
    // actually does the boosting
    public void boost() {
        if (boost > minboost) {
        	boosting = true;
            double rad = Math.toRadians(angle);
            if (facingRight) {
                vx += Math.cos(rad) * 1.2; // every tap of r1/l1 will return the car angle to a angle 20 percent higher
            } else {
                vx -= Math.cos(rad) * 1.2; // every tap of r1/l1 will return the car angle to a angle 20 percent higher
            }
            vy += Math.sin(rad) * 1.2;
            vy -= 0.5;
            boost -= boostcost;
        }
    }

    // faster turning with L1/R1
    public void turnLeft() {
        angle -= 6; 
        angle = Math.max(-90, Math.min(90, angle));
    }

    public void turnRight() {
        angle += 6;
        angle = Math.max(-90, Math.min(90, angle));
    }}