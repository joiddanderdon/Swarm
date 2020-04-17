public class Sprite {
	private boolean isAlive;
	private String color;
	private int x;
	private int y;
	private String id;
	
	
	public Sprite() {
		isAlive = true;
	}
	
	public void kill() {
		isAlive = false;
	}
	public boolean isAlive() {
		return this.isAlive;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
