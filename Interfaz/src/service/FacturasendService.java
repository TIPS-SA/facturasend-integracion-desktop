package service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import core.CoreIntegracionService;
import core.CoreService;
import util.StringUtil;
import views.table.TableDesign;

public class FacturasendService {
	public static Log log = LogFactory.getLog(FacturasendService.class);

	/**
	 * Busca en el proyecto CORE los datos de la Vista
	 * en formato MAP, para mostrar en el JTable
	 * 
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> loadDocumentosElectronicos(String q, Integer tipo, Integer page, Integer size, boolean inverso, boolean incluirInutilizados) throws Exception {
		
		//Llamar a la consulta de Datos
		//ConfigProperties configProperties = new ConfigProperties();
		
		Map<String, Object> returnData = CoreService.getTransaccionesList(q, tipo, page, size, inverso, incluirInutilizados, readDBProperties());
		
		//log.info(returnData);
		if (Boolean.valueOf(returnData.get("success")+"") == true) {
			return returnData;
		} else {
			throw new Exception(returnData.get("error")+"");
		}
	}
	

	/**
	 * Busca en el proyecto CORE los datos de la Vista
	 * en formato MAP, para mostrar en el JTable
	 * 
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> loadTransaccionesItem(Integer transaccionId, Integer tipoDocumento, Integer page, Integer size) throws Exception {
		
		//Llamar a la consulta de Datos
		//ConfigProperties configProperties = new ConfigProperties();
		
		Map<String, Object> returnData = CoreService.getTransaccionesItem(transaccionId, tipoDocumento, page, size, readDBProperties());
		
		log.info(returnData);
		if (Boolean.valueOf(returnData.get("success")+"") == true) {
			return returnData;
		} else {
			throw new Exception(returnData.get("error")+"");
		}
	}

	/**
	 * Ejecuta el proceso de integración desde el Core, el 
	 * cual tiene su logica propia para mantener las transacciones
	 * sincronizadas.
	 * 
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	public static void pausarEnviar(Integer transaccionId, Integer tipoDocumento, String clasificador) throws Exception {
		
		//Llamar a la consulta de Datos
		
		CoreIntegracionService.pausarEnviar(transaccionId, tipoDocumento, clasificador, readDBProperties());
		
	}
	
	public static void actualizarEstado(Integer tipoDocumento) throws Exception {
		
		//Llamar a la consulta de Datos
		
		CoreIntegracionService.actualizarEstado(tipoDocumento, readDBProperties());
		
	}

	/**
	 * Ejecuta el proceso de integración desde el Core, el 
	 * cual tiene su logica propia para mantener las transacciones
	 * sincronizadas.
	 * 
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	public static void iniciarIntegracion() throws Exception {
		
		//Llamar a la consulta de Datos
		
		Map<String, Object> returnData1 = CoreIntegracionService.iniciarIntegracion(1, readDBProperties());
		Map<String, Object> returnData4 = CoreIntegracionService.iniciarIntegracion(4, readDBProperties());
		Map<String, Object> returnData5 = CoreIntegracionService.iniciarIntegracion(5, readDBProperties());
		Map<String, Object> returnData6 = CoreIntegracionService.iniciarIntegracion(6, readDBProperties());
		Map<String, Object> returnData7 = CoreIntegracionService.iniciarIntegracion(7, readDBProperties());
		
		/*log.info(returnData);
		if (Boolean.valueOf(returnData.get("success")+"") == true) {
			return returnData;
		} else {
			throw new Exception(returnData.get("error")+"");
		}*/
	}
	
	//TableDesign tb = new TableDesign();
	public Integer populateTransactionTable(JTable jTable, String q, Integer tipoDocumento, Integer page, Integer size, boolean refreshAll){
		Integer retorno = 0;
//		Object [] titulos = {"Mov #", "Fecha", "Cliente", "N° Factura", "Moneda", "Total", "Estado", "CDC", "Clasificador"};	//CDC
		Object registro[] = { null, null, null, null, null, null, null, null, null};
		    
		DefaultTableModel model = (DefaultTableModel) jTable.getModel();
		
		try {
			Map<String, Object> result = loadDocumentosElectronicos(q, tipoDocumento, page, size, true, false);
			List<Map<String, Object>> rs = (List<Map<String, Object>>)result.get("result");
			retorno =  (Integer)result.get("count");
			if (refreshAll) {
				while (model.getRowCount() > 0) {
					model.removeRow( model.getRowCount()-1 );
				}
			}
			for (int i = 0; i < rs.size(); i++) {
				System.out.println(rs.get(i));
				String moneda = (String)CoreService.getValueForKey(rs.get(i), "moneda");
				DecimalFormat df = new DecimalFormat("###,###,###,##0.##");	//Preparado para PYG
				if (!moneda.equals("PYG")) {
					df = new DecimalFormat("###,###,###,##0.00######");	
				}
				
				Double total = ((BigDecimal) CoreService.getValueForKey(rs.get(i), "total")).doubleValue();
				
				registro[0] = CoreService.getValueForKey(rs.get(i), "transaccion_id", "tra_id");
				registro[1] = CoreService.getValueForKey(rs.get(i), "fecha");
				String infoDoc = "";
				if (CoreService.getValueForKey(rs.get(i), "cliente_ruc", "c_ruc") != null) {
					infoDoc = (String)CoreService.getValueForKey(rs.get(i), "cliente_ruc", "c_ruc");
				}
				if (CoreService.getValueForKey(rs.get(i), "cliente_documento_numero", "c_doc_num") != null) {
					infoDoc = (String)CoreService.getValueForKey(rs.get(i), "cliente_documento_numero", "c_doc_num");
				}
				
				registro[2] = infoDoc + "-" + CoreService.getValueForKey(rs.get(i), "cliente_razon_social", "c_raz_soc");
				registro[3] = StringUtil.padLeftZeros(CoreService.getValueForKey(rs.get(i), "establecimiento", "estable")+"", 3)  + "-" + StringUtil.padLeftZeros(CoreService.getValueForKey(rs.get(i), "punto")+"", 3) + "-" + StringUtil.padLeftZeros(CoreService.getValueForKey(rs.get(i), "numero")+"", 7);
				registro[4] = moneda;
				registro[5] = df.format(total);
				
				String fieldPausado = (String)CoreService.getValueForKey(rs.get(i), "pausado");
				Object fieldEstado = CoreService.getValueForKey(rs.get(i), "estado");

				Integer valueEstadoInt = -99;	//Sin estado	
				
				if (fieldEstado != null) {
					valueEstadoInt = Integer.valueOf( (fieldEstado+"").trim() );
				}
				if (CoreService.getValueForKey(rs.get(i), "error") != null) {
					registro[6] = "Error";
				} else {
					registro[6] = CoreService.getEstadoDescripcion(valueEstadoInt);					
				}
				
				if (fieldPausado != null) {
					registro[6] += "(Pausado)";
				}
				
				registro[7] = CoreService.getValueForKey(rs.get(i), "cdc");
				registro[8] = CoreService.getValueForKey(rs.get(i), "clasific");
				
				if (refreshAll) {
					model.insertRow(0, registro);
				} else {
					Integer posicionEncontrada = registroExisteEnModel(model, registro);
					if ( posicionEncontrada == -1) {
						//Si no existe, inserta
						model.insertRow(0, registro);
					} else {
						//Si ya existe, actualiza los estados
						Integer newTransaccionId = Integer.valueOf(registro[0] + "");
						String newEstado = registro[6] + "";
						String newCdc = registro[7] + "";
						
						Integer transaccionIdModel = Integer.valueOf(model.getValueAt(posicionEncontrada, 0) + "");
						String estadoModel = model.getValueAt(posicionEncontrada, 6) + "";
						String cdcModel = model.getValueAt(posicionEncontrada, 7) + "";
						
						if ( ! newEstado.equals(estadoModel)) {
							model.setValueAt(newEstado, posicionEncontrada, 6);
						} 
						if ( ! newCdc.equals(cdcModel)) {
							model.setValueAt(newCdc, posicionEncontrada, 7);
						} 
					}
				}
				
			}
			
			while (model.getRowCount() > 20) {
				model.removeRow( model.getRowCount()-1 );
			}
			jTable.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return retorno;
	}
		
	
	public Integer registroExisteEnModel(DefaultTableModel model, Object [] datos){
		Integer encontrado = -1;
		Integer transaccionId = ((BigDecimal)datos[0]).intValue();
		
		//System.out.println("->" + model.getRowCount());
		for (int row2 = 0; row2 < model.getRowCount(); row2++) {

			Integer transaccionIdLocal = Integer.valueOf(model.getValueAt(row2, 0) + "");
            //System.out.println("corroborando tranasccion id si son iguales" + transaccionId + "-" + transaccionIdLocal);
			if (transaccionIdLocal.intValue() == transaccionId.intValue()) {
				encontrado = row2;
			}
		}
		return encontrado;
	}
	
	public Integer registroNoExisteEnModel(DefaultTableModel model, Object [] datos){
		Integer encontrado = -1;
		Integer transaccionId = ((BigDecimal)datos[0]).intValue();
		
		//System.out.println("->" + model.getRowCount());
		for (int row2 = 0; row2 < model.getRowCount(); row2++) {

			Integer transaccionIdLocal = Integer.valueOf(model.getValueAt(row2, 0) + "");
            //System.out.println("corroborando tranasccion id si son iguales" + transaccionId + "-" + transaccionIdLocal);
			if (transaccionIdLocal.intValue() == transaccionId.intValue()) {
				encontrado = row2;
			}
		}
		return encontrado;
	}

	
	/*public Integer populateTransactionTableBackup20221213(JTable table, String q, Integer tipoDocumento, Integer page, Integer size){
		Integer retorno = 0;
		Object [] titulos = {"Mov #", "Fecha","Cliente","N° Factura","Moneda", "Total", "Estado", "CDC", "Clasificador"};	//CDC
		Object datos[] = { null, null, null, null, null, null, null, null, null};
		    
		DefaultTableModel model = new DefaultTableModel(null, titulos) {
			 @Override
			    public boolean isCellEditable(int row, int column) {
			       //all cells false
			       return false;
			    }
		};
		try {
			Map<String, Object> result = loadDocumentosElectronicos(q, tipoDocumento, page, size);
			List<Map<String, Object>> rs = (List<Map<String, Object>>)result.get("result");
			retorno =  (Integer)result.get("count");
			//log.info("rs"  + rs);
			for (int i = 0; i < rs.size(); i++) {
				System.out.println(rs.get(i));
				String moneda = (String)CoreService.getValueForKey(rs.get(i), "moneda");
				DecimalFormat df = new DecimalFormat("###,###,###,##0.##");	//Preparado para PYG
				if (!moneda.equals("PYG")) {
					df = new DecimalFormat("###,###,###,##0.00######");	
				}
				
				Double total = ((BigDecimal) CoreService.getValueForKey(rs.get(i), "total")).doubleValue();
				
				datos[0] = CoreService.getValueForKey(rs.get(i), "transaccion_id", "tra_id");
				datos[1] = CoreService.getValueForKey(rs.get(i), "fecha");
				String infoDoc = "";
				if (CoreService.getValueForKey(rs.get(i), "cliente_ruc", "c_ruc") != null) {
					infoDoc = (String)CoreService.getValueForKey(rs.get(i), "cliente_ruc", "c_ruc");
				}
				if (CoreService.getValueForKey(rs.get(i), "cliente_documento_numero", "c_doc_num") != null) {
					infoDoc = (String)CoreService.getValueForKey(rs.get(i), "cliente_documento_numero", "c_doc_num");
				}
				
				datos[2] = infoDoc + "-" + CoreService.getValueForKey(rs.get(i), "cliente_razon_social", "c_raz_soc");
				datos[3] = StringUtil.padLeftZeros(CoreService.getValueForKey(rs.get(i), "establecimiento", "estable")+"", 3)  + "-" + StringUtil.padLeftZeros(CoreService.getValueForKey(rs.get(i), "punto")+"", 3) + "-" + StringUtil.padLeftZeros(CoreService.getValueForKey(rs.get(i), "numero")+"", 7);
				datos[4] = moneda;
				datos[5] = df.format(total);
				
				String fieldPausado = (String)CoreService.getValueForKey(rs.get(i), "pausado");
				Object fieldEstado = CoreService.getValueForKey(rs.get(i), "estado");

				Integer valueEstadoInt = -99;	//Sin estado	
				
				if (fieldEstado != null) {
					valueEstadoInt = Integer.valueOf( (fieldEstado+"").trim() );
				}
				if (CoreService.getValueForKey(rs.get(i), "error") != null) {
					datos[6] = "Error";
				} else {
					datos[6] = CoreService.getEstadoDescripcion(valueEstadoInt);					
				}
				
				if (fieldPausado != null) {
					datos[6] += "(Pausado)";
				}
				
				datos[7] = CoreService.getValueForKey(rs.get(i), "cdc");
				datos[8] = CoreService.getValueForKey(rs.get(i), "clasific");

				model.addRow(datos);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		table.setModel(model);
		tb.setPrincipalTableCellsStyle(table);
		//tb.addCheckBox(0, table);
		table.getColumnModel().getColumn(0).setPreferredWidth(10);
		table.getColumnModel().getColumn(4).setPreferredWidth(20);
		table.getColumnModel().getColumn(8).setMaxWidth(0);
		table.getColumnModel().getColumn(8).setMinWidth(0);
		table.getTableHeader().getColumnModel().getColumn(8).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(8).setMinWidth(0);
//		table.getColumnModel().getColumn(8).setWidth(0);
//		table.getTableHeader().getColumnModel().getColumn(8).setWidth(0);
		
		return retorno;
	}*/
	
	public List<Map<String, Object>> populateTransactionDetailsTable(JTable table, BigDecimal nroMov, Integer tipoDocumento) {
		Object [] titulos = {"Codigo" , "Descripcion", "Cantidad", "Precio Unitario", "Descuento", "SubTotal"};
		Object datos[] = {null, null, null, null,null, null};
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		
		DefaultTableModel model = new DefaultTableModel(null, titulos) {
			 @Override
			    public boolean isCellEditable(int row, int column) {
			       //all cells false
			       return false;
			    }
		};
		
		try {
			Map<String, Object> result = loadTransaccionesItem(nroMov.intValue(), tipoDocumento, 0, 999999);

			rs = (List<Map<String, Object>>)result.get("result");

			for (int i = 0; i < rs.size(); i++) {
				String moneda = (String)CoreService.getValueForKey(rs.get(i), "moneda");
				DecimalFormat dfCantidad = new DecimalFormat("###,###,##0.00######");	//Cantidad

				DecimalFormat df = new DecimalFormat("###,###,###,##0.##");	//Preparado para PYG
				if (!moneda.equals("PYG")) {
					df = new DecimalFormat("###,###,###,##0.00######");	
				}
				
				Double cantidad = ((BigDecimal) CoreService.getValueForKey(rs.get(i), "item_cantidad", "i_cantidad")).doubleValue();
				Double precioUnitario = ((BigDecimal) CoreService.getValueForKey(rs.get(i), "item_precio_unitario", "i_pre_uni")).doubleValue();
				Double descuento = ((BigDecimal) CoreService.getValueForKey(rs.get(i), "item_descuento", "i_descue")).doubleValue();
				
				datos[0] = CoreService.getValueForKey(rs.get(i), "item_codigo","i_codigo");
				datos[1] = CoreService.getValueForKey(rs.get(i), "item_descripcion", "i_descrip");
				datos[2] = dfCantidad.format(cantidad);
				datos[3] = df.format(precioUnitario);
				datos[4] = df.format(descuento);
				
		        double subtotalNumber = cantidad.doubleValue() * precioUnitario.doubleValue() - descuento.doubleValue();
		        
				datos[5] = df.format(subtotalNumber);
				
				model.addRow(datos);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		table.setModel(model);
		
		TableDesign tb = new TableDesign();

		tb.setItemsTableCellsStyle(table);
		table.getColumnModel().getColumn(0).setPreferredWidth(35);
		table.getColumnModel().getColumn(1).setPreferredWidth(285);
		table.getColumnModel().getColumn(2).setPreferredWidth(10);
		table.getColumnModel().getColumn(3).setPreferredWidth(25);
		table.getColumnModel().getColumn(4).setPreferredWidth(10);
		table.getColumnModel().getColumn(5).setPreferredWidth(10);
		return rs;
	}
	
	public static Map<String, Object> ejecutarEventoCancelacion (Integer tipoDocumento, String clasific, Integer transaccionId, String cdc, String motivo) {
		return CoreIntegracionService.eventoCancelacion(tipoDocumento, clasific, transaccionId, cdc, motivo, readDBProperties());
	}
	
	public static Map<String, Object> ejecutarEventoInutilizacion (Map<String, Object> body) {
		return CoreIntegracionService.eventoInutilizacion(body, readDBProperties());
	}
	
	public static Map<String, Object> ejecutarObtenerJsonDelXml (String cdc){
		return CoreIntegracionService.ObtenerJsonDelXml(cdc, readDBProperties());
	}
	
	static ConfigProperties configProperties;
	public static Map readDBProperties() {
		if (configProperties == null) {
			configProperties = new ConfigProperties();	
		}
		
		return configProperties.readDbProperties();
	}

}




