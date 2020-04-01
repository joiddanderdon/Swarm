
public class Player extends Sprite {
	public String name;
	public final int MOVESPEED = 3;
	private long[] aimAt = {0,0};
	public Player() {
		this("", 100,100);
	}
	public Player(String name) {
		this(name, 100, 100);
	}
	public Player(int x, int y) {
		this("", x, y);
	}
	public Player(String name, int x, int y) {
		this.name = name;
		this.setX(x);
		this.setY(y);
	}
	public void moveRight() {
		this.setX(this.getX() + MOVESPEED);
	}
	public void moveLeft() {
		this.setX(this.getX() - MOVESPEED);
	}
	public void moveUp() {
		this.setY(this.getY() - MOVESPEED);
	}
	public void moveDown() {
		this.setY(this.getY() + MOVESPEED);
	}
	public String getName() {
		return this.name;
	}
	public void setAim(long x, long y) {
		this.aimAt[0] = x;
		this.aimAt[1] = y;
	}
	public long[] getAim() {
		return aimAt;
	}
	
}