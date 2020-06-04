import java.util.ArrayList;


public class TargetSetter implements Runnable {
	private Zombie self;
	private ArrayList<Player> players;
	
	public void run() {
		int currentDelta = Math.abs(self.getX()-self.getTargetX()) * Math.abs(self.getY()-self.getTargetY());
		int newDelta = 0;
		
		for (Player p: players) {
			newDelta = Math.abs(self.getX()-p.getX()) * Math.abs(self.getY()-p.getY());
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
