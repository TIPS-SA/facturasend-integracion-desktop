package core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLUtil {

	public static Map<String, String> convertResultSetToMapString(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    Map<String, String> map = new HashMap<String, String>();

	    if (rs.next()) {
	        //HashMap<String,Object> row = new HashMap<String, Object>(columns);
	        for(int i=1; i<=columns; ++i) {
	        	map.put(md.getColumnName(i), rs.getObject(i)+"");
	        }
	        //list.add(row);
	    }

	    return map;
	}

	public static Map<String,Object> convertResultSetToMap(ResultSet rs) throws SQLException {
	    Map<String,Object> map = null;
		if (rs != null) {
			ResultSetMetaData md = rs.getMetaData();
		    int columns = md.getColumnCount();

		    if (rs.next()) {
		    	map = new HashMap<String,Object>();
		    	
		    	for(int i=1; i<=columns; ++i) {
		        	map.put(md.getColumnName(i), rs.getObject(i));
		        }
		    }
		}
	    
	    return map;
	}
	
	public static List<Map<String, Object>> convertResultSetToList(ResultSet rs) throws SQLException {
	    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
	    if (rs != null) {
		    ResultSetMetaData md = rs.getMetaData();
		    int columns = md.getColumnCount();
	
		    while (rs.next()) {
		        HashMap<String,Object> row = new HashMap<String, Object>(columns);
		        for(int i=1; i<=columns; ++i) {
		            row.put(md.getColumnName(i), rs.getObject(i));
		        }
		        list.add(row);
		    }
	    }
	    return list;
	}
	
	public static List<Map<String, Object>> executeAndConvertToListMap(Connection conn, String sql) throws SQLException {
		Statement statement = conn.createStatement();

		System.out.println(sql);
		ResultSet results = statement.executeQuery(sql);

		List<Map<String, Object>> resultados = SQLUtil.convertResultSetToList(results);
		results.close();
		statement.close();
		
	    return resultados;
	}
	
	/**
	 * Para que funcione correctamente ésta función, previamente en la ejecución, se le 
	 * debe pasar como segundo parámetro, el valor de la clave primaria.
	 * 
	 * Usage:
	 * statement.executeUpdate(sql, new String[] { "campo_id" });
	 * @param statement
	 * @return
	 * @throws SQLException
	 */
	public static Long getPKValue(Statement statement) throws SQLException {
		Long resultado = null;
	    ResultSet generatedKeys = statement.getGeneratedKeys();
	    if (null != generatedKeys && generatedKeys.next()) {
	    	resultado = generatedKeys.getLong(1);
	    }
	    generatedKeys.close();
	    return resultado;
	}
	
	public static Integer getCountFromSQL(Statement statement, String sql) {
		
		Integer total = 0;
		try {
			String sqlCount = "SELECT COUNT(*) FROM (" + sql + ") AS cnt";
			ResultSet rs = statement.executeQuery(sqlCount);
			
			rs.next();
			total = rs.getInt(1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return total; 
	}
}
