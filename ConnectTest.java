import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

/**
 * Tests connection to Swarm DB with mysql jdbc driver
 * 
 * @author Steve
 *
 */
public class ConnectTest {
private static final String dbConnectionUrl = "jdbc:mysql://localhost:3306/swarm";
	
	private static final String dbConnectionUser = "root";
	private static final String dbConnectionPass = "root";
	private static final String connectString = dbConnectionUrl + "?user=" + dbConnectionUser + "&password=" + dbConnectionPass;
	private static Connection connection;
	private static Statement statement;
	private ResultSet resultSet;
	private static ResultSetMetaData metaData;
	
	public static void main(String[] args) throws SQLException {
		//Connect String is GOOOOOOOD
		connection = DriverManager.getConnection(connectString);
		String idToOperate = "rando"; //sprite to add
		String idToOperate2 = "rando"; //sprite to remove
		
		//Set up to execute statements
		statement = connection.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		
		//Insert a sprite into the database
		try {
			statement.execute("insert into sprites (id, x_coord, y_coord) values ('"
					+ idToOperate + "',50,50);");
		} catch (SQLIntegrityConstraintViolationException se) { //Error for duplicate values
			System.out.println("Object already in database!");
		}
		
		//Remove a sprite from database
		statement.execute("delete from sprites where id='" + idToOperate2 + "';");
		
			
		//Query Sprites
		ResultSet resultSet = statement.executeQuery("SELECT id, x_coord, y_coord FROM sprites");
		
		//print results of query
		metaData = resultSet.getMetaData();
		
		while (resultSet.next()) {
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
				System.out.printf("%-8s\t", resultSet.getObject(i));
			}
			System.out.println();
		}
	}

}
