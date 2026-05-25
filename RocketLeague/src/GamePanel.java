import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.QuadCurve2D;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Component;

public class GamePanel extends JPanel implements KeyListener {
    Car p1; // player 1 car 
    Car p2; // player 2 car
    
    //image for background of game might add more later 
    Image arenaBG;
    
    // Controller inputs
    private Controller gamepad = null;
    
    // player scores
    int scoreP1 = 0;
    int scoreP2 = 0;
    
    // Field parameters
    final int LEFT = 100; 
    final int RIGHT = 1230; 
    final int TOP = 0; 
    final int BOTTOM = 590;
    
    // Goal parameters
    final int GOAL_TOP = 170; 
    final int GOAL_BOTTOM = 380;
    final int LEFT_GOAL_X = 0;
    final int RIGHT_GOAL_X = 1280;
    
    // Ball parameters
    double ballX = 650;
    double ballY = 450;
    double ballVX = 0;
    double ballVY = 0;
    
    // boost 
    int boost1 = 100;
    int boost2 = 100;

    /**
     * Constructor
     */
    public GamePanel() {
    	//background image
    	arenaBG = new ImageIcon("Arena1.png").getImage(); 
    	
        int ground = 550; // ground level for cars

        p1 = new Car(300, ground, ground, Color.BLUE); // creates player 1 car
        p2 = new Car(900, ground, ground, Color.ORANGE); // creates player 2 car

        setFocusable(true); // sets frame on game 
        addKeyListener(this); // checks for key inputs

        // Initialize Controller
        initController();

        Timer timer = new Timer(16, e -> { // game loop
            updateGame();
            repaint();
        });

        timer.start();
    }

    /**
     * Finds and initializes a connected controller
     * used jinput plugin library (very complex but used ai to understand library plugin and used jinput guide for methods)
     */
    private void initController() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers(); // gets controllers for game ( used jinput guide for this)

        for (Controller c : controllers) {
            if (c.getType() == Controller.Type.GAMEPAD ||  c.getType() == Controller.Type.STICK) {
                gamepad = c;
                System.out.println("Connected: " + c.getName());
                return;
            }
        }

        System.out.println("No controller found.");
    }

    /**
     * Reads analog stick status from the gamepad and updates player controls
     */
    private void pollControllerInputs() {
        if (gamepad == null) {
            return;
        }
        gamepad.poll();
        for (Component c : gamepad.getComponents()) {
            if (c.getName().equals("X Axis")) {
                double x = c.getPollData();
                if (x < -0.2) {
                    p1.left = true;
                } else {
                    p1.left = false;
                }
                if (x > 0.2) {
                    p1.right = true;
                } else {
                    p1.right = false;
                }	
            }
        }
    }
    /**
     * UPDATE GAME LOGIC
     */
    public void updateGame() {
        checkGoal();
        
        // Read gamepad movements before car updates positions
        pollControllerInputs();
        
        // update players
        p1.update();
        p2.update();
        
        // checks for car hitting ball
        handleCollision(p1);
        handleCollision(p2);
        
        // update ball physics
        updateBall();

        // keep inside field
        clamp(p1);
        clamp(p2);
    }
    
    public void updateBall() {
        double gravity = 0.15;
        // gravity pulls ball down
        ballVY += gravity;

        // apply movement
        ballX += ballVX;
        ballY += ballVY;

        // friction
        ballVX *= 0.98;
        ballVY *= 0.98;

        // ground bounce
        if (ballY > BOTTOM - 20) {
            ballY = BOTTOM - 20;
            ballVY *= -0.6;
        }

        // ceiling
        if (ballY < TOP) {
            ballY = TOP;
            ballVY *= -0.6;
        }

        // walls
        if (ballX < LEFT) {
            ballX = LEFT;
            ballVX *= -0.6;
        }

        if (ballX > RIGHT) {
            ballX = RIGHT;
            ballVX *= -0.6;
        }
    }
    
    /**
    * Resets ball after goals
    */
    public void resetBall() {
        ballX = 650;
        ballY = 300;
        ballVX = 0;
        ballVY = 0;
    }

    /**
     * Keeps car inside the field 
     */
    public void clamp(Car c) {
        if (c.x < LEFT) c.x = LEFT;
        if (c.x > RIGHT - 120) c.x = RIGHT - 120;

        // lock car to ground system
        if (c.y > c.groundY) c.y = c.groundY;
    }
    
    public void handleCollision(Car c) {
        // center of car
        double carCenterX = c.x + 60;
        double carCenterY = c.y + 20;

        // direction from car -> ball
        double dx = ballX - carCenterX;
        double dy = ballY - carCenterY;

        double dist = Math.sqrt(dx * dx + dy * dy);

        // collision radius
        double radius = 70;

        if (dist < radius) {
            // normalize
            double nx = dx / dist;
            double ny = dy / dist;

            // hit strength
            double impact = Math.abs(c.vx) * 0.8 + 5;

            ballVX += nx * impact;
            ballVY += ny * impact;

            // prevent sticking
            ballX += nx * 5;
            ballY += ny * 5;
        }
    }
    
    public void checkGoal() {
        // Left goal (Player 2 scores)
        if (ballX < LEFT_GOAL_X && ballY > GOAL_TOP && ballY < GOAL_BOTTOM) {
            scoreP2++;
            resetBall();
        }

        // Right goal (Player 1 scores)
        if (ballX > RIGHT_GOAL_X && ballY > GOAL_TOP && ballY < GOAL_BOTTOM) {
            scoreP1++;
            resetBall();
        }
    }

    /**
     * Draws field, background, and players 
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // drawing background image of arena
        g.drawImage(arenaBG, 0, 0, getWidth(), getHeight(), this);
        // changed into graphics g2 so I can rotate care and use other additional methods
        Graphics2D g2 = (Graphics2D) g;

        // smooth graphics
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background 
       // g2.setColor(new Color(30, 30, 40));
       // g2.fillRect(0, 0, getWidth(), getHeight()); 
        
        // Field 
        g2.setColor(new Color(0, 255, 120, 30));
        g2.fillRect(LEFT, TOP, RIGHT - LEFT, BOTTOM - TOP);

        //Right Ramp
        int[] x = { 1120, 1190, 1220};
        int[] y = { 580, 550, 390};

        g2.setColor(Color.ORANGE);
        g2.setStroke(new BasicStroke(20)); // changes thickness of ramp ( just used for tweaking ramp zones right now) 

        for (int i = 0; i < x.length - 1; i++) { 
            g2.drawLine(x[i], y[i], x[i + 1], y[i + 1]); //  uses x array and y array to create a curve
        }
     // Left Ramp
        int[] xLeft = { 180, 110, 80};
        int[] yLeft = { 580, 550, 390};

        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(20)); // ramp thickness

        for (int i = 0; i < xLeft.length - 1; i++) {
            g2.drawLine(
                xLeft[i], yLeft[i],
                xLeft[i + 1], yLeft[i + 1]
            );
        }
        
        // Score Board
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.drawString("BLUE: " + scoreP1, 500, 50);
        g2.drawString("ORANGE: " + scoreP2, 700, 50);
        
        // ball
        int ballSize = 35;
        g2.setColor(Color.WHITE);
        g2.fillOval((int) ballX - ballSize / 2, (int) ballY - ballSize / 2, ballSize, ballSize);
        g2.setColor(Color.BLACK);
        g2.drawOval((int) ballX - ballSize / 2, (int) ballY - ballSize / 2, ballSize, ballSize);
        
        // goal zones
        g2.setColor(Color.red);
        g2.fillRect(120, GOAL_TOP, 20, GOAL_BOTTOM - GOAL_TOP);
        g2.setColor(new Color(255, 140, 0, 80));
        g2.fillRect(1180, GOAL_TOP, 20, GOAL_BOTTOM - GOAL_TOP);

        // Draw the players
        p1.draw(g2);
        p2.draw(g2);
    }

    // Key inputs (Hybrid control option remains active!)
    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_A) 
            p1.left = true;
        if (e.getKeyCode() == KeyEvent.VK_D)
            p1.right = true;
        if (e.getKeyCode() == KeyEvent.VK_LEFT) 
            p2.left = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
            p2.right = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	if (e.getKeyCode() == KeyEvent.VK_A) 
            p1.left = false;
        if (e.getKeyCode() == KeyEvent.VK_D) 
            p1.right = false;
        if (e.getKeyCode() == KeyEvent.VK_LEFT) 
            p2.left = false;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) 
            p2.right = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
