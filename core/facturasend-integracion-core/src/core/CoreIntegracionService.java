package core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.util.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import connect.BDConnect;
import connect.SQLConnection;
import print.PrintPdf;
import util.HttpUtil;
import util.StringUtil;

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
public class CoreIntegracionService {
	
	private static Gson gson = new Gson();
	private static Gson gsonPP = new GsonBuilder().setPrettyPrinting().create();
	public static Log log = LogFactory.getLog(CoreIntegracionService.class);

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
		
		
		if (databaseProperties.get("facturaend.do_integrar_tipo_1").equalsIgnoreCase("Y")) {
			iniciarIntegracion(1, databaseProperties);			
		}
		if (databaseProperties.get("facturaend.do_integrar_tipo_2").equalsIgnoreCase("Y")) {
			iniciarIntegracion(2, databaseProperties);			
		}
		if (databaseProperties.get("facturaend.do_integrar_tipo_3").equalsIgnoreCase("Y")) {
			iniciarIntegracion(3, databaseProperties);			
		}
		if (databaseProperties.get("facturaend.do_integrar_tipo_4").equalsIgnoreCase("Y")) {
			iniciarIntegracion(4, databaseProperties);			
		}
		if (databaseProperties.get("facturaend.do_integrar_tipo_5").equalsIgnoreCase("Y")) {
			iniciarIntegracion(5, databaseProperties);			
		}
		if (databaseProperties.get("facturaend.do_integrar_tipo_6").equalsIgnoreCase("Y")) {
			iniciarIntegracion(6, databaseProperties);			
		}
		if (databaseProperties.get("facturaend.do_integrar_tipo_7").equalsIgnoreCase("Y")) {
			iniciarIntegracion(7, databaseProperties);			
		}
		
		if (databaseProperties.get("facturaend.do_obtener_estado_tipo_1").equalsIgnoreCase("Y")) {
			setTimeout(() -> actualizarEstadoDesdeFacturaSend(1, databaseProperties), 1000);	//Ejecuta en un thread			
		}
		if (databaseProperties.get("facturaend.do_obtener_estado_tipo_2").equalsIgnoreCase("Y")) {
			setTimeout(() -> actualizarEstadoDesdeFacturaSend(2, databaseProperties), 1000);	//Ejecuta en un thread			
		}
		if (databaseProperties.get("facturaend.do_obtener_estado_tipo_3").equalsIgnoreCase("Y")) {
			setTimeout(() -> actualizarEstadoDesdeFacturaSend(3, databaseProperties), 1000);	//Ejecuta en un thread			
		}
		if (databaseProperties.get("facturaend.do_obtener_estado_tipo_4").equalsIgnoreCase("Y")) {
			setTimeout(() -> actualizarEstadoDesdeFacturaSend(4, databaseProperties), 1000);	//Ejecuta en un thread			
		}
		if (databaseProperties.get("facturaend.do_obtener_estado_tipo_5").equalsIgnoreCase("Y")) {
			setTimeout(() -> actualizarEstadoDesdeFacturaSend(5, databaseProperties), 1000);	//Ejecuta en un thread			
		}
		if (databaseProperties.get("facturaend.do_obtener_estado_tipo_6").equalsIgnoreCase("Y")) {
			setTimeout(() -> actualizarEstadoDesdeFacturaSend(6, databaseProperties), 1000);	//Ejecuta en un thread			
		}
		if (databaseProperties.get("facturaend.do_obtener_estado_tipo_7").equalsIgnoreCase("Y")) {
			setTimeout(() -> actualizarEstadoDesdeFacturaSend(7, databaseProperties), 1000);	//Ejecuta en un thread			
		}
		
		
		if (databaseProperties.get("facturaend.do_evento_cancelacion").equalsIgnoreCase("Y")) {
			iniciarIntegracionCancelado(databaseProperties);	//Cancelacion para todos los tipos de documentos
		}
		if (databaseProperties.get("facturaend.do_evento_inutilizacion").equalsIgnoreCase("Y")) {
			iniciarIntegracionInutilizacion(databaseProperties);  //Inutilizacion para todos los registros que se crearon, aun no se mandaron a la set y se quiere inutilizar
		}
		//iniciarIntegracion(4, databaseProperties);
		//iniciarIntegracion(5, databaseProperties);
		//iniciarIntegracion(6, databaseProperties);
		//iniciarIntegracion(7, databaseProperties);
//		iniciarIntegracionCancelado(databaseProperties);	//Cancelacion para todos los tipos de documentos
//		iniciarIntegracionInutilizacion(databaseProperties);  //Inutilizacion para todos los registros que se crearon, aun no se mandaron a la set y se quiere inutilizar
		
		//Actualizacion de Estados de DE con Estado 0
		//setTimeout(() -> actualizarEstadoDesdeFacturaSend(1, databaseProperties), 1000);	//Ejecuta en un thread
		//setTimeout(() -> actualizarEstadoDesdeFacturaSend(4, databaseProperties), 1000);	//Ejecuta en un thread
//		setTimeout(() -> actualizarEstadoDesdeFacturaSend(5, databaseProperties), 1000);	//Ejecuta en un thread
	//	setTimeout(() -> actualizarEstadoDesdeFacturaSend(6, databaseProperties), 1000);	//Ejecuta en un thread
//		setTimeout(() -> actualizarEstadoDesdeFacturaSend(7, databaseProperties), 1000);	//Ejecuta en un thread

		
		log.info("Lote de integración concluido...!");
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
					transaccionIdString += CoreService.getValueForKey(map, "transaccion_id", "tra_id", databaseProperties) + ",";
				}
				transaccionIdString += "";
				
				//log.info(transaccionIdString);
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
				List<Map<String, Object>> paymentViewAllMap = CoreIntegracionService.formasPagosByTransaccion(tipoDocumento, transaccionIdStringInClause, databaseProperties);
				
				
				//Generar JSON de documentos electronicos.
				//List<Map<String, Object>> documentosParaEnvioJsonMap = DocumentoElectronicoCore.generarJSONLote(transaccionIdString.split(","), documentoParaEnvioAllJsonMap, paymentViewAllMap, databaseProperties);
				Map<String, Object> documentosStructurado = CoreDocumentoElectronico.generarJSONLote(transaccionIdString.split(","), documentoParaEnvioAllJsonMap, paymentViewAllMap, databaseProperties);
				
				//Obtener un array de documentos a integrar
				List<Map<String, Object>> documentosParaEnvioJsonMap = (List<Map<String, Object>>)documentosStructurado.get("jsonDEs");
				//Obtener los registros de la vista relacionados con el documento a Integrar
				List<List<Map<String,Object>>> documentosParaEnvioFiltradoList = (List<List<Map<String,Object>>>)documentosStructurado.get("documentosParaEnvioFiltradoList");
				
				if (documentosParaEnvioJsonMap.size() > 0) {
					
					//Antes de envar los docuementos, limpia cualquier error previo que haya tenido dicha transaccion
					for (int i = 0; i < documentosParaEnvioJsonMap.size(); i++) {
						Map<String, Object> viewRec = documentosParaEnvioFiltradoList.get(i).get(0);	//Toma el primer registro.

						setearErrorInDocument(viewRec, null, databaseProperties);	
					}
					
					Map header = new HashMap();
					header.put("Authorization", "Bearer api_key_" + databaseProperties.get("facturasend.token"));
					String url = databaseProperties.get("facturasend.url");
					if (databaseProperties.get("facturasend.sincrono").equalsIgnoreCase("S")) {
						url += "/de/create";
					} else {
						url += "/lote/create?xml=true&qr=true";
					}
					
					log.info("Total de Documentos Electronicos enviados: " + documentosParaEnvioJsonMap.size());
					Map<String, Object> resultadoJson = HttpUtil.invocarRest(url, "POST", gson.toJson(documentosParaEnvioJsonMap), header);
					
					List<Map<String, Object>> deList = null;
					if (resultadoJson != null) {
						
						createTableFacturaSendData(databaseProperties);
						
						String tableToUpdate = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table");
						if (Boolean.valueOf(resultadoJson.get("success")+"") == true ) {
		
							Map<String, Object> result = (Map<String, Object>)resultadoJson.get("result");
							
							deList = (List<Map<String, Object>>)result.get("deList");
							log.info("Total de Documentos Electronicos recibidos: " + deList.size());
							
							if (documentosParaEnvioJsonMap.size() != deList.size()) {
								throw new Exception("Error, Cantidad de Documentos enviados difiere de los recibidos ");
							}
							for (int i = 0; i < documentosParaEnvioJsonMap.size(); i++) {
								Map<String, Object> jsonDeGenerado = documentosParaEnvioJsonMap.get(i);
								Map<String, Object> viewRec = documentosParaEnvioFiltradoList.get(i).get(0);	//Toma el primer registro.
		
								Map<String, Object> respuestaDE = deList.get(i);	//Utiliza el mismo Indice de List de Json
										
								//---
								//Actualiza la tabla destino de acuerdo a la configuracion
								Map<String, Object> datosUpdate = new HashMap<String, Object>();
								datosUpdate.put("CDC", CoreService.getValueForKey(respuestaDE, "cdc", databaseProperties) + "");
								//datosUpdate.put("ESTADO", respuestaDE.get("estado") != null ? respuestaDE.get("estado", databaseProperties) + "" : "0");
								datosUpdate.put("ESTADO", CoreService.getValueForKey(respuestaDE, "estado", databaseProperties) != null ? CoreService.getValueForKey(respuestaDE, "estado", databaseProperties) + "" : "0");
								datosUpdate.put("TIPO_DOCUMENTO", tipoDocumento);
								datosUpdate.put("TRANSACCION_ID", CoreService.getValueForKey(viewRec, "transaccion_id", "tra_id", databaseProperties));
								datosUpdate.put("CLASIFIC", CoreService.getValueForKey(viewRec, "clasific", databaseProperties));
								datosUpdate.put("TIMBRADO", CoreService.getValueForKey(viewRec, "timbrado", databaseProperties));
								datosUpdate.put("NUMERO_FACTURA", CoreService.getValueForKey(viewRec, "numero_factura", "num_fac", databaseProperties));
								
								updateFacturaSendDataInTableTransacciones(datosUpdate, databaseProperties, true);
								//---
								
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
									//facturasend.carpetaXML
								}
								
								setTimeout(() -> imprimirXMLFacturaSend(respuestaDE.get("cdc")+"", respuestaDE.get("xml") + "", tipoDocumento, databaseProperties), 10);	//Ejecuta en un thread						
								setTimeout(() -> imprimirKUDEFacturaSend(respuestaDE.get("cdc")+"", tipoDocumento, databaseProperties), 10);	//Ejecuta en un thread						
							}	// end-for 
							
						} else {
							//Si success=false,  hay errores
							//log.debug(arg0);
							String errorGenerico = (String)resultadoJson.get("error");
							List<Map<String, Object>> errores = (List<Map<String, Object>>)resultadoJson.get("errores");
		
								
							for (int i = 0; i < documentosParaEnvioJsonMap.size(); i++) {
								
								Map<String, Object> jsonDeGenerado = documentosParaEnvioJsonMap.get(i);
								Map<String, Object> viewRec = documentosParaEnvioFiltradoList.get(i).get(0);
									
								if (resultadoJson.get("errores") != null) {
									for (int j = 0; j < errores.size(); j++) {
										
										//if (i == j) {
											String error = (String)errores.get(j).get("error");
											Integer index = ((Double)errores.get(j).get("index")).intValue();
											
										if (i == index.intValue()) {

											if (databaseProperties.get("database.type").equals("dbf")) {
												if (error.length() > 254) {
													error = error.substring(0, 254);	
												}
											}
											
											setearErrorInDocument(viewRec, error, databaseProperties);									
										}
									}	
								} else {
									setearErrorInDocument(viewRec, errorGenerico, databaseProperties);	
								}
								
							}
						}
						
						if (databaseProperties.get("database.type").equals("dbf")) {
							
							Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
							
							String dbfTableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table");
							if (dbfTableName.endsWith(".dbf")) {
								dbfTableName = dbfTableName.substring(0, dbfTableName.indexOf(".dbf"));
							}
							String sql = "save '" + dbfTableName + "' to '" + databaseProperties.get("database.dbf.parent_folder") + "'";
							//+ "\\saved' ";
							log.debug("\n" + sql + " ");
							//PreparedStatement statement = conn.prepareStatement(sql);
							Statement statement = conn.createStatement();
							boolean ejecutado = statement.execute(sql);
							log.info("Ejecutado: " + ejecutado);
						}
						
						
						//Aqui ejecutar la consulta de Estado.
						setTimeout(() -> actualizarEstadoDesdeFacturaSend(tipoDocumento, databaseProperties), 1500);	//Ejecuta en un thread
						
						
		
						//Aqui ejecutar la Impre
					}
				} else {
					log.info("No se invoco a la Api de Facturasend por que no existian datos para enviar "  + documentosParaEnvioJsonMap + " para el Tipo de Documento " + tipoDocumento);
				}
			} else {
				log.info("No se encontraron transaccion_id(s) (-1) al recuperar registros " + tipoDocumento);
			}
			//
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		//Cambiar éste resultado, por el resultado del lote
		log.info("fin de integracion tipo documento " + tipoDocumento);
		return obtener50registrosNoIntegradosMap;
	}

	/**
	 * Paso 1. Proceso que inicia la integración de una cancelacion, para todos los tipos de documento.
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	
	//Evento de Cancelacion, no se pudo culminar
	public static Map<String, Object> iniciarIntegracionCancelado(Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		Map<String, Object> registrosAprobadosACancelarMap = obtenerRegistrosAprobadosACancelar(databaseProperties);
		
		try {
			
			if (Boolean.valueOf(registrosAprobadosACancelarMap.get("success")+"") == true) {
				List<Map<String, Object>> registrosAprobadosACancelarListMap = (List<Map<String, Object>>)registrosAprobadosACancelarMap.get("result");

				for (Map<String, Object> registroAprobadoACancelar : registrosAprobadosACancelarListMap) {
					Integer tipoDocumento = ((BigDecimal)CoreService.getValueForKey(registroAprobadoACancelar, "tipo_documento", "tip_doc", databaseProperties)).intValue();
					Integer transaccionId = ((BigDecimal)CoreService.getValueForKey(registroAprobadoACancelar, "transaccion_id", "tra_id", databaseProperties)).intValue();
					String cdc = ((String)CoreService.getValueForKey(registroAprobadoACancelar, "cdc", databaseProperties));
					String motivo = ((String)CoreService.getValueForKey(registroAprobadoACancelar, "evento_motivo", databaseProperties));
					String clasific = ((String)CoreService.getValueForKey(registroAprobadoACancelar, "clasific", databaseProperties));
					
					eventoCancelacion(tipoDocumento, clasific, transaccionId, cdc, motivo, registroAprobadoACancelar, databaseProperties);
				}
				
				//log.info(transaccionIdString);
			} else {
				throw new Exception(registrosAprobadosACancelarMap.get("error")+"");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	/**
	 * Paso 1. Proceso que inicia la integración de una inutilizacion, para todos los tipos de documento.
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	
	public static Map<String, Object> iniciarIntegracionInutilizacion(Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		Map<String, Object> registrosAInutilizarMap = obtenerRegistrosAInutilizar(databaseProperties);
		
		try {
			
			if (Boolean.valueOf(registrosAInutilizarMap.get("success")+"") == true) {
				List<Map<String, Object>> registrosAInutilizarListMap = (List<Map<String, Object>>)registrosAInutilizarMap.get("result");

				for (Map<String, Object> registroAInutilizar : registrosAInutilizarListMap) {
					Integer tipoDocumento = ((BigDecimal)CoreService.getValueForKey(registroAInutilizar, "tipo_documento", "tip_doc", databaseProperties)).intValue();
					Integer transaccionId = ((BigDecimal)CoreService.getValueForKey(registroAInutilizar, "transaccion_id", "tra_id", databaseProperties)).intValue();
					String cdc = ((String)CoreService.getValueForKey(registroAInutilizar, "cdc", databaseProperties));
					String motivo = ((String)CoreService.getValueForKey(registroAInutilizar, "evento_motivo", databaseProperties));
					String clasific = ((String)CoreService.getValueForKey(registroAInutilizar, "clasific", databaseProperties));
					String establecimiento = ((String)CoreService.getValueForKey(registroAInutilizar, "establecimiento", "estable", databaseProperties));
					String punto = ((String)CoreService.getValueForKey(registroAInutilizar, "punto", databaseProperties));
					String numeroFactura = ((String)CoreService.getValueForKey(registroAInutilizar, "numero", databaseProperties));
					String serie = ((String)CoreService.getValueForKey(registroAInutilizar, "serie", databaseProperties));
					
					
					eventoInutilizacion(transaccionId, clasific, serie, tipoDocumento, establecimiento, punto, numeroFactura, numeroFactura, motivo, registroAInutilizar, databaseProperties);
				}
				
				//log.info(transaccionIdString);
			} else {
				throw new Exception(registrosAInutilizarMap.get("error")+"");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	/*
	 * Setea el error en el registro de transaccion, luego de que este haya sido integrado a facturasend
	 * y FS haya devuelto error 
	 * 
	 */
	private static void setearErrorInDocument(Map<String, Object> viewRec, String error, Map<String, String> databaseProperties) throws Exception {
		String pauseIfError = databaseProperties.get("database." + databaseProperties.get("database.type") + ".pause_if_error");
		//---
		//Actualiza la tabla destino de acuerdo a la configuracion
		Map<String, Object> datosUpdate = new HashMap<String, Object>();
		datosUpdate.put("ERROR", error);
		if (pauseIfError != null && pauseIfError.equalsIgnoreCase("Y")){
			if (error != null) {
				datosUpdate.put("PAUSADO", 1);	//Si dio Error (que no es null) debe pausar.	
			}
		}
		datosUpdate.put("TIPO_DOCUMENTO", ((BigDecimal)CoreService.getValueForKey(viewRec, "tipo_documento", "tip_doc", databaseProperties)).intValue());
		datosUpdate.put("TRANSACCION_ID", CoreService.getValueForKey(viewRec, "transaccion_id", "tra_id", databaseProperties));
		datosUpdate.put("CLASIFIC", CoreService.getValueForKey(viewRec, "clasific", databaseProperties));
		
		updateFacturaSendDataInTableTransacciones(datosUpdate, databaseProperties, true);
		//---
		
		//Borrar registros previamente cargados, para evitar duplicidad
		deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties);

		Map<String, Object> datosGuardar = new HashMap<String, Object>();
		datosGuardar.put("ERROR", error);
		saveDataToFacturaSendTable(viewRec, datosGuardar, databaseProperties);
		
		if (pauseIfError != null && pauseIfError.equalsIgnoreCase("Y")) {
			//Si dio Error debe pausar.
			Map<String, Object> datosPausar = new HashMap<String, Object>();
			if (error != null) {
				datosPausar.put("PAUSADO", 1);	//Si dio error (que no es null) debe pausar				
				saveDataToFacturaSendTable(viewRec, datosPausar, databaseProperties);
			}
//			saveDataToFacturaSendTable(viewRec, datosPausar, databaseProperties);

		}
	}
	
	/**
	 * Utiliza la misma logica del WHERE del update pero para recuperar los datos relacionados a FacturaSend
	 * de la tabla Transacciones
	 *  
	 * @param datosUpdate Map conteniendo minimamente
	 * 						tipo_documento|tip_doc
	 * 						clasific
	 * 						y los valores para los WHEREs
	 * 
	 * @param databaseProperties
	 * @throws Exception
	 */
	private static Map<String, Object> selectFacturaSendDataFromTableTransacciones(Map<String, Object> datosUpdate, Map<String, String> databaseProperties) throws Exception {
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");

		Integer tipoDocumento = (Integer)CoreService.getValueForKey(datosUpdate, "tipo_documento", "tip_doc", databaseProperties);
		String clasificador = (String)CoreService.getValueForKey(datosUpdate, "clasific", databaseProperties);
		String tipoDE = tipoDocumento == 1 ? "fe" : tipoDocumento == 2 ? "ni" : tipoDocumento == 3 ? "ne" : tipoDocumento == 4 ? "af" : tipoDocumento == 5 ? "nc" : tipoDocumento == 6 ? "nd" : tipoDocumento == 7 ? "nr" : tipoDocumento == 8 ? "fe" : "";
		
		Map<String, Object> preSQLListMap = new HashMap<String, Object>();
				
		String prefijoTable = "database." + databaseProperties.get("database.type") + ".transaction_table_update." + tipoDE;
		
		if (clasificador != null) {
			prefijoTable += "." + clasificador;
		}
		if (databaseProperties.get(prefijoTable) != null) {
			//Si existe un nombre de tabla para actualizar
			
			String sql = "SELECT ";
			
			//Realiza el Seteo de los campos
			Iterator itr = databaseProperties.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+"";
				String value = e.getValue()+""; 
				String prefix = prefijoTable + ".field.";
				if ( key.startsWith(prefix)) {
					key = key.substring(prefix.length(), key.length());	//Extrae el nombre del camp
					Object valor = CoreService.getValueForKey(datosUpdate, value, databaseProperties);
					sql += key + ", ";

				}
			}
			sql = sql.substring(0, sql.length()-2) +  " ";
			sql += "FROM " + databaseProperties.get(prefijoTable) + " ";
			sql += "WHERE ";
					
			boolean poseeWhere = false;
			//Realiza el WHERE
			Iterator itr2 = databaseProperties.entrySet().iterator();
			while (itr2.hasNext()) {
				Map.Entry e = (Map.Entry)itr2.next();
				
				String key = e.getKey()+"";
				String value = e.getValue()+""; 
				String prefix = prefijoTable + ".where.";
				if ( key.startsWith(prefix)) {
					key = key.substring(prefix.length(), key.length());	//Extrae el nombre del camp
					Object valor = CoreService.getValueForKey(datosUpdate, value, databaseProperties);
					if (valor != null) {
						poseeWhere = true;
						sql += key + "= '" + valor + "' AND ";
					}
				}
			}
			sql = sql.substring(0, sql.length()-4) + "";
			
			if (poseeWhere) {
				//log.info("Comando a ejecutar para actualizar la BD " + sql);

				log.debug("\n" + sql + " ");
				PreparedStatement statement = conn.prepareStatement(sql);

				ResultSet rs = statement.executeQuery();
				
				Map<String, Object> preSQLListMapPrevio = SQLUtil.convertResultSetToMap(rs);

				//Antes de retornar, adiciona los mismos campos pero con el nombre de las key por defecto utilizado en la aplicacion
				Iterator itr3 = preSQLListMapPrevio.entrySet().iterator();
				while (itr3.hasNext()) {
					Map.Entry e = (Map.Entry)itr3.next();
					
					String key = e.getKey()+"";
					Object value = e.getValue(); 
					
					String prefix = prefijoTable + ".field." + key;

					preSQLListMap.put(CoreService.getValueFromMapCaseInsensitive(databaseProperties, prefix) + "", value);
				}
				
			} else {
				log.info("No se ejecutó el SELECT por que el WHERE no pudo ser resuelto " + sql);
			}
		}
		return preSQLListMap;
	}

	/**
	 * Actualiza la tabla, luego de realizar la integración de acuerdo al archivo de configuración
	 * 
	 * Se actualizarán solo los campos que no contengan valores nulos, independiente de que 
	 * esten especificados en el archivo de conf.
	 * 
	 * @param datosUpdate			Map con datos para actualizar, generalmente conteniendo:
	 * 									transaccion_id, 
	 * 									tra_id, 
	 * 									tipo_documento, 
	 * 									tip_doc, 
	 * 									clasific
	 * 									cdc, 
	 * 									estado, 
	 * 									pausado, 
	 * 									error
	 * @param tipoDocumento			Tipo de documento a actualizar
	 * @param databaseProperties
	 * @param updateWithNullNotPassedParams Indica si se va actualizar con nulos, los valores que están en el archivo de 
	 * 										configuracion y que no son pasados como parametros en datosUpdate.
	 * 										Pasarle false, si solo se desea actualizar en la tabla el parametro pasado sin tocar los demas valores. 
	 * @throws Exception
	 */
	private static void updateFacturaSendDataInTableTransacciones(Map<String, Object> datosUpdate, Map<String, String> databaseProperties, boolean updateWithNullNotPassedParams) throws Exception {
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");

		Integer tipoDocumento = (Integer)CoreService.getValueForKey(datosUpdate, "tipo_documento", "tip_doc", databaseProperties);
		String clasificador = (String)CoreService.getValueForKey(datosUpdate, "clasific", databaseProperties);
		String tipoDE = tipoDocumento == 1 ? "fe" : tipoDocumento == 2 ? "ni" : tipoDocumento == 3 ? "ne" : 
						tipoDocumento == 4 ? "af" : tipoDocumento == 5 ? "nc" : tipoDocumento == 6 ? "nd" : 
						tipoDocumento == 7 ? "nr" : tipoDocumento == 8 ? "" : "";
		
		String prefixTable = "database." + databaseProperties.get("database.type") + ".transaction_table_update";
		
		String discrimateByTp = databaseProperties.get("database.discriminate_facturasend_table_by_tipo_documento") + ""; 
		if ( discrimateByTp.equalsIgnoreCase("Y")) {
			prefixTable += "." + tipoDE;
		}
		if (clasificador != null) {
			prefixTable += "." + clasificador;
		}
		if (databaseProperties.get(prefixTable) != null) {
			//Si existe un nombre de tabla para actualizar
			
			String sql = "UPDATE " + databaseProperties.get(prefixTable) + " SET ";
			
			//Realiza el Seteo de los campos
			Iterator itr = databaseProperties.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+"";
				String value = e.getValue()+""; 
				String prefix = prefixTable + ".field.";
				if ( key.startsWith(prefix)) {
					key = key.substring(prefix.length(), key.length());	//Extrae el nombre del campo
					Object valor = CoreService.getValueForKey(datosUpdate, value, databaseProperties);
					if (valor != null) {
						sql += key + "= ?, ";
						
					} else {
						if (updateWithNullNotPassedParams) {
							sql += key + "= ?, ";
						}
					}
				}
			}
			
			sql = sql.substring(0, sql.length()-2) + " WHERE ";
			
			boolean poseeWhere = false;
			
			//Realiza el WHERE
			Iterator itr2 = databaseProperties.entrySet().iterator();
			while (itr2.hasNext()) {
				Map.Entry e = (Map.Entry)itr2.next();
				
				String key = e.getKey()+"";
				String value = e.getValue()+""; 
				String prefix = prefixTable + ".where.";
				if ( key.startsWith(prefix)) {
					key = key.substring(prefix.length(), key.length());	//Extrae el nombre del camp
					Object valor = CoreService.getValueForKey(datosUpdate, value, databaseProperties);
					if (valor != null) {
						poseeWhere = true;
						sql += key + "= ? AND ";
					}
				}
			}
			sql = sql.substring(0, sql.length()-4) + "";
			
			if (poseeWhere) {
				//log.info("Comando a ejecutar para actualizar la BD " + sql);

				String prefixSize = "database." + databaseProperties.get("database.type") + ".transaction_table_update.statusPaused.fieldSize";
				Integer statusPauedSize = 0;
				if (databaseProperties.get(prefixSize) != null){
					statusPauedSize = Integer.valueOf(databaseProperties.get(prefixSize) + "");
				}

				log.debug("\n" + sql + " ");
				PreparedStatement statement = conn.prepareStatement(sql);
				
				//SET Params Value
				int cParams = 1;
				Iterator itr3 = databaseProperties.entrySet().iterator();
				while (itr3.hasNext()) {
					Map.Entry e = (Map.Entry)itr3.next();
					
					String key = e.getKey()+"";
					String value = e.getValue()+""; 
					String prefix = prefixTable + ".field.";
					
					if ( key.startsWith(prefix)) {
						key = key.substring(prefix.length(), key.length());	//Extrae el nombre del camp
						Object valor = CoreService.getValueForKey(datosUpdate, value, databaseProperties);
						if (valor != null) {
							log.info("Params(" + cParams + "," + valor +")");
							
							if ((valor+"").length() < statusPauedSize) {
								valor = StringUtil.padLeftZeros(valor+"", statusPauedSize);
							}
							statement.setObject(cParams++, valor);
							
						} else {
							if (updateWithNullNotPassedParams) {
								log.info("Params(" + cParams + "," + valor +")");
								statement.setObject(cParams++, null);
							}
						}
					}
				}
				
				//WHERE Params Value
				Iterator itr4 = databaseProperties.entrySet().iterator();
				while (itr4.hasNext()) {
					Map.Entry e = (Map.Entry)itr4.next();
					
					String key = e.getKey()+"";
					String value = e.getValue()+""; 
					String prefix = prefixTable + ".where.";
					if ( key.startsWith(prefix)) {
						key = key.substring(prefix.length(), key.length());	//Extrae el nombre del camp
						Object valor = CoreService.getValueForKey(datosUpdate, value, databaseProperties);
						if (valor != null) {
							poseeWhere = true;
							log.info("Params(" + cParams + "," + valor +")");
							//sql += key + "= '" + valor + "' AND ";
							statement.setObject(cParams++, valor);
						}
					}
				}
				
				
				
				
				
				
				
				
				
				
				
				int result = statement.executeUpdate();
				log.info("result: " + result);
			} else {
				log.info("No se ejecutó el UPDATE por que el WHERE no pudo ser resuelto " + sql);
			}


		}				
	}

	
	public static Map<String, Object> actualizarEstadoDesdeFacturaSend(Integer tipoDocumento, Map<String, String> databaseProperties)  {
		Map<String, Object> result = new HashMap<String, Object>();

		Map<String, Object> clasificMap = new HashMap<String, Object>();	//Aqui se cargaran fe, nc, mio, mov, para uso posterior.
		
		String tipoDE = tipoDocumento == 1 ? "fe" : tipoDocumento == 2 ? "ni" : tipoDocumento == 3 ? "ne" : tipoDocumento == 4 ? "af" : tipoDocumento == 5 ? "nc" : tipoDocumento == 6 ? "nd" : tipoDocumento == 7 ? "nr" : tipoDocumento == 8 ? "fe" : "";

		String prefixForFields = "database." + databaseProperties.get("database.type") + ".facturasend_table";
		if (!databaseProperties.get("database.type").equalsIgnoreCase("dbf")) {
			prefixForFields += "." + tipoDE;
		}
		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+""; 
			if ((key).startsWith(prefixForFields) && key.contains(".field.")) {
				String clasificadorFromConfig = key.substring(prefixForFields.length()+1, key.length());
				//System.out.println("clasificadorFromConfig no" + clasificadorFromConfig);
				clasificadorFromConfig = clasificadorFromConfig.substring(0, clasificadorFromConfig.indexOf("."));
				//System.out.println(clasificadorFromConfig);
				clasificMap.put(clasificadorFromConfig, clasificadorFromConfig);
			}
		}
		
		
		Iterator itr2 = clasificMap.entrySet().iterator();
		while (itr2.hasNext()) {
			Map.Entry e = (Map.Entry)itr2.next();
			String clasific = e.getKey() + "";
			actualizarEstadoDesdeFacturaSendConClasif(tipoDocumento, clasific, databaseProperties);
		}
		
		return null;
	}

	/*
	 * Obtiene el Estado de los documentos desde FacturaSend
	 * 
	 * Se intentaran obtener los estados de los DEs cuyos estado actual = 0-Generado
	 */
	public static Map<String, Object> actualizarEstadoDesdeFacturaSendConClasif(Integer tipoDocumento, String clasific, Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		Map<String, Object> result = new HashMap<String, Object>();
		
		
		try {
			//cdcList=> contendrá el campo cdc, y el campo transaccion_id|tra_id, para uso futuro
			List<Map<String, Object>> cdcList = obtenerCDCsConEstadoGenerado(tipoDocumento, clasific, databaseProperties);
			
			for (Map<String, Object> map : cdcList) {
				map.put("cdc", (map.get("cdc")+"").trim());
			}
			
			if (cdcList.size() > 0) {	
				
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("cdcList", cdcList);
				
				Map header = new HashMap();
				header.put("Authorization", "Bearer api_key_" + databaseProperties.get("facturasend.token"));
				String url = databaseProperties.get("facturasend.url");
				url += "/de/estado";
				
				log.info("Verificar estados de : " + data.size() + " De(s)");
				Map<String, Object> resultadoJson = HttpUtil.invocarRest(url, "POST", gson.toJson(data), header);
				
				if (resultadoJson != null) {
					if (Boolean.valueOf(resultadoJson.get("success")+"") == true ) {

						//Verificar si resultado de enviados y recibidos son iguales.
						List<Map<String, Object>> deListConEstados = (List<Map<String, Object>>)resultadoJson.get("deList");
						
						if (cdcList.size() == deListConEstados.size()) {
							if (deListConEstados.size() > 0) {
								//Actualizar estados en la Base de datos, solo si el estado es != 0
								for (int j = 0; j < deListConEstados.size(); j++) {
									Map<String, Object> deConEstado = deListConEstados.get(j);
								
									if ( ! (CoreService.getValueForKey(deConEstado, "estado", databaseProperties) + "").equals("0")) {
										
										System.out.println("-----------------------------" + deConEstado);
										//---
										Object transaccion_id = CoreService.getValueForKey(cdcList.get(j), "transaccion_id", "tra_id", databaseProperties);
										Integer estadoActualizar = Double.valueOf(CoreService.getValueForKey(deConEstado, "situacion", databaseProperties) + "").intValue();
										String respuestaMensaje = CoreService.getValueForKey(deConEstado, "respuesta_mensaje", databaseProperties) + "";
										if (estadoActualizar > 0) {
											//Solo actualizar si el estado es > 0
											
											//Actualiza la tabla destino de acuerdo a la configuracion
											Map<String, Object> datosUpdate = new HashMap<String, Object>();
											datosUpdate.put("ESTADO", estadoActualizar);
											datosUpdate.put("TIPO_DOCUMENTO", tipoDocumento);
											datosUpdate.put("TRANSACCION_ID", CoreService.getValueForKey(cdcList.get(j), "transaccion_id", "tra_id", databaseProperties));
											datosUpdate.put("CLASIFIC", CoreService.getValueForKey(cdcList.get(j), "clasific", databaseProperties));
											
											updateFacturaSendDataInTableTransacciones(datosUpdate, databaseProperties, false);
											//---
											
											//Borrar registros de ESTADO previamente cargados, para evitar duplicidad
											deleteFacturaSendTableByTransaccionId(cdcList.get(j), databaseProperties, "('ESTADO')");
					
											Map<String, Object> datosGuardar1 = new HashMap<String, Object>();
											datosGuardar1.put("ESTADO", estadoActualizar);
											saveDataToFacturaSendTable(cdcList.get(j), datosGuardar1, databaseProperties);
											
											//System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + estadoActualizar);
											//System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + respuestaMensaje);
											
											if (estadoActualizar == 4) {
												System.out.println("XYSY actualizar aqui la descripcion del estado" + respuestaMensaje);
												
												Map<String, Object> datosRechazoUpdate = new HashMap<String, Object>();
												datosRechazoUpdate.put("TIPO_DOCUMENTO", tipoDocumento);
												datosRechazoUpdate.put("TRANSACCION_ID", CoreService.getValueForKey(cdcList.get(j), "transaccion_id", "tra_id", databaseProperties));
												datosRechazoUpdate.put("CLASIFIC", CoreService.getValueForKey(cdcList.get(j), "clasific", databaseProperties));
												datosRechazoUpdate.put("ERROR", respuestaMensaje);
																								
												updateFacturaSendDataInTableTransacciones(datosRechazoUpdate, databaseProperties, false);
												
												Map<String, Object> datosRechazo = new HashMap<String, Object>();
												datosRechazo.put("ERROR", respuestaMensaje);
												saveDataToFacturaSendTable(cdcList.get(j), datosRechazo, databaseProperties);
												
											}
										} else {
											System.err.println("transaccion_id=" + transaccion_id + " sin variación de estado(0). Ignorado");
										}
									}
								}
							}
						} else {
							System.err.println("Cantidad de CDCs enviados en verificar estado = " + cdcList.size() + ", recibidos = " + deListConEstados.size() + ". Ignorado por diferencia");
						}
					} else {
						throw new Exception(resultadoJson.get("error") + "");
					}
				}
				
				result.put("success", true);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
		}
		return result;
	}
	
	/*
	 * Guarda el Archivo XML en la Carpeta indicada en el config.properties
	 */
	public static Map<String, Object> imprimirXMLFacturaSend(String cdc, String xml, Integer tipoDocumento, Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		Map<String, Object> result = new HashMap<String, Object>();
		
		
		try {
			if (cdc != null) {
                cdc = cdc.trim();
                     
				String nombreDE = tipoDocumento == 1 ? "FE" : tipoDocumento == 2 ? "NI" : tipoDocumento == 3 ? "NE" : tipoDocumento == 4 ? "AF" : tipoDocumento == 5 ? "NC" : tipoDocumento == 6 ? "ND" : tipoDocumento == 7 ? "NR" : "";
				String establecimiento = cdc.substring(11, 14);
				String punto = cdc.substring(14, 17);
				String numero = cdc.substring(17, 24);
				String carpetaXml = databaseProperties.get("facturasend.carpetaXML");

				if (carpetaXml != null) {
					if (new File(carpetaXml).exists()) {
						File targetFile = new File(carpetaXml + File.separator + nombreDE + "_" + establecimiento + "-" + punto + "-" + numero + ".xml");
					   
					    FileWriter myWriter = new FileWriter(targetFile);
					    myWriter.write(xml);
					    myWriter.close();
					      
					} else {
						log.info("Carpeta " + carpetaXml + " no encontrado. Ignorado guardado de archivo");
					}
				} else {
					log.info("Parametro facturasend.carpetaKude no informado. Ignorado guardado de archivo");
				}
			    
			} else {
            	JOptionPane.showMessageDialog(null, "El item no posee CDC");
            }
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
		}
		return result;
	}

	/*
	 * Guarda el Archivo PDF en la Carpeta indicada en el config.properties
	 * 
	 * y tambien lanza el PDF directamente a la impresora, caso asi este 
	 * configurado en config.properties
	 *  
	 */
	public static Map<String, Object> imprimirKUDEFacturaSend(String cdc, Integer tipoDocumento, Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		Map<String, Object> result = new HashMap<String, Object>();
		
		
		try {
			if (cdc != null) {
                cdc = cdc.trim();
                
                List<Map<String, Object>> deList = new ArrayList<Map<String,Object>>();
				Map data = new HashMap();
				data.put("type", "base64");
				data.put("cdcList", deList);
				
				Map cdcMap = new HashMap();
				cdcMap.put("cdc", cdc);

				deList.add(cdcMap);
				
				Map header = new HashMap();
				header.put("Authorization", "Bearer api_key_" + databaseProperties.get("facturasend.token"));
				String url = databaseProperties.get("facturasend.url")+"";
				url += "/de/pdf";
				
				try {
					Map<String, Object> resultadoJson = HttpUtil.invocarRest(url, "POST", gson.toJson(data), header);
					
					//probar con un pdf fijo, del folder
					if (resultadoJson != null) {
						if (Boolean.valueOf(resultadoJson.get("success") + "") == true) {
							String nombreDE = tipoDocumento == 1 ? "FE" : tipoDocumento == 2 ? "NI" : tipoDocumento == 3 ? "NE" : tipoDocumento == 4 ? "AF" : tipoDocumento == 5 ? "NC" : tipoDocumento == 6 ? "ND" : tipoDocumento == 7 ? "NR" : "";
							String establecimiento = cdc.substring(11, 14);
							String punto = cdc.substring(14, 17);
							String numero = cdc.substring(17, 24);
							String carpetaKude = databaseProperties.get("facturasend.carpetaKude");

							if (carpetaKude != null) {
								if (new File(carpetaKude).exists()) {
									File targetFile = new File(carpetaKude + File.separator + nombreDE + "_" + establecimiento + "-" + punto + "-" + numero + ".pdf");
								    OutputStream outStream = new FileOutputStream(targetFile);
								    outStream.write(Base64.getDecoder().decode(resultadoJson.get("value") + ""));

								    IOUtils.closeSilently(outStream);									
								} else {
									log.info("Carpeta " + carpetaKude + " no encontrado. Ignorado guardado de archivo");
								}
							} else {
								log.info("Parametro facturasend.carpetaKude no informado. Ignorado guardado de archivo");
							}
						    

							//Impresion 
							ByteArrayOutputStream out = new ByteArrayOutputStream();

							byte[] decoder = Base64.getDecoder().decode(resultadoJson.get("value") + "");

							out.write(decoder);

							ByteArrayInputStream inStream = new ByteArrayInputStream(out.toByteArray());

							String printerName = databaseProperties.get("config.otros.nombre_impresora")+"";
							String enviarKUDEImpresora = databaseProperties.get("config.otros.enviar_kude_impresora")+"";
							
							
							if (enviarKUDEImpresora.equalsIgnoreCase("Y")) {
								PrintPdf printPDFFile = new PrintPdf(inStream, "FacturaSend", printerName, "PDF");
					            printPDFFile.print();	
							}
							

						}

					}
										
				} catch (Exception e2) {
					// TODO: handle exception
					JOptionPane.showMessageDialog(null, "Ocurrio un problema inesperado\n"+e2);
					e2.printStackTrace();
				}
			} else {
            	JOptionPane.showMessageDialog(null, "El item no posee CDC");
            }
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
		}
		return result;
	}
	
	public static Integer deleteFacturaSendTableByTransaccionId(Map<String, Object> de, Map<String, String> databaseProperties) throws Exception{
		Integer result = deleteFacturaSendTableByTransaccionId(de, databaseProperties, "('ERROR', 'ESTADO', 'PAUSADO', 'XML', 'JSON', 'QR', 'CDC', 'TIPO')");

		return result;
	}

	/**
	 * Borra los registros de la tabla FacturaSend Data, relacionada con la Transaccion, para 
	 * que pueda posteriormente recibir datos actualizados.
	 * 
	 * Debe especifiarse en "inNames", las Keys de la tabla, que deberán eliminarse
	 *  
	 * @param de		Map conteniendo el 
	 * 					transaccion_id|tra_id, 
	 * 					tipo_documento|tip_doc
	 * 					clasific (opcional)
	 * @param error
	 * @param databaseProperties
	 * @param inNames	Clausula IN con las names que deberán eliminarse, ej.: "('ERROR', 'CDC')"
	 * @return
	 */
	public static Integer deleteFacturaSendTableByTransaccionId(Map<String, Object> de, Map<String, String> databaseProperties, String inNames) throws Exception{
		Integer result = 0;

		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
		
		Integer tipoDocumento = Integer.valueOf(CoreService.getValueForKey(de, "tipo_documento", "tip_doc", databaseProperties)+"");
		
		String clasificador = (String)CoreService.getValueForKey(de, "clasific", databaseProperties);
		String tipoDE = tipoDocumento == 1 ? "fe" : tipoDocumento == 2 ? "ni" : tipoDocumento == 3 ? "ne" : tipoDocumento == 4 ? "af" : tipoDocumento == 5 ? "nc" : tipoDocumento == 6 ? "nd" : tipoDocumento == 7 ? "nr" : tipoDocumento == 8 ? "fe" : "";
		String prefixForTable = "database." + databaseProperties.get("database.type") + ".facturasend_table";
		
		String tableToUpdateKey =  prefixForTable + ".key";
		tableToUpdateKey = databaseProperties.get(tableToUpdateKey);
		if (databaseProperties.get("database.type").equalsIgnoreCase("dbf")) {
			if (prefixForTable.endsWith(".dbf")) {
				prefixForTable = prefixForTable.substring(0, prefixForTable.indexOf(".dbf"));	
			}
		}else {
			prefixForTable += "." + tipoDE;			
		}

		if (clasificador != null) {
			prefixForTable += "." + clasificador;
		}
		
		
		//---
		String tableToDelete = databaseProperties.get(prefixForTable);
		String prefix = "database." + databaseProperties.get("database.type") + ".facturasend_table" + ((databaseProperties.get("database.type").equalsIgnoreCase("dbf")) ? "" : "." + tipoDE);
		//en dbf mi clasificador me trae como 10 espacios vacios, por lo cual no me deja integrar
		if (clasificador != null) {
			prefix += "." + clasificador;
		}
		prefix += ".field.";
		String transaccionIdForeignKeyField = CoreService.findKeyByValueInProperties(databaseProperties, prefix, "transaccion_id");
		System.out.println("\nDE\n"+de+"\n");
		System.out.println("1-------" + transaccionIdForeignKeyField + " prefix "+prefix);
		if (transaccionIdForeignKeyField == null) {
			transaccionIdForeignKeyField = CoreService.findKeyByValueInProperties(databaseProperties, prefix, "tra_id");
		}
		System.out.println("2-------" + transaccionIdForeignKeyField);
		transaccionIdForeignKeyField = transaccionIdForeignKeyField.substring(prefix.length(), transaccionIdForeignKeyField.length());
		System.out.println("3-------" + transaccionIdForeignKeyField);
		if ( tableToDelete.endsWith(".dbf")) {
			tableToDelete = tableToDelete.substring(0, tableToDelete.indexOf(".dbf"));
		}
		
		
		
		String sql = "DELETE FROM " 
						+ tableToDelete + " "
						+ "WHERE 1=1 "; 
		
		//Agrega el where del tipo de documento solamente si esta establecido en el config
		String tipoDocumentoOrTipDoc = CoreService.getKeyExists(de, "tipo_documento", "tip_doc") + "";
		if (CoreService.findKeyByValueInProperties(databaseProperties, prefix, tipoDocumentoOrTipDoc) != null) {
			sql += "AND " + tipoDocumentoOrTipDoc + " = " + CoreService.getValueForKey(de, "tipo_documento", "tip_doc", databaseProperties) + " ";
		}
		
		sql += "AND " + transaccionIdForeignKeyField + " = " + CoreService.getValueForKey(de, "transaccion_id", "tra_id", databaseProperties) + " "
			+ "AND TRIM(UPPER(" + tableToUpdateKey + ")) IN " + inNames;

		log.debug("\n" + sql + " ");
		PreparedStatement statement = conn.prepareStatement(sql);

		result = statement.executeUpdate();
		log.info("result: " + result);

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
				log.debug("\n" + sql + " ");
				statement = conn.prepareStatement(sql);
				int dropTableResult = statement.executeUpdate();
				
				log.info("rows: " + dropTableResult + " ");				
			}
		}
	}
	
	/**
	 * 
	 * @param viewPrincipal		Map conteniendo el 
	 * 							transaccion_id|tra_id, 
	 * 							tipo_documento|tip_doc,
	 * 							clasific, 
	 * 							como asi tambien los valores para la 
	 * 							actualización de los elementos cuyos campos se definen en el archivo de configuración
	 * 							en el campo database.type.facturasend_table.field.
	 * @param error
	 * @param databaseProperties
	 * @return
	 */
	public static Integer saveDataToFacturaSendTable(Map<String, Object> viewPrincipal, Map<String, Object> datosGuardar, Map<String, String> databaseProperties) throws Exception{
		Integer result = 0;
	
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
		
		Integer tipoDocumento = Integer.valueOf(CoreService.getValueForKey(viewPrincipal, "tipo_documento", "tip_doc", databaseProperties)+"");
		String clasificador = (String)CoreService.getValueForKey(viewPrincipal, "clasific", databaseProperties);
		String tipoDE = tipoDocumento == 1 ? "fe" : tipoDocumento == 2 ? "ni" : tipoDocumento == 3 ? "ne" : tipoDocumento == 4 ? "af" : tipoDocumento == 5 ? "nc" : tipoDocumento == 6 ? "nd" : tipoDocumento == 7 ? "nr" : tipoDocumento == 8 ? "fe" : "";
		
		String prefixForTable = "database." + databaseProperties.get("database.type") + ".facturasend_table" + (databaseProperties.get("database.type").equals("dbf")?"":"."+tipoDE);
		if (clasificador != null) {
			prefixForTable += "." + clasificador;
		}
		String tableToUpdate = databaseProperties.get(prefixForTable);
		String tableToUpdateKey = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.value");
		
		if (databaseProperties.get("database.type").equals("oracle")) {
			tableToUpdateKey = tableToUpdateKey.toUpperCase();
			tableToUpdateValue = tableToUpdateValue.toUpperCase();
		}
		String fieldPrefix = prefixForTable + ".field." + tipoDE;
		if (clasificador != null) {
			fieldPrefix += "." + clasificador;
		}
		fieldPrefix += ".";
		
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
		
		if (databaseProperties.get("database.type").equalsIgnoreCase("dbf")) {
			if (tableToUpdate.endsWith(".dbf")) {
				tableToUpdate = tableToUpdate.substring(0, tableToUpdate.indexOf(".dbf"));	
			}
		}
		
		String sqlUpdate = "INSERT INTO " + tableToUpdate + " (";
		
		//Agrega el tipo documento si asi esta establecido en el config
		/*String tipoDocumentoOrTipDoc = CoreService.getKeyExists(viewPrincipal, "tipo_documento", "tip_doc") + "";
		if (CoreService.findKeyByValueInProperties(databaseProperties, prefix, tipoDocumentoOrTipDoc) != null) {
			//sqlUpdate += "\"" + CoreService.getKeyExists(viewPrincipal, "tipo_documento", "tip_doc") + "\", ";
			sqlUpdate += "" + CoreService.getKeyExists(viewPrincipal, "tipo_documento", "tip_doc") + ", ";
		}*/
		
		//Buscar fields adicionales
		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+""; 
			if ((key).startsWith(prefixForTable + ".field.")) {
				sqlUpdate += "" + key.substring((prefixForTable + ".field.").length(), key.length()) + ", ";
			}
		}
				
		sqlUpdate += "\"" + tableToUpdateKey + "\", ";
		sqlUpdate += "\"" + tableToUpdateValue + "\") VALUES ";
		
		//Buscar fields value adicionales
		Iterator itrDato = datosGuardar.entrySet().iterator();
		while (itrDato.hasNext()) {	//Recorre los datos que se tienen que guardar, cdc, numero, estado, error, etc
			Map.Entry eDato = (Map.Entry)itrDato.next();

			sqlUpdate += "(";	//Fijo para tipo de documento.
			
			/*//Agrega el parametro de tipo de documento, si esta establecido en el config. 
			if (CoreService.findKeyByValueInProperties(databaseProperties, prefix, tipoDocumentoOrTipDoc) != null) {
				sqlUpdate += "?, ";
			}*/
			
			itr = databaseProperties.entrySet().iterator();
			while (itr.hasNext()) {	//Recorre los campos de la tabla a almacenar
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+""; 
				String value = e.getValue()+"";
				if ((key).startsWith(prefixForTable + ".field.")) {
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
		System.out.println("SQL UPDATE"+sqlUpdate);
		PreparedStatement statement = conn.prepareStatement(sqlUpdate);

		

		
		
		//Agregar los parametros.
		boolean swTieneValores = false;

		itrDato = datosGuardar.entrySet().iterator();
		while (itrDato.hasNext()) {	//Recorre los datos que se tienen que guardar, cdc, numero, estado, error, etc
			swTieneValores = true;

			Map.Entry eDato = (Map.Entry)itrDato.next();
			
			itr = databaseProperties.entrySet().iterator();
			int f = 1;
			
			/*//Agrega el parametro de tipo de documento, si esta establecido en el config. 
			if (CoreService.findKeyByValueInProperties(databaseProperties, prefix, tipoDocumentoOrTipDoc) != null) {
				statement.setObject(f++, CoreService.getValueForKey(viewPrincipal, "tipo_documento", "tip_doc"));	//Fijo
			}*/

			while (itr.hasNext()) {	//Recorre los campos de la tabla a almacenar
				Map.Entry e = (Map.Entry)itr.next();
				
				String key = e.getKey()+""; 
				String value = e.getValue()+"";
				if ((key).startsWith(prefixForTable + ".field.")) {
					if (value.startsWith("@SQL(")) {
						//Como ya coloco el SQL, entonces aqui no inserta los parámetros.
						
					} else {
						//Toma los valores del view-principal, dependiendo de las claves que estan en el config.
						statement.setObject(f++, CoreService.getValueForKey(viewPrincipal, e.getValue()+"", databaseProperties));
						System.out.println("ALGO\n"+CoreService.getValueForKey(viewPrincipal, e.getValue()+"", databaseProperties));
					}
				}
			}
		
			statement.setString(f++, eDato.getKey() + "");
			
			Clob clob = conn.createClob();
			clob.setString(1, eDato.getValue()+"" );

			statement.setClob(f++, clob );
		}
	
		if (swTieneValores) {
			log.debug("\n" + sqlUpdate + " ");
			result = statement.executeUpdate();
			
			log.info("result: " + result + "");
			String prefix = "database." + databaseProperties.get("database.type") + ".facturasend_table.";
			
			String posUpdateSQL = databaseProperties.get(prefix + ".pos_update_sql");

			if (posUpdateSQL == null) {
				prefix += tipoDE;
				
				if (clasificador != null) {
					prefix += "." + clasificador;
				}
				
				posUpdateSQL = databaseProperties.get(prefix + ".pos_update_sql");
			}

			//String posUpdateSQL = databaseProperties.get(prefix + ".pos_update_sql");
			if (posUpdateSQL != null) {
				log.debug("\n" + posUpdateSQL + " ");
				PreparedStatement statement2 = conn.prepareStatement(posUpdateSQL);
				//ResultSet rs = statement2.executeQuery();
				Integer resultExecuteUpdate = statement2.executeUpdate();
				
				//Map<String, Object> posUpdateSQLMap = SQLUtil.convertResultSetToMap(rs);
				log.info("resultExecuteUpdate:" + resultExecuteUpdate);
			}
		}

		
		return result;
	}
	
	
	/**
	 * Paso 1. Proceso que marca el registro de ventas con una opcion para pausar (o iniciar en caso que estaba pausado) 
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static void pausarEnviar(Integer transaccionId, Integer tipoDocumento, String clasificador, Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		
		try {
						
			
			guardarPausarEnviarTablaTransacciones(transaccionId, tipoDocumento, clasificador, databaseProperties);
			guardarPausarEnviar(transaccionId, tipoDocumento, clasificador, databaseProperties);

			
			//
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Cambiar éste resultado, por el resultado estandar
		
		return;
	}

	
	/**
	 *  
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static void actualizarEstado(Integer tipoDocumento, Map<String, String> databaseProperties)  {
		//Recupera los transaccion_id que se deben integrar
		
		try {
						
			
			actualizarEstadoDesdeFacturaSend(tipoDocumento, databaseProperties);
			
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
	public static Integer guardarPausarEnviarTablaTransacciones(Integer transaccionId, Integer tipoDocumento, String clasificador, Map<String, String> databaseProperties) throws Exception{
		Integer result = 0;
		
		//Antes recuperar Datos, por si ya tenga almacenado el Error 
		//o por si el registro ya haya sido integrado antes.
		Map<String, Object> datosWhere = new HashMap<String, Object>();
		datosWhere.put("TIPO_DOCUMENTO", tipoDocumento);
		datosWhere.put("TRANSACCION_ID", transaccionId);
		datosWhere.put("CLASIFIC", clasificador);
		Map<String, Object> situacionPausadoActualMap = selectFacturaSendDataFromTableTransacciones(datosWhere, databaseProperties);
		System.out.println("MAP ------------------" + situacionPausadoActualMap);
		if (CoreService.getValueForKey(situacionPausadoActualMap, "estado", databaseProperties) == "02" || CoreService.getValueForKey(situacionPausadoActualMap, "estado", databaseProperties) == "03") {
			throw new Exception("La transacción # " + transaccionId + " - Tipo: " + tipoDocumento + " ya está integrado");
		}
		
		//Actualiza la tabla destino de acuerdo a la configuracion
		Map<String, Object> datosUpdate = new HashMap<String, Object>();
		datosUpdate.put("CDC", CoreService.getValueForKey(situacionPausadoActualMap, "cdc", databaseProperties)=="null" ? null : CoreService.getValueForKey(situacionPausadoActualMap, "cdc", databaseProperties));
		//datosUpdate.put("PAUSADO", CoreService.getValueForKey(respuestaDE, "estado"));
		datosUpdate.put("TIPO_DOCUMENTO", tipoDocumento);
		datosUpdate.put("TRANSACCION_ID", transaccionId);
		datosUpdate.put("CLASIFIC", clasificador);

		if (situacionPausadoActualMap.get("pausado") == null) {
			//Significa que aun luego no se integro
			//Significa que aun no tiene pausado, entonces debe poner
			datosUpdate.put("PAUSADO", 1);
			datosUpdate.put("ERROR", CoreService.getValueForKey(situacionPausadoActualMap, "error", databaseProperties));
			
		} else {
			//Ya tiene pausado, debe retirar.
			datosUpdate.put("PAUSADO", null);
			datosUpdate.put("ERROR", CoreService.getValueForKey(situacionPausadoActualMap, "error", databaseProperties));
		}
		updateFacturaSendDataInTableTransacciones(datosUpdate, databaseProperties, true);
		
		return result;
	}
	
	/**
	 * 
	 * @param viewPrincipal
	 * @param error
	 * @param databaseProperties
	 * @return
	 */
	public static Integer guardarPausarEnviar(Integer transaccionId, Integer tipoDocumento, String clasificador, Map<String, String> databaseProperties) throws Exception{
		Integer result = 0;
		//TODO:: Ver como traer el clasificador aqui por que no encuentra la tabla
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
		String tipoDE = tipoDocumento == 1 ? "fe" : tipoDocumento == 2 ? "ni" : tipoDocumento == 3 ? "ne" : tipoDocumento == 4 ? "af" : tipoDocumento == 5 ? "nc" : tipoDocumento == 6 ? "nd" : tipoDocumento == 7 ? "nr" : tipoDocumento == 8 ? "fe" : "";
		String prefixTable = "database." + databaseProperties.get("database.type") + ".facturasend_table";
		
		String discrimateByTp = databaseProperties.get("database.discriminate_facturasend_table_by_tipo_documento") + ""; 
		if ( discrimateByTp.equalsIgnoreCase("Y")) {
			prefixTable += "." + tipoDE;
		}
		if (clasificador != null) {
			prefixTable += "." + clasificador;
		}
		String tableToUpdate = databaseProperties.get(prefixTable);
		if (tableToUpdate.endsWith(".dbf")) {
			tableToUpdate = tableToUpdate.substring(0, tableToUpdate.indexOf(".dbf"));
		}
		String tableToUpdateKey = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.value");
		
		//String transaccionIdForeignKeyField = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.field.transaccion_id");
		String prefix = "database." + databaseProperties.get("database.type") + ".facturasend_table";
		if ( discrimateByTp.equalsIgnoreCase("Y")) {
			prefix += "." + tipoDE;
		}
		if (clasificador != null) {
			prefix += "." + clasificador;
		}
		prefix += ".field.";
		String transaccionIdForeignKeyField = CoreService.findKeyByValueInProperties(databaseProperties, prefix, "transaccion_id");
		if (transaccionIdForeignKeyField == null) {
			transaccionIdForeignKeyField = CoreService.findKeyByValueInProperties(databaseProperties, prefix, "tra_id");
		}
		transaccionIdForeignKeyField = transaccionIdForeignKeyField.substring(prefix.length(), transaccionIdForeignKeyField.length());
		
		//Obtener de la BD
		String sqlConsulta = "SELECT CAST(\"" + tableToUpdateValue + "\" AS VARCHAR(10)) AS \"" + tableToUpdateValue + "\" "
						+ "FROM " + tableToUpdate + " "
						+ "WHERE "
						+ transaccionIdForeignKeyField + " = ? " 
						+ "AND TRIM(UPPER(" + tableToUpdateKey + ")) = 'PAUSADO' "; 
		
		PreparedStatement statement = conn.prepareStatement(sqlConsulta);
		statement.setInt(1, transaccionId);
		
		log.debug("\n" + sqlConsulta + " ");
		ResultSet rs = statement.executeQuery();
		
		Map<String, Object> situacionPausadoActualMap = SQLUtil.convertResultSetToMap(rs);
		log.info("situacionPausadoActualMap.size:" + situacionPausadoActualMap.size());
		log.debug("situacionPausadoActualMap:" + situacionPausadoActualMap);
		
		if ( situacionPausadoActualMap != null ) {
			
			Integer situacionPausadoValue = 0;
			if (CoreService.getValueForKey(situacionPausadoActualMap, tableToUpdateValue, databaseProperties) != null) {
				situacionPausadoValue = Integer.valueOf((CoreService.getValueForKey(situacionPausadoActualMap, tableToUpdateValue, databaseProperties) +"").trim());
			}
			
			if (situacionPausadoValue == 1) {
				//Ya existe el registro de PAUSADO y esta PAUSADO
			
			String sqlDelete = "DELETE "
					+ "FROM " + tableToUpdate + " "
							+ "WHERE "
							+ transaccionIdForeignKeyField + " = ? " 
							+ "AND TRIM(UPPER(" + tableToUpdateKey + ")) = 'PAUSADO' "; 
			
			log.info("\n" + sqlDelete + " ");

			PreparedStatement statementDelete = conn.prepareStatement(sqlDelete);
			statementDelete.setInt(1, transaccionId);
			
			int resultDelete = statementDelete.executeUpdate();
			log.info("resultDelete: " + resultDelete);
			}
		} else {
			//Consultar el objeto completo del Servidor, por el transaccion_id
			Map<String, Object> resultViewMap = obtenerTransaccionesParaEnvioLote("(" + transaccionId + ")", databaseProperties);
			if (resultViewMap != null) {

				if ( Boolean.valueOf(resultViewMap.get("success")+"")) {

					List<Map<String, Object>> listaTransaccionesView = (List<Map<String, Object>>)resultViewMap.get("result");
					
					if (listaTransaccionesView.size() > 0) {

						Map<String, Object> datosGuardar = new HashMap<String, Object>();
						datosGuardar.put("PAUSADO", "1");
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

			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
			
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

			log.debug("\n" + sql + " ");

			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> listadoDes = SQLUtil.convertResultSetToList(rs);
			log.info("listadoDes.size:" + listadoDes.size());
			log.debug("listadoDes:" + listadoDes);
			
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
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transaction_table_read");
		String sql = "";
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT transaccion_id \n"
						+ "FROM " + tableName + " \n"
						+ "WHERE 1=1 \n"
						+ "AND tipo_documento = " + tipoDocumento + " \n"
						+ "AND pausado IS NULL \n"
						+ "AND evento IS NULL \n"
						+ "AND ( \n"
							+ "CDC IS NULL \n"
							+ "OR \n"
							+ "(ESTADO IS NOT NULL AND ESTADO = 4) \n"
						+ ") \n"
						+ "GROUP BY transaccion_id, establecimiento, punto, numero \n"
						+ "ORDER BY establecimiento, punto, numero \n";	//Ordena de forma normal, para obtener el ultimo	
		} else {
			boolean obtenerCdcEstadoPausadoPorSubSelect = true;
			String transactionTableName = databaseProperties.get("database.dbf.transaccion_table");
			transactionTableName = transactionTableName.substring(0, transactionTableName.indexOf(".dbf"));

			String facturaSendTableName = databaseProperties.get("database.dbf.facturasend_table");
			facturaSendTableName = facturaSendTableName.substring(0, facturaSendTableName.indexOf(".dbf"));
			String facturaSendTableKey = databaseProperties.get("database.dbf.facturasend_table.key");
			String facturaSendTableValue = databaseProperties.get("database.dbf.facturasend_table.value");
			
			tableName = databaseProperties.get("database.dbf.transaccion_table");
			tableName = tableName.substring(0, tableName.indexOf(".dbf"));

			sql = "SELECT tra_id \n"
					+ "FROM " + tableName + " vp \n"
					+ "WHERE 1=1 \n"
					+ "AND tip_doc = " + tipoDocumento + " \n";

			if (obtenerCdcEstadoPausadoPorSubSelect) {

				sql += "AND (SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='PAUSADO' LIMIT 1) IS NULL \n"
						+ "AND ( \n"
						+ "(SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='CDC' LIMIT 1) IS NULL \n"
						+ "OR \n"
						+ "COALESCE(CAST((SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='ESTADO' LIMIT 1) AS INTEGER), 999) = 4 \n"
					+ ") \n";
			} else {
				sql += "AND pausado IS NULL AND (cdc IS NULL OR estado = 4) ";
			}
			sql += "GROUP BY tra_id, estable, punto, numero \n"
					+ "ORDER BY estable, punto, numero \n";	//Ordena de forma normal, para obtener el ultimo				
		}
		
		
		return sql;
	}
	
	
	/**
	 * Paso 1.1 - Obtener los registros que ya estan aprobados y cuyo action_event = 'Cancelar'
	 * 
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static Map<String, Object> obtenerRegistrosAprobadosACancelar(Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
			
			Statement statement = conn.createStatement();
			
			String sql = obtenerRegistrosAprobadosACancelarSQL(databaseProperties);

			log.debug("\n" + sql + " ");

			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> listadoRegistrosACancelar = SQLUtil.convertResultSetToList(rs);
			log.info("listadoRegistrosACancelar:" + listadoRegistrosACancelar);
			
			result.put("success", true);
			result.put("result", listadoRegistrosACancelar);
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
			
		}
		return result;
	}
			
	/**
	 * Paso 1.2 - Devuelve el SQL para Obtener los registros que ya estan aprobados y cuyo action_event = 'Cancelar'
	 * 
	 * @param databaseProperties
	 * @param tipoDocumento
	 * @return
	 */
	private static String obtenerRegistrosAprobadosACancelarSQL(Map<String, String> databaseProperties) {
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transaction_table_read");
		Map<String, String> fieldsSelectMap = new HashMap<String, String>();	//Lista de Campos que seran utilizados en el SELECT-SQL
		
		
		
		//-- Proceso para recuperar los datos adicionales, que se necesitaran mas adelante, cuando se va actualizar la tabla FacturaSend_data
		Integer tipoDocumento = 1;	//Fijo por el momento, si todos lostipos tienen los mismos campos adicionales va funcionar
		String clasificador = "fe";	//Fijo por el momento, si todos lostipos tienen los mismos campos adicionales va funcionar
		
		String tipoDE = tipoDocumento == 1 ? "fe" : tipoDocumento == 2 ? "ni" : tipoDocumento == 3 ? "ne" : tipoDocumento == 4 ? "af" : tipoDocumento == 5 ? "nc" : tipoDocumento == 6 ? "nd" : tipoDocumento == 7 ? "nr" : tipoDocumento == 8 ? "fe" : "";
		
		String prefixForTable = "database." + databaseProperties.get("database.type") + ".facturasend_table" + (databaseProperties.get("database.type").equals("dbf")?"":"."+tipoDE);
		if (clasificador != null) {
			prefixForTable += "." + clasificador;
		}
		String tableToUpdate = databaseProperties.get(prefixForTable);
		String tableToUpdateKey = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.value");
		
		if (databaseProperties.get("database.type").equals("oracle")) {
			tableToUpdateKey = tableToUpdateKey.toUpperCase();
			tableToUpdateValue = tableToUpdateValue.toUpperCase();
		}
		String fieldPrefix = prefixForTable + ".field." + tipoDE;
		if (clasificador != null) {
			fieldPrefix += "." + clasificador;
		}
		fieldPrefix += ".";
		
		//Buscar fields adicionales
		//String camposAdicionales = "";
		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+""; 
			String value = e.getValue()+""; 
			if (key.startsWith(prefixForTable + ".field.") && !value.startsWith("@SQL")) {
				String fieldName = key.substring((prefixForTable + ".field.").length(), key.length());
				//Agrega solo si el campo esta en la tabla transacciones
				if (!fieldName.equalsIgnoreCase("c_invoice_id"))	//TODO cambiar.
					fieldsSelectMap.put(fieldName + "", "");
			}
		}
		//System.out.println("camposAdicionales " + camposAdicionales);
		//-- Fin Proceso para recuperar los datos adicionales, que se necesitaran mas adelante, cuando se va actualizar la tabla FacturaSend_data

		fieldsSelectMap.put("clasific", "");
		fieldsSelectMap.put("cdc", "");
		fieldsSelectMap.put("estado", "");
		fieldsSelectMap.put("punto", "");
		fieldsSelectMap.put("numero", "");
		if (!databaseProperties.get("database.type").equals("dbf")) {
			fieldsSelectMap.put("transaccion_id", "");
			fieldsSelectMap.put("tipo_documento", "");
			fieldsSelectMap.put("evento_motivo", "");
			fieldsSelectMap.put("establecimiento", "");
		} else {
			fieldsSelectMap.put("tra_id", "");
			fieldsSelectMap.put("tip_doc", "");
			fieldsSelectMap.put("eve_mot", "");
			fieldsSelectMap.put("establec", "");
		}

		String fieldsSelectString = "";
		Iterator itr2 = fieldsSelectMap.entrySet().iterator();
		while (itr2.hasNext()) {
			fieldsSelectString += ((Map.Entry)itr2.next()).getKey()+", ";
		}
		fieldsSelectString = fieldsSelectString.substring(0, fieldsSelectString.length() -2);
		
		String sql = "";
		if (!databaseProperties.get("database.type").equals("dbf")) {
			
			sql = "SELECT " + fieldsSelectString + " \n"
						+ "FROM " + tableName + " \n"
						+ "WHERE 1=1 \n"
						//+ "AND tipo_documento = " + tipoDocumento + " \n"
						//+ "AND pausado IS NULL \n"
						+ "AND UPPER(evento) = 'CANCELAR' \n"
						+ "AND ( \n"
							+ "cdc IS NOT NULL \n"
							+ "AND \n"
							+ "(estado IS NOT NULL AND (estado = 2 OR estado = 3)) \n"
						+ ") \n"
						+ "GROUP BY " + fieldsSelectString + " \n"
						+ "ORDER BY establecimiento, punto, numero \n";	//Ordena de forma normal, para obtener el ultimo	
		} else {
			boolean obtenerCdcEstadoPausadoPorSubSelect = true;
			String transactionTableName = databaseProperties.get("database.dbf.transaccion_table");
			transactionTableName = transactionTableName.substring(0, transactionTableName.indexOf(".dbf"));

			String facturaSendTableName = databaseProperties.get("database.dbf.facturasend_table");
			facturaSendTableName = facturaSendTableName.substring(0, facturaSendTableName.indexOf(".dbf"));
			String facturaSendTableKey = databaseProperties.get("database.dbf.facturasend_table.key");
			String facturaSendTableValue = databaseProperties.get("database.dbf.facturasend_table.value");
			
			tableName = databaseProperties.get("database.dbf.transaccion_table");
			tableName = tableName.substring(0, tableName.indexOf(".dbf"));

			sql = "SELECT " + fieldsSelectString + " \n"
					+ "FROM " + tableName + " vp \n"
					+ "WHERE 1=1 \n";
					//+ "AND tip_doc = " + tipoDocumento + " \n";

			if (obtenerCdcEstadoPausadoPorSubSelect) {

				sql += "AND (SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='PAUSADO' LIMIT 1) IS NULL \n"
						+ "AND ( \n"
						+ "(SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='CDC' LIMIT 1) IS NULL \n"
						+ "OR \n"
						+ "COALESCE(CAST((SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='ESTADO' LIMIT 1) AS INTEGER), 999) = 4 \n"
					+ ") \n";
			} else {
				sql += "AND pausado IS NULL AND (cdc IS NULL OR estado = 4) ";
			}
			sql += "GROUP BY " + fieldsSelectString + " \n"
					+ "ORDER BY estable, punto, numero \n";	//Ordena de forma normal, para obtener el ultimo				
		}
		
		
		return sql;
	}
	
	
	
	/**
	 * Paso 1.1 - Obtener los registros que ya estan aprobados y cuyo action_event = 'Cancelar'
	 * 
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static Map<String, Object> obtenerRegistrosAInutilizar(Map<String, String> databaseProperties) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
			
			Statement statement = conn.createStatement();
			
			String sql = obtenerRegistrosAInutilizarSQL(databaseProperties);

//			Integer rowsLoteRequest = 50;
//			if (databaseProperties.get("facturasend.rows_lote_request") != null) {
//				rowsLoteRequest = Integer.valueOf(databaseProperties.get("facturasend.rows_lote_request"));
//			}
//			if (rowsLoteRequest > 50) {
//				throw new Exception("Cantidad máxima de documentos por lote = 50 (facturasend.rows_lote_request)");
//			}

//			if (databaseProperties.get("database.type").equals("oracle")) {
//				sql = CoreService.getOracleSQLPaginado(sql, 1, rowsLoteRequest);	
//			} else if (databaseProperties.get("database.type").equals("postgres")) {
//				sql = CoreService.getPostgreSQLPaginado(sql, 1, rowsLoteRequest);
//			} else if (databaseProperties.get("database.type").equals("dbf")) {
//				sql = CoreService.getPostgreSQLPaginado(sql, 1, rowsLoteRequest);
//			}

			log.debug("\n" + sql + " ");

			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> listadoRegistrosAinutilizar = SQLUtil.convertResultSetToList(rs);
			log.info("listadoRegistrosAinutilizar:" + listadoRegistrosAinutilizar);
			
			result.put("success", true);
			result.put("result", listadoRegistrosAinutilizar);
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("success", false);
			result.put("error", e.getMessage());
			
		}
		return result;
	}
	
	
	
	/**
	 * Paso 1.2 - Devuelve el SQL para Obtener los registros que ya estan aprobados y cuyo action_event = 'Cancelar'
	 * 
	 * @param databaseProperties
	 * @param tipoDocumento
	 * @return
	 */
	private static String obtenerRegistrosAInutilizarSQL(Map<String, String> databaseProperties) {
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transaction_table_read");
		
		Map<String, String> fieldsSelectMap = new HashMap<String, String>();	//Lista de Campos que seran utilizados en el SELECT-SQL
		
		
		
		//-- Proceso para recuperar los datos adicionales, que se necesitaran mas adelante, cuando se va actualizar la tabla FacturaSend_data
		Integer tipoDocumento = 1;	//Fijo por el momento, si todos lostipos tienen los mismos campos adicionales va funcionar
		String clasificador = "fe";	//Fijo por el momento, si todos lostipos tienen los mismos campos adicionales va funcionar
		
		String tipoDE = tipoDocumento == 1 ? "fe" : tipoDocumento == 2 ? "ni" : tipoDocumento == 3 ? "ne" : tipoDocumento == 4 ? "af" : tipoDocumento == 5 ? "nc" : tipoDocumento == 6 ? "nd" : tipoDocumento == 7 ? "nr" : tipoDocumento == 8 ? "fe" : "";
		
		String prefixForTable = "database." + databaseProperties.get("database.type") + ".facturasend_table" + (databaseProperties.get("database.type").equals("dbf")?"":"."+tipoDE);
		if (clasificador != null) {
			prefixForTable += "." + clasificador;
		}
		String tableToUpdate = databaseProperties.get(prefixForTable);
		String tableToUpdateKey = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.key");
		String tableToUpdateValue = databaseProperties.get("database." + databaseProperties.get("database.type") + ".facturasend_table.value");
		
		if (databaseProperties.get("database.type").equals("oracle")) {
			tableToUpdateKey = tableToUpdateKey.toUpperCase();
			tableToUpdateValue = tableToUpdateValue.toUpperCase();
		}
		String fieldPrefix = prefixForTable + ".field." + tipoDE;
		if (clasificador != null) {
			fieldPrefix += "." + clasificador;
		}
		fieldPrefix += ".";
		
		//Buscar fields adicionales
		//String camposAdicionales = "";
		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+""; 
			String value = e.getValue()+""; 
			if (key.startsWith(prefixForTable + ".field.") && !value.startsWith("@SQL")) {
				String fieldName = key.substring((prefixForTable + ".field.").length(), key.length());
				//Agrega solo si el campo esta en la tabla transacciones
				if (!fieldName.equalsIgnoreCase("c_invoice_id"))	//TODO cambiar.
					fieldsSelectMap.put(fieldName + "", "");
			}
		}
		//System.out.println("camposAdicionales " + camposAdicionales);
		//-- Fin Proceso para recuperar los datos adicionales, que se necesitaran mas adelante, cuando se va actualizar la tabla FacturaSend_data

		fieldsSelectMap.put("clasific", "");
		fieldsSelectMap.put("cdc", "");
		fieldsSelectMap.put("estado", "");
		fieldsSelectMap.put("punto", "");
		fieldsSelectMap.put("numero", "");
		fieldsSelectMap.put("serie", "");
		if (!databaseProperties.get("database.type").equals("dbf")) {
			fieldsSelectMap.put("transaccion_id", "");
			fieldsSelectMap.put("tipo_documento", "");
			fieldsSelectMap.put("evento_motivo", "");
			fieldsSelectMap.put("establecimiento", "");
		} else {
			fieldsSelectMap.put("tra_id", "");
			fieldsSelectMap.put("tip_doc", "");
			fieldsSelectMap.put("eve_mot", "");
			fieldsSelectMap.put("establec", "");
		}

		String fieldsSelectString = "";
		Iterator itr2 = fieldsSelectMap.entrySet().iterator();
		while (itr2.hasNext()) {
			fieldsSelectString += ((Map.Entry)itr2.next()).getKey()+", ";
		}
		fieldsSelectString = fieldsSelectString.substring(0, fieldsSelectString.length() -2);
		
		String sql = "";
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT " + fieldsSelectString + "  \n"
						+ "FROM " + tableName + " \n"
						+ "WHERE 1=1 \n"
						//+ "AND tipo_documento = " + tipoDocumento + " \n"
						//+ "AND pausado IS NULL \n"
						+ "AND UPPER(evento) = 'INUTILIZAR' \n"
						+ "and (ESTADO != 97) \n"
						+ "GROUP BY " + fieldsSelectString + " \n"
						+ "ORDER BY establecimiento, punto, numero \n";	//Ordena de forma normal, para obtener el ultimo	
		} else {
			boolean obtenerCdcEstadoPausadoPorSubSelect = true;
			String transactionTableName = databaseProperties.get("database.dbf.transaccion_table");
			transactionTableName = transactionTableName.substring(0, transactionTableName.indexOf(".dbf"));

			String facturaSendTableName = databaseProperties.get("database.dbf.facturasend_table");
			facturaSendTableName = facturaSendTableName.substring(0, facturaSendTableName.indexOf(".dbf"));
			String facturaSendTableKey = databaseProperties.get("database.dbf.facturasend_table.key");
			String facturaSendTableValue = databaseProperties.get("database.dbf.facturasend_table.value");
			
			tableName = databaseProperties.get("database.dbf.transaccion_table");
			tableName = tableName.substring(0, tableName.indexOf(".dbf"));

			sql = "SELECT " + fieldsSelectString + " \n"
					+ "FROM " + tableName + " vp \n"
					+ "WHERE 1=1 \n";
					//+ "AND tip_doc = " + tipoDocumento + " \n";

			if (obtenerCdcEstadoPausadoPorSubSelect) {

				sql += "AND (SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='PAUSADO' LIMIT 1) IS NULL \n"
						+ "AND ( \n"
						+ "(SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='CDC' LIMIT 1) IS NULL \n"
						+ "OR \n"
						+ "COALESCE(CAST((SELECT \"" + facturaSendTableValue + "\" FROM " + facturaSendTableName + " mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='ESTADO' LIMIT 1) AS INTEGER), 999) = 4 \n"
					+ ") \n";
			} else {
				sql += "AND pausado IS NULL AND (cdc IS NULL OR estado = 4) ";
			}
			sql += "GROUP BY " + fieldsSelectString + " \n"
					+ "ORDER BY estable, punto, numero \n";	//Ordena de forma normal, para obtener el ultimo				
		}
		
		
		return sql;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Obtiene una lista de los registros de transaccionse que ya tienen CDC pero cuyo estado este en generado.
	 * 
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static List<Map<String, Object>> obtenerCDCsConEstadoGenerado(Integer tipoDocumento, String clasific, Map<String, String> databaseProperties) throws Exception {
		
		List<Map<String, Object>> listadoDes = new ArrayList<Map<String,Object>>();
		
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
		
		Statement statement = conn.createStatement();
		
		String sql = obtenerCDCsConEstadoGeneradoSQL(databaseProperties, tipoDocumento, clasific);

		log.debug("\n" + sql + " ");

		ResultSet rs = statement.executeQuery(sql);
		
		listadoDes = SQLUtil.convertResultSetToList(rs);
		log.info("listadoDes:" + listadoDes);
		
		return listadoDes;
	}
	
	/**
	 * Obtiene una lista de los estados de las transacciones de acuerdo a los transaccion_ids pasados como parametros
	 * 
	 * @param tipoDocumento
	 * @param databaseProperties
	 * @return
	 */
	public static List<Map<String, Object>> obtenerEstadosPorTransaccionesId(Integer tipoDocumento, String transaccionesId, String clasific, Map<String, String> databaseProperties) throws Exception {
		
		List<Map<String, Object>> listadoDes = new ArrayList<Map<String,Object>>();
		
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
		
		Statement statement = conn.createStatement();
		
		String sql = obtenerEstadosPorTransaccionesIdSQL(databaseProperties, tipoDocumento, transaccionesId, clasific);

		log.debug("\n" + sql + " ");

		ResultSet rs = statement.executeQuery(sql);
		
		listadoDes = SQLUtil.convertResultSetToList(rs);
		log.info("listadoDes:" + listadoDes);
		
		return listadoDes;
	}
	
	/**
	 * 
	 * 
	 * @param databaseProperties
	 * @param tipoDocumento
	 * @return
	 */
	private static String obtenerCDCsConEstadoGeneradoSQL(Map<String, String> databaseProperties, Integer tipoDocumento, String clasificador) {
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transaction_table_read");
		String sql = "";
		
		//Integer tipoDocumento = (Integer)CoreService.getValueForKey(datosUpdate, "tipo_documento", "tip_doc");
		//String clasificador = (String)CoreService.getValueForKey(datosUpdate, "clasific");
		String tipoDE = tipoDocumento == 1 ? "fe" : tipoDocumento == 2 ? "ni" : tipoDocumento == 3 ? "ne" : tipoDocumento == 4 ? "af" : tipoDocumento == 5 ? "nc" : tipoDocumento == 6 ? "nd" : tipoDocumento == 7 ? "nr" : tipoDocumento == 8 ? "fe" : "";

		String prefixForFields = "database." + databaseProperties.get("database.type") + ".facturasend_table." + tipoDE;
			
		if (clasificador != null) {
			prefixForFields += "." + clasificador;
		}
		prefixForFields += ".field"; 
		//---
		String extraFields = "";

		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+""; 
			if ((key).startsWith(prefixForFields)) {
				//sql += key.substring(("database." + databaseProperties.get("database.type") + ".facturasend_table.field.").length(), key.length()) + ", ";
				if (!(e.getValue()+"").startsWith("@SQL") && !(e.getValue()+"").equals("transaccion_id") && !(e.getValue()+"").equals("tra_id")) {
					extraFields += e.getValue() + ", ";	
				}
				
			}
		}

		
		if (!databaseProperties.get("database.type").equals("dbf")) {
			//agregar mas campos 
			sql = "SELECT ";
					//Primero los Fields adicionales que se requerirán al guardar 
					sql += extraFields;
					sql += "transaccion_id, tipo_documento, clasific, cdc AS \"cdc\" \n";

					sql += "FROM " + tableName + " \n"
						+ "WHERE 1=1 \n"
						+ "AND tipo_documento = " + tipoDocumento + " \n"
						+ "AND \n"
							+ "CDC IS NOT NULL \n"
							+ "AND \n"
							+ "(ESTADO IS NOT NULL AND ESTADO = 0) \n"
						+ "GROUP BY "
						+ extraFields
						+ "transaccion_id, tipo_documento, clasific, cdc \n";	//Ordena de forma normal, para obtener el ultimo	
		} else {
			
			boolean obtenerCdcEstadoPausadoPorSubSelect = true;
			String transactionTableName = databaseProperties.get("database.dbf.transaccion_table");
			transactionTableName = transactionTableName.substring(0, transactionTableName.indexOf(".dbf"));

			String facturaSendTableName = databaseProperties.get("database.dbf.facturasend_table");
			facturaSendTableName = facturaSendTableName.substring(0, facturaSendTableName.indexOf(".dbf"));
			String facturaSendTableKey = databaseProperties.get("database.dbf.facturasend_table.key");
			String facturaSendTableValue = databaseProperties.get("database.dbf.facturasend_table.value");

			sql = "SELECT "
					+ extraFields
					+ "tra_id, \n";
			if (obtenerCdcEstadoPausadoPorSubSelect) {
//				sql += "cdc AS \"cdc\" \n";
				sql += "(SELECT \"" + facturaSendTableValue + "\" FROM \"" + facturaSendTableName + "\" mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='CDC' LIMIT 1) AS \"cdc\" \n";

			} else {
				sql += "cdc AS \"cdc\" \n";
			}
			
			sql += "FROM " + transactionTableName + " vp \n"
				+ "WHERE 1=1 \n"
				+ "AND tip_doc = " + tipoDocumento + " \n";
				
			if (obtenerCdcEstadoPausadoPorSubSelect) {
				sql += "AND ( \n"
					+ "(SELECT \"" + facturaSendTableValue + "\" FROM \"" + facturaSendTableName + "\" mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='CDC' LIMIT 1) IS NOT NULL \n"
					+ "AND \n"
					+ "COALESCE(CAST((SELECT \"" + facturaSendTableValue + "\" FROM \"" + facturaSendTableName + "\" mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='ESTADO' LIMIT 1) AS INTEGER), 999) = 0 \n"
				+ ") \n";
			} else {
				sql += "AND (cdc IS NOT NULL OR estado = 0) ";
			}
			sql += "GROUP BY "
				+ extraFields
				+ "tra_id, cdc";
		}
		
		
		return sql;
	}
	
	
	/**
	 * 
	 * 
	 * @param databaseProperties
	 * @param tipoDocumento
	 * @return
	 */
	private static String obtenerEstadosPorTransaccionesIdSQL(Map<String, String> databaseProperties, Integer tipoDocumento, String transaccionesId, String clasificador) {
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transaction_table_read");
		String sql = "";
		
		//Integer tipoDocumento = (Integer)CoreService.getValueForKey(datosUpdate, "tipo_documento", "tip_doc");
		//String clasificador = (String)CoreService.getValueForKey(datosUpdate, "clasific");
		String tipoDE = tipoDocumento == 1 ? "fe" : tipoDocumento == 2 ? "ni" : tipoDocumento == 3 ? "ne" : tipoDocumento == 4 ? "af" : tipoDocumento == 5 ? "nc" : tipoDocumento == 6 ? "nd" : tipoDocumento == 7 ? "nr" : tipoDocumento == 8 ? "fe" : "";

		String prefixForFields = "database." + databaseProperties.get("database.type") + ".facturasend_table." + tipoDE;
			
		if (clasificador != null) {
			prefixForFields += "." + clasificador;
		}
		prefixForFields += ".field"; 
		//---
		String extraFields = "";

		Iterator itr = databaseProperties.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			String key = e.getKey()+""; 
			if ((key).startsWith(prefixForFields)) {
				//sql += key.substring(("database." + databaseProperties.get("database.type") + ".facturasend_table.field.").length(), key.length()) + ", ";
				if (!(e.getValue()+"").startsWith("@SQL") && !(e.getValue()+"").equals("transaccion_id") && !(e.getValue()+"").equals("tra_id")) {
					extraFields += e.getValue() + ", ";	
				}
				
			}
		}

		
		if (!databaseProperties.get("database.type").equals("dbf")) {
			//agregar mas campos 
			sql = "SELECT ";
					//Primero los Fields adicionales que se requerirán al guardar 
					sql += extraFields;
					sql += "transaccion_id, tipo_documento, clasific, cdc AS \"cdc\" \n";

					sql += "FROM " + tableName + " \n"
						+ "WHERE 1=1 \n"
						+ "AND tipo_documento = " + tipoDocumento + " \n"
						+ "AND \n"
							+ "TRANSACCION_ID IN (" + transaccionesId + ") \n"
						+ "GROUP BY "
						+ extraFields
						+ "transaccion_id, tipo_documento, clasific, cdc \n";	//Ordena de forma normal, para obtener el ultimo	
		} else {
			
			boolean obtenerCdcEstadoPausadoPorSubSelect = true;
			String transactionTableName = databaseProperties.get("database.dbf.transaccion_table");
			transactionTableName = transactionTableName.substring(0, transactionTableName.indexOf(".dbf"));

			String facturaSendTableName = databaseProperties.get("database.dbf.facturasend_table");
			facturaSendTableName = facturaSendTableName.substring(0, facturaSendTableName.indexOf(".dbf"));
			String facturaSendTableKey = databaseProperties.get("database.dbf.facturasend_table.key");
			String facturaSendTableValue = databaseProperties.get("database.dbf.facturasend_table.value");

			sql = "SELECT "
					+ extraFields
					+ "tra_id, \n";
			if (obtenerCdcEstadoPausadoPorSubSelect) {
//				sql += "cdc AS \"cdc\" \n";
				sql += "(SELECT \"" + facturaSendTableValue + "\" FROM \"" + facturaSendTableName + "\" mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='CDC' LIMIT 1) AS \"cdc\" \n";

			} else {
				sql += "cdc AS \"cdc\" \n";
			}
			
			sql += "FROM " + transactionTableName + " vp \n"
				+ "WHERE 1=1 \n"
				+ "AND tip_doc = " + tipoDocumento + " \n";
				
			if (obtenerCdcEstadoPausadoPorSubSelect) {
				sql += "AND ( \n"
					+ "(SELECT \"" + facturaSendTableValue + "\" FROM \"" + facturaSendTableName + "\" mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='CDC' LIMIT 1) IS NOT NULL \n"
					+ "AND \n"
					+ "COALESCE(CAST((SELECT \"" + facturaSendTableValue + "\" FROM \"" + facturaSendTableName + "\" mid WHERE mid.tra_id = vp.tra_id AND mid.tip_doc = vp.tip_doc AND \"" + facturaSendTableKey + "\"='ESTADO' LIMIT 1) AS INTEGER), 999) = 0 \n"
				+ ") \n";
			} else {
				sql += "AND (cdc IS NOT NULL OR estado = 0) ";
			}
			sql += "GROUP BY "
				+ extraFields
				+ "tra_id, cdc";
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
			
			log.debug("\n" + sql + " ");
			ResultSet rs = statement.executeQuery(sql);
			
			List<Map<String, Object>> transaccionesParaEnvioLote = SQLUtil.convertResultSetToList(rs);
			
			log.info("transaccionesParaEnvioLote.size: " + transaccionesParaEnvioLote.size());
			
			result.put("success", true);
			result.put("result", transaccionesParaEnvioLote);
			
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
		String tableName = databaseProperties.get("database." + databaseProperties.get("database.type") + ".transaction_table_read");

		String sql = "";
		if (!databaseProperties.get("database.type").equals("dbf")) {
			sql = "SELECT * \n"
						+ "FROM " + tableName + " \n"
						+ "WHERE 1=1 \n"
						+ "AND transaccion_id IN " + transaccionIdString + " \n"
						+ "ORDER BY numero DESC \n";		
		} else {
			
			tableName = databaseProperties.get("database.dbf.transaccion_table");
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
		
		List<Map<String, Object>> formasPagoByTransacction = new ArrayList<Map<String,Object>>();
		Connection conn = SQLConnection.getInstance(BDConnect.fromMap(databaseProperties)).getConnection("integracion");
				
		Statement statement = conn.createStatement();
		
		String sql = formasPagosSQLByTransaccion(databaseProperties, tipoDocumento, transaccionIdString);
		log.debug("\n" + sql + " ");
		ResultSet rs = statement.executeQuery(sql);
		
		formasPagoByTransacction = SQLUtil.convertResultSetToList(rs);
		log.info("formasPagoByTransacction.size: " + formasPagoByTransacction.size());
		return formasPagoByTransacction;
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
			String dbfPaymentTableName = databaseProperties.get("database.dbf.payment_view");
			dbfPaymentTableName = dbfPaymentTableName.substring(0, dbfPaymentTableName.indexOf(".dbf"));
			
			sql = "SELECT * \n"
					+ "FROM " + dbfPaymentTableName + " \n"
					+ "WHERE 1=1 \n"
					+ "AND tip_doc = " + tipoDocumento + " \n"
					+ "AND tra_id IN " + transaccionIdString + " \n"
					+ "";
		}
		return sql;
	}
	
	public static Map<String, Object> eventoCancelacion(Integer tipoDocumento, String clasific, Integer transaccionId, String cdc, String motivo, Map<String, Object> viewRec, Map<String, String> databaseProperties) {
		Map<String, String> body  = new HashMap<String, String>();
		Map<String, Object> resultadoJson = new HashMap<String, Object>();
		try {
			body.put("cdc", cdc);
			body.put("motivo", motivo);			
			Map header = new HashMap();
			header.put("Authorization", "Bearer api_key_" + databaseProperties.get("facturasend.token"));
			String url = databaseProperties.get("facturasend.url");
			url += "/evento/cancelacion";
			
			try {
				resultadoJson = HttpUtil.invocarRest(url, "POST", gson.toJson(body), header);
				if (resultadoJson != null) {
					if (Boolean.valueOf(resultadoJson.get("success") + "") == true) {
						
						//System.out.println("----" + resultadoJson);

						Map<String, Object> resultadoJsonMap = (Map<String, Object>)resultadoJson.get("result");
						if  (resultadoJsonMap.get("ns2:rRetEnviEventoDe") != null) {
							Map<String, Object> rRetEnviEventoDe = (Map<String, Object>)resultadoJsonMap.get("ns2:rRetEnviEventoDe");
							
							if  (rRetEnviEventoDe.get("ns2:gResProcEVe") != null) {
								Map<String, Object> gResProcEVe = (Map<String, Object>)rRetEnviEventoDe.get("ns2:gResProcEVe");
								
								if  (gResProcEVe.get("ns2:dEstRes") != null) {
									String dEstRes = (String)gResProcEVe.get("ns2:dEstRes");
									
									if  (dEstRes.equalsIgnoreCase("Aprobado")) {
										
										
										//Actualiza la tabla destino de acuerdo a la configuracion
										Map<String, Object> datosUpdateCancelado = new HashMap<String, Object>();
										datosUpdateCancelado.put("ESTADO", 99);	//CANCELADO
										datosUpdateCancelado.put("TIPO_DOCUMENTO", tipoDocumento);
										datosUpdateCancelado.put("TRANSACCION_ID", transaccionId);
										datosUpdateCancelado.put("CLASIFIC", clasific);
										
										updateFacturaSendDataInTableTransacciones(datosUpdateCancelado, databaseProperties, false);
										//---
										
										//Borrar registros previamente cargados, para evitar duplicidad
										//deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties);

										
										//COMENTADO POR MIENTRAS PARA EL COMPIERER 
										//Habilitar pra probar con DBF

										//Borrar registros previamente cargados, para evitar duplicidad
										deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties, "('ESTADO')");

										Map<String, Object> datosGuardar1 = new HashMap<String, Object>();
										datosGuardar1.put("ESTADO", 99);
										saveDataToFacturaSendTable(viewRec, datosGuardar1, databaseProperties);
										
										
										
									} else if (dEstRes.equalsIgnoreCase("Rechazado")){
										Map<String, Object> gResProc = (Map<String, Object>)gResProcEVe.get("ns2:gResProc");
										String dCodRes = (String)gResProc.get("ns2:dCodRes");

										if  (dCodRes.equalsIgnoreCase("4003")) {
											// Si el evento fallo antes con el Error de XML mal formado, y ahora dice que ya tiene ese evento
											// es por que es un problema de la SET y hay que actualizar el estado.
											
											//Actualiza la tabla destino de acuerdo a la configuracion
											Map<String, Object> datosUpdateCancelado = new HashMap<String, Object>();
											datosUpdateCancelado.put("ESTADO", 99);	//CANCELADO
											datosUpdateCancelado.put("TIPO_DOCUMENTO", tipoDocumento);
											datosUpdateCancelado.put("TRANSACCION_ID", transaccionId);
											datosUpdateCancelado.put("CLASIFIC", clasific);
											
											updateFacturaSendDataInTableTransacciones(datosUpdateCancelado, databaseProperties, false);
											//---
											
											//Borrar registros previamente cargados, para evitar duplicidad
											//deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties);

											
											//COMENTADO POR MIENTRAS PARA EL COMPIERER 
											//Habilitar pra probar con DBF
									/*		Map<String, Object> viewRec = new HashMap<String, Object>();
											viewRec.put("TIPO_DOCUMENTO", tipoDocumento);
											viewRec.put("TRANSACCION_ID", transaccionId);
											viewRec.put("AD_CLIENT_ID", transaccionId);
											
											//Borrar registros previamente cargados, para evitar duplicidad
											deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties, "('ESTADO')");

											Map<String, Object> datosGuardar1 = new HashMap<String, Object>();
											datosGuardar1.put("ESTADO", 99);
											saveDataToFacturaSendTable(viewRec, datosGuardar1, databaseProperties);
											*/
											
											
										}
										log.error("Error al ejecutar evento de Cancelaciòn, ver resultado de la API 4");
									}	
									
								} else {
									log.error("Error al ejecutar evento de Cancelaciòn, ver resultado de la API 3");
								}
							} else {
								log.error("Error al ejecutar evento de Cancelaciòn, ver resultado de la API 2");
							}
							
						} else {
							log.error("Error al ejecutar evento de Cancelaciòn, ver resultado de la API 1");
						} 
					}

				}
			}catch(Exception e2) {
				e2.printStackTrace();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return resultadoJson;
	}
	
	public static Map<String, Object> eventoInutilizacion(Integer transaccionId, String clasific, String serie, Integer tipoDocumento, String establecimiento, String punto, String desde, String hasta, String motivo, Map<String, Object> viewRec, Map<String, String> databaseProperties) {
		Map<String, Object> resultadoJson = new HashMap<String, Object>();
		
		Map<String, Object> body = new HashMap<String, Object>();
		body.put("tipoDocumento", tipoDocumento);
		body.put("establecimiento", establecimiento);
		body.put("punto", punto);
		body.put("desde", desde);
		body.put("hasta", hasta);
		body.put("serie", serie);
		body.put("motivo", motivo);
		
		
		System.out.println("body" + body);
		try {		
			Map header = new HashMap();
			header.put("Authorization", "Bearer api_key_" + databaseProperties.get("facturasend.token"));
			String url = databaseProperties.get("facturasend.url");
			url += "/evento/inutilizacion";
			
			try {
				resultadoJson = HttpUtil.invocarRest(url, "POST", gson.toJson(body), header);
				if (resultadoJson != null) {
					if (Boolean.valueOf(resultadoJson.get("success") + "") == true) {
						
						System.out.println("----" + resultadoJson);
						Map<String, Object> resultadoJsonMap = (Map<String, Object>)resultadoJson.get("result");
						if  (resultadoJsonMap.get("ns2:rRetEnviEventoDe") != null) {
							Map<String, Object> rRetEnviEventoDe = (Map<String, Object>)resultadoJsonMap.get("ns2:rRetEnviEventoDe");
							
							if  (rRetEnviEventoDe.get("ns2:gResProcEVe") != null) {
								Map<String, Object> gResProcEVe = (Map<String, Object>)rRetEnviEventoDe.get("ns2:gResProcEVe");
								
								if  (gResProcEVe.get("ns2:dEstRes") != null) {
									String dEstRes = (String)gResProcEVe.get("ns2:dEstRes");
									
									if  (dEstRes.equalsIgnoreCase("Aprobado")) {
										
										
										//Actualiza la tabla destino de acuerdo a la configuracion
										Map<String, Object> datosUpdateCancelado = new HashMap<String, Object>();
										datosUpdateCancelado.put("ESTADO", 97);	//INULIZADO
										datosUpdateCancelado.put("TIPO_DOCUMENTO", tipoDocumento);
										datosUpdateCancelado.put("TRANSACCION_ID", transaccionId);
										datosUpdateCancelado.put("CLASIFIC", clasific);
										
										updateFacturaSendDataInTableTransacciones(datosUpdateCancelado, databaseProperties, false);
										//---
										
										//Borrar registros previamente cargados, para evitar duplicidad
										//deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties);

										
										//COMENTADO POR MIENTRAS PARA EL COMPIERER 
										//Habilitar pra probar con DBF
										

										//Borrar registros previamente cargados, para evitar duplicidad
										deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties, "('ESTADO')");

										Map<String, Object> datosGuardar1 = new HashMap<String, Object>();
										datosGuardar1.put("ESTADO", 99);
										saveDataToFacturaSendTable(viewRec, datosGuardar1, databaseProperties);
										
										
										
									} else if (dEstRes.equalsIgnoreCase("Rechazado")){
										Map<String, Object> gResProc = (Map<String, Object>)gResProcEVe.get("ns2:gResProc");
										String dCodRes = (String)gResProc.get("ns2:dCodRes");

										if  (dCodRes.equalsIgnoreCase("4003")) {
											// Si el evento fallo antes con el Error de XML mal formado, y ahora dice que ya tiene ese evento
											// es por que es un problema de la SET y hay que actualizar el estado.
											
											//Actualiza la tabla destino de acuerdo a la configuracion
											Map<String, Object> datosUpdateCancelado = new HashMap<String, Object>();
											datosUpdateCancelado.put("ESTADO", 99);	//CANCELADO
											datosUpdateCancelado.put("TIPO_DOCUMENTO", tipoDocumento);
											datosUpdateCancelado.put("TRANSACCION_ID", transaccionId);
											datosUpdateCancelado.put("CLASIFIC", clasific);
											
											updateFacturaSendDataInTableTransacciones(datosUpdateCancelado, databaseProperties, false);
											//---
											
											//Borrar registros previamente cargados, para evitar duplicidad
											//deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties);

											
											//COMENTADO POR MIENTRAS PARA EL COMPIERER 
											//Habilitar pra probar con DBF
									/*		Map<String, Object> viewRec = new HashMap<String, Object>();
											viewRec.put("TIPO_DOCUMENTO", tipoDocumento);
											viewRec.put("TRANSACCION_ID", transaccionId);
											viewRec.put("AD_CLIENT_ID", transaccionId);
											
											//Borrar registros previamente cargados, para evitar duplicidad
											deleteFacturaSendTableByTransaccionId(viewRec, databaseProperties, "('ESTADO')");

											Map<String, Object> datosGuardar1 = new HashMap<String, Object>();
											datosGuardar1.put("ESTADO", 99);
											saveDataToFacturaSendTable(viewRec, datosGuardar1, databaseProperties);
											*/
											
											
										}
										log.error("Error al ejecutar evento de Cancelaciòn, ver resultado de la API 4");
									}
								} else {
									System.out.println("Ocurrio alun error 3");
								}
							} else {
								System.out.println("Ocurrio alun error 2");
							}
							
						} else {
							System.out.println("Ocurrio alun error 1");
						} 
					}

				}
			}catch(Exception e2) {
				e2.printStackTrace();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return resultadoJson;
	}
	
	
	public static Map<String, Object> ObtenerJsonDelXml (String cdc,Map<String, String> databaseProperties){
		Map<String, Object> resultadoJson = new HashMap<String, Object>();
		try {
			Map header = new HashMap();
			header.put("Authorization", "Bearer api_key_" + databaseProperties.get("facturasend.token"));
			String url = databaseProperties.get("facturasend.url");
			url += "/de/xml/"+cdc+"?json=true";
			try {
				resultadoJson = HttpUtil.invocarRest(url, "GET", null, header);
				if (resultadoJson != null) {
					return resultadoJson;
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	
	

	public static void setTimeout(Runnable runnable, int delay){
	    new Thread(() -> {
	        try {
	            Thread.sleep(delay);
	            runnable.run();
	        }
	        catch (Exception e){
	            System.err.println(e);
	        }
	    }).start();
	}
}
