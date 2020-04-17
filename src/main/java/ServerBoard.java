//import com.google.gson.Gson; 

import java.util.ArrayList;

//import javax.json.JsonArray;

//Databse Connectivity

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;

// JAX RS Modules
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/")
public class ServerBoard {
	private static final String dbConnectionUrl = "jdbc:mysql://localhost:3306/swarm";
	private static final String dbConnectionUser = "root";
	private static final String dbConnectionPass = "root";
	private static final String connectString = dbConnectionUrl + "?user=" + dbConnectionUser + "&password=" + dbConnectionPass;
	private static Connection connection;
	private static Statement statement;
	private ResultSet resultSet;
	private static ResultSetMetaData metaData;
	
	
	
	//We'll need to keep a list of sprites, then in the Tick() method,
	//run the Stalk method on each Zombie contained within
	private static ArrayList<Sprite> sprites;
	
	private int tickCount;
	
	public ServerBoard() throws SQLException {
		connection = DriverManager.getConnection(connectString);
		statement = connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = statement.executeQuery("SELECT id, x_coord, y_coord FROM sprites");
		metaData = resultSet.getMetaData();
		
		while (resultSet.next()) {
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				System.out.printf("%-8s\t", resultSet.getObject(i));
			}
			System.out.println();
		}
		
		sprites = new ArrayList<Sprite>();
		//After instantiating list, may need to reload from DB.
		
		
		//For testing
		addZombie();
		tickCount = 0;
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBoard() {
		//May want to change this to pull directly from DB
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
		
		
	}
	
	public boolean addZombie() {
		Zombie newZom = new Zombie();
		sprites.add(newZom);
		/*
		try {
			Statement stmt = dbConnect.createStatement();
			stmt.executeQuery("insert into sprites (id, x_coord, y_coord) values ( "
					+ newZom.id + "," + newZom.getX() + "," + newZom.getY() );
		} catch (SQLException e) {
			System.out.println("Issue adding zombie to DB");
			e.printStackTrace();
		}
		*/
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
					//Statement stmt = dbConnect.createStatement();
					//stmt.executeQuery("select * from sprites where \"id\"=" + s.id + 
					//					" ");
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