//Gonna need to insert rest API tags
import com.google.gson.Gson;

public class ServerBoard {
	private BoardSlot[][] boardSlots;
	
	public ServerBoard(int size) {
		boardSlots = new BoardSlot[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				boardSlots[i][j] = new BoardSlot();
			}
		}
	}
	//This may work.
	public String getBoard() {
		return (new Gson()).toJson(this.boardSlots);
	}
	
	public boolean addZombie() {
		boardSlots[0][0].addToSlot(new Zombie());
		return true;
	}
}