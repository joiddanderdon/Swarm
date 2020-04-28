/**
 * Sprite object representing a Player.
 * 
 * @author Steve Cina
 * @since April 2020
 *
 */
public class Player extends Sprite {
	public final int MOVESPEED = 3;
	private long[] aimAt = {0,0};
	
	public Player(String id, int x, int y) {
		this.setId(id);
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
		return this.getId();
	}
	public void setAim(long x, long y) {
		this.aimAt[0] = x;
		this.aimAt[1] = y;
	}
	public long[] getAim() {
		return aimAt;
	}
	
}