import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class DbConfig {
	public static String Config() {	
		String dbName;
		String dbHost;
		String dbPort;
		String dbUser;
		String dbPass;
		String dbAutoReconnect;
		String useSSL;
		Document doc = null;
		String path = (new File("")).getAbsolutePath() + File.separator + "server.xml";
		File fileToRead = new File(path);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(fileToRead);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		doc.getDocumentElement().normalize();
		

		Node propNode = doc.getElementsByTagName("dataSource").item(0).getChildNodes().item(3);
		NamedNodeMap nnm = propNode.getAttributes();
		nnm.getNamedItem("serverName");
		
		dbHost = nnm.getNamedItem("serverName").getTextContent();
		dbPort = nnm.getNamedItem("portNumber").getTextContent();
		dbName = nnm.getNamedItem("databaseName").getTextContent();
		dbUser = nnm.getNamedItem("user").getTextContent();
		dbPass = nnm.getNamedItem("password").getTextContent();
		dbAutoReconnect = nnm.getNamedItem("autoReconnect").getTextContent();
		useSSL = nnm.getNamedItem("useSSL").getTextContent();
		
		
		String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?autoReconnect=" + dbAutoReconnect + "&useSSL=" + useSSL;
		
		String connectString = dbUrl + "&user=" + dbUser + "&password=" + dbPass;
		
		return connectString;
	}
}
