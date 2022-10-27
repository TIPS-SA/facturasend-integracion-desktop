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
			
			String sql = getSQLListDes(databaseProperties, q, tipoDocumento, page, size);
			
			result.put("count", SQLUtil.getCountFromSQL(statement, sql));

			sql = getSQLListDesPaginado(databaseProperties, sql, q, page, size);
			
			System.out.println("" + sql);
			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> listadoDes = SQLUtil.convertResultSetToList(rs);
			
			result.put("success", true);
			result.put("result", listadoDes);
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
			
		}
		return result;
	}
			
	private static String getSQLListDes(Map<String, String> databaseProperties, String q, Integer tipoDocumento, Integer page, Integer size) {
		String tableName = databaseProperties.get("database.transaction_view");

		String sql = "SELECT transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, \n"
				+ "cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, \n"
				+ "establecimiento, punto, numero, serie, total \n"
				+ "FROM " + tableName + " \n"
				+ "WHERE "
				+ "( \n"
				+ "	(establecimiento || '-' || punto || '-' || numero || COALESCE(serie, '')) LIKE '%" + q + "%' \n" 
				+ "	OR UPPER(COALESCE(cliente_ruc, '')) LIKE '%" + q.toUpperCase() + "%' \n"
				+ "	OR UPPER(COALESCE(cliente_documento_numero, '')) LIKE '%" + q.toUpperCase() + "%' \n"
				+ "	OR UPPER(cliente_razon_social) LIKE '%" + q.toUpperCase() + "%' \n"
				+ ") \n"
				+ "AND tipo_documento = " + tipoDocumento + " \n"
				+ "GROUP BY transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, \n"
				+ "cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, \n"
				+ "establecimiento, punto, numero, serie, total \n"
				+ "ORDER BY numero DESC \n";		
		return sql;
	}
	
	private static String getSQLListDesPaginado(Map<String, String> databaseProperties, String sql, String q, Integer page, Integer size) {
		
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
