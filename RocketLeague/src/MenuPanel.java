import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPanel extends JPanel implements MouseListener {

    Image bg;

    // invisible button hitboxes
    Rectangle playButton = new Rectangle(845, 580, 300, 160); 
    Rectangle infoButton = new Rectangle(100, 580, 350, 160);

    public MenuPanel() {
        bg = new ImageIcon("RLStartingScreen.png").getImage(); // starting screen photo
        addMouseListener(this); // mouse inputs
        setFocusable(true); // focus on tab
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // draw menu image
        g2.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
        
        //dummy testers
        //g2.fillRect(playButton.x, playButton.y, playButton.width, playButton.height);
        //g2.fillRect(infoButton.x, infoButton.y, infoButton.width, infoButton.height);
    }

    @Override
    public void mousePressed(MouseEvent e) {

        Point p = e.getPoint();

        if (playButton.contains(p)) {
            System.out.println("Play clicked");

            // switch to game
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            GamePanel game = new GamePanel();
            frame.setContentPane(game);
            frame.revalidate();
            frame.repaint();
            game.requestFocusInWindow();
        }
        
        if (infoButton.contains(p)) {
            JOptionPane.showMessageDialog(
                this,
                "CONTROLLER SETUP INFO\n\n" +
                "• Controllers only work using jinput" +
                "PLAYER 1:\n" +
                "• Use WASD on keyboard\n\n" +
                "PLAYER 2:\n" +
                "• Use Arrow Keys\n\n" +
                "CONTROLLER SETUP:\n" +
                "• Left stick = movement\n" +
                "• R2/L2 = boost (optional)\n\n",
                
                "Controls Info",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // unused mouse methods
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}