public class CarAI {

	public void update(Car ai, double ballX, double ballY, int delay) {

	    if (delay < 4000) return;

	    double speed = 0.2;

	    if (GamePanel.AI_DIFFICULTY.equals("MEDIUM")) {
	        speed = 0.5;
	    }

	    if (GamePanel.AI_DIFFICULTY.equals("HARD")) {
	        speed = .8;
	    }

	    if (ballX < ai.x - 30) {
	        ai.vx -= speed;
	    }

	    if (ballX > ai.x + 30) {
	        ai.vx += speed;
	    }

	    if (ballY < ai.y - 50) {
	        ai.jump();
	    }
	}
}