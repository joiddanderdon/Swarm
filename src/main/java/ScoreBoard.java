import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * Return top ten high scores.
 * 
 * @author Steve Cina
 *
 */
@Path("/ScoreBoard")
public class ScoreBoard extends Application {
	private static final String dbConnectionUrl = "jdbc:mysql://localhost:3306/swarm?autoReconnect=true&useSSL=false";
	private static final String dbConnectionUser = "root";
	private static final String dbConnectionPass = "root";
	private static final String connectString = dbConnectionUrl + "&user=" + dbConnectionUser + "&password=" + dbConnectionPass;
	private static Connection connection;
	private static Statement statement;
	@GET
	@Produces("application/json")
	public static String getScoreBoard() {
		String scoreReturn = ""; 
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(connectString);
			statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ResultSet result = statement.executeQuery("SELECT * FROM scores ORDER BY score DESC LIMIT 10");
			while (result.next()) {
				scoreReturn += (result.getObject(1) + " " + result.getObject(2)) + "\r\n";
				
			}
			return (scoreReturn);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		
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
			//statement = connection.createStatement(	ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO scores (id, score) VALUES (?, ?)");
			stmt.setString(1, id);
			stmt.setString(2,  String.valueOf(score));
			stmt.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			try {
				PreparedStatement sqlCommand = connection.prepareStatement("UPDATE scores SET score=? WHERE id =?");
				sqlCommand.setString(1, String.valueOf(score));
				sqlCommand.setString(2, id);
				sqlCommand.executeUpdate();
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
