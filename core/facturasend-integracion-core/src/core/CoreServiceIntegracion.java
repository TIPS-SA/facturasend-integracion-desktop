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

/**
 * Clase que se encarga de iniciar la integracion de los datos con FacturaSend
 * 
 * Contiene mayormente los métodos para iniciar y ejecutar la integracion
 * 
 * Tambien invoca a los metodos del CoreService
 * 
 * @author Marcos Jara
 *
 */
public class CoreServiceIntegracion {
	
	private static Gson gson = new Gson();
	private static Gson gsonPP = new GsonBuilder().setPrettyPrinting().create();
	public static Log log = LogFactory.getLog(CoreServiceIntegracion.class);

	/*public static void main(String[] args) {
		Integer autoUpdateIntegracion = Integer.valueOf(FacturasendService.readDBProperties().get("database.autoupdate_millis.integracion")+"");
		
		if (autoUpdateIntegracion > 0) {
			new Timer().schedule(new TimerTask() {
			    @Override
			    public void run() {
			    	try {
			    		iniciarIntegracion();	
					} catch (Exception e2) {
						JOptionPane.showMessageDialog(null, "Ocurrio un problema inesperado\n"+e2);
						System.out.println("Mostrar error en pantalla, " + e2);
					};
			    }
			}, new Date(), autoUpdateIntegracion); //Cada N millis segundos						
		} else {
			
		}
	}*/

	/**
	 * Paso 0
	 * 
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static Map<String, Object> iniciarIntegracion(Map<String, String> databaseProperties)  {
		
		//En el archivo de propiedades debe haber un key que defina si se va ejecutar infinito.
		//o cada vez que se invoca
		
		iniciarIntegracion(1, databaseProperties);
		iniciarIntegracion(4, databaseProperties);
		iniciarIntegracion(5, databaseProperties);
		iniciarIntegracion(6, databaseProperties);
		iniciarIntegracion(7, databaseProperties);
		
		System.out.println("Lote de integración concluido...!");
		return null; //Seria bueno que aqui se retornen los resultados success y result
	}
		
	/**
	 * Paso 1. Proceso que inicia la integración, dependiendo del tipo de documento.
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static Map<String, Object> iniciarIntegracion(Integer tipoDocumento, Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		Map<String, Object> obtener50registrosNoIntegradosMap = obtenerHasta50registrosNoIntegrados(tipoDocumento, databaseProperties);
		
		try {
			
			String transaccionIdString = "(-1)";
			if (Boolean.valueOf(obtener50registrosNoIntegradosMap.get("success")+"") == true) {
				List<Map<String, Object>> obtener50registrosNoIntegradosListMap = (List<Map<String, Object>>)obtener50registrosNoIntegradosMap.get("result");

				transaccionIdString = "";
				for (Map<String, Object> map : obtener50registrosNoIntegradosListMap) {
					transaccionIdString += CoreService.getValueForKey(map, "transaccion_id", "tra_id") + ",";
				}
				transaccionIdString += "";
				
				//System.out.println(transaccionIdString);
				
			} else {
				throw new Exception(obtener50registrosNoIntegradosMap.get("error")+"");
			}
			
			
			
			//De acuerdo a los transaccion_id obtendidos, busca todos los registros relacionados.
			String transaccionIdStringInClause = "(" + transaccionIdString + "-1)";
			
			if (!transaccionIdStringInClause.equals("(-1)")) {	//Solo si existen transacciones que procesar
				
				Map<String, Object> documentosParaEnvioAllMap = procesarTransacciones(transaccionIdStringInClause, databaseProperties);
				List<Map<String, Object>> documentoParaEnvioAllJsonMap = null;
				
				if (Boolean.valueOf(documentosParaEnvioAllMap.get("success")+"") == true) {
					documentoParaEnvioAllJsonMap = (List<Map<String, Object>>)documentosParaEnvioAllMap.get("result");
				} else {
					throw new Exception(documentosParaEnvioAllMap.get("error")+"");
	
				}
				
				//Tambien busca todas las formas de Cobro relacionados a las transacciones
				List<Map<String, Object>> paymentViewAllMap = CoreServiceIntegracion.formasPagosByTransaccion(tipoDocumento, transaccionIdStringInClause, databaseProperties);
				
				
				//Generar JSON de documentos electronicos.
				List<Map<String, Object>> documentosParaEnvioJsonMap = DocumentoElectronicoCore.generarJSONLote(transaccionIdString.split(","), documentoParaEnvioAllJsonMap, paymentViewAllMap, databaseProperties);
				
				if (documentosParaEnvioJsonMap.size() > 0) {
					
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
						
						createTableFacturaSendData(databaseProperties);
						
						String tableToUpdate = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table");
						if (Boolean.valueOf(resultadoJson.get("success")+"") == true ) {
		
							Map<String, Object> result = (Map<String, Object>)resultadoJson.get("result");
							
							List<Map<String, Object>> deList = (List<Map<String, Object>>)result.get("deList");
							
							for (int i = 0; i < documentosParaEnvioJsonMap.size(); i++) {
								Map<String, Object> jsonDeGenerado = documentosParaEnvioJsonMap.get(i);
								Map<String, Object> viewRec = documentoParaEnvioAllJsonMap.get(i);
		
								Map<String, Object> respuestaDE = deList.get(i);	//Utiliza el mismo Indice de List de Json
										
								//Borrar registros previamente cargados, para evitar duplicidad
								deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties);
		
								Map<String, Object> datosGuardar = new HashMap<String, Object>();
								datosGuardar.put("CDC", respuestaDE.get("cdc") + "");
								saveDataToFacturaSendTable(viewRec, datosGuardar, databaseProperties);
		
								String estado = respuestaDE.get("estado") != null ? respuestaDE.get("estado") + "" : "0";
								Map<String, Object> datosGuardar1 = new HashMap<String, Object>();
								datosGuardar1.put("ESTADO", estado);
								saveDataToFacturaSendTable(viewRec, datosGuardar1, databaseProperties);
		
		
								Map<String, Object> datosGuardar5 = new HashMap<String, Object>();
								datosGuardar5.put("TIPO", "Mayorista");
								saveDataToFacturaSendTable(viewRec, datosGuardar5, databaseProperties);
		
								if ( ! databaseProperties.get("database.type").equals("dbf")) {
									Map<String, Object> datosGuardar3 = new HashMap<String, Object>();
									datosGuardar3.put("QR", respuestaDE.get("qr") + "");
									saveDataToFacturaSendTable(viewRec, datosGuardar3, databaseProperties);
		
									Map<String, Object> datosGuardar4 = new HashMap<String, Object>();
									datosGuardar4.put("XML", respuestaDE.get("xml") + "");
									saveDataToFacturaSendTable(viewRec, datosGuardar4, databaseProperties);
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
									Map<String, Object> viewRec = documentoParaEnvioAllJsonMap.get(i);
									
									
									for (int j = 0; j < errores.size(); j++) {
										if (i == j) {
											
											//Borrar registros previamente cargados, para evitar duplicidad
											deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties);
		
											String error = (String)errores.get(j).get("error");
											if (databaseProperties.get("database.type").equals("dbf")) {
												if (error.length() > 254) {
													error = error.substring(0, 254);	
												}
											}
											Map<String, Object> datosGuardar = new HashMap<String, Object>();
											datosGuardar.put("ERROR", error);
											saveDataToFacturaSendTable(viewRec, datosGuardar, databaseProperties);
											
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
							
							Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
							
							String sql = "save '" + databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table") + "' to '" + databaseProperties.get("database.dbf.parent_folder") + "'";
							//+ "\\saved' ";
							System.out.println("\n" + sql + " ");
							//PreparedStatement statement = conn.prepareStatement(sql);
							Statement statement = conn.createStatement();
							boolean ejecutado = statement.execute(sql);
							System.out.println("Ejecutado: " + ejecutado);
						}
		
					}
				} else {
					System.out.println("No se invoco a la Api de Facturasend por que no existian datos para enviar "  + documentosParaEnvioJsonMap + " para el Tipo de Documento " + tipoDocumento);
				}
			} else {
				System.out.println("No se encontraron transaccion_id(s) (-1) al reperar registros " + tipoDocumento);
			}
			//
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		//Cambiar éste resultado, por el resultado del lote
		System.out.println("fin de integracion tipo documenot " + tipoDocumento);
		return obtener50registrosNoIntegradosMap;
	}
	
	/**
	 * 
	 * @param de
	 * @param error
	 * @param databaseProperties
	 * @return
	 */
	public static Integer deleteFacturaSendTableByTransaccionId(Map<String, Object> de, Map<String, String> databaseProperties) throws Exception{
		Integer result = 0;

		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
		
		String tableToUpdate = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table");
		String tableToUpdateKey = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.value");

		String pk = "";
		//Buscar el campo que relaciona con el transaccion_id 
		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+"";
			String value = e.getValue()+""; 
			if ( value.equalsIgnoreCase("transaccion_id") || value.equalsIgnoreCase("tra_id")) {
				pk = key.substring(("database." + databaseProperties.get("database.type") + ".facturasend_table.field.").length(), key.length()) + "";
			}
		}
		
		String sql = "DELETE FROM " + tableToUpdate + " WHERE " + pk + " = "+ CoreService.getValueForKey(de, "transaccion_id", "tra_id") + " "
				+ " AND TRIM(UPPER(" + tableToUpdateKey + ")) IN ('ERROR', 'ESTADO', 'PAUSADO', 'XML', 'JSON', 'QR', 'CDC', 'TIPO') ";
		
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
	
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
		
		String tableToCreate = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table");
		String tableToUpdateKey = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.value");

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
		}
	}
	
	/**
	 * 
	 * @param viewPrincipal
	 * @param error
	 * @param databaseProperties
	 * @return
	 */
	public static Integer saveDataToFacturaSendTable(Map<String, Object> viewPrincipal, Map<String, Object> datosGuardar, Map<String, String> databaseProperties) throws Exception{
		Integer result = 0;
	
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
		
		String tableToUpdate = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table");
		String tableToUpdateKey = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.value");
		
		String preUpdateSQL = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.pre_update_sql");
		if (preUpdateSQL != null) {
			PreparedStatement statement2 = conn.prepareStatement(preUpdateSQL);
			ResultSet rs = statement2.executeQuery();
			List<Map<String, Object>> preSQLListMap = SQLUtil.convertResultSetToList(rs);

			for (int i = 0; i < preSQLListMap.size(); i++) {
				Map<String, Object> preSQLMap = preSQLListMap.get(i); 
				
				Iterator itrPreSQL = preSQLMap.entrySet().iterator();
				while (itrPreSQL.hasNext()) {	//Recorre los datos que se tienen que guardar, cdc, numero, estado, error, etc
					//Agregar el valor previo generado desde la consulta pre-update
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
			if ((key).startsWith("database." + databaseProperties.get("database.type") + ".facturasend_table.field.")) {
				sqlUpdate += key.substring(("database." + databaseProperties.get("database.type") + ".facturasend_table.field.").length(), key.length()) + ", ";
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
				if ((key).startsWith("database." + databaseProperties.get("database.type") + ".facturasend_table.field.")) {
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
				if ((key).startsWith("database." + databaseProperties.get("database.type") + ".facturasend_table.field.")) {
					if (value.startsWith("@SQL(")) {
						//Como ya coloco el SQL, entonces aqui no inserta los parámetros.
						
					} else {
						//Toma los valores del view-principal, dependiendo de las claves que estan en el config.
						statement.setObject(f++, CoreService.getValueForKey(viewPrincipal, e.getValue()+""));
					}
				}
			}
		
			statement.setString(f++, eDato.getKey() + "");
			
			Clob clob = conn.createClob();
			clob.setString(1, eDato.getValue()+"" );

			statement.setClob(f++, clob );
		}
	
		System.out.print("\n" + sqlUpdate + " ");
		
		result = statement.executeUpdate();
		System.out.println("result: " + result + "");
		
		String posUpdateSQL = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.pos_update_sql");
		if (posUpdateSQL != null) {
			System.out.print("\n" + posUpdateSQL + " ");
			PreparedStatement statement2 = conn.prepareStatement(posUpdateSQL);
			//ResultSet rs = statement2.executeQuery();
			Integer resultExecuteUpdate = statement2.executeUpdate();
			
			//Map<String, Object> posUpdateSQLMap = SQLUtil.convertResultSetToMap(rs);
			System.out.println("resultExecuteUpdate:" + resultExecuteUpdate);
		}
		
		return result;
	}
	
	
	/**
	 * Paso 1. Proceso que marca el registro de ventas con una opcion para pausar (o iniciar en caso que estaba pausado) 
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static void pausarIniciar(Integer transaccionId, Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		
		try {
						
			guardarPausarIniciar(transaccionId, databaseProperties);

			
			//
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Cambiar éste resultado, por el resultado estandar
		
		return;
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
	
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
		
		String tableToUpdate = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table");
		String tableToUpdateKey = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.value");
		
		//String transaccionIdForeignKeyField = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.field.transaccion_id");
		String prefix = "database." + databaseProperties.get("database.type") + ".facturasend_table.field.";
		String transaccionIdForeignKeyField = CoreService.findKeyByValueInProperties(databaseProperties, prefix, "transaccion_id");	//or tra_id
		transaccionIdForeignKeyField = transaccionIdForeignKeyField.substring(prefix.length(), transaccionIdForeignKeyField.length());
		
		//Obtener de la BD
		String sqlConsulta = "SELECT CAST(" + tableToUpdateValue + " AS VARCHAR(10)) AS " + tableToUpdateValue + " "
						+ "FROM " + tableToUpdate + " "
						+ "WHERE "
						+ transaccionIdForeignKeyField + " = ? " 
						+ "AND TRIM(UPPER(" + tableToUpdateKey + ")) = 'PAUSADO' "; 
		
		PreparedStatement statement = conn.prepareStatement(sqlConsulta);
		statement.setInt(1, transaccionId);
		
		System.out.print("\n" + sqlConsulta + " ");
		ResultSet rs = statement.executeQuery();
		
		Map<String, Object> situacionPausadoActualMap = SQLUtil.convertResultSetToMap(rs);
		System.out.println("listadoDes:" + situacionPausadoActualMap);
		
		if ( situacionPausadoActualMap != null && Boolean.valueOf(CoreService.getValueForKey(situacionPausadoActualMap, tableToUpdateValue) +"") ) {
			//Ya existe el registro de PAUSADO y esta PAUSADO
			
			String sqlDelete = "DELETE "
					+ "FROM " + tableToUpdate + " "
							+ "WHERE "
							+ transaccionIdForeignKeyField + " = ? " 
							+ "AND TRIM(UPPER(" + tableToUpdateKey + ")) = 'PAUSADO' "; 
			
			System.out.print("\n" + sqlDelete + " ");

			PreparedStatement statementDelete = conn.prepareStatement(sqlDelete);
			statementDelete.setInt(1, transaccionId);
			
			int resultDelete = statementDelete.executeUpdate();
			System.out.print("resultDelete: " + resultDelete);
			
		} else {
			//Consultar el objeto completo del Servidor, por el transaccion_id
			Map<String, Object> resultViewMap = obtenerTransaccionesParaEnvioLote("(" + transaccionId + ")", databaseProperties);
			if (resultViewMap != null) {

				if ( Boolean.valueOf(resultViewMap.get("success")+"")) {

					List<Map<String, Object>> listaTransaccionesView = (List<Map<String, Object>>)resultViewMap.get("result");
					
					if (listaTransaccionesView.size() > 0) {

						Map<String, Object> datosGuardar = new HashMap<String, Object>();
						datosGuardar.put("PAUSADO", "true");
						saveDataToFacturaSendTable(listaTransaccionesView.get(0), datosGuardar, databaseProperties);
						
					}
				}
			}
			
		}
		
		return result;
	}
	
	
	
	/**
	 * Paso 1.1 - Obtener 50 registros no integrados
	 * 
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static Map<String, Object> obtenerHasta50registrosNoIntegrados(Integer tipoDocumento, Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			System.out.println("Obteniendo conexion..." );
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
			System.out.println("Conexion obtenida..." );
			
			Statement statement = conn.createStatement();
			
			String sql = obtenerHasta50registrosSQLNoIntegrados(databaseProperties, tipoDocumento);

			Integer rowsLoteRequest = 50;
			if (databaseProperties.get("facturasend.rows_lote_request") != null) {
				rowsLoteRequest = Integer.valueOf(databaseProperties.get("facturasend.rows_lote_request"));
			}
			if (rowsLoteRequest > 50) {
				throw new Exception("Cantidad máxima de documentos por lote = 50 (facturasend.rows_lote_request)");
			}

			if (databaseProperties.get("database.type").equals("oracle")) {
				sql = CoreService.getOracleSQLPaginado(sql, 1, rowsLoteRequest);	
			} else if (databaseProperties.get("database.type").equals("postgres")) {
				sql = CoreService.getPostgreSQLPaginado(sql, 1, rowsLoteRequest);
			} else if (databaseProperties.get("database.type").equals("dbf")) {
				sql = CoreService.getPostgreSQLPaginado(sql, 1, rowsLoteRequest);
			}

			System.out.print("\n" + sql + " ");

			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> listadoDes = SQLUtil.convertResultSetToList(rs);
			System.out.println("listadoDes:" + listadoDes);
			
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
	private static String obtenerHasta50registrosSQLNoIntegrados(Map<String, String> databaseProperties, Integer tipoDocumento) {
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
					+ "COALESCE(CAST((SELECT moli_value FROM MOLI_invoiceData mid WHERE mid.tra_id = vp.tra_id AND moli_name='ESTADO' LIMIT 1) AS INTEGER), 999) = 4 \n"
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
		
		Map<String, Object> obtenerTransaccionesMap = obtenerTransaccionesParaEnvioLote(transaccionIdString, databaseProperties);

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
	public static Map<String, Object> obtenerTransaccionesParaEnvioLote(String transaccionIdString, Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
			
			Statement statement = conn.createStatement();
			
			String sql = obtenerTransaccionesSQLParaEnvioLote(databaseProperties, transaccionIdString);
			
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
	private static String obtenerTransaccionesSQLParaEnvioLote(Map<String, String> databaseProperties, String transaccionIdString) {
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
	
	
	public static List<Map<String, Object>> formasPagosByTransaccion(Integer tipoDocumento, String transaccionIdString, Map<String, String> databaseProperties) throws Exception{
		
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
				
		Statement statement = conn.createStatement();
		
		String sql = formasPagosSQLByTransaccion(databaseProperties, tipoDocumento, transaccionIdString);
		System.out.print("\n" + sql + " ");
		ResultSet rs = statement.executeQuery(sql);
		
		result = SQLUtil.convertResultSetToList(rs);
		System.out.println("result: " + result);
		return result;
	}
			
	private static String formasPagosSQLByTransaccion(Map<String, String> databaseProperties, Integer tipoDocumento, String transaccionIdString) {
		String paymentTableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".payment_view");

		String sql = "";
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT * \n"
				+ "FROM " + paymentTableName + " \n"
				+ "WHERE 1=1 \n"
				+ "AND tipo_documento = " + tipoDocumento + " \n"
				+ "AND transaccion_id IN " + transaccionIdString + " \n"
				+ "";
		} else {
			sql = "SELECT * \n"
					+ "FROM " + paymentTableName + " \n"
					+ "WHERE 1=1 \n"
					+ "AND tip_doc = " + tipoDocumento + " \n"
					+ "AND tra_id IN (" + transaccionIdString + ") \n"
					+ "";
		}
		return sql;
	}
	
}