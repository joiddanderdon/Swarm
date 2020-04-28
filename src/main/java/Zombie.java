import java.security.SecureRandom;
/**
 * A zombie object that will stalk players.
 * 
 * @author Steve Cina
 * @since April 2020
 *
 */
public class Zombie extends Sprite {
	private int speed;
	private int targetX;
	private int targetY;
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
		this.setTarget(800, 800);
		this.speed = RNG.nextInt(4) + 1;
		this.setId("z" + (++idCount));
	}
	//Reload from DB
	public Zombie(String id, int x, int y, int xTarg, int yTarg) {
		RNG = new SecureRandom();
		RANDOMNESS = RNG.nextInt(5) + 1;
		this.setX(x);
		this.setY(y);
		this.setTarget(xTarg, yTarg);
		this.speed = RNG.nextInt(4) + 1;
		this.setId(id);
	}
	public int getSpeed() {
		return speed;
	}
	public int getTargetX() {
		return targetX;
	}
	public int getTargetY() {
		return targetY;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public void stalk() {
		
		if (RNG.nextInt(RANDOMNESS) <= 2) {
			if (targetX < this.getX()) {
				this.setX(this.getX() - speed);
			}
			else if (targetX > this.getX()) {
				this.setX(this.getX() + speed);
			}
			
			if (targetY < this.getY()) {
				this.setY(this.getY() - speed);
			}
			else if (targetY > this.getY()) {
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
		//Keep zombies within board parameter
		if (this.getX() < 0) this.setX(0);
		if (this.getY() < 0) this.setY(0);
		if (this.getX() > 800) this.setX(800);
		if (this.getY() > 800) this.setY(800);
		
	}
	public void setTarget(int x, int y) {
		targetX = x;
		targetY = y;
	}
	
}
