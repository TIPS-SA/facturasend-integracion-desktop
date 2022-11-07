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
	
	private String dbfFilePathRead;
	private String dbfFilePathWrite;
						
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

	
	public String getDbfFilePathRead() {
		return dbfFilePathRead;
	}

	public void setDbfFilePathRead(String dbfFilePathRead) {
		this.dbfFilePathRead = dbfFilePathRead;
	}

	public String getDbfFilePathWrite() {
		return dbfFilePathWrite;
	}

	public void setDbfFilePathWrite(String dbfFilePathWrite) {
		this.dbfFilePathWrite = dbfFilePathWrite;
	}

	@Override
	public String toString() {
		return "BDConnect [tipo=" + tipo + ", host=" + host + ", port=" + port + ", database=" + database
				+ ", username=" + username + ", password=" + password + "]";
	}

	public static BDConnect fromMap(Map<String, String> map) {
		String tipo = map.get("database.type");
		BDConnect bdConnect = new BDConnect();
		bdConnect.setTipo(map.get("database.type"));
		bdConnect.setHost(map.get("database." + tipo + ".host"));
		if (map.get("database." + tipo + ".port") != null) {
				bdConnect.setPort(Integer.valueOf(map.get("database." + tipo + ".port")+""));
		}
		bdConnect.setDatabase(map.get("database." + tipo + ".name"));
		bdConnect.setUsername(map.get("database."+ tipo +".username"));
		bdConnect.setPassword(map.get("database."+tipo+".password"));
		if (map.get("database.pool.minIdle") != null) {
			bdConnect.setPoolMinIdle(Integer.valueOf(map.get("database.pool.minIdle")+""));
		}
		if (map.get("database.pool.maxIdle") != null) {
			bdConnect.setPoolMaxIdle(Integer.valueOf(map.get("database.pool.maxIdle")+""));
		}
		if (map.get("database.pool.maxTotal") != null) {
			bdConnect.setPoolMaxTotal(Integer.valueOf(map.get("database.pool.maxTotal")+""));
		}
		if(map.get("database.pool.maxWaitMillis") != null) {
			bdConnect.setPoolMaxWaitMillis(Integer.valueOf(map.get("database.pool.maxWaitMillis")+""));
		}
				
		bdConnect.setDbfFilePathRead(map.get("database.dbf.parent_folder"));
		bdConnect.setDbfFilePathWrite(map.get("database.dbf.facturasend_file"));

		return bdConnect;
	}
}
