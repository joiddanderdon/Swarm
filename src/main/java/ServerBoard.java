import com.google.gson.Gson;
import javax.json.JsonArray;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ServerBoardResource {
	private BoardSlot[][] boardSlots;
	
	public ServerBoardResource() {
		int size = 800;
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
	@Produces(MediaType.APPLICATION_JSON)
	public String getBoard() {
		return (new Gson()).toJson(this.boardSlots);
	}
	
	public boolean addZombie() {
		boardSlots[0][0].addToSlot(new Zombie());
		return true;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String updatePlayerPos(String name, int startX, int startY, int finishX, int finishY) {
		if (boardSlots[startX][startY].isPresent(name)) {
			//Get a reference to the sprite to be moved
			Sprite movedSprite = boardSlots[startX][startY].getSprite(name);
			//Remove sprite from current slot
			boardSlots[startX][startY].removeSprite(movedSprite);
			//Place sprite into new slot.
			boardSlots[finishX][finishY].addToSlot(movedSprite);
		}
		
		
		return getBoard();
	}
}