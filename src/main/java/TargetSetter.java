import java.util.ArrayList;


public class TargetSetter implements Runnable {
	private Zombie self;
	private ArrayList<Player> players;
	
	public void run() {
		int currentX = self.getX();
		int targetX = self.getTargetX();
		int currentY = self.getY();
		int targetY = self.getTargetY();
		int currentDelta = Math.abs(currentX-targetX) * Math.abs(currentY-targetY);
		int newDelta = 0;
		
		for (Player p: players) {
			newDelta = Math.abs(currentX-p.getX()) * Math.abs(currentY-p.getY());
			if (newDelta < currentDelta) {
				self.setTarget(p.getX(), p.getY());
			}
		}
		self.stalk();
	}
	
	public TargetSetter(Zombie self, ArrayList<Player> players) {
		this.self = self;
		this.players = players;
	}
	
	
}
