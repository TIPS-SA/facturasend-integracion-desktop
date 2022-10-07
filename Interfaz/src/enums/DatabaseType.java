package enums;
public enum DatabaseType
{
	
    POSTGRES ("PostgresSQL","localhost", "5432", "postgres", "root", "pgdriver" ), 
    MYSQL ("MySQL","localhost", "3306", "system", "root", "mysqldriver" ), 
    ORACLE ("Oracle","localhost", "1411", "system", "root", "oradriver" );
    
	public String name;
	public String defaultHost;
	public String defaultPort;
	public String defaultDatabase;
	public String defaultUsername;
	public String defaultDriver;

	private DatabaseType(String name, String defaultHost, String defaultPort, String defaultDatabase, String defaultUsername, String defaultDriver) {
    	this.name = name;
    	this.defaultDatabase =defaultDatabase;
    	this.defaultDriver=defaultDriver;
    	this.defaultHost=defaultHost;
    	this.defaultPort=defaultPort;
    	this.defaultUsername=defaultUsername;
    } 
}