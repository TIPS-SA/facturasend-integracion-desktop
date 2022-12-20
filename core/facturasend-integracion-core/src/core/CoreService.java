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
import org.h2.jdbc.JdbcSQLSyntaxErrorException;

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
	
	public static Map<String, Object> getTransaccionesList(String q, Integer tipoDocumento, Integer page, Integer size, boolean inverso, boolean incluirInutilizados, Map<String, String> databaseProperties) {
		log.info(databaseProperties);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("vista");
			
			Statement statement = conn.createStatement();
			
			String sql = getSQLTransaccionesList(databaseProperties, q, tipoDocumento, page, size, incluirInutilizados);
			
			//if ( ! databaseProperties.get("database.type").equals("dbf")) {
				//Si es DBF el count va realizar despues
				//result.put("count", SQLUtil.getCountFromSQL(statement, sql));
			//}


			
			if (databaseProperties.get("database.type").equals("dbf")) {
				/*if (inverso) {
					//Agrega el orden inverso, que seria sin utilizar el DESC
					sql += "ORDER BY estable, punto, numero";
				}*/
				log.info("\nReload ");
				Statement st = conn.createStatement();
				st.execute("reload '" + databaseProperties.get("database.dbf.parent_folder") + "/" + databaseProperties.get("database.dbf.transaccion_table") + "'");

				//Despues de la Carga consulta la Cantidad

			//} else {
				/*if (inverso) {
					//Agrega el orden inverso, que seria sin utilizar el DESC
					sql += "ORDER BY establecimiento, punto, numero";
				}*/
			}

			result.put("count", SQLUtil.getCountFromSQL(statement, sql)); 

			//sql = getSQLListDesPaginado(databaseProperties, sql, q, page, size);
			if (databaseProperties.get("database.type").equals("oracle")) {
				sql = getOracleSQLPaginado(sql, page, size);
			} else if (databaseProperties.get("database.type").equals("postgres")) {
				sql = getPostgreSQLPaginado(sql, page, size);
			} else if (databaseProperties.get("database.type").equals("dbf")) {
				sql = getPostgreSQLPaginado(sql, page, size);
			}
			
			if (inverso) {
				//Agrega el orden inverso, que seria sin utilizar el DESC
				if (databaseProperties.get("database.type").equals("dbf")) {
					sql += "ORDER BY estable, punto, numero";
				} else {
					sql += "ORDER BY establecimiento, punto, numero";
				}
			}
			
			log.debug("\n" + sql + " ");
			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> transaccionesList = SQLUtil.convertResultSetToList(rs);
			
			log.info("transaccionesList.size: " + transaccionesList.size() + " ");
			log.debug("transaccionesList: " + transaccionesList + " ");
			result.put("success", true);
			result.put("result", transaccionesList);

		} catch (Exception e) {
			//log.error(e); 	//Mejor asi, solo que no esta mostrando.
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
			
		}
		return result;
	}
			
	private static String getSQLTransaccionesList(Map<String, String> databaseProperties, String q, Integer tipoDocumento, Integer page, Integer size, boolean incluirInutilizados) {
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transaction_table_read");
		
		//String filtroInutilizado = " ";
		String filtroInutilizado = "AND (evento IS NULL OR TRIM(evento) = '' OR evento = 'Cancelar' ) \n";
		/*if (!incluirInutilizados) {
			filtroInutilizado += "AND evento != 'Inutilizar' ";
		}
		filtroInutilizado += ") ";**/
		
		String sql = "";
		if ( ! databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, \n"
				+ "cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, \n"
				+ "establecimiento, punto, numero, serie, total, cdc, estado, error, pausado, clasific \n"
				+ "FROM " + tableName + " \n"
				+ "WHERE "
				+ "( \n"
				+ "	(establecimiento || '-' || punto || '-' || numero || COALESCE(serie, '')) LIKE '%" + q + "%' \n" 
				+ "	OR UPPER(COALESCE(cliente_ruc, '')) LIKE '%" + q.toUpperCase() + "%' \n"
				+ "	OR UPPER(COALESCE(cliente_documento_numero, '')) LIKE '%" + q.toUpperCase() + "%' \n"
				+ "	OR UPPER(cliente_razon_social) LIKE '%" + q.toUpperCase() + "%' \n"
				+ ") \n"
				+ filtroInutilizado
				+ "AND tipo_documento = " + tipoDocumento + " \n"
				+ "GROUP BY transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, \n"
				+ "cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, \n"
				+ "establecimiento, punto, numero, serie, total, cdc, estado, error, pausado, clasific \n"
				+ "ORDER BY establecimiento DESC, punto DESC, numero DESC\n";
		} else {
			//DBF
			
			boolean obtenerCdcEstadoPausadoPorSubSelect = true;
			String transactionTableName = databaseProperties.get("database.dbf.transaccion_table");
			transactionTableName = transactionTableName.substring(0, transactionTableName.indexOf(".dbf"));

			String facturaSendTableName = databaseProperties.get("database.dbf.facturasend_table");
			facturaSendTableName = facturaSendTableName.substring(0, facturaSendTableName.indexOf(".dbf"));
			String facturaSendTableKey = databaseProperties.get("database.dbf.facturasend_table.key");
			String facturaSendTableValue = databaseProperties.get("database.dbf.facturasend_table.value");
			
			sql = "SELECT tra_id, tip_doc, descrip, observa, fecha, moneda, \n"
					+ "c_contribu, c_ruc, c_doc_num, c_raz_soc, \n"
					+ "estable, punto, numero, serie, total, evento, \n";
			
			if (obtenerCdcEstadoPausadoPorSubSelect) {
				sql += "(SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='CDC' LIMIT 1) AS cdc, \n"
					+ "CAST((SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='ESTADO' LIMIT 1) AS INTEGER) AS estado, \n"
					+ "(SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='ERROR' LIMIT 1) AS error, \n"
					+ "(SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='PAUSADO' LIMIT 1) AS pausado \n";
					
			} else {
				sql += "cdc, estado, error, qr ";
			}
			
			sql += "FROM " + transactionTableName + " vp \n"
				+ "WHERE "
				+ "( \n"
				+ "	(estable || '-' || punto || '-' || numero || COALESCE(serie, '')) LIKE '%" + q + "%' \n" 
				+ "	OR UPPER(COALESCE(c_ruc, '')) LIKE '%" + q.toUpperCase() + "%' \n"
				+ "	OR UPPER(COALESCE(c_doc_num, '')) LIKE '%" + q.toUpperCase() + "%' \n"
				+ "	OR UPPER(c_raz_soc) LIKE '%" + q.toUpperCase() + "%' \n"
				+ ") \n"
				+ filtroInutilizado 
				+ "AND tip_doc = " + tipoDocumento + " \n"
				+ "GROUP BY tra_id, tip_doc, descrip, observa, fecha, moneda, \n"
				+ "c_contribu, c_ruc, c_doc_num, c_raz_soc, \n"
				+ "estable, punto, numero, serie, total, cdc, estado, error, pausado, evento \n"
				+ "ORDER BY estable DESC, punto DESC, numero DESC \n"; 

			
		}
		
		return sql;
	}
	
	public static Map<String, Object> getTransaccionesItem(Integer transaccionId, Integer tipoDocumento, Integer page, Integer size, Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("vista");
			
			Statement statement = conn.createStatement();
			
			String sql = getSQLTransaccionesItem(databaseProperties, transaccionId, tipoDocumento, page, size);
			
			log.info("\n" + sql + " ");
			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> listadoTransaccionesItem = SQLUtil.convertResultSetToList(rs);
			log.info("listadoTransaccionesItem: " + listadoTransaccionesItem + " ");
			
			result.put("success", true);
			result.put("result", listadoTransaccionesItem);
			
		} catch (Exception e) {
			log.info("Error 2 " + e);
			result.put("success", false);
			result.put("error", e.getMessage());
			
		}
		return result;
	}
			
	private static String getSQLTransaccionesItem(Map<String, String> databaseProperties, Integer transaccionId, Integer tipoDocumento, Integer page, Integer size) {
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transaction_table_read");
		String sql = "";
		
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT * \n"
					+ "FROM " + tableName + " \n"
					+ "WHERE "
					
					+ "transaccion_id = " + transaccionId + " \n"
					
					+ "ORDER BY establecimiento DESC, punto DESC, numero DESC \n";	
		} else {
			
			boolean obtenerCdcEstadoPausadoPorSubSelect = true;
			String transactionTableName = databaseProperties.get("database.dbf.transaccion_table");
			transactionTableName = transactionTableName.substring(0, transactionTableName.indexOf(".dbf"));

			String facturaSendTableName = databaseProperties.get("database.dbf.facturasend_table");
			facturaSendTableName = facturaSendTableName.substring(0, facturaSendTableName.indexOf(".dbf"));
			String facturaSendTableKey = databaseProperties.get("database.dbf.facturasend_table.key");
			String facturaSendTableValue = databaseProperties.get("database.dbf.facturasend_table.value");
			
			sql = "SELECT tra_id, \ntip_doc, \ni_descrip, \ni_cantidad, \ni_pre_uni, \ni_descue, \nmoneda, \n";
			
			if (obtenerCdcEstadoPausadoPorSubSelect) {
				sql += "(SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='CDC' LIMIT 1) AS cdc, \n"
					+ "CAST((SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='ESTADO' LIMIT 1) AS INTEGER) AS estado, \n"
					+ "(SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='ERROR' LIMIT 1) AS \"error\", \n"
					+ "(SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='QR' LIMIT 1) AS \"qr\" \n";
					
			} else {
				sql += "cdc, estado, error, qr \n";
			}
			
			sql += ""
				+ "FROM " + transactionTableName + " vp \n"
				+ "WHERE \n"
				
				+ "vp.tra_id = " + transaccionId + " \n"
				+ "AND vp.tip_doc = " + tipoDocumento + " \n"
				
				+ "ORDER BY estable DESC, punto DESC, numero DESC \n";
		}
				
		return sql;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	/*
	 * 
	 	public static List<Map<String, Object>> formasPagosByTransaccion(Integer tipoDocumento, Integer transaccionId, Map<String, String> databaseProperties) throws Exception{
		
		log.info("Obteniendo conexion");
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
		
		log.info("conexion obtenida." );
		
		Statement statement = conn.createStatement();
		
		String sql = formasPagosSQLByTransaccion(databaseProperties, tipoDocumento, transaccionId);
		log.info("\n" + sql + " ");
		ResultSet rs = statement.executeQuery(sql);
		
		result = SQLUtil.convertResultSetToList(rs);
		log.info("result: " + result);
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
		//sql += " LIMIT " + size + " OFFSET " + (page == 1 ? (page-1) : ((((page-1) * size) + 1))-1) + " \n";
		sql += " LIMIT " + size + " OFFSET " + (page == 1 ? (page-1) : ((((page-1) * size) + 1))-1) + " \n";
		sql = "SELECT * FROM (" + sql + ") ";
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
		if (map.get(key1.toLowerCase()) != null) {
			return map.get(key1.toLowerCase());
		}
		if (key2 != null) {
			if (map.get(key2) != null) {
				return map.get(key2);
			}
			if (map.get(key2.toUpperCase()) != null) {
				return map.get(key2.toUpperCase());
			}
			if (map.get(key2.toLowerCase()) != null) {
				return map.get(key2.toLowerCase());
			}	
		}
		return null;
	}
	
	/**
	 * Retorna la key que no contiene valores nulos, de acuerdo a las 2 keys pasadas como parmetro.
	 * @param map
	 * @param key1
	 * @param key2
	 * @return
	 */
	public static Object getKeyExists(Map<String, Object> map, String key1, String key2) {
		if (map.get(key1) != null) {
			return key1.toLowerCase();
		}
		if (map.get(key1.toUpperCase()) != null) {
			return key1.toLowerCase();
		}
		if (map.get(key1.toLowerCase()) != null) {
			return key1.toLowerCase();
		}
		if (map.get(key2) != null) {
			return key2.toLowerCase();
		}
		if (map.get(key2.toUpperCase()) != null) {
			return key2.toLowerCase();
		}
		if (map.get(key2.toLowerCase()) != null) {
			return key2.toLowerCase();
		}	
		return null;
	}
	
	public static Object getValueFromMapCaseInsensitive(Map<String, String> map, String keyToFind) {
		Object valueReturn = null;
		Iterator itr = map.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+"";
			
			if ( key.equalsIgnoreCase(keyToFind)) {
				valueReturn = e.getValue();
			}
		}
		return valueReturn;
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
	
	public static int getTipoDocumentoNro(String documento) {
		int returnValue = 0;
		switch (documento) {
		case "Factura Electronica":
			returnValue= 1;
			break;
		case "Auto Factura Electronica":
			returnValue= 4;
			break;
		case "Nota Credito":
			returnValue= 5;
			break;
		case "Nota Debito":
			returnValue= 6;
			break;
		case "Nota Remision":
			returnValue= 7;
			break;
		}
		return returnValue;
	}
}
