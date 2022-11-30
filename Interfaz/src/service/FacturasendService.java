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
	public static Map<String, Object> loadDocumentosElectronicos(String q, Integer tipo, Integer page, Integer size) throws Exception {
		
		//Llamar a la consulta de Datos
		//ConfigProperties configProperties = new ConfigProperties();
		
		Map<String, Object> returnData = CoreService.getTransaccionesList(q, tipo, page, size, readDBProperties());
		
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
	public static void pausarEnviar(Integer transaccionId, Integer tipoDocumento) throws Exception {
		
		//Llamar a la consulta de Datos
		
		CoreIntegracionService.pausarEnviar(transaccionId, tipoDocumento, readDBProperties());
		
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
	
	TableDesign tb = new TableDesign();
	public Integer populateTransactionTable(JTable table, String q, Integer tipoDocumento, Integer page, Integer size){
		Integer retorno = 0;
		Object [] titulos = {"Mov #", "Fecha","Cliente","N° Factura","Moneda", "Total", "Estado", "CDC"};	//CDC
		Object datos[] = { null, null, null, null, null, null, null, null};
		    
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
				
				String moneda = (String)CoreService.getValueForKey(rs.get(i), "moneda");
				DecimalFormat df = new DecimalFormat("###,###,###,##0.##");	//Preparado para PYG
				if (!moneda.equals("PYG")) {
					df = new DecimalFormat("###,###,###,##0.00######");	
				}
				
				Double total = ((BigDecimal) CoreService.getValueForKey(rs.get(i), "total")).doubleValue();
				
				datos[0] = CoreService.getValueForKey(rs.get(i), "transaccion_id", "tra_id");
				datos[1] = CoreService.getValueForKey(rs.get(i), "fecha");
				datos[2] = CoreService.getValueForKey(rs.get(i), "cliente_razon_social", "c_raz_soc");
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
		
		return retorno;
	}
		
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
		tb.setItemsTableCellsStyle(table);
		table.getColumnModel().getColumn(0).setPreferredWidth(35);
		table.getColumnModel().getColumn(1).setPreferredWidth(285);
		table.getColumnModel().getColumn(2).setPreferredWidth(10);
		table.getColumnModel().getColumn(3).setPreferredWidth(25);
		table.getColumnModel().getColumn(4).setPreferredWidth(10);
		table.getColumnModel().getColumn(5).setPreferredWidth(10);
		return rs;
	}
	
	
	
	static ConfigProperties configProperties;
	public static Map readDBProperties() {
		if (configProperties == null) {
			configProperties = new ConfigProperties();	
		}
		
		return configProperties.readDbProperties();
	}

}




