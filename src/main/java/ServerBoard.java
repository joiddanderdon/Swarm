import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Main component of Liberty Swarm Back-End
 * 
 * @author Steve Cina
 * @since April 2020
 */
@Path("/")
public class ServerBoard {
	private static final int ZOMBIE_RATE = 10; //The lower, the faster.
	private static final String connectString = DbConfig.Config();
	private static Connection connection;
	private static Statement statement;
	private static ArrayList<Sprite> sprites;
	
	
	public ServerBoard() {
		sprites = new ArrayList<Sprite>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectString);
			statement = connection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet resultSet = statement.executeQuery("SELECT id, x_coord, y_coord, target_x, target_y, speed FROM sprites");
			while (resultSet.next()) {
				String zid = (String) resultSet.getObject(1);
				int zx = Integer.parseInt(resultSet.getObject(2).toString());
				int zy = Integer.parseInt(resultSet.getObject(3).toString());
				if ( zid.charAt(0) == 'p' ){
					sprites.add(new Player(zid, zx, zy));
				} else {
					try {
						int zTargX = Integer.parseInt(resultSet.getObject(4).toString());
						int zTargY = Integer.parseInt(resultSet.getObject(5).toString());
						int zSpeed = Integer.parseInt(resultSet.getObject(6).toString());
						Zombie zom = new Zombie(zid, zx, zy, zTargX, zTargY, zSpeed);
						sprites.add(zom);
					} catch (NullPointerException npe) {
						System.out.println("Malformed object in database! ID ='" + zid + "'");
						System.out.printf("Object %s removed: %s", zid, killSprite(zid));
					}
				}
			}
			resultSet.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
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
			ResultSet resultSet = statement.executeQuery("SELECT id, x_coord, y_coord, target_x, target_y, speed FROM sprites WHERE id LIKE \"z%\"");
			while (resultSet.next()) {
					String zid = (String) resultSet.getObject(1);
					int zx = Integer.parseInt(resultSet.getObject(2).toString());
					int zy = Integer.parseInt(resultSet.getObject(3).toString());
					try {
						int zxTarg = Integer.parseInt(resultSet.getObject(4).toString());
						int zyTarg = Integer.parseInt(resultSet.getObject(5).toString());
						int zSpeed = Integer.parseInt(resultSet.getObject(6).toString());
						sprites.add(new Zombie(zid, zx, zy, zxTarg, zyTarg, zSpeed));	
					} catch (NullPointerException npe) {
						System.out.println("Malformed object in database! ID ='" + zid + "'");
						System.out.printf("Object %s removed: %s", zid, killSprite(zid));
					}
				}
			resultSet.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String boardString = "" + sprites.size() + "&";
		for (Sprite s: sprites) {
			boardString += s.getId();
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
					+ "(id, x_coord, y_coord, target_x, target_y, speed) values "
					+ "('"+ newZom.getId() + "'," + newZom.getX() + "," + newZom.getY()
					+ "," + newZom.getTargetX() + "," + newZom.getTargetY() + "," + newZom.getSpeed() + ");");
			return true;	
		} catch (MySQLIntegrityConstraintViolationException msicve) {
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
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePlayerPos(@QueryParam("id")   String id, 
								  @QueryParam("newX")   int finishX, 
								  @QueryParam("newY")	int finishY) throws ClassNotFoundException, SQLException 
	{
		try {
			Player newPlay = new Player(id, finishX, finishY);
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectString);
			PreparedStatement statement = connection.prepareStatement("INSERT INTO sprites (id, x_coord, y_coord) values (?, ?, ?)");
			statement.setString(1, newPlay.getId());
			statement.setInt(2, newPlay.getX());
			statement.setInt(3, newPlay.getY());
			statement.executeUpdate();
			sprites.add(newPlay);
			
		} catch (MySQLIntegrityConstraintViolationException msicve) {
			try {
				PreparedStatement statement = connection.prepareStatement("UPDATE sprites SET x_coord=?, y_coord=? WHERE id=?");
				statement.setInt(1, finishX);
				statement.setInt(2, finishY);
				statement.setString(3, id);
				statement.executeUpdate();
				sprites.add(new Player(id, finishX, finishY));
			} catch (SQLException e) {
				e.printStackTrace();
				return Response.status(Response.Status.NOT_IMPLEMENTED).entity(591).build();
			}
		} catch (SQLException se) {
			se.printStackTrace();
			return Response.status(Response.Status.NOT_IMPLEMENTED).entity(592).build();
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			return Response.status(Response.Status.NOT_IMPLEMENTED).entity(593).build();
		} 
		this.Tick(sprites);
		return Response.ok(200, MediaType.APPLICATION_JSON).build();
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
		
		sprites = setTarget(sprites);
		
		for (Sprite s : sprites) {
			if (s.getClass().equals(Zombie.class)) {
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
		ArrayList<Player> players = new ArrayList<Player>();
		ArrayList<Zombie> zombies = new ArrayList<Zombie>();
		ExecutorService ES = Executors.newCachedThreadPool();
		for (Sprite s: sprites) {
			if (s.getClass().equals(Player.class)) players.add((Player) s);
			if (s.getClass().equals(Zombie.class)) zombies.add((Zombie) s);
		}
		for (Zombie z: zombies) {
			ES.submit(new TargetSetter(z, players));
		}
		
		return sprites;
	}
	/** 
	 * Kills Player/Zombie with given id
	 * 
	 * @param id ID of player or zombie to be destroyed.
	 * 
	 * @return HTTP Response Code [200 || 404]
	 */
	@DELETE
	@Produces("application/json")
	public Response killSprite(@QueryParam("id") String id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectString);
			PreparedStatement stmt = connection.prepareStatement("SELECT id FROM sprites WHERE id =?");
			stmt.setString(1, id);
			ResultSet resultSet = stmt.executeQuery();
					
			if (resultSet.next()) {
				stmt = connection.prepareStatement("DELETE FROM sprites WHERE id =?");
				stmt.setString(1, id);
				stmt.executeUpdate();
				return Response.ok(200, MediaType.APPLICATION_JSON).build();
			}
		} catch (ClassNotFoundException e) {	
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return Response.status(Response.Status.NOT_FOUND).entity(404).build();
	}
	
	
}