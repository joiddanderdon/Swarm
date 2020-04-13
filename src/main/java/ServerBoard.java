import com.google.gson.Gson;

import java.util.ArrayList;

import javax.json.JsonArray;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ServerBoard {
	//We'll need to keep a list of sprites, then in the Tick() method,
	//run the Stalk method on each Zombie contained within
	private static ArrayList<Sprite> sprites;
	
	private int tickCount;
	
	public ServerBoard() {
		
		
		sprites = new ArrayList<Sprite>();
		//For testing
		addZombie();
		tickCount = 0;
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBoard() {
		//I want to restructure this return
		
		String boardString = "" + sprites.size() + "&";
		for (Sprite s: sprites) {
			boardString+= s.id;
			boardString += "-";
			boardString += s.getX();
			boardString += "-";
			boardString += s.getY();
			boardString += "&";
			
		}
		return boardString;
		
		//return (new Gson()).toJson(this.sprites);
	}
	
	public boolean addZombie() {
		
		sprites.add(new Zombie());
		return true;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String updatePlayerPos(@QueryParam("id")   String id, 
								  @QueryParam("newX")   int finishX, 
								  @QueryParam("newY")	int finishY) 
	{
		boolean found = false;
		for (Sprite s: sprites) {
			if (s.getClass().equals(Player.class)) {
				if (((Player) s).getName().equals(id)){
					found = true;
					s.setX(finishX);
					s.setY(finishY);
				}
			}
		}
		if (!found) {
			sprites.add(new Player(id, finishX, finishY));
		}
		
		Tick();
		
		
		return getBoard();
	}
	/**
	 * Increment the tickCount variable.
	 * Once every 60 updates, a new zombie will be added to the board.
	 * 
	 * Also, on every tick, the zombies will update position.
	 */
	private void Tick() {
		if (++tickCount%10==0) addZombie();
		if (tickCount >= 3600) tickCount = 0;
		for (Sprite s : sprites) {
			
			if (s.getClass().equals(Zombie.class)) {
				//Find nearest player, set target
				for (Sprite sP: sprites) {
					if (sP.getClass().equals(Player.class)) {
						((Zombie) s).setTarget((Player) sP);
					}
				}
				((Zombie) s).stalk();
			}
		}
	}
}