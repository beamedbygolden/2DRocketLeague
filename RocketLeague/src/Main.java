
import java.lang.foreign.Arena;

import javax.swing.JFrame;
public class Main {
   public static void main(String[] args) {
	   System.out.println(System.getProperty("java.version"));
       JFrame frame = new JFrame("2D Rocket League");
       GamePanel panel = new GamePanel();
       frame.add(panel);
       frame.setSize(1300,800);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       frame.setLocationRelativeTo(null);
       frame.setResizable(false);
       frame.setVisible(true);
   }
}
