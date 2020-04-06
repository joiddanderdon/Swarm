

import java.util.LinkedList;
public class BoardSlot {
	private LinkedList<Sprite> slot;
	
	public BoardSlot() {
		slot = new LinkedList<Sprite>();
	}
	
	public LinkedList<Sprite> getSlot(){
		return this.slot;
	}
	
	public Sprite getSprite(String name) {
		for (Sprite s: slot) {
			if (s.name.equals(name)){
				return s;
			}
		}
		return null;
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
	public boolean isPresent(String name) {
		for (Sprite s: slot) {
			if (s.name == name) {
				return true;
			}
		}
		return false;
	}
}
