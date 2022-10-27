package core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connect.BDConnect;
import connect.FSConnect;
import connect.SQLConnection;

public class Core {
	
	public static Map<String, Object> listDes(String q, Integer tipoDocumento, Integer page, Integer size, Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getConnection(BDConnect.fromMap(databaseProperties));
			
			Statement statement = conn.createStatement();
			
			String sql = getSQLListDes(databaseProperties, q, page, size);
			
			System.out.println("" + sql);
			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> listadoDes = SQLUtil.convertResultSetToList(rs);
			
			/*for (Map<String, Object> map : listadoDes) {
				
			}*/
			
			result.put("success", true);
			result.put("result", listadoDes);
			result.put("count", SQLUtil.getCountFromSQL(statement, sql));
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
			
		}
		return result;
	}
			
	private static String getSQLListDes(Map<String, String> databaseProperties, String q, Integer page, Integer size) {
		String tableName = databaseProperties.get("database.transaction_view");

		String sql = "SELECT * FROM " + tableName;
		
		//Filter
		
		//Paginacion
		if (databaseProperties.get("database.type").equals("oracle")) {
			sql = "SELECT * FROM \n" +  
		    "( SELECT \n" +  
		    "      ROWNUM rn, a.* \n" + 
		    "  FROM \n" +  
		     "   ( " + sql + " ) a \n" +  
		      "WHERE \n" +  
		        "ROWNUM <= " + (size * page) + " \n" + 
		    ") \n" + 
		"WHERE \n" +
		    "rn  >= " + (page == 1 ? 1 : (size * (page-1)) + 1) + "\n";
		}
		return sql;
	}
}
