import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
/**
 * API Rest call test.
 * 
 * @author Steve Cina
 * @since April 2020
 *
 */
@Path("/hello")
public class TestHello {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String Hello(@DefaultValue("World") @QueryParam("name") String name) {
		return "Hello " + name + "!";
		
	}
}
