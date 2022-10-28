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

			//sql = getSQLListDesPaginado(databaseProperties, sql, q, page, size);
			if (databaseProperties.get("database.type").equals("oracle")) {
				sql = getOracleSQLPaginado(sql, page, size);
			}
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
	
	/*private static String getSQLListDesPaginado(Map<String, String> databaseProperties, String sql, String q, Integer page, Integer size) {
		
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
	}*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Paso 1. Proceso que inicia la integración, dependiendo del tipo de documento.
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static Map<String, Object> iniciarIntegracion(Integer tipoDocumento, Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		Map<String, Object> obtener50registrosNoIntegradosMap = obtener50registrosNoIntegrados(tipoDocumento, databaseProperties);
		
		try {
			
			String transaccionIdString = "(-1)";
			if (Boolean.valueOf(obtener50registrosNoIntegradosMap.get("success")+"") == true) {
				List<Map<String, Object>> obtener50registrosNoIntegradosListMap = (List<Map<String, Object>>)obtener50registrosNoIntegradosMap.get("result");

				transaccionIdString = "(";
				for (Map<String, Object> map : obtener50registrosNoIntegradosListMap) {
					transaccionIdString += map.get(getFieldName("transaccion_id", databaseProperties)) + ", ";
				}
				transaccionIdString += "-1)";
				
				//System.out.println(transaccionIdString);
				
			} else {
				throw new Exception(obtener50registrosNoIntegradosMap.get("error")+"");
			}
			
			
			//De acuerdo a los transaccion_id obtendidos, busca todos los registros relacionados.
			Map<String, Object> documentosParaEnvioMap = procesarTransacciones(transaccionIdString, databaseProperties);
			if (Boolean.valueOf(documentosParaEnvioMap.get("success")+"") == true) {
				List<Map<String, Object>> documentosParaEnvioList = (List<Map<String, Object>>)documentosParaEnvioMap.get("result");
				System.out.println("resultado lote 2 principal " + documentosParaEnvioList);
			} else {
				throw new Exception(documentosParaEnvioMap.get("error")+"");

			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			obtener50registrosNoIntegradosMap.put("success", false);
			obtener50registrosNoIntegradosMap.put("error", e.getMessage());
		}
		//Cambiar éste resultado, por el resultado del lote
		
		return obtener50registrosNoIntegradosMap;
	}
	
	/**
	 * Paso 1.1 - Obtener 50 registros no integrados
	 * 
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static Map<String, Object> obtener50registrosNoIntegrados(Integer tipoDocumento, Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getConnection(BDConnect.fromMap(databaseProperties));
			
			Statement statement = conn.createStatement();
			
			String sql = obtenerSQL50registrosNoIntegrados(databaseProperties, tipoDocumento);
			
			//result.put("count", SQLUtil.getCountFromSQL(statement, sql));

			if (databaseProperties.get("database.type").equals("oracle")) {
				
				Integer rowsLoteRequest = 50;
				if (databaseProperties.get("facturasend.rows_lote_request") != null) {
					rowsLoteRequest = Integer.valueOf(databaseProperties.get("facturasend.rows_lote_request"));
				}
				if (rowsLoteRequest > 50) {
					throw new Exception("Cantidad máxima de documentos por lote = 50 (facturasend.rows_lote_request)");
				}
				
				sql = getOracleSQLPaginado(sql, 1, rowsLoteRequest);
			}
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
			
	/**
	 * Paso 1.2 - Obtiene los registros mas viejos.
	 * 
	 * @param databaseProperties
	 * @param tipoDocumento
	 * @return
	 */
	private static String obtenerSQL50registrosNoIntegrados(Map<String, String> databaseProperties, Integer tipoDocumento) {
		String tableName = databaseProperties.get("database.transaction_view");

		String sql = "SELECT transaccion_id \n"
						+ "FROM " + tableName + " \n"
						+ "WHERE 1=1 \n"
						+ "AND tipo_documento = " + tipoDocumento + " \n"
						+ "GROUP BY transaccion_id, numero \n"
						+ "ORDER BY numero DESC \n";		
		return sql;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Paso 2 - Recibe los transaccion_id de 50 o N registros que hay que integrar
	 * y busca todos esos registros de la base de datos 
	 * 
	 * @param transaccionIdString	Formato (332, 832, 434, 642,...)
	 * @param databaseProperties
	 * @return
	 */
	public static Map<String, Object> procesarTransacciones(String transaccionIdString, Map<String, String> databaseProperties)  {
		
		Map<String, Object> obtenerTransaccionesMap = procesarTransaccionesParaEnvioLote(transaccionIdString, databaseProperties);

		try {
			
			if (Boolean.valueOf(obtenerTransaccionesMap.get("success")+"") == true) {
				List<Map<String, Object>> obtenerTransaccionesListMap = (List<Map<String, Object>>)obtenerTransaccionesMap.get("result");

				
				
				//System.out.println("resultado paso 2 " + obtenerTransaccionesListMap);
				
				
				//return returnData;
			} else {
				throw new Exception(obtenerTransaccionesMap.get("error")+"");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			obtenerTransaccionesMap.put("success", false);
			obtenerTransaccionesMap.put("error", e.getMessage());
		}
		return obtenerTransaccionesMap;
	}
	
	/**
	 * Paso 2.1 - 
	 * 
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static Map<String, Object> procesarTransaccionesParaEnvioLote(String transaccionIdString, Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getConnection(BDConnect.fromMap(databaseProperties));
			
			Statement statement = conn.createStatement();
			
			String sql = obtenerTransaccionesParaEnvioLote(databaseProperties, transaccionIdString);
			
			//result.put("count", SQLUtil.getCountFromSQL(statement, sql));

			
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
	
	/**
	 * Paso 2.2 - Obtiene el SQL para recuperar las transacciones con las transaccion_id especificas.
	 * 
	 * @param databaseProperties
	 * @param tipoDocumento
	 * @return
	 */
	private static String obtenerTransaccionesParaEnvioLote(Map<String, String> databaseProperties, String transaccionIdString) {
		String tableName = databaseProperties.get("database.transaction_view");

		String sql = "SELECT * \n"
						+ "FROM " + tableName + " \n"
						+ "WHERE 1=1 \n"
						+ "AND transaccion_id IN " + transaccionIdString + " \n"
						+ "ORDER BY numero DESC \n";		
		return sql;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Recibe un SQL y agrega sobre el mismo la paginacion de Oracle
	 * @param sql
	 * @param page
	 * @param size
	 * @return
	 */
	private static String getOracleSQLPaginado(String sql, Integer page, Integer size) {
		
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
	
	public static String getFieldName(String fieldName, Map<String, String> databaseProperties) {
		boolean fieldsInUpperCase = databaseProperties.get("database.fields_in_uppercase").equals("true");
		if (fieldsInUpperCase) {
			fieldName = fieldName.toUpperCase();
		}
		return fieldName;
	}
	
}
