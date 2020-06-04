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
	private static final SecureRandom RNG = new SecureRandom();
	private static int idCount = 0;
	
	//Create Zombie from scratch
	public Zombie() {
		String id = "z" + (++idCount);
		
		switch(RNG.nextInt(4)) {
			case 0:
				this.setX(0);
				this.setY(0);
				this.setTarget(600,600);
				break;
			case 1:
				this.setX(0);
				this.setY(600);
				this.setTarget(600, 0);
				break;
			case 2:
				this.setX(600);
				this.setY(0);
				this.setTarget(0, 600);
				break;
			case 3:
				this.setX(600);
				this.setY(600);
				this.setTarget(0, 0);
			}
		this.setId(id);
		this.speed = RNG.nextInt(7) + 3;
	}
	//Reload from DB
	public Zombie(String id, int x, int y, int xTarg, int yTarg, int speed) {
		this.setX(x);
		this.setY(y);
		this.setTarget(xTarg, yTarg);
		this.speed = speed;
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
	public void stalk() {
		if (this.getX()==this.getTargetX() && this.getY()==this.getTargetY()) {
			if (this.getX() < 300) this.targetX = 600;
			else this.targetX = 0;
			
			if (this.getY() < 300) this.targetY = 600;
			else this.targetY = 0;
		}
		if (targetX < this.getX()) {
			if (this.getX() - speed < this.targetX) this.setX(targetX);
			else this.setX(this.getX() - speed);
		}
		else if (targetX > this.getX()) {
			if (this.getX() + speed > this.targetX) this.setX(targetX); 
			else this.setX(this.getX() + speed);
		}
		
		if (targetY < this.getY()) {
			if (this.getY() - speed < this.targetY) this.setY(targetY);
			else this.setY(this.getY() - speed);
		}
		else if (targetY > this.getY()) {
			if (this.getY() + speed > this.targetY) this.setY(targetY);
			else this.setY(this.getY() + speed);
		}
	
		
		//Keep zombies within board parameter
		if (this.getX() < 0) this.setX(0);
		if (this.getY() < 0) this.setY(0);
		if (this.getX() > 600) this.setX(600);
		if (this.getY() > 600) this.setY(600);
		
	}
	public void setTarget(int x, int y) {
		targetX = x;
		targetY = y;
	}
	
}
