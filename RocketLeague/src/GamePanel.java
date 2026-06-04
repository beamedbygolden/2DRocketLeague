import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.QuadCurve2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.Delayed;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Component;

public class GamePanel extends JPanel implements KeyListener {
    Car p1; // player 1 car 
    Car p2; // player 2 car
    
    //ai car
    CarAI aiController = new CarAI();
    public static boolean AI_MODE = false;
    public static String AI_DIFFICULTY = "EASY";
    
    //goal scored boolean
    private boolean goalscored = false;
    
    //image for background of game might add more later 
    Image arenaBG;
    
    // image for ball
    Image ballImg;
    
    // Controller inputs
    private Controller gamepad1 = null;
    private Controller gamepad2 = null;
    
    // player scores
    int scoreP1 = 0;
    int scoreP2 = 0;
    
    // Field parameters
    final int LEFT = 50; 
    final int RIGHT = 1260; 
    final int TOP = 100; 
    final int BOTTOM = 620;
    
    // Goal parameters ( for map I want to use)
    final int GOAL_TOP = 170; 
    final int GOAL_BOTTOM = 380;
    final int LEFT_GOAL_X = 100;
    final int RIGHT_GOAL_X = 1200;
    
    // Ball parameters
    double ballX = 650;
    double ballY = 620;
    double ballVX = 0;
    double ballVY = 0;
    
    // ball start position
    final int BALL_SPAWN_X = 650;
    final int BALL_SPAWN_Y = 620;
    
    // boost 
    int boost1 = 100;
    int boost2 = 100;
    
    // time interval of frames 
    int delaymax = 4000;
    int delay = 4000;
    int Gametimer = 100000;
    int totaltime = 100000;
    
    //max ball speed (for ramps)
    int maxballv = 1;
    
    // keyboard state tracking ( for some reason keyboard didnt work while controller worked)
    private boolean kb_p1_left, kb_p1_right;
    private boolean kb_p2_left, kb_p2_right;

    
    // ramp arraylist for coordinates
    ArrayList<Slope> slopes = new ArrayList<>();
    

    /**
     * Constructor
     */
    public GamePanel() {
    	//background image
    	arenaBG = new ImageIcon("Arena1.png").getImage(); 
    	ballImg = new ImageIcon("ballimg.png").getImage();
    	int p1Ground = (int) getFloorHeight(300);
    	int p2Ground = (int) getFloorHeight(900); // ground level for cars

        p1 = new Car(300,p1Ground, p1Ground, Color.BLUE,"boost.png", "bluecar.png", this); //player 1 car
        p2 = new Car(900, p2Ground, p2Ground, Color.ORANGE,"boost.png", "orangecar.png", this); //player 2 car
        p1.facingRight = true;
        p2.facingRight = false;
        
        // ramp coordinates
        //right ramp
        slopes.add(new Slope(1120, 580, 1190, 550));
        slopes.add(new Slope(1190, 550, 1220, 390));
        //left ramp
        slopes.add(new Slope(180, 580, 110, 550));
        slopes.add(new Slope(110, 550, 80, 390));
        
        setFocusable(true); // sets frame on game 
        addKeyListener(this); // checks for key inputs

        // Initialize Controller
        initController();

        Timer timer = new Timer(16, totaltime -> { // game loop
            updateGame();
            repaint();
            delay+=16;
            Gametimer-= 16;
        });

        timer.start();
    }

    /**
     * Finds and initializes a connected controller
     * used jinput plugin library (very complex but used ai to understand library plugin and used jinput guide for methods)
     */
    private void initController() {
        System.setProperty("net.java.games.input.librarypath", System.getProperty("user.dir"));
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        System.out.println("Total controllers found: " + controllers.length);

        int found = 0;
        for (Controller c : controllers) {
            System.out.println("  -> " + c.getName() + " | type: " + c.getType());
            if (c.getType() == Controller.Type.GAMEPAD || c.getType() == Controller.Type.STICK) {
                if (found == 0) {
                    gamepad1 = c;
                    System.out.println("Player 1 Controller: " + c.getName());
                    found++;
                } else if (found == 1) {
                    gamepad2 = c;
                    System.out.println("Player 2 Controller: " + c.getName());
                    break;
                }
            }
        }

        System.out.println("P1 = " + gamepad1);
        System.out.println("P2 = " + gamepad2);
    }
    
    private void pollControllerInputs() {
        if (delay < delaymax) return;

        p1.boosting = false;
        p2.boosting = false;

        if (gamepad1 != null) {
            gamepad1.poll();
            double stickX1 = 0;
            for (Component c : gamepad1.getComponents()) {
                if (c.getIdentifier() == Component.Identifier.Axis.X) {
                    stickX1 = c.getPollData();
                    if (stickX1 < -0.2) {
                        p1.facingRight = false;
                        p1.left = true;
                        p1.right = false;
                    } else if (stickX1 > 0.2) {
                        p1.facingRight = true;
                        p1.right = true;
                        p1.left = false;
                    } else {
                        p1.left = false;
                        p1.right = false;
                    }
                }
                // R2 = accelerate, L2 = brake
                if (c.getIdentifier() == Component.Identifier.Axis.RY) {
                    double trigger = c.getPollData();
                    if (trigger > 0.1) {
                        p1.boosting = false;       // R2 is driving, not boosting
                        p1.driveForward(trigger);
                    } else if (trigger < -0.1) {
                        p1.brake(trigger);         // L2
                    }
                }
                // R1 = turn/angle right
                if (c.getIdentifier() == Component.Identifier.Button._5) {
                    if (c.getPollData() > 0) p1.turnRight();
                }
                // L1 = turn/angle left
                if (c.getIdentifier() == Component.Identifier.Button._4) {
                    if (c.getPollData() > 0) p1.turnLeft();
                }

                // circle/square = boost
                if (c.getIdentifier() == Component.Identifier.Button._2) {
                    if (c.getPollData() > 0) {
                        p1.boosting = true;
                        p1.boost();
                    }
                }

                // cross = jump
                if (c.getIdentifier() == Component.Identifier.Button._1) {
                    if (c.getPollData() > 0) p1.jump();
                }
            }
            p1.tilt(stickX1); // apply tilt once per frame after reading stick
        }

        if (gamepad2 != null) {
            gamepad2.poll();
            double stickX2 = 0;
            for (Component c : gamepad2.getComponents()) {
                if (c.getIdentifier() == Component.Identifier.Axis.RY) {
                    double trigger = c.getPollData();
                    if (trigger > 0.1) {
                        p2.boosting = false;
                        p2.driveForward(trigger);
                    } else if (trigger < -0.1) {
                        p2.brake(trigger);
                    }
                }
                
                if (c.getIdentifier() == Component.Identifier.Axis.X) {
                    stickX2 = c.getPollData();
                    if (stickX2 < -0.2) {
                        p2.facingRight = false;
                        p2.left = true;
                        p2.right = false;
                    } else if (stickX2 > 0.2) {
                        p2.facingRight = true;
                        p2.right = true;
                        p2.left = false;
                    } else {
                        p2.left = false;
                        p2.right = false;
                    }
                }
                
                // R1 = turn/angle right
                if (c.getIdentifier() == Component.Identifier.Button._5) {
                    if (c.getPollData() > 0)
                    	p2.turnRight();
                }
                // L1 = turn/angle left
                if (c.getIdentifier() == Component.Identifier.Button._4) {
                    if (c.getPollData() > 0) 
                    	p2.turnLeft();
                }

                if (c.getIdentifier() == Component.Identifier.Button._2) {
                    if (c.getPollData() > 0) {
                        p2.boosting = true;
                        p2.boost();
                    }
                }
                if (c.getIdentifier() == Component.Identifier.Button._1) {
                    if (c.getPollData() > 0) 
                    	p2.jump();
                }
            }
            p2.tilt(stickX2);
        }
    }

    /**
     * update game logic
     */
    public void updateGame() {
    	 if (gamepad1 == null) {
    	        p1.left  = kb_p1_left;
    	        p1.right = kb_p1_right;
    	        if (kb_p1_left)  p1.facingRight = false;
    	        if (kb_p1_right) p1.facingRight = true;
    	    }
    	    if (gamepad2 == null) {
    	        p2.left  = kb_p2_left;
    	        p2.right = kb_p2_right;
    	        if (kb_p2_left)  p2.facingRight = false;
    	        if (kb_p2_right) p2.facingRight = true;
    	    }
        checkGoal();
        if (AI_MODE) {

            if (delay >= 4000) {
                aiController.update(p2, ballX, ballY, delay);
            } else {
                p2.left = false;
                p2.right = false;
            }

        }// else {
         //   p2.update();
        //}
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
        double ground = getFloorHeight(ballX);
        if (ballY > ground - 20) {
            ballY = ground - 20;
            ballVY *= -0.8;
            ballVX *= .6;
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
        ballX = BALL_SPAWN_X;
        ballY = BALL_SPAWN_Y;
        ballVX = 0;
        ballVY = 0;
    }
    // ramps 
    class Slope {
        int xStart, yStart, xEnd, yEnd;
        public Slope(int xStart, int yStart, int xEnd, int yEnd) {
            this.xStart = xStart;
            this.yStart = yStart;
            this.xEnd = xEnd;
            this.yEnd = yEnd;
        }
    }
    
    public double getFloorHeight(double x) {
        for (Slope s : slopes) {
            if (x >= Math.min(s.xStart, s.xEnd) && x <= Math.max(s.xStart, s.xEnd)) { // if the ball is on the ramp
                double progress = (x - s.xStart) / (double)(s.xEnd - s.xStart); // how far the ball actually is on the ramp
                return s.yStart + progress * (s.yEnd - s.yStart);  // gets floor height at given point 
            }
        }

        return 550; // flat ground
    }

    /**
     * Keeps car inside the field 
     */
    public void clamp(Car c) {
        if (c.x < LEFT) c.x = LEFT;
        if (c.x > RIGHT - 120) c.x = RIGHT - 120;
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
            double impact = Math.abs(c.vx) * 1 + 5;

            ballVX += nx * impact;
            ballVY += ny * impact;

            // prevent sticking
            ballX += nx * 5;
            ballY += ny * 5;
        }
    }
    public void resetplayers() {
        p1.reset();
        p2.reset();
        p1.vx = 0;
        p2.vx = 0;
    }
    public void checkGoal() {
        // Left goal (Player 2 scores)
        if (ballX < LEFT_GOAL_X && ballY > GOAL_TOP && ballY < GOAL_BOTTOM) {
            scoreP2++;
            resetBall();
            resetplayers();
            goalscored = true;
            delay = 0;
        }

        // Right goal (Player 1 scores)
        if (ballX > RIGHT_GOAL_X && ballY > GOAL_TOP && ballY < GOAL_BOTTOM) {
            scoreP1++;
            resetBall();
            resetplayers();
            goalscored = true;
            delay = 0;
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

        // Field 
      // g2.setColor(new Color(0, 255, 120, 30));
      // g2.fillRect(LEFT, TOP, RIGHT - LEFT, BOTTOM - TOP);

        //Right Ramp
        //int[] x = { 1120, 1190, 1220};
        //int[] y = { 580, 550, 390};

        g2.setColor(Color.ORANGE);
        g2.setStroke(new BasicStroke(20)); // changes thickness of ramp (just used for tweaking ramp zones right now) 

     // for (int i = 0; i < x.length - 1; i++) { 
         // g2.drawLine(x[i], y[i], x[i + 1], y[i + 1]); //  uses x array and y array to create a curve
     // }
     //Left Ramp
        //int[] xLeft = { 180, 110, 80};
        //int[] yLeft = { 580, 550, 390};

        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(20)); // ramp thickness

        //for (int i = 0; i < xLeft.length - 1; i++) {
           // g2.drawLine(xLeft[i], yLeft[i],xLeft[i + 1], yLeft[i + 1]);
        //}
        
        // Score Board
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.drawString("BLUE: " + scoreP1, 300, 50);
        g2.drawString("ORANGE: " + scoreP2, 800, 50);
       if(delay < delaymax) {
    	   g2.drawString("Timer: " + (int) (4 -(delay/1000)), 600, 50);
       } else {
    	   int totalSeconds = (int)(Gametimer / 1000);
    	   int minutes = totalSeconds / 60;
    	   int seconds = totalSeconds % 60;
    	   g2.drawString("Timer: " + minutes + ":" + String.format("%02d", seconds), 600, 50);
       }
        
        // ball
       int size = 60;
       g2.drawImage(ballImg, (int) ballX - size / 2, (int) ballY - size / 2, size, size, null);
        
       // goal zones
       // g2.setColor(Color.red);
       // g2.fillRect(LEFT_GOAL_X, GOAL_TOP, 20, GOAL_BOTTOM - GOAL_TOP);
       //g2.setColor(new Color(255, 140, 0, 80));
       // g2.fillRect(RIGHT_GOAL_X, GOAL_TOP, 20, GOAL_BOTTOM - GOAL_TOP);
       
     //right ramp
       g2.drawLine(1120, 560, 1190, 550);// bottom line
       g2.drawLine(1190, 550, 1220, 390); // top line
       //left ramp
       g2.drawLine(180, 560, 110, 550); //bottom line
       g2.drawLine(110, 550, 80, 390); // top line
       
    // P1 boost bar (bottom left)
       g2.setColor(Color.DARK_GRAY);
       g2.fillRoundRect(60, 640, 200, 20, 10, 10);         // background
       g2.setColor(new Color(0, 180, 255));                  // blue fill
       g2.fillRoundRect(60, 640, (int)(200 * (p1.boost / 100.0)), 20, 10, 10);
       g2.setColor(Color.WHITE);
       g2.setFont(new Font("Arial", Font.BOLD, 14));
       g2.drawString("BOOST", 62, 658);

       // P2 boost bar (bottom right)
       g2.setColor(Color.DARK_GRAY);
       g2.fillRoundRect(1050, 640, 200, 20, 10, 10);        // background
       g2.setColor(new Color(255, 140, 0));                  // orange fill
       g2.fillRoundRect(1050, 640, (int)(200 * (p2.boost / 100.0)), 20, 10, 10);
       g2.setColor(Color.WHITE);
       g2.drawString("BOOST", 1052, 658);

        // Draw the players
        p1.draw(g2);
        p2.draw(g2);
    }

    // Key inputs (Hybrid control option remains active!)
    @Override
    public void keyPressed(KeyEvent e) {
    if (delay >= 4000) {
    	 if( gamepad1 == null) {
    	//player 1 keyinputs
        if (e.getKeyCode() == KeyEvent.VK_A) 
        	kb_p1_left = true;
        if (e.getKeyCode() == KeyEvent.VK_D)
        	kb_p1_right = true;
        if (e.getKeyCode() == KeyEvent.VK_W)
        	p1.jump();
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
        	p1.boosting = true;
        	p1.boost();
        }
        }
     if(gamepad2 == null) {
        //player 2 key inputs
        if (e.getKeyCode() == KeyEvent.VK_LEFT) 
        	 kb_p2_left = true;
        if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        	kb_p2_right = true;
        if (e.getKeyCode() == KeyEvent.VK_UP)
            p2.jump();
        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
            p2.boosting = true;
            p2.boost();
        }
        }
    }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    	//player 1 key inputs
    	  if (e.getKeyCode() == KeyEvent.VK_A) 
    		  kb_p1_left = false;
          if (e.getKeyCode() == KeyEvent.VK_D)
        	  kb_p1_right = false;
          //player 2 key inputs
          if (e.getKeyCode() == KeyEvent.VK_LEFT) 
        	  kb_p2_left = false;
          if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        	  kb_p2_right = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}