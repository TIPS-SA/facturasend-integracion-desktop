package enums;
public enum DatabaseType
{
	
    POSTGRES ("PostgreSQL","postgres","localhost", "5432", "postgres","public", "root", "pgdriver" ,"" ), 
    MYSQL ("MySQL","mysql","localhost", "3306", "system", "root","public", "mysqldriver","" ), 
    ORACLE ("Oracle","oracle","localhost", "1411", "system", "root","public", "oradriver","" ),
	DBF("Archivo DBF","dbf","","","","","","","");
	
    
	public String name;
	public String value;
	public String defaultHost;
	public String defaultPort;
	public String defaultDatabase;
	public String defaultSchema;
	public String defaultUsername;
	public String defaultDriver;
	public String defaultPass;

	private DatabaseType(String name, String value, String defaultHost, String defaultPort, String defaultDatabase, String defaultSchema, String defaultUsername, String defaultDriver, String defaultPass) {
    	this.name = name;
    	this.value = value;
    	this.defaultDatabase =defaultDatabase;
    	this.defaultDriver=defaultDriver;
    	this.defaultHost=defaultHost;
    	this.defaultPort=defaultPort;
    	this.defaultSchema = defaultSchema;
    	this.defaultUsername=defaultUsername;
    	this.defaultPass=defaultPass;
    } 
}