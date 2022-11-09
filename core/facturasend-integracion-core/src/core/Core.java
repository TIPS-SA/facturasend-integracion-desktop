package core;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import util.HttpUtil;

public class Core {
	
	private static Gson gson = new Gson();
	private static Gson gsonPP = new GsonBuilder().setPrettyPrinting().create();
	public static Log log = LogFactory.getLog(Core.class);
	
	public static Map<String, Object> getTransaccionesList(String q, Integer tipoDocumento, Integer page, Integer size, Map<String, String> databaseProperties) {
		System.out.println(databaseProperties);
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
			
			Statement statement = conn.createStatement();
			
			String sql = getSQLTransaccionesList(databaseProperties, q, tipoDocumento, page, size);
			
			result.put("count", SQLUtil.getCountFromSQL(statement, sql)); 

			//sql = getSQLListDesPaginado(databaseProperties, sql, q, page, size);
			if (databaseProperties.get("database.type").equals("oracle")) {
				sql = getOracleSQLPaginado(sql, page, size);
			} else if (databaseProperties.get("database.type").equals("postgres")) {
				sql = getPostgreSQLPaginado(sql, page, size);
			} else if (databaseProperties.get("database.type").equals("dbf")) {
				sql = getPostgreSQLPaginado(sql, page, size);
			}
			
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
				+ "establecimiento, punto, numero, serie, total, cdc, estado, error \n"
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
				+ "establecimiento, punto, numero, serie, total, cdc, estado, error \n"
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
				+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='ESTADO' LIMIT 1) AS estado, "
				+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='ERROR' LIMIT 1) AS error "
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
				+ "estable, punto, numero, serie, total, cdc, estado, error \n"
				+ "ORDER BY estable DESC, punto DESC, numero DESC \n";			
		}
		
		return sql;
	}
	
	public static Map<String, Object> getTransaccionesItem(Integer transaccionId, Integer page, Integer size, Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
			
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
					+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='ESTADO' LIMIT 1) AS estado, \n"
					+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='ERROR' LIMIT 1) AS error, \n"
					+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='QR' LIMIT 1) AS qr \n"

					+ "FROM " + tableName + " vp \n"
					+ "WHERE \n"
					
					+ "tra_id = " + transaccionId + " \n"
					
					+ "ORDER BY estable DESC, punto DESC, numero DESC \n";
		}
				
		return sql;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static List<Map<String, Object>> formasPagosByTransaccion(Integer tipoDocumento, Integer transaccionId, Map<String, String> databaseProperties) throws Exception{
		
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
		
		Statement statement = conn.createStatement();
		
		String sql = formasPagosSQLByTransaccion(databaseProperties, tipoDocumento, transaccionId);
		System.out.print("\n" + sql + " ");
		ResultSet rs = statement.executeQuery(sql);
		
		result = SQLUtil.convertResultSetToList(rs);
		System.out.println("result: " + result);
		return result;
	}
			
	private static String formasPagosSQLByTransaccion(Map<String, String> databaseProperties, Integer tipoDocumento, Integer transaccionId) {
		String tableName = databaseProperties.get("database.payment_view");

		String sql = "";
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT * \n"
				+ "FROM " + tableName + " \n"
				+ "WHERE 1=1 \n"
				+ "AND tipo_documento = " + tipoDocumento + " \n"
				+ "AND tranasaccion_id = " + transaccionId + " \n"
				+ "";
		} else {
			sql = "SELECT * \n"
					+ "FROM " + tableName + " \n"
					+ "WHERE 1=1 \n"
					+ "AND tip_doc = " + tipoDocumento + " \n"
					+ "AND tra_id = " + transaccionId + " \n"
					+ "";
		}
		return sql;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Paso 1. Proceso que inicia la integración, dependiendo del tipo de documento.
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static Map<String, Object> pausarIniciar(Integer transaccionId, Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		
		try {
						
			guardarPausarIniciar(transaccionId, databaseProperties);

			
			//
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Cambiar éste resultado, por el resultado del lote
		
		return null;
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
					transaccionIdString += Core.getValueForKey(map, "transaccion_id", "tra_id") + ",";
				}
				transaccionIdString += "";
				
				//System.out.println(transaccionIdString);
				
			} else {
				throw new Exception(obtener50registrosNoIntegradosMap.get("error")+"");
			}
			
			
			
			//De acuerdo a los transaccion_id obtendidos, busca todos los registros relacionados.
			String transaccionIdStringInClause = "(" + transaccionIdString + "-1)";
			Map<String, Object> documentosParaEnvioMap = procesarTransacciones(transaccionIdStringInClause, databaseProperties);
			List<Map<String, Object>> documentoParaEnvioJsonMap = null;
			
			if (Boolean.valueOf(documentosParaEnvioMap.get("success")+"") == true) {
				documentoParaEnvioJsonMap = (List<Map<String, Object>>)documentosParaEnvioMap.get("result");
			} else {
				throw new Exception(documentosParaEnvioMap.get("error")+"");

			}
			
			
			
			//Generar JSON de documentos electronicos.
			List<Map<String, Object>> documentosParaEnvioJsonMap = DocumentoElectronicoCore.generarJSONLote(transaccionIdString.split(","), documentoParaEnvioJsonMap, databaseProperties);

			Map header = new HashMap();
			header.put("Authorization", "Bearer api_key_" + databaseProperties.get("facturasend.token"));
			String url = databaseProperties.get("facturasend.url");
			if (databaseProperties.get("facturasend.sincrono").equalsIgnoreCase("S")) {
				url += "/de/create";
			} else {
				url += "/lote/create?xml=true&qr=true";
			}
			
			Map<String, Object> resultadoJson = HttpUtil.invocarRest(url, "POST", gson.toJson(documentosParaEnvioJsonMap), header);
			
			if (resultadoJson != null) {
				
				//Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
				//connection.setAutoCommit(false);
				//conn.iniciar transaccion

				createTableFacturaSendData(databaseProperties);
				
				String tableToUpdate = databaseProperties.get("database.facturasend_table");
				if (Boolean.valueOf(resultadoJson.get("success")+"") == true ) {

					Map<String, Object> result = (Map<String, Object>)resultadoJson.get("result");
					
					List<Map<String, Object>> deList = (List<Map<String, Object>>)result.get("deList");
					
					for (int i = 0; i < documentosParaEnvioJsonMap.size(); i++) {
						Map<String, Object> jsonDeGenerado = documentosParaEnvioJsonMap.get(i);
						Map<String, Object> viewRec = documentoParaEnvioJsonMap.get(i);

						Map<String, Object> respuestaDE = deList.get(i);	//Utiliza el mismo Indice de List de Json
								
						//Borrar registros previamente cargados, para evitar duplicidad
						borrarPorTransaccionId(viewRec, databaseProperties);

						Map<String, Object> datosGuardar = new HashMap<String, Object>();
						datosGuardar.put("CDC", respuestaDE.get("cdc") + "");
						guardarFacturaSendData(viewRec, datosGuardar, databaseProperties);

						String estado = respuestaDE.get("estado") != null ? respuestaDE.get("estado") + "" : "0";
						Map<String, Object> datosGuardar1 = new HashMap<String, Object>();
						datosGuardar1.put("ESTADO", estado);
						guardarFacturaSendData(viewRec, datosGuardar1, databaseProperties);


						Map<String, Object> datosGuardar5 = new HashMap<String, Object>();
						datosGuardar5.put("TIPO", "Mayorista");
						guardarFacturaSendData(viewRec, datosGuardar5, databaseProperties);

						if ( ! databaseProperties.get("database.type").equals("dbf")) {
							Map<String, Object> datosGuardar3 = new HashMap<String, Object>();
							datosGuardar3.put("QR", respuestaDE.get("qr") + "");
							guardarFacturaSendData(viewRec, datosGuardar3, databaseProperties);

							Map<String, Object> datosGuardar4 = new HashMap<String, Object>();
							datosGuardar4.put("XML", respuestaDE.get("xml") + "");
							guardarFacturaSendData(viewRec, datosGuardar4, databaseProperties);
						} else {
							//El XML debe guardar en un archivo en el disco
							
						}
						

					}

					
				} else {
					//Si hay errores
					//log.debug(arg0);
					if (resultadoJson.get("errores") != null) {
						List<Map<String, Object>> errores = (List<Map<String, Object>>)resultadoJson.get("errores");


						for (int i = 0; i < documentosParaEnvioJsonMap.size(); i++) {
							Map<String, Object> jsonDeGenerado = documentosParaEnvioJsonMap.get(i);
							Map<String, Object> viewRec = documentoParaEnvioJsonMap.get(i);
							
							
							for (int j = 0; j < errores.size(); j++) {
								if (i == j) {
									
									//Borrar registros previamente cargados, para evitar duplicidad
									borrarPorTransaccionId(viewRec, databaseProperties);

									String error = (String)errores.get(j).get("error");
									if (databaseProperties.get("database.type").equals("dbf")) {
										if (error.length() > 254) {
											error = error.substring(0, 254);	
										}
									}
									Map<String, Object> datosGuardar = new HashMap<String, Object>();
									datosGuardar.put("ERROR", error);
									guardarFacturaSendData(viewRec, datosGuardar, databaseProperties);
									
									/*Map<String, Object> datosGuardar2 = new HashMap<String, Object>();
//									datosGuardar.put("JSON", gsonPP.toJson(viewRec) + "");
									System.out.println(viewRec);
									datosGuardar.put("JSON", gson.toJson(viewRec) + "");
									System.out.println("despues del error");
									guardarFacturaSendData(viewRec, datosGuardar2, databaseProperties);*/
								}
							}	
						}
						
					}
				}
				
				if (databaseProperties.get("database.type").equals("dbf")) {
					
					Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
					
					String sql = "save '" + databaseProperties.get("database.facturasend_table") + "' to '" + databaseProperties.get("database.dbf.parent_folder") + "'";
					//+ "\\saved' ";
					System.out.println("\n" + sql + " ");
					//PreparedStatement statement = conn.prepareStatement(sql);
					Statement statement = conn.createStatement();
					boolean ejecutado = statement.execute(sql);
					System.out.println("Ejecutado: " + ejecutado);
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
		Integer result = 0;

		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
		
		String tableToUpdate = databaseProperties.get("database.facturasend_table");
		String tableToUpdateKey = databaseProperties.get("database.facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database.facturasend_table.value");

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
		
		String sql = "DELETE FROM " + tableToUpdate + " WHERE " + pk + " = "+ getValueForKey(de, "transaccion_id", "tra_id") + " "
				+ " AND TRIM(UPPER(" + tableToUpdateKey + ")) IN ('ERROR','ESTADO', 'XML', 'JSON', 'QR', 'CDC', 'TIPO') ";
		
		System.out.print("\n" + sql);
		PreparedStatement statement = conn.prepareStatement(sql);

		result = statement.executeUpdate();
		System.out.println("result: " + result);

		return result;
	}
	
	/**
	 * 
	 * @param viewPrincipal
	 * @param error
	 * @param databaseProperties
	 * @return
	 */
	public static void createTableFacturaSendData(Map<String, String> databaseProperties) throws Exception{
		
		Integer result = 0;
	
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
		
		String tableToCreate = databaseProperties.get("database.facturasend_table");
		String tableToUpdateKey = databaseProperties.get("database.facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database.facturasend_table.value");

		PreparedStatement statement = null;
		
		if (databaseProperties.get("database.type").equals("dbf")) {
			// Borrar tabla, opcional
			if (false) {
				String sql = "DROP TABLE " + tableToCreate + " ";
				System.out.println("\n" + sql + " ");
				statement = conn.prepareStatement(sql);
				int dropTableResult = statement.executeUpdate();
				
				System.out.print("rows: " + dropTableResult + " ");				
			}
			
			
			//---
			String sqlCreateTable = "CREATE TABLE IF NOT EXISTS " + tableToCreate + " (";
			Iterator itr = databaseProperties.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+""; 
				if ((key).startsWith("database.facturasend_table.field.")) {
					sqlCreateTable += key.substring("database.facturasend_table.field.".length(), key.length()) + " VARCHAR(254), ";
				}
			}
			//sqlCreateTable += sqlCreateTable.substring(0, sqlCreateTable.length() - 2);
			sqlCreateTable += tableToUpdateKey + " VARCHAR(254), ";
			sqlCreateTable += tableToUpdateValue + " VARCHAR(254))";
			
			System.out.print("\n" + sqlCreateTable + " ");
		
			/*statement = conn.prepareStatement(sqlCreateTable);
			int row = statement.executeUpdate();
			System.out.print("rows: " + row);
			
			String sql = "show columns from " + tableToCreate;
			System.out.println("\n" + sql + " ");
			statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			
			List<Map<String, Object>> tableColumns = SQLUtil.convertResultSetToList(rs);
			
			System.out.println("TableColumns: " + tableColumns + " ");
			*/
		}
		
	}
	
	/**
	 * 
	 * @param viewPrincipal
	 * @param error
	 * @param databaseProperties
	 * @return
	 */
	public static Integer guardarFacturaSendData(Map<String, Object> viewPrincipal, Map<String, Object> datosGuardar, Map<String, String> databaseProperties) throws Exception{
		Integer result = 0;
	
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
		
		String tableToUpdate = databaseProperties.get("database.facturasend_table");
		String tableToUpdateKey = databaseProperties.get("database.facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database.facturasend_table.value");
		
		String preUpdateSQL = databaseProperties.get("database.facturasend_table.pre_update_sql");
		if (preUpdateSQL != null) {
			PreparedStatement statement2 = conn.prepareStatement(preUpdateSQL);
			ResultSet rs = statement2.executeQuery();
			List<Map<String, Object>> preSQLListMap = SQLUtil.convertResultSetToList(rs);

			for (int i = 0; i < preSQLListMap.size(); i++) {
				Map<String, Object> preSQLMap = preSQLListMap.get(i); 
				
				Iterator itrPreSQL = preSQLMap.entrySet().iterator();
				while (itrPreSQL.hasNext()) {	//Recorre los datos que se tienen que guardar, cdc, numero, estado, error, etc
					
					Map.Entry eDato = (Map.Entry)itrPreSQL.next();
					viewPrincipal.put(eDato.getKey()+"", eDato.getValue());	//Si en el array hay mas de una fila, entonces el ultimo perdurará
				}
			}
			
		}
		
		
		
		String sqlUpdate = "INSERT INTO " + tableToUpdate + " (";
		
		//Buscar fields adicionales
		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+""; 
			if ((key).startsWith("database.facturasend_table.field.")) {
				sqlUpdate += key.substring("database.facturasend_table.field.".length(), key.length()) + ", ";
			}
		}
				
		sqlUpdate += tableToUpdateKey + ", ";
		sqlUpdate += tableToUpdateValue + ") VALUES ";
		
		//Buscar fields value adicionales
		Iterator itrDato = datosGuardar.entrySet().iterator();
		while (itrDato.hasNext()) {	//Recorre los datos que se tienen que guardar, cdc, numero, estado, error, etc
			Map.Entry eDato = (Map.Entry)itrDato.next();

			sqlUpdate += "( ";
			itr = databaseProperties.entrySet().iterator();
			while (itr.hasNext()) {	//Recorre los campos de la tabla a almacenar
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+""; 
				String value = e.getValue()+"";
				if ((key).startsWith("database.facturasend_table.field.")) {
					if (value.startsWith("@SQL(")) {
						//Si es un SQL debe colocar directo la sentencia
						sqlUpdate += "" + value.substring(4, value.length() ) +  ", ";
						
					} else {
						sqlUpdate += "?, ";	
					}
				}
			}
		
			sqlUpdate += "?, ? ";
			sqlUpdate += "), ";
		}
		
		//Al final retirar la coma restante
		sqlUpdate = sqlUpdate.substring(0, sqlUpdate.length() - 2);
		
		PreparedStatement statement = conn.prepareStatement(sqlUpdate);

		

		
		
		//Agregar los parametros.
		
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
						statement.setObject(f++, getValueForKey(viewPrincipal, e.getValue()+""));
					}
				}
			}
		
			statement.setString(f++, eDato.getKey() + "");
			
			Clob clob = conn.createClob();
			clob.setString(1, eDato.getValue()+"" );

			statement.setClob(f++, clob );
		}
	
		System.out.println("" + sqlUpdate);
		result = statement.executeUpdate();
		
		String posUpdateSQL = databaseProperties.get("database.facturasend_table.pos_update_sql");
		if (posUpdateSQL != null) {
			System.out.println(posUpdateSQL);
			PreparedStatement statement2 = conn.prepareStatement(posUpdateSQL);
			statement2.executeQuery();
			
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param viewPrincipal
	 * @param error
	 * @param databaseProperties
	 * @return
	 */
	public static Integer guardarPausarIniciar(Integer transaccionId, Map<String, String> databaseProperties) throws Exception{
		Integer result = 0;
	
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
		
		String tableToUpdate = databaseProperties.get("database.facturasend_table");
		String tableToUpdateKey = databaseProperties.get("database.facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database.facturasend_table.value");
		
		//Obtener de la BD
		
		
		
		String sqlUpdate = "INSERT INTO " + tableToUpdate + " (";
		
		//Buscar fields adicionales
		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+""; 
			if ((key).startsWith("database.facturasend_table.field.")) {
				sqlUpdate += key.substring("database.facturasend_table.field.".length(), key.length()) + ", ";
			}
		}
				/*
		sqlUpdate += tableToUpdateKey + ", ";
		sqlUpdate += tableToUpdateValue + ") VALUES ";
		
		//Buscar fields value adicionales
		Iterator itrDato = datosGuardar.entrySet().iterator();
		while (itrDato.hasNext()) {	//Recorre los datos que se tienen que guardar, cdc, numero, estado, error, etc
			Map.Entry eDato = (Map.Entry)itrDato.next();

			sqlUpdate += "( ";
			itr = databaseProperties.entrySet().iterator();
			while (itr.hasNext()) {	//Recorre los campos de la tabla a almacenar
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+""; 
				String value = e.getValue()+"";
				if ((key).startsWith("database.facturasend_table.field.")) {
					if (value.startsWith("@SQL(")) {
						//Si es un SQL debe colocar directo la sentencia
						sqlUpdate += "" + value.substring(4, value.length() ) +  ", ";
						
					} else {
						sqlUpdate += "?, ";	
					}
				}
			}
		
			sqlUpdate += "?, ? ";
			sqlUpdate += "), ";
		}
		
		//Al final retirar la coma restante
		sqlUpdate = sqlUpdate.substring(0, sqlUpdate.length() - 2);
		
		PreparedStatement statement = conn.prepareStatement(sqlUpdate);

		*/

		
		
/*		//Agregar los parametros.
		
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
						statement.setObject(f++, getValueForKey(viewPrincipal, e.getValue()+""));
					}
				}
			}
		
			statement.setString(f++, eDato.getKey() + "");
			
			Clob clob = conn.createClob();
			clob.setString(1, eDato.getValue()+"" );

			statement.setClob(f++, clob );
		}
	
		System.out.println("" + sqlUpdate);
		result = statement.executeUpdate();
		
		String posUpdateSQL = databaseProperties.get("database.facturasend_table.pos_update_sql");
		if (posUpdateSQL != null) {
			System.out.println(posUpdateSQL);
			PreparedStatement statement2 = conn.prepareStatement(posUpdateSQL);
			statement2.executeQuery();
			
		}*/
		
		return result;
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
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection();
			
			Statement statement = conn.createStatement();
			
			String sql = obtenerSQL50registrosNoIntegrados(databaseProperties, tipoDocumento);

			Integer rowsLoteRequest = 50;
			if (databaseProperties.get("facturasend.rows_lote_request") != null) {
				rowsLoteRequest = Integer.valueOf(databaseProperties.get("facturasend.rows_lote_request"));
			}
			if (rowsLoteRequest > 50) {
				throw new Exception("Cantidad máxima de documentos por lote = 50 (facturasend.rows_lote_request)");
			}

			if (databaseProperties.get("database.type").equals("oracle")) {
				sql = getOracleSQLPaginado(sql, 1, rowsLoteRequest);	
			} else if (databaseProperties.get("database.type").equals("postgres")) {
				sql = getPostgreSQLPaginado(sql, 1, rowsLoteRequest);
			} else if (databaseProperties.get("database.type").equals("dbf")) {
				sql = getPostgreSQLPaginado(sql, 1, rowsLoteRequest);
			}

			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> listadoDes = SQLUtil.convertResultSetToList(rs);
			System.out.println("" + sql + "\nlistadoDes:" + listadoDes);
			
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
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transacctions_table");
		String sql = "";
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT transaccion_id \n"
						+ "FROM " + tableName + " \n"
						+ "WHERE 1=1 \n"
						+ "AND tipo_documento = " + tipoDocumento + " \n"
						
						+ "AND ( \n"
						+ "CDC IS NULL \n"
						+ "OR \n"
						+ "TRIM(ESTADO) = 4 \n"
						+ ") \n"
						+ "GROUP BY transaccion_id, establecimiento, punto, numero \n"
						+ "ORDER BY establecimiento, punto, numero \n";	//Ordena de forma normal, para obtener el ultimo	
		} else {
			
			tableName = databaseProperties.get("database.dbf.facturasend_file");
			tableName = tableName.substring(0, tableName.indexOf(".dbf"));

			sql = "SELECT tra_id \n"
					+ "FROM " + tableName + " vp \n"
					+ "WHERE 1=1 \n"
					+ "AND tip_doc = " + tipoDocumento + " \n"
					
					+ "AND ( \n"
					+ "(SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='CDC' LIMIT 1) IS NULL \n"
					+ "OR \n"
					+ "COALESCE((SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='ESTADO' LIMIT 1), 999) = 4 \n"
					+ ") \n"
					+ "GROUP BY tra_id, estable, punto, numero \n"
					+ "ORDER BY estable, punto, numero \n";	//Ordena de forma normal, para obtener el ultimo				
		}
		
		
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

			
			System.out.print("\n" + sql + " ");
			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> transacionesParaEnvioLote = SQLUtil.convertResultSetToList(rs);
			
			System.out.println("transacionesParaEnvioLote: " + transacionesParaEnvioLote);
			
			result.put("success", true);
			result.put("result", transacionesParaEnvioLote);
			
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
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transacctions_table");

		String sql = "";
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT * \n"
						+ "FROM " + tableName + " \n"
						+ "WHERE 1=1 \n"
						+ "AND transaccion_id IN " + transaccionIdString + " \n"
						+ "ORDER BY numero DESC \n";		
		} else {
			
			tableName = databaseProperties.get("database.dbf.facturasend_file");
			tableName = tableName.substring(0, tableName.indexOf(".dbf"));

			sql = "SELECT * \n"
					+ "FROM " + tableName + " \n"
					+ "WHERE 1=1 \n"
					+ "AND tra_id IN " + transaccionIdString + " \n"
					+ "ORDER BY numero DESC \n";
		}
			
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
	
	/**
	 * Recibe un SQL y agrega sobre el mismo la paginacion de Postgres
	 * @param sql
	 * @param page
	 * @param size
	 * @return
	 */
	private static String getPostgreSQLPaginado(String sql, Integer page, Integer size) {
		
		//Paginacion Oracle
		sql += " LIMIT " + size + " OFFSET " + (page == 1 ? page : (((page-1) * size) + 1)) + " \n";
		return sql;
	}
	
	/*public static String getFieldName(String fieldName, Map<String, String> databaseProperties) {
		boolean fieldsInUpperCase = databaseProperties.get("database.fields_in_uppercase").equals("true");
		if (fieldsInUpperCase) {
			fieldName = fieldName.toUpperCase();
		}
		return fieldName;
	}*/

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
