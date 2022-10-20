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
	
	public static Map<String, Object> listDes(String q, Integer page, Integer size, Map<String, String> bdConnectMap) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getConnection(BDConnect.fromMap(bdConnectMap));
			
			Statement statement = conn.createStatement();
			
			String sql = getSQLListDes(q, page, size);
			
			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> listadoDes = SQLUtil.convertResultSetToList(rs);
			
			/*for (Map<String, Object> map : listadoDes) {
				
			}*/
			
			result.put("success", true);
			result.put("result", listadoDes);
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
			
		}
		return result;
	}
			
	private static String getSQLListDes(String q, Integer page, Integer size) {
		return "SELECT * FROM tipssa.transacciones_fe_view ";
	}
}
