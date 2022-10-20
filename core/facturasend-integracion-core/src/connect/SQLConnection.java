package connect;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class SQLConnection {
	public static Connection getConnection(BDConnect bdConnect) throws SQLException {

	    Connection conn = null;
		try {
			System.out.println(bdConnect);
		    Properties connectionProps = new Properties();
		    connectionProps.put("user", bdConnect.getUsername());
		    connectionProps.put("password", bdConnect.getPassword());

		    if (bdConnect.getTipo().equals("mysql")) {
		    	Class.forName("com.mysql.jdbc.Driver");  
		        conn = DriverManager.getConnection(
		                   "jdbc:mysql://" +
		                   bdConnect.getHost() +
		                   ":" + bdConnect.getPort() + "/",
		                   connectionProps);
		    } else if (bdConnect.getTipo().equals("derby")) {
		    	Class.forName("com.mysql.jdbc.Driver");  
		        conn = DriverManager.getConnection(
		                   "jdbc:derby:" +
		                	bdConnect.getDatabase() +
		                   ";create=true",
		                   connectionProps);
		    } else if (bdConnect.getTipo().equals("oracle")) {
		    	Class.forName("com.mysql.jdbc.Driver");  
		        conn = DriverManager.getConnection(
		                   "jdbc:oracle:" +
		                	bdConnect.getDatabase() +
		                   ";create=true",
		                   connectionProps);
		    } else if (bdConnect.getTipo().equals("postgres")) {
		    	//System.out.println("-->" + System.getProperty("user.dir") + File.separator);
		    	System.out.println(SQLConnection.class.getResource(""));
		    	/*
		    	File file  = new File("c:\\myjar.jar");

		    	URL url = file.toURL();  
		    	URL[] urls = new URL[]{url};

		    	ClassLoader cl = new URLClassLoader(urls);
		    	Class cls = cl.loadClass("com.mypackage.myclass");
		    	*/
		    	
		    	System.out.println("entro en postgres");
		    	Class.forName("org.postgresql.Driver");  
		        conn = DriverManager.getConnection(
		                   "jdbc:postgresql://" +
		                	bdConnect.getHost() + ":" + bdConnect.getPort() + "/" + bdConnect.getDatabase() + 
		                   "",
		                   bdConnect.getUsername(), bdConnect.getPassword());
		        
		    } else {
		    	throw new Exception("No fue especificado el motor de Base de Datos");
		    }
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	    System.out.println("Connected to database");
	    return conn;
	}
}
