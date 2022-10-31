package connect;

import java.util.Map;

public class BDConnect {
	private String tipo;	//mysql, derby, oracle, postgres, sqlserver, dbf
	private String host;
	private Integer port;
	private String database;
	private String username;
	private String password;
	
	private Integer poolMinIdle;
	private Integer poolMaxIdle;
	private Integer poolMaxTotal;
	private Integer poolMaxWaitMillis;
	
	private String dbfFilePath;
						
	public BDConnect() {
	
	}
	
	public BDConnect(String tipo, String host, Integer port, String database, String username, String password) {
		super();
		this.tipo = tipo;
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getDatabase() {
		return database;
	}
	public void setDatabase(String database) {
		this.database = database;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String passwrod) {
		this.password = passwrod;
	}
	
	public Integer getPoolMinIdle() {
		return poolMinIdle;
	}

	public void setPoolMinIdle(Integer poolMinIdle) {
		this.poolMinIdle = poolMinIdle;
	}

	public Integer getPoolMaxIdle() {
		return poolMaxIdle;
	}

	public void setPoolMaxIdle(Integer poolMaxIdle) {
		this.poolMaxIdle = poolMaxIdle;
	}

	public Integer getPoolMaxTotal() {
		return poolMaxTotal;
	}

	public void setPoolMaxTotal(Integer poolMaxTotal) {
		this.poolMaxTotal = poolMaxTotal;
	}

	public Integer getPoolMaxWaitMillis() {
		return poolMaxWaitMillis;
	}

	public void setPoolMaxWaitMillis(Integer poolMaxWaitMillis) {
		this.poolMaxWaitMillis = poolMaxWaitMillis;
	}

	public String getDbfFilePath() {
		return dbfFilePath;
	}

	public void setDbfFilePath(String dbfFilePath) {
		this.dbfFilePath = dbfFilePath;
	}

	@Override
	public String toString() {
		return "BDConnect [tipo=" + tipo + ", host=" + host + ", port=" + port + ", database=" + database
				+ ", username=" + username + ", password=" + password + "]";
	}

	public static BDConnect fromMap(Map<String, String> map) {
		BDConnect bdConnect = new BDConnect();
		bdConnect.setTipo(map.get("database.type"));
		bdConnect.setHost(map.get("database.host"));
		bdConnect.setPort(Integer.valueOf(map.get("database.port")+""));
		bdConnect.setDatabase(map.get("database.name"));
		bdConnect.setUsername(map.get("database.username"));
		bdConnect.setPassword(map.get("database.password"));
		
		bdConnect.setPoolMinIdle(Integer.valueOf(map.get("database.pool.minIdle")+""));
		bdConnect.setPoolMaxIdle(Integer.valueOf(map.get("database.pool.maxIdle")+""));
		bdConnect.setPoolMaxTotal(Integer.valueOf(map.get("database.pool.maxTotal")+""));
		bdConnect.setPoolMaxWaitMillis(Integer.valueOf(map.get("database.pool.maxWaitMillis")+""));
				
		bdConnect.setDbfFilePath(map.get("database.dbf_file_path"));

		return bdConnect;
	}
}
