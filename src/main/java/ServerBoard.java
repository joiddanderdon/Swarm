//import com.google.gson.Gson; 

import java.util.ArrayList;
import java.util.Collections;

//import javax.json.JsonArray;

//Databse Connectivity

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;

import javax.ws.rs.DELETE;
// JAX RS Modules
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
/**
 * Main component of Liberty Swarm Back-End
 * 
 * @author Steve Cina
 * @since April 2020
 */
@Path("/")
public class ServerBoard {
	private static final int ZOMBIE_RATE = 10; //The lower, the faster.
	private static final String dbConnectionUrl = "jdbc:mysql://localhost:3306/swarm?autoReconnect=true&useSSL=false";
	private static final String dbConnectionUser = "root";
	private static final String dbConnectionPass = "root";
	private static final String connectString = dbConnectionUrl + "&user=" + dbConnectionUser + "&password=" + dbConnectionPass;
	//private static final String connectString = dbConnectionUrl  + "?useSSL=false&" + "?user=" + dbConnectionUser + "&password=" + dbConnectionPass;
	private static Connection connection;
	private static Statement statement;
	
	
	
	
	//We'll need to keep a list of sprites, then in the Tick() method,
	//run the Stalk method on each Zombie contained within
	private static ArrayList<Sprite> sprites;
	
	
	public ServerBoard() throws SQLException, ClassNotFoundException {
		sprites = new ArrayList<Sprite>();
		
		//Database connection
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(connectString);
		statement = connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = statement.executeQuery("SELECT id, x_coord, y_coord FROM sprites");
		

		// parse results as a list of sprites, add to the sprites arrayList
		while (resultSet.next()) {
				String zid = (String) resultSet.getObject(1);
				int zx = Integer.parseInt(resultSet.getObject(2).toString());
				int zy = Integer.parseInt(resultSet.getObject(3).toString());
				if ( zid.charAt(0) == 'p' ){
					sprites.add(new Player(zid, zx, zy));
				} else {
					//Don't have to pay too much attention to the target params here
					//On the next PUT they'll all update anyway.
					sprites.add(new Zombie(zid, zx, zy, 5, 5));
				}
			}
	}
	
	/**
	 * This is what is called on the basic non-param GET request.
	 * 
	 * @return A String consisting of all the sprites on the board.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getBoard() {
		sprites = new ArrayList<Sprite>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		
			connection = DriverManager.getConnection(connectString);
			statement = connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
			ResultSet resultSetPlayer = statement.executeQuery("SELECT id, x_coord, y_coord FROM sprites WHERE id LIKE \"p%\"");
		
			//Load Players onto list
			while (resultSetPlayer.next()) {
				String pid = (String) resultSetPlayer.getObject(1);
				int px = Integer.parseInt(resultSetPlayer.getObject(2).toString());
				int py = Integer.parseInt(resultSetPlayer.getObject(3).toString());
				sprites.add(new Player(pid, px, py));
			}
			resultSetPlayer.close();
			//Load Zombies into list
			ResultSet resultSet = statement.executeQuery("SELECT id, x_coord, y_coord, target_x, target_y FROM sprites WHERE id LIKE \"z%\"");
			while (resultSet.next()) {
					String zid = (String) resultSet.getObject(1);
					int zx = Integer.parseInt(resultSet.getObject(2).toString());
					int zy = Integer.parseInt(resultSet.getObject(3).toString());
					int zxTarg = Integer.parseInt(resultSet.getObject(4).toString());
					int zyTarg = Integer.parseInt(resultSet.getObject(5).toString());
					sprites.add(new Zombie(zid, zx, zy, zxTarg, zyTarg));
					
				}
			resultSet.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
		
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
	/**
	 * Periodically adds a zombie to the board.
	 * 
	 * @return true if zombie successfully added
	 * 		false if zombie not added for ANY reason.
	 */
	private boolean addZombie() {
		Zombie newZom = new Zombie();
		if (sprites.size() >= 100) return false;
		sprites.add(newZom);
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectString);
			statement = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			statement.execute("insert into sprites "
					+ "(id, x_coord, y_coord, target_x, target_y) values "
					+ "('"+ newZom.getId() + "'," + newZom.getX() + "," + newZom.getY()
					+ "," + newZom.getTargetX() + "," + newZom.getTargetY() + ");");
			System.out.println("Zombie added!");
			return true;	
		} catch (MySQLIntegrityConstraintViolationException msicve) {
			//id already in use, just call addZombie to increase the counter and try again until success.
			return addZombie();
		} catch (SQLException se) {
			se.printStackTrace();
			return false;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return false;
		} 
	}
	
	/**
	 * Update player position on board. Activates the Tick() method.
	 * 
	 * @param id The String id of the player to be moved.
	 * @param finishX The new X coordinate
	 * @param finishY The new Y coordinate
	 * @return The output of GetBoard(), after movement has occurred.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public String updatePlayerPos(@QueryParam("id")   String id, 
								  @QueryParam("newX")   int finishX, 
								  @QueryParam("newY")	int finishY) throws ClassNotFoundException, SQLException 
	{
		try {
			Player newPlay = new Player(id, finishX, finishY);
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectString);
			statement = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			sprites.add(newPlay);
			statement.execute("insert into sprites "
					+ "(id, x_coord, y_coord) values "
					+ "('"+ newPlay.getId() + "'," + newPlay.getX() + "," + newPlay.getY() + ");");
		} catch (MySQLIntegrityConstraintViolationException msicve) { //Player already in database, find em and move em
			try {
				String sqlCommand = "UPDATE sprites SET x_coord = " + finishX + ", y_coord = " + finishY
						+ " WHERE id = \"" + id + "\"";
				statement.execute(sqlCommand);
				sprites.add(new Player(id, finishX, finishY)); //This might create duplicates in the sprites list?
				
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
			
		} catch (SQLException se) {
			se.printStackTrace();
			return null;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return null;
		} 
		
		this.Tick(sprites);
		
		
		return getBoard();
	}
	/**
	 * Increments the tick counter. After a certain
	 * number of "ticks", and dependent on the ZOMBIE_RATE
	 * variable, a zombie is released.
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 * 
	 */
	private void Tick(ArrayList<Sprite> sprites) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(connectString);
		statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = statement.executeQuery("SELECT ticks FROM variables");
		int tickCount = 0;
		while (resultSet.next()) {
			tickCount = ((Long) resultSet.getObject(1)).intValue();
		}
		
		if (tickCount%ZOMBIE_RATE==0) {
			addZombie();
			tickCount = 0;
			
		}
		tickCount++;
		statement.execute("UPDATE variables SET ticks = " + tickCount + " WHERE idvariables = 1");
		System.out.println("Current tickCount is " + tickCount);
		
		
		
		
		
		//Update Positions & Targets	
		sprites = setTarget(sprites);
		
		//Save back to DB
		for (Sprite s : sprites) {
			
			if (s.getClass().equals(Zombie.class)) {//Update Zombie Positions
				statement.execute("UPDATE sprites SET x_coord = " + s.getX() +
						", y_coord = " + s.getY() + ", target_x = " + ((Zombie) s).getTargetX() + ", target_y = " + ((Zombie) s).getTargetY() +
						" WHERE id = \"" + s.getId() + "\"");
			}
			else {
				statement.execute("UPDATE sprites SET x_coord = " + s.getX() + 
						", y_coord = " + s.getY() + 
						" WHERE id = \"" + s.getId() + "\"");
			}
			
			
		}
		
		
		
	}
	
	/**
	 * Updates target and current x/y coordinates of zombies within the arrayList
	 * @param sprites
	 * @return
	 */
	private ArrayList<Sprite> setTarget(ArrayList<Sprite> sprites){
		
		//Mix up the list, so the zombies randomly change targets.
		//Not sure if this is a good idea right now, so leaving it commented out.
		//##
		Collections.shuffle(sprites);
		
		
		
		
		for (Sprite s : sprites) {
			if (s.getClass().equals(Zombie.class)) {
				for (Sprite sP: sprites) {
					if (sP.getClass().equals(Player.class)) {
						((Zombie) s).setTarget(((Player) sP).getX(), ((Player) sP).getY());
						((Zombie) s).stalk();
					}
				}
				
			}
		}

		return sprites;
	}
	/** 
	 * Kills Player/Zombie with given id
	 * 
	 * @param id ID of player or zombie to be destroyed.
	 * 
	 * @return true if sprite was successfully destroyed.
	 * <p>false if sprite not in database
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	@DELETE
	@Produces("application/json")
	public Response killSprite(@QueryParam("id") String id) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(connectString);
		statement = connection.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet resultSet = statement.executeQuery("SELECT id FROM sprites WHERE id = \"" + id + "\"");
		if (resultSet.next()) { //if resultSet query worked, we found our match - so KILL IT!
			statement.execute("DELETE FROM sprites WHERE id = \"" + id + "\"");
			return Response.ok(200, MediaType.APPLICATION_JSON).build();
		}
		return Response.status(Response.Status.NOT_FOUND).entity(404).build();
	}
	
	/**
	 * Post a high score to the database. If player already has a high score
	 * in the database, this method will update it to reflect the higher of the two.
	 * @param id The id of the player whose score to update
	 * @param score The high score that is being posted.
	 * @return 200 on success.
	 */
	@POST
	@Produces("application/json")
	public Response postScore(@QueryParam("id") String id, @QueryParam("score") int score) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectString);
			statement = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			statement.execute("INSERT INTO scores (id, score) VALUES (\""+ id +"\", " + score + ");");
		} catch (MySQLIntegrityConstraintViolationException e) {
			String sqlCommand = "UPDATE scores SET score=" + score + " WHERE id = \"" + id + "\"";
			try {
				statement.execute(sqlCommand);
			} catch (SQLException e1) {
				return Response.status(Response.Status.BAD_REQUEST).entity(400).build();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return Response.ok(200, MediaType.APPLICATION_JSON).build();
	}
}