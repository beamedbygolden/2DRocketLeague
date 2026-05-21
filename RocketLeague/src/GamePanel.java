import javax.swing.*;
import javax.swing.text.TabStop;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements KeyListener {
    Car p1; // player 1 car 
    Car p2; // player 2 car
    
    //player scores
	int scoreP1 = 0;
	int scoreP2 = 0;
	
	//Field parameters
    final int LEFT = 120; 
    final int RIGHT = 1180; 
    final int TOP = 110; 
    final int BOTTOM = 490;
    //Goal parameters
    final int GOAL_TOP = 250; 
	final int GOAL_BOTTOM = 400;
	final int LEFT_GOAL_X = 120;
	final int RIGHT_GOAL_X = 1180;
	// Ball parameters
	double ballX = 650;
	double ballY = 450;
	double ballVX = 0;
	double ballVY = 0;

    /**
     * Constructor
     */
    public GamePanel() {
	 int ground = 420; // ground level for cars

        p1 = new Car(300, ground, ground, Color.BLUE); // creates player 1 car
        p2 = new Car(900, ground, ground, Color.ORANGE); // creates player 2 car

       
        setFocusable(true); // sets frame on game 
        addKeyListener(this); // checks for key inputs


        Timer timer = new Timer(16, e -> { // game loop
            updateGame();
            repaint();
        });

        timer.start();
    }

    /**
     * UPDATE GAME LOGIC
     */
    public void updateGame() {
    	checkGoal();

        // update players
        p1.update();
        p2.update();
        
        // checks for car hitting ball
        handleCollision(p1);
		handleCollision(p2);
        
        // update ball physic
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

    double dx = ballX - c.x;
    double dy = ballY - c.y;

    double dist = Math.sqrt(dx * dx + dy * dy);

    if (dist < 60) {

        // normalize direction
        double nx = dx / dist;
        double ny = dy / dist;

        // apply consistent force
        double power = 6;

        ballVX += nx * power;
        ballVY += ny * power;
    }
}
    
    public void checkGoal() {
    // Left goal (Player 2 scores)
    if (ballX < LEFT_GOAL_X &&
        ballY > GOAL_TOP &&
        ballY < GOAL_BOTTOM) {

        scoreP2++;
        resetBall();
    }

    //
    // Right goal (Player 1 scores)
    // 
    if (ballX > RIGHT_GOAL_X &&
        ballY > GOAL_TOP &&
        ballY < GOAL_BOTTOM) {

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

        Graphics2D g2 = (Graphics2D) g;

        // smooth graphics (60 fps) // took from past codes but was initially explained by ai
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background 
        g2.setColor(new Color(30, 30, 40));
        g2.fillRect(0, 0, getWidth(), getHeight()); 
        
        // Field 
        g2.setColor(new Color(0, 255, 120, 30));
        g2.fillRect(LEFT, TOP, RIGHT - LEFT, BOTTOM - TOP);
        
        //Score Board
        g2.setColor(Color.WHITE);
		g2.setFont(new Font("Arial", Font.BOLD, 30));
		g2.drawString("BLUE: " + scoreP1, 500, 50);
		g2.drawString("ORANGE: " + scoreP2, 700, 50);
		
		//ball
		int ballSize = 35;
		g2.setColor(Color.WHITE);
		g2.fillOval((int) ballX - ballSize / 2, (int) ballY - ballSize / 2, ballSize, ballSize);
		g2.setColor(Color.BLACK);
		g2.drawOval((int) ballX - ballSize / 2, (int) ballY - ballSize / 2, ballSize, ballSize);
		
		//goal zones
		g2.setColor(new Color(0, 140, 255, 80));
		g2.fillRect(120, GOAL_TOP, 20, GOAL_BOTTOM - GOAL_TOP);
		g2.setColor(new Color(255, 140, 0, 80));
		g2.fillRect(1180, GOAL_TOP, 20, GOAL_BOTTOM - GOAL_TOP);


        // Draw the players
        p1.draw(g2);
        p2.draw(g2);
    }

    // Key inputs ( just keyboard for now)
    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {

            // Player 1 (WASD) to move
            case KeyEvent.VK_A -> p1.left = true;
            case KeyEvent.VK_D -> p1.right = true;

            // Player 2 (arrows) to move
            case KeyEvent.VK_LEFT -> p2.left = true;
            case KeyEvent.VK_RIGHT -> p2.right = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        switch (e.getKeyCode()) {

            // Player 1 
            case KeyEvent.VK_A -> p1.left = false;
            case KeyEvent.VK_D -> p1.right = false;

            // Player 2 
            case KeyEvent.VK_LEFT -> p2.left = false;
            case KeyEvent.VK_RIGHT -> p2.right = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}