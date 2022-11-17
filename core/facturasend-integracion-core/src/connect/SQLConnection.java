package connect;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

public class SQLConnection {
	
	private static SQLConnection sqlConnection;
    private BasicDataSource basicDataSource = null;
    private static BDConnect bdConnect;
    
    private Map<String, Connection> connections = new HashMap<String, Connection>();
    
    //DBF
    private Connection dbfConnection = null;
    
	private SQLConnection(BDConnect bdConnect) throws Exception{
		
		this.bdConnect = bdConnect;
		//System.out.println(bdConnect);
	    Properties connectionProps = new Properties();
	    //connectionProps.put("user", bdConnect.getUsername());
	    //connectionProps.put("password", bdConnect.getPassword());

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
	        
	        basicDataSource.setMinIdle(bdConnect.getPoolMinIdle());
	        basicDataSource.setMaxIdle(bdConnect.getPoolMaxIdle());
	        basicDataSource.setMaxTotal(bdConnect.getPoolMaxTotal());
	        basicDataSource.setMaxWaitMillis(bdConnect.getPoolMaxWaitMillis());
	        
	    	/*Class.forName("oracle.jdbc.driver.OracleDriver");
	        conn = DriverManager.getConnection(
	                   "jdbc:oracle:thin:@" + bdConnect.getHost() + ":" + bdConnect.getPort() + ":" +  
	                	bdConnect.getDatabase(),
	                	bdConnect.getUsername(), bdConnect.getPassword());
	        */
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
	    	/*Class.forName("org.postgresql.Driver");  
	        conn = DriverManager.getConnection(
	                   "jdbc:postgresql://" +
	                	bdConnect.getHost() + ":" + bdConnect.getPort() + "/" + bdConnect.getDatabase() + 
	                   "",
	                   bdConnect.getUsername(), bdConnect.getPassword());
	        */
	    	
	    } else if (bdConnect.getTipo().equals("dbf")) {
	    	 
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
	
	public static SQLConnection getInstance(BDConnect bdConnect) throws Exception {
		if (sqlConnection == null) {
			 sqlConnection = new SQLConnection(bdConnect);
			 
			 return sqlConnection;
		} else {
			return sqlConnection;
		}
	}

	public Connection getConnection(String connName) throws Exception {
		if (!this.bdConnect.getTipo().equals("dbf")) {
			
			//Buscar la conexion por el nombre y si existe enviar el que ya fue creado
			Iterator itr = connections.entrySet().iterator();
			Connection connEncontrado = null;
			while (itr.hasNext()) {
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+"";
				if ( key.equals(connName)) {
					connEncontrado = (Connection) e.getValue();
				}
			}
			
			if (connEncontrado != null) {
				if (connEncontrado.isClosed()) {
					System.out.println("Conexion estaba cerrada");
				}
				return connEncontrado;	
			} else {
				Connection newConn = this.basicDataSource.getConnection();
				connections.put(connName, newConn);
				return newConn;
			}
				
		} else {
			//DBF
			return this.getDBFConnection();
		}
		
	}

	public Connection getDBFConnection() throws Exception{	
		if (dbfConnection == null) {
			//Aqui crear la conexx y retornar
			//dbfConnection = DriverManager.getConnection( "jdbc:dbschema:dbf:/sample_dbf_folder" );
			dbfConnection = DriverManager.getConnection( "jdbc:dbschema:dbf:" + this.bdConnect.getDbfFilePathRead() );
		} else {
			return dbfConnection;
		}
		return dbfConnection;
	}
	
	public void closeConnection(Connection connection) throws SQLException {
		connection.close();
	} 
}
