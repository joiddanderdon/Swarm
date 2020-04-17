
import java.security.SecureRandom;

public class Zombie extends Sprite {
	private int speed;
	private Player currentTarget;
	private final SecureRandom RNG;
	//Not sure how much of a performance hit is taken by keeping the SecureRandom as an instance variable
	private final int RANDOMNESS;
	private static int idCount = 0;
	
	//Create Zombie from scratch
	public Zombie() {
		RNG = new SecureRandom();
		RANDOMNESS = RNG.nextInt(5) + 1;
		this.setX(5);
		this.setY(5);
		this.speed = RNG.nextInt(4) + 1;
		this.setId("z" + (++idCount));
	}
	//Reload from DB
	public Zombie(String id, int x, int y) {
		RNG = new SecureRandom();
		RANDOMNESS = RNG.nextInt(5) + 1;
		this.setX(x);
		this.setY(y);
		this.speed = RNG.nextInt(4) + 1;
		this.setId(id);
	}
	public int getSpeed() {
		return speed;
	}
	public Player getTarget() {
		return currentTarget;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public void stalk() {
		
		if (RNG.nextInt(RANDOMNESS) <= 2) {
			if (currentTarget.getX() < this.getX()) {
				this.setX(this.getX() - speed);
			}
			else if (currentTarget.getX() > this.getX()) {
				this.setX(this.getX() + speed);
			}
			
			if (currentTarget.getY() < this.getY()) {
				this.setY(this.getY() - speed);
			}
			else if (currentTarget.getY() > this.getY()) {
				this.setY(this.getY() + speed);
			}
		}
		else {
			switch (RNG.nextInt(4)) {
			case 0:
				this.setX(this.getX()+speed);
				break;
			case 1:
				this.setX(this.getX()-speed);
				break;
			case 2:
				this.setY(this.getY()+speed);
				break;
			case 3:
				this.setY(this.getY()-speed);
				break;
			}
		}
	}
	public void setTarget(Player p) {
		currentTarget = p;
	}
	
}
