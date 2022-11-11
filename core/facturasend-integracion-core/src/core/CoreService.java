package core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import connect.BDConnect;
import connect.SQLConnection;

/**
 * Clase que contiene los servicios básicos del backend.
 * 
 * Contiene mayormente los métodos que se requieren desde el frontend
 * 
 * @author Marcos Jara
 *
 */
public class CoreService {
	
	private static Gson gson = new Gson();
	private static Gson gsonPP = new GsonBuilder().setPrettyPrinting().create();
	public static Log log = LogFactory.getLog(CoreService.class);
	
	public static Map<String, Object> getTransaccionesList(String q, Integer tipoDocumento, Integer page, Integer size, Map<String, String> databaseProperties) {
		System.out.println(databaseProperties);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			System.out.println("obteniendo conexion");
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("vista");
			System.out.println("conexio obenida");
			
			Statement statement = conn.createStatement();
			System.out.println("conexio obenida 111");
			
			String sql = getSQLTransaccionesList(databaseProperties, q, tipoDocumento, page, size);
			System.out.println("conexio obenida 222");
			
			result.put("count", SQLUtil.getCountFromSQL(statement, sql)); 

			System.out.println("conexio obenida 333");

			//sql = getSQLListDesPaginado(databaseProperties, sql, q, page, size);
			if (databaseProperties.get("database.type").equals("oracle")) {
				sql = getOracleSQLPaginado(sql, page, size);
			} else if (databaseProperties.get("database.type").equals("postgres")) {
				sql = getPostgreSQLPaginado(sql, page, size);
			} else if (databaseProperties.get("database.type").equals("dbf")) {
				sql = getPostgreSQLPaginado(sql, page, size);
			}
			
			System.out.println("conexio obenida 444");

			if (databaseProperties.get("database.type").equals("dbf")) {
				System.out.print("\nReload ");
				Statement st = conn.createStatement();
				st.execute("reload '" + databaseProperties.get("database.dbf.parent_folder") + "/" + databaseProperties.get("database.dbf.facturasend_file") + "'");
			}
			
			
			
			System.out.print("\n" + sql + " ");
			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> transaccionesList = SQLUtil.convertResultSetToList(rs);
			
			System.out.println("transaccionesList: " + transaccionesList + " ");
			result.put("success", true);
			result.put("result", transaccionesList);
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
			
		}
		return result;
	}
			
	private static String getSQLTransaccionesList(Map<String, String> databaseProperties, String q, Integer tipoDocumento, Integer page, Integer size) {
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transacctions_table");
		
		String sql = "";
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, \n"
				+ "cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, \n"
				+ "establecimiento, punto, numero, serie, total, cdc, estado, error, pausado \n"
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
				+ "establecimiento, punto, numero, serie, total, cdc, estado, error, pausado \n"
				+ "ORDER BY establecimiento DESC, punto DESC, numero DESC \n";			
		} else {
			//DBF
			
			tableName = databaseProperties.get("database.dbf.facturasend_file");
			tableName = tableName.substring(0, tableName.indexOf(".dbf"));

			sql = "SELECT tra_id, tip_doc, descrip, observa, fecha, moneda, \n"
				+ "c_contribu, c_ruc, c_doc_num, c_raz_soc, \n"
				+ "estable, punto, numero, serie, total, "
				/*+ "cdc, "
				+ "estado, "
				+ "error "*/
				+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='CDC' LIMIT 1) AS cdc, "
				+ "CAST ((SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='ESTADO' LIMIT 1) AS INTEGER) AS estado, "
				+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='ERROR' LIMIT 1) AS error, "
				+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='PAUSADO' LIMIT 1) AS pausado "
				+ "\n"
				+ "FROM " + tableName + " vp \n"
				+ "WHERE "
				+ "( \n"
				+ "	(estable || '-' || punto || '-' || numero || COALESCE(serie, '')) LIKE '%" + q + "%' \n" 
				+ "	OR UPPER(COALESCE(c_ruc, '')) LIKE '%" + q.toUpperCase() + "%' \n"
				+ "	OR UPPER(COALESCE(c_doc_num, '')) LIKE '%" + q.toUpperCase() + "%' \n"
				+ "	OR UPPER(c_raz_soc) LIKE '%" + q.toUpperCase() + "%' \n"
				+ ") \n"
				+ "AND tip_doc = " + tipoDocumento + " \n"
				+ "GROUP BY tra_id, tip_doc, descrip, observa, fecha, moneda, \n"
				+ "c_contribu, c_ruc, c_doc_num, c_raz_soc, \n"
				+ "estable, punto, numero, serie, total, cdc, estado, error, pausado \n"
				+ "ORDER BY estable DESC, punto DESC, numero DESC \n";			
		}
		
		return sql;
	}
	
	public static Map<String, Object> getTransaccionesItem(Integer transaccionId, Integer page, Integer size, Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("vista");
			
			Statement statement = conn.createStatement();
			
			String sql = getSQLTransaccionesItem(databaseProperties, transaccionId, page, size);
			
			//result.put("count", SQLUtil.getCountFromSQL(statement, sql)); 

			//sql = getSQLListDesPaginado(databaseProperties, sql, q, page, size);
			if (databaseProperties.get("database.type").equals("oracle")) {
				//sql = getOracleSQLPaginado(sql, page, size);
			}
			System.out.print("\n" + sql + " ");
			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> listadoTransaccionesItem = SQLUtil.convertResultSetToList(rs);
			System.out.println("listadoTransaccionesItem: " + listadoTransaccionesItem + " ");
			
			result.put("success", true);
			result.put("result", listadoTransaccionesItem);
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
			
		}
		return result;
	}
			
	private static String getSQLTransaccionesItem(Map<String, String> databaseProperties, Integer transaccionId, Integer page, Integer size) {
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transacctions_table");
		String sql = "";
		
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT * \n"
					+ "FROM " + tableName + " \n"
					+ "WHERE "
					
					+ "transaccion_id = " + transaccionId + " \n"
					
					+ "ORDER BY establecimiento DESC, punto DESC, numero DESC \n";	
		} else {
			
			tableName = databaseProperties.get("database.dbf.facturasend_file");
			tableName = tableName.substring(0, tableName.indexOf(".dbf"));

			sql = "SELECT tra_id, i_descrip, i_cantidad, i_pre_uni, i_descue, \n"
					+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='CDC' LIMIT 1) AS cdc, \n"
					+ "CAST((SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='ESTADO' LIMIT 1) AS INTEGER) AS estado, \n"
					+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='ERROR' LIMIT 1) AS error, \n"
					+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='QR' LIMIT 1) AS qr \n"

					+ "FROM " + tableName + " vp \n"
					+ "WHERE \n"
					
					+ "tra_id = " + transaccionId + " \n"
					
					+ "ORDER BY estable DESC, punto DESC, numero DESC \n";
		}
				
		return sql;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	/*
	 * 
	 	public static List<Map<String, Object>> formasPagosByTransaccion(Integer tipoDocumento, Integer transaccionId, Map<String, String> databaseProperties) throws Exception{
		
		System.out.println("Obteniendo conexion");
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
		
		System.out.println("conexion obtenida." );
		
		Statement statement = conn.createStatement();
		
		String sql = formasPagosSQLByTransaccion(databaseProperties, tipoDocumento, transaccionId);
		System.out.print("\n" + sql + " ");
		ResultSet rs = statement.executeQuery(sql);
		
		result = SQLUtil.convertResultSetToList(rs);
		System.out.println("result: " + result);
		return result;
	}
			
	private static String formasPagosSQLByTransaccion(Map<String, String> databaseProperties, Integer tipoDocumento, Integer transaccionId) {
		String paymentTableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".payment_view");

		String sql = "";
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT * \n"
				+ "FROM " + paymentTableName + " \n"
				+ "WHERE 1=1 \n"
				+ "AND tipo_documento = " + tipoDocumento + " \n"
				+ "AND transaccion_id = " + transaccionId + " \n"
				+ "";
		} else {
			sql = "SELECT * \n"
					+ "FROM " + paymentTableName + " \n"
					+ "WHERE 1=1 \n"
					+ "AND tip_doc = " + tipoDocumento + " \n"
					+ "AND tra_id = " + transaccionId + " \n"
					+ "";
		}
		return sql;
	}
	 
	 */
	
	
	
	/**
	 * Recibe un SQL y agrega sobre el mismo la paginacion de Oracle
	 * @param sql
	 * @param page
	 * @param size
	 * @return
	 */
	public static String getOracleSQLPaginado(String sql, Integer page, Integer size) {
		
		//Paginacion Oracle
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
		return sql;
	}
	
	/**
	 * Recibe un SQL y agrega sobre el mismo la paginacion de Postgres
	 * @param sql
	 * @param page
	 * @param size
	 * @return
	 */
	public static String getPostgreSQLPaginado(String sql, Integer page, Integer size) {
		
		//Paginacion Oracle
		sql += " LIMIT " + size + " OFFSET " + (page == 1 ? page : (((page-1) * size) + 1)) + " \n";
		return sql;
	}

	public static Object getValueForKey(Map<String, Object> map, String key1) {
		
		return getValueForKey( map, key1, null);
	}
	
	public static Object getValueForKey(Map<String, Object> map, String key1, String key2) {
		if (map.get(key1) != null) {
			return map.get(key1);
		}
		if (map.get(key1.toUpperCase()) != null) {
			return map.get(key1.toUpperCase());
		}
		if (key2 != null) {
			if (map.get(key2) != null) {
				return map.get(key2);
			}
			if (map.get(key2.toUpperCase()) != null) {
				return map.get(key2.toUpperCase());
			}			
		}
		return null;
	}
	
	/**
	 * Busca en el map o archivo de propiedades un registro cuyo valor 
	 * coincida con el que se pasa como parámetro.
	 * 
	 * La clave encontrada debe coincidir tambien con el prefijo keyPrefixFilter para retornar
	 * 
	 * @param map
	 * @param keyPrefixFilter
	 * @param value
	 * @return
	 */
	public static String findKeyByValue(Map<String, Object> map, String keyPrefixFilter, Object valueToFind) {
		String result = null;
		
		Iterator itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+"";
			Object value = e.getValue();
			
			if ( value.equals(valueToFind) && key.startsWith(keyPrefixFilter)) {
				result = key;
			}
		}
		
		return result;
	}
	

	public static String findKeyByValueInProperties(Map<String, String> map, String keyPrefixFilter, String valueToFind) {
		String result = null;
		
		Iterator itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+"";
			Object value = e.getValue();
			
			if ( value.equals(valueToFind) && key.startsWith(keyPrefixFilter)) {
				result = key;
			}
		}
		
		return result;
	}


	public static String getEstadoDescripcion(int estado) {
		String returnValue;
		switch (estado) {
		case 0:
			returnValue= "Generado";
			break;
		case -1:
			returnValue= "Borrador";
			break;
		case 2:
			returnValue= "Aprobado";
			break;
		case 3:
			returnValue= "Aprobado c/ Error";
			break;
		case 4:
			returnValue= "Rechazado";
			break;
		case 98:
			returnValue= "Inexistente";
			break;
		case 99:
			returnValue= "Cancelado";
			break;
		default:
			returnValue= "No integrado";
			break;
		}
		return returnValue;
	}
}