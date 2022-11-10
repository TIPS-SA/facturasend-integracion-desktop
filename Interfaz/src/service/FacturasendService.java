package service;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import bean.Cliente;
import bean.DocumentoElectronico;
import connect.BDConnect;
import core.Core;
import views.table.TableDesign;

public class FacturasendService {

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
		
		Map<String, Object> returnData = Core.getTransaccionesList(q, tipo, page, size, readDBProperties());
		
		System.out.println(returnData);
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
	public static Map<String, Object> loadTransaccionesItem(Integer transaccionId, Integer page, Integer size) throws Exception {
		
		//Llamar a la consulta de Datos
		//ConfigProperties configProperties = new ConfigProperties();
		
		Map<String, Object> returnData = Core.getTransaccionesItem(transaccionId, page, size, readDBProperties());
		
		System.out.println(returnData);
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
	public static void pausarIniciar(Integer transaccionId) throws Exception {
		
		//Llamar a la consulta de Datos
		
		Core.pausarIniciar(transaccionId, readDBProperties());
		
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
		
		Map<String, Object> returnData1 = Core.iniciarIntegracion(1, readDBProperties());
		Map<String, Object> returnData4 = Core.iniciarIntegracion(4, readDBProperties());
		Map<String, Object> returnData5 = Core.iniciarIntegracion(5, readDBProperties());
		Map<String, Object> returnData6 = Core.iniciarIntegracion(6, readDBProperties());
		Map<String, Object> returnData7 = Core.iniciarIntegracion(7, readDBProperties());
		
		/*System.out.println(returnData);
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
			//System.out.println("rs"  + rs);
			for (int i = 0; i < rs.size(); i++) {
				
				datos[0] = Core.getValueForKey(rs.get(i), "transaccion_id", "tra_id");
				datos[1] = Core.getValueForKey(rs.get(i), "fecha");
				datos[2] = Core.getValueForKey(rs.get(i), "cliente_razon_social", "c_raz_soc");
				datos[3] = StringUtil.padLeftZeros(Core.getValueForKey(rs.get(i), "establecimiento", "estable")+"", 3)  + "-" + StringUtil.padLeftZeros(Core.getValueForKey(rs.get(i), "punto")+"", 3) + "-" + StringUtil.padLeftZeros(Core.getValueForKey(rs.get(i), "numero")+"", 7);
				datos[4] = Core.getValueForKey(rs.get(i), "moneda");
				datos[5] = Core.getValueForKey(rs.get(i), "total") != null ? Core.getValueForKey(rs.get(i), "total") : 0;
				
				//String fieldEstado = (String) Core.getValueForKey(rs.get(i), "estado"); 
				//Integer fieldEstado = (Integer) Core.getValueForKey(rs.get(i), "estado");
				String fieldPausado = (String)Core.getValueForKey(rs.get(i), "pausado");
				Object fieldEstado = Core.getValueForKey(rs.get(i), "estado");
				
				//String valueEstadoStr = (String) rs.get(i).get( fieldEstado );
				Integer valueEstadoInt = -99;	//Sin estado	
				
				if (fieldEstado != null) {
					valueEstadoInt = Integer.valueOf( fieldEstado+"" );
				}
				if (Core.getValueForKey(rs.get(i), "error") != null) {
					datos[6] = "Error";
				} else {
					datos[6] = Core.getEstadoDescripcion(valueEstadoInt);					
				}
				
				if (fieldPausado != null) {
					datos[6] += "(Pausado)";
				}
				
				datos[7] = Core.getValueForKey(rs.get(i), "cdc");

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
		
	public List<Map<String, Object>> populateTransactionDetailsTable(JTable table, BigDecimal nroMov) {
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
			Map<String, Object> result = loadTransaccionesItem(nroMov.intValue(), 0, 999999);
			System.out.println("items" + result);
			rs = (List<Map<String, Object>>)result.get("result");
			//retorno =  (Integer)result.get("count");
			System.out.println("rs"  + rs);
			for (int i = 0; i < rs.size(); i++) {
				
				datos[0] = Core.getValueForKey(rs.get(i), "transaccion_id", "tra_id");
				datos[1] = Core.getValueForKey(rs.get(i), "item_descripcion", "i_descrip");
				datos[2] = Core.getValueForKey(rs.get(i), "item_cantidad", "i_cantidad");
				datos[3] = Core.getValueForKey(rs.get(i), "item_precio_unitario", "i_pre_uni");
				datos[4] = Core.getValueForKey(rs.get(i), "item_descuento", "i_descue");
				
				DecimalFormat df = new DecimalFormat("###########.00");
		        double subtotalNumber = Double.valueOf(datos[2].toString()) * Double.valueOf(datos[3].toString()) - Double.valueOf(datos[4].toString());
		        String subtotal = df.format(subtotalNumber);
		        subtotal = subtotal.replace(",", ".");
				datos[5] = Double.valueOf(subtotal).doubleValue();
				
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




