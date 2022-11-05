package connect;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

public class DBFConnection {
	
	private static DBFConnection sqlConnection;
    private BasicDataSource basicDataSource = null;
    
	private DBFConnection(BDConnect bdConnect) throws Exception{
		
        
		System.out.println(bdConnect);
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", bdConnect.getUsername());
	    connectionProps.put("password", bdConnect.getPassword());

	    if (bdConnect.getTipo().equals("mysql")) {
	    	/*Class.forName("com.mysql.jdbc.Driver");  
	        conn = DriverManager.getConnection(
	                   "jdbc:mysql://" +
	                   bdConnect.getHost() +
	                   ":" + bdConnect.getPort() + "/",
	                   connectionProps);
	                   */
	    } else if (bdConnect.getTipo().equals("derby")) {
	    	/*Class.forName("com.mysql.jdbc.Driver");  
	        conn = DriverManager.getConnection(
	                   "jdbc:derby:" +
	                	bdConnect.getDatabase() +
	                   ";create=true",
	                   connectionProps);*/
	    } else if (bdConnect.getTipo().equals("oracle")) {
	    	 
	    	basicDataSource = new BasicDataSource();
	        basicDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
	        basicDataSource.setUsername(bdConnect.getUsername());
	        basicDataSource.setPassword(bdConnect.getPassword());
	        basicDataSource.setUrl("jdbc:oracle:thin:@" + bdConnect.getHost() + ":" + bdConnect.getPort() + ":" + bdConnect.getDatabase());
	        
	        basicDataSource.setMinIdle(5);
	        basicDataSource.setMaxIdle(20);
	        basicDataSource.setMaxTotal(50);
	        basicDataSource.setMaxWaitMillis(-1);
	        
	    	/*Class.forName("oracle.jdbc.driver.OracleDriver");
	        conn = DriverManager.getConnection(
	                   "jdbc:oracle:thin:@" + bdConnect.getHost() + ":" + bdConnect.getPort() + ":" +  
	                	bdConnect.getDatabase(),
	                	bdConnect.getUsername(), bdConnect.getPassword());
	        */
	    } else if (bdConnect.getTipo().equals("postgres")) {
	    	//System.out.println("-->" + System.getProperty("user.dir") + File.separator);
	    	System.out.println(DBFConnection.class.getResource(""));
	    	/*
	    	File file  = new File("c:\\myjar.jar");

	    	URL url = file.toURL();  
	    	URL[] urls = new URL[]{url};

	    	ClassLoader cl = new URLClassLoader(urls);
	    	Class cls = cl.loadClass("com.mypackage.myclass");
	    	*/
	    	
	    	System.out.println("entro en postgres");
	    	/*Class.forName("org.postgresql.Driver");  
	        conn = DriverManager.getConnection(
	                   "jdbc:postgresql://" +
	                	bdConnect.getHost() + ":" + bdConnect.getPort() + "/" + bdConnect.getDatabase() + 
	                   "",
	                   bdConnect.getUsername(), bdConnect.getPassword());
	        */
	    	
	    } else if (bdConnect.getTipo().equals("Archivo DBF")) {
	    	 
	    	/*basicDataSource = new BasicDataSource();
	        basicDataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
	        basicDataSource.setUsername(bdConnect.getUsername());
	        basicDataSource.setPassword(bdConnect.getPassword());
	        //basicDataSource.setUrl("jdbc:oracle:thin:@" + bdConnect.getHost() + ":" + bdConnect.getPort() + ":" + bdConnect.getDatabase());
	        
	        basicDataSource.setMinIdle(5);
	        basicDataSource.setMaxIdle(20);
	        basicDataSource.setMaxTotal(50);
	        basicDataSource.setMaxWaitMillis(-1);
	    	*/
	    } else {
	    	throw new Exception("No fue especificado el motor de Base de Datos");
	    }
        
	}
	
	public static DBFConnection getInstance(BDConnect bdConnect) throws Exception {
		if (sqlConnection == null) {
			 sqlConnection = new DBFConnection(bdConnect);
			 
			 return sqlConnection;
		} else {
			return sqlConnection;
		}
	}

	public Connection getConnection() throws SQLException {
		return this.basicDataSource.getConnection();
	}

	public void closeConnection(Connection connection) throws SQLException {
		connection.close();
	} 
}
