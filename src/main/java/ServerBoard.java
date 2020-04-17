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

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

@Path("/")
public class ServerBoard {
	private static final String dbConnectionUrl = "jdbc:mysql://localhost:3306/swarm";
	private static final String dbConnectionUser = "root";
	private static final String dbConnectionPass = "root";
	private static final String connectString = dbConnectionUrl + "?user=" + dbConnectionUser + "&password=" + dbConnectionPass;
	//private static final String connectString = dbConnectionUrl  + "?useSSL=false&" + "?user=" + dbConnectionUser + "&password=" + dbConnectionPass;
	private static Connection connection;
	private static Statement statement;
	private ResultSet resultSet;
	private static ResultSetMetaData metaData;
	
	//The first 5 lines or so of the constructor connect to the database,
	//so we now know how to query the database.
	//Insertions & deletions work similarly, as demonstrated
	//in the ConnectTest class. 
	
	//We'll need to keep a list of sprites, then in the Tick() method,
	//run the Stalk method on each Zombie contained within
	private static ArrayList<Sprite> sprites;
	
	private int tickCount;
	
	public ServerBoard() throws SQLException, ClassNotFoundException {
		sprites = new ArrayList<Sprite>();
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(connectString);
		statement = connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = statement.executeQuery("SELECT id, x_coord, y_coord FROM sprites");
		
		//After instantiating list, load from DB

		// Getting a SQL error during this loop.. before Result Set
		while (resultSet.next()) {
				String zid = (String) resultSet.getObject(1);
				int zx = Integer.parseInt(resultSet.getObject(2).toString());
				int zy = Integer.parseInt(resultSet.getObject(3).toString());
				if ( zid.charAt(0) == 'p' ){
					sprites.add(new Player(zid, zx, zy));
				} else {
					sprites.add(new Zombie(zid, zx, zy));
				}
			}
		
		
		
		//For testing
		//addZombie();
		tickCount = 0;
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBoard() {
		//May want to change this to pull directly from DB
		String boardString = "" + sprites.size() + "&";
		for (Sprite s: sprites) {
			boardString+= s.getId();
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
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectString);
			statement = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			statement.execute("insert into sprites "
					+ "(id, x_coord, y_coord) values "
					+ "('"+ newZom.getId() + "'," + newZom.getX() + "," + newZom.getY() + ");");
			
			return true;	
		} catch (MySQLIntegrityConstraintViolationException msicve) {
			System.out.println("Attempted to add duplicate entry");
			return addZombie();
		} catch (SQLException se) {
			se.printStackTrace();
			return false;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return false;
		} 
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