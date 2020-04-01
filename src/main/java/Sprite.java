
import javafx.scene.paint.Color;

public class Sprite {
	private boolean isAlive;
	private Color color;
	private int x;
	private int y;
	
	
	
	public Sprite() {
		isAlive = true;
	}
	
	public void kill() {
		isAlive = false;
	}
	public boolean isAlive() {
		return this.isAlive;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
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
}
