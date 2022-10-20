package enums;
public enum DatabaseType
{
	
    POSTGRES ("postgres","localhost", "5432", "postgres", "root", "pgdriver" ,"" ), 
    MYSQL ("mysql","localhost", "3306", "system", "root", "mysqldriver","" ), 
    ORACLE ("oracle","localhost", "1411", "system", "root", "oradriver","" );
    
	public String name;
	public String defaultHost;
	public String defaultPort;
	public String defaultDatabase;
	public String defaultSchema;
	public String defaultUsername;
	public String defaultDriver;
	public String defaultPass;

	private DatabaseType(String name, String defaultHost, String defaultPort, String defaultDatabase, String defaultUsername, String defaultDriver, String defaultPass) {
    	this.name = name;
    	this.defaultDatabase =defaultDatabase;
    	this.defaultDriver=defaultDriver;
    	this.defaultHost=defaultHost;
    	this.defaultPort=defaultPort;
    	this.defaultUsername=defaultUsername;
    	this.defaultPass=defaultPass;
    } 
}