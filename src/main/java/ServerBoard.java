import com.google.gson.Gson;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class ServerBoard extends Application {
	private BoardSlot[][] boardSlots;
	
	public ServerBoard(int size) {
		boardSlots = new BoardSlot[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				boardSlots[i][j] = new BoardSlot();
			}
		}
		//For testing
		addZombie();
	}
	//This may work.
	@GET
	public String getBoard() {
		return (new Gson()).toJson(this.boardSlots);
	}
	
	public boolean addZombie() {
		boardSlots[0][0].addToSlot(new Zombie());
		return true;
	}
}