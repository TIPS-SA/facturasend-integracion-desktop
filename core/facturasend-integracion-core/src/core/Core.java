package core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import connect.BDConnect;
import connect.FSConnect;
import connect.SQLConnection;
import util.HttpUtil;

public class Core {
	
	private static Gson gson = new Gson();
	public static Log log = LogFactory.getLog(Core.class);
	
	public static Map<String, Object> listDes(String q, Integer tipoDocumento, Integer page, Integer size, Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
			
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static List<Map<String, Object>> formasPagosByTransaccion(Integer tipoDocumento, Integer transaccionId, Map<String, String> databaseProperties) throws Exception{
		
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
		
		Statement statement = conn.createStatement();
		
		String sql = formasPagosSQLByTransaccion(databaseProperties, tipoDocumento, transaccionId);
		System.out.println("" + sql);
		ResultSet rs = statement.executeQuery(sql);
		
		result = SQLUtil.convertResultSetToList(rs);
			
		return result;
	}
			
	private static String formasPagosSQLByTransaccion(Map<String, String> databaseProperties, Integer tipoDocumento, Integer transaccionId) {
		String tableName = databaseProperties.get("database.payment_view");

		String sql = "SELECT * \n"
				+ "FROM " + tableName + " \n"
				+ "WHERE 1=1 \n"
				+ "AND tipo_documento = " + tipoDocumento + " \n"
				+ "AND id = " + transaccionId + " \n"
				+ "";		
		return sql;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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

				transaccionIdString = "";
				for (Map<String, Object> map : obtener50registrosNoIntegradosListMap) {
					transaccionIdString += map.get(getFieldName("transaccion_id", databaseProperties)) + ",";
				}
				transaccionIdString += "";
				
				//System.out.println(transaccionIdString);
				
			} else {
				throw new Exception(obtener50registrosNoIntegradosMap.get("error")+"");
			}
			
			
			
			//De acuerdo a los transaccion_id obtendidos, busca todos los registros relacionados.
			String transaccionIdStringInClause = "(" + transaccionIdString + "-1)";
			Map<String, Object> documentosParaEnvioMap = procesarTransacciones(transaccionIdStringInClause, databaseProperties);
			List<Map<String, Object>> documentosParaEnvioList = null;
			
			if (Boolean.valueOf(documentosParaEnvioMap.get("success")+"") == true) {
				documentosParaEnvioList = (List<Map<String, Object>>)documentosParaEnvioMap.get("result");
				//System.out.println("resultado lote 2 principal " + documentosParaEnvioList);
			} else {
				throw new Exception(documentosParaEnvioMap.get("error")+"");

			}
			
			
			
			//Generar JSON de documentos electronicos.
			List<Map<String, Object>> documentosParaEnvioJsonMap = DocumentoElectronicoCore.generarJSONLote(transaccionIdString.split(","), documentosParaEnvioList, databaseProperties);

			Map header = new HashMap();
			header.put("Authorization", "Bearer api_key_" + databaseProperties.get("facturasend.token"));
			String url = databaseProperties.get("facturasend.url");
			if (databaseProperties.get("facturasend.sincrono").equalsIgnoreCase("S")) {
				url += "/de/create";
			} else {
				url += "/lote/create";
			}
			
			Map<String, Object> resultadoJson = HttpUtil.invocarRest(url, "POST", gson.toJson(documentosParaEnvioJsonMap), header);
			
			if (resultadoJson != null) {
				
				String tableToUpdate = databaseProperties.get("database.facturasend_table");
				if (Boolean.valueOf(resultadoJson.get("success")+"") == true ) {

					List<Map<String, Object>> deList = (List<Map<String, Object>>)resultadoJson.get("deList");

					for (int i = 0; i < documentosParaEnvioJsonMap.size(); i++) {
						Map<String, Object> jsonDeGenerado = documentosParaEnvioJsonMap.get(i);
						Map<String, Object> viewRec = documentosParaEnvioList.get(i);

						Map<String, Object> respuestaDE = deList.get(i);	//Utiliza el mismo Indice de List de Json
								
						//Borrar registros previamente cargados, para evitar duplicidad
						borrarPorTransaccionId(viewRec, databaseProperties);

						//Guarda los datos en la Tabla, asi mismo como se obtiene la respuesta de facturasend
						guardarFacturaSendData(viewRec, respuestaDE, databaseProperties);
					}

					
				} else {
					//Si hay errores
					//log.debug(arg0);
					if (resultadoJson.get("errores") != null) {
						List<Map<String, Object>> errores = (List<Map<String, Object>>)resultadoJson.get("errores");


						for (int i = 0; i < documentosParaEnvioJsonMap.size(); i++) {
							Map<String, Object> jsonDeGenerado = documentosParaEnvioJsonMap.get(i);
							Map<String, Object> viewRec = documentosParaEnvioList.get(i);
							
							
							for (int j = 0; j < errores.size(); j++) {
								if (i == j) {
									
									//Borrar registros previamente cargados, para evitar duplicidad
									borrarPorTransaccionId(viewRec, databaseProperties);

									Map<String, Object> datosGuardar = new HashMap<String, Object>();
									datosGuardar.put("error", errores.get(j).get("error") + "");
									
									guardarFacturaSendData(viewRec, datosGuardar, databaseProperties);
								}
							}	
						}
						
					}
				}
			}
			
			//
		} catch (Exception e) {
			e.printStackTrace();
			obtener50registrosNoIntegradosMap.put("success", false);
			obtener50registrosNoIntegradosMap.put("error", e.getMessage());
		}
		//Cambiar éste resultado, por el resultado del lote
		
		return obtener50registrosNoIntegradosMap;
	}
	
	/**
	 * 
	 * @param de
	 * @param error
	 * @param databaseProperties
	 * @return
	 */
	public static Integer borrarPorTransaccionId(Map<String, Object> de, Map<String, String> databaseProperties) throws Exception{
		System.out.println("de " + de);
		Integer result = 0;

		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
		
		String tableToUpdate = databaseProperties.get("database.facturasend_table");
		
		String pk = "";
		//Buscar el campo que relaciona con el transaccion_id
		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+"";
			String value = e.getValue()+""; 
			if ( value.equalsIgnoreCase("transaccion_id") || value.equalsIgnoreCase("tra_id")) {
				pk = key.substring("database.facturasend_table.field.".length(), key.length()) + "";
			}
		}
		
		String sql = "DELETE FROM " + tableToUpdate + " WHERE " + pk + " = "+ getValueForKey(de, "transaccion_id", "tra_id");
		
		System.out.println("" + sql);
		PreparedStatement statement = conn.prepareStatement(sql);

		result = statement.executeUpdate();

		return result;
	}
	
	/**
	 * 
	 * @param de
	 * @param error
	 * @param databaseProperties
	 * @return
	 */
	public static Integer guardarFacturaSendData(Map<String, Object> de, Map<String, Object> datosGuardar, Map<String, String> databaseProperties) throws Exception{
		System.out.println("de " + de);
		Integer result = 0;
	
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
		
		String tableToUpdate = databaseProperties.get("database.facturasend_table");
		
		String sql = "INSERT INTO " + tableToUpdate + " (";
		
		//Buscar fields adicionales
		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+""; 
			if ((key).startsWith("database.facturasend_table.field.")) {
				sql += key.substring("database.facturasend_table.field.".length(), key.length()) + ", ";
			}
		}
				
		sql += "moli_name, ";
		sql += "moli_value) VALUES ";
		
		//Buscar fields value adicionales
		Iterator itrDato = datosGuardar.entrySet().iterator();
		while (itrDato.hasNext()) {	//Recorre los datos que se tienen que guardar, cdc, numero, estado, error, etc
			Map.Entry eDato = (Map.Entry)itrDato.next();

			sql += "( ";
			itr = databaseProperties.entrySet().iterator();
			while (itr.hasNext()) {	//Recorre los campos de la tabla a almacenar
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+""; 
				String value = e.getValue()+"";
				if ((key).startsWith("database.facturasend_table.field.")) {
					if (value.startsWith("@SQL(")) {
						//Si es un SQL debe colocar directo la sentencia
						sql += "" + value.substring(4, value.length() ) +  ", ";
					} else {
						sql += "?, ";	
					}
				}
			}
		
			sql += "?, ?), ";
		}
		
		//Al final retirar la coma restante
		sql = sql.substring(0, sql.length() - 2);
		
		System.out.println("" + sql);
		PreparedStatement statement = conn.prepareStatement(sql);

		//Buscar fields value adicionales
		itrDato = datosGuardar.entrySet().iterator();
		while (itrDato.hasNext()) {	//Recorre los datos que se tienen que guardar, cdc, numero, estado, error, etc
			Map.Entry eDato = (Map.Entry)itrDato.next();
			
			itr = databaseProperties.entrySet().iterator();
			int f = 1;
			while (itr.hasNext()) {	//Recorre los campos de la tabla a almacenar
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+""; 
				String value = e.getValue()+"";
				if ((key).startsWith("database.facturasend_table.field.")) {
					if (value.startsWith("@SQL(")) {
						//Como ya coloco el SQL, entonces aqui no inserta los parámetros.
						
					} else {
						statement.setObject(f++, getValueForKey(de, e.getValue()+""));
					}
				}
			}
	
			statement.setString(f++, eDato.getKey() + "");
			statement.setString(f++, eDato.getValue() + "");
			System.out.println("fINALMENTE K VALE " + f );
		}		
		result = statement.executeUpdate();
		
		String posUpdateSQL = databaseProperties.get("database.facturasend_table.pos_update_sql");
		if (posUpdateSQL != null) {
			PreparedStatement statement2 = conn.prepareStatement(posUpdateSQL);
			statement2.executeQuery();
			
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param de
	 * @param error
	 * @param databaseProperties
	 * @return
	 */
	/*public static Integer guardarError(Map<String, Object> de, String error, Map<String, String> databaseProperties) throws Exception{
		System.out.println("de " + de);
		Integer result = 0;
		//try {
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
			
			
			
			String tableToUpdate = databaseProperties.get("database.facturasend_table");
			
			
			String sql = "INSERT INTO " + tableToUpdate + " (";
			
			//Buscar fields adicionales
			Iterator itr = databaseProperties.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+""; 
				if ((key).startsWith("database.facturasend_table.field.")) {
					sql += key.substring("database.facturasend_table.field.".length(), key.length()) + ", ";
				}
			}
					
			sql += "moli_name, ";
			sql += "moli_value) VALUES ( ";
			
			//Buscar fields value adicionales
			itr = databaseProperties.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+""; 
				if ((key).startsWith("database.facturasend_table.field.")) {
					sql += "?, ";
				}
			}
			
			sql += "?, ?)";
			
			System.out.println("" + sql);
			PreparedStatement statement = conn.prepareStatement(sql);

			//Buscar fields value adicionales
			itr = databaseProperties.entrySet().iterator();
			int f = 1;
			while (itr.hasNext()) {
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+""; 
				if ((key).startsWith("database.facturasend_table.field.")) {
					statement.setObject(f++, getValueForKey(de, e.getValue()+""));
				}
			}

			statement.setString(f++, "Error");
			statement.setString(f++, error);
			
			result = statement.executeUpdate();
			
			
			
		/*} catch (Exception e) {
			
			
		}*/
		//return result;
	//}
	
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
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
			
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
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
			
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
}
