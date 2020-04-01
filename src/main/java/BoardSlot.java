

import java.util.LinkedList;
public class BoardSlot {
	private LinkedList<Sprite> slot;
	
	public BoardSlot() {
		slot = new LinkedList<Sprite>();
	}
	
	public LinkedList<Sprite> getSlot(){
		return this.slot;
	}
	
	public void addToSlot(Sprite toAdd) {
		this.slot.push(toAdd);
	}
	
	public Sprite removeSprite(Sprite toRemove) {
		for (Sprite s: slot) {
			if (s.equals(toRemove)) {
				slot.remove(toRemove);
				return toRemove;
			}
		}
		return null;
	}
}
