package service;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
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
		
		Map<String, Object> returnData = Core.listDes(q, tipo, page, size, readDBProperties());
		
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
	public static Map<String, Object> iniciarIntegracion(Integer tipoDocumento) throws Exception {
		
		//Llamar a la consulta de Datos
		
		Map<String, Object> returnData = Core.iniciarIntegracion(tipoDocumento, readDBProperties());
		
		System.out.println(returnData);
		if (Boolean.valueOf(returnData.get("success")+"") == true) {
			return returnData;
		} else {
			throw new Exception(returnData.get("error")+"");
		}
	}
	
	TableDesign tb = new TableDesign();
	public Integer populateTransactionTable(JTable table, String q, Integer tipoDocumento, Integer page, Integer size){
		Integer retorno = 0;
		Object [] titulos = {null,"Mov #", "Fecha","Cliente","N° Factura","Moneda", "Total", "Estado"};
		Object datos[] = {null, null, null, null,null, null, null, null};
		    
		DefaultTableModel model = new DefaultTableModel(null, titulos) {
			 @Override
			    public boolean isCellEditable(int row, int column) {
			       //all cells false
			       return column==0;
			    }
		};
		try {
			Map<String, Object> result = loadDocumentosElectronicos(q, tipoDocumento, page, size);
			List<Map<String, Object>> rs = (List<Map<String, Object>>)result.get("result");
			retorno =  (Integer)result.get("count");
			//System.out.println("rs"  + rs);
			for (int i = 0; i < rs.size(); i++) {
				
				datos[1] = rs.get(i).get(Core.getFieldName("transaccion_id", readDBProperties()));
				datos[2] = rs.get(i).get(Core.getFieldName("fecha", readDBProperties()));
				datos[3] = rs.get(i).get(Core.getFieldName("cliente_razon_social", readDBProperties()));
				datos[4] = StringUtil.padLeftZeros(rs.get(i).get(Core.getFieldName("establecimiento", readDBProperties()))+"", 3)  + "-" + StringUtil.padLeftZeros(rs.get(i).get(Core.getFieldName("punto", readDBProperties()))+"", 3) + "-" + StringUtil.padLeftZeros(rs.get(i).get(Core.getFieldName("numero", readDBProperties())) + "", 7);
				datos[5] = rs.get(i).get(Core.getFieldName("moneda", readDBProperties()));
				datos[6] = rs.get(i).get(Core.getFieldName("total", readDBProperties())) != null ? rs.get(i).get(Core.getFieldName("total", readDBProperties())) : 0;
				datos[7] = tb.getEstadoValue(0);
				
				model.addRow(datos);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		table.setModel(model);
		tb.setCellAlignPositiion(table);
		tb.addCheckBox(0, table);
		table.getColumnModel().getColumn(1).setPreferredWidth(10);
		table.getColumnModel().getColumn(5).setPreferredWidth(20);
		
		return retorno;
	}
		
	
	
	
	static ConfigProperties configProperties;
	public static Map readDBProperties() {
		if (configProperties == null) {
			configProperties = new ConfigProperties();	
		}
		
		return configProperties.readDbProperties();
	}
}




