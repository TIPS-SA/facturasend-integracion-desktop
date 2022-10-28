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
	
	public Integer populateTransactionTable(JTable table, String q, Integer tipoDocumento, Integer page, Integer size){
		Integer retorno = 0;
		Object [] titulos = {null,"Mov #", "Fecha","Cliente","N° Factura","Moneda", "Total", "Estado"};
		Object datos[] = {null, null, null, null,null, null, null, null};
		    
		DefaultTableModel model = new DefaultTableModel(null, titulos);
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
				datos[7] = getEstadoValue(0);
				
				model.addRow(datos);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		table.setModel(model);
		setCellAlignPositiion(table);
		addCheckBox(0, table);
		table.getColumnModel().getColumn(1).setPreferredWidth(10);
		table.getColumnModel().getColumn(5).setPreferredWidth(20);
		
		return retorno;
	}
		
	
	private void addCheckBox(int columna, JTable table) {
		TableColumn tc = table.getColumnModel().getColumn(columna);
//		tc.setHeaderRenderer(new HeaderRenderer2(table.getTableHeader()));//Esta es la linea que trata de poner el chk pero da error
		tc.setCellEditor(table.getDefaultEditor(Boolean.class));
		tc.setCellRenderer(table.getDefaultRenderer(Boolean.class));
		tc.setPreferredWidth(50);
	}
	
	private String getEstadoValue(int valor) {
		String returnValue;
		switch (valor) {
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
			returnValue= "Desconocido";
			break;
		}
		return returnValue;
	}
	
	private void setCellAlignPositiion(JTable table) {
		final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalTextPosition(0);
		table.setDefaultRenderer(getClass(), defaultTableCellRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(5).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(6).setCellRenderer(new CurrencyCellRenderer());
		table.getColumnModel().getColumn(7).setCellRenderer(new CeldaPersonalizada());
	}
	
	static ConfigProperties configProperties;
	public static Map readDBProperties() {
		if (configProperties == null) {
			configProperties = new ConfigProperties();	
		}
		
		return configProperties.readDbProperties();
	}
}

class CurrencyCellRenderer extends DefaultTableCellRenderer {
	 
    private static final NumberFormat FORMAT = NumberFormat.getCurrencyInstance();

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        final Component result = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        if (value instanceof Number) {
            //setHorizontalAlignment(JLabel.RIGHT);
            setText(FORMAT.format(value));
        } else {
            setText("");
        }
        return result;
    }
}

class CeldaPersonalizada extends DefaultTableCellRenderer {
	 
	Font font = new Font("helvetica", Font.PLAIN, 12);
	Map  attributes = font.getAttributes();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
		Component componente = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
		if(column == 7) {
			//String estadoStr = this.getText().toString();
			switch (this.getText()) {
			case "Borrador":
				componente.setBackground(Color.white);
				componente.setForeground(Color.gray);
				break;
			case  "Aprobado":
				componente.setBackground(Color.green);
				componente.setForeground(Color.black);
				break;
			case "Aprobado c/ Error":
				componente.setBackground(Color.green);
				componente.setForeground(Color.black);
				//resaltar o algo
				attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON); 
				componente.setFont( new Font(attributes));
				break;
			case "Rechazado":
				componente.setBackground(Color.red);
				componente.setForeground(Color.white);
				break;
			case "Inexistente":
				componente.setBackground(Color.white);
				componente.setForeground(Color.black);
				//preguntar al profe
				attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON); 
				componente.setFont( new Font(attributes));
				break;
			case "Cancelado":
				componente.setBackground(Color.red);
				componente.setForeground(Color.white);
				//tachar
				attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON); 
				componente.setFont( new Font(attributes));
				break;
			case "Desconocido":
				componente.setBackground(Color.white);
				componente.setForeground(Color.black);
				break;
			}
		}
		return componente;
		
	}
}

class HeaderRenderer2 implements TableCellRenderer {

private final JCheckBox check = new JCheckBox();

public HeaderRenderer2(JTableHeader header) {
    check.setOpaque(false);
    check.setFont(header.getFont());
    header.addMouseListener(new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            JTable table = ((JTableHeader) e.getSource()).getTable();
            TableColumnModel columnModel = table.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int modelColumn = table.convertColumnIndexToModel(viewColumn);
            if (modelColumn == 0) {
                check.setSelected(!check.isSelected());
                TableModel m = table.getModel();
                Boolean f = check.isSelected();
                for (int i = 0; i < m.getRowCount(); i++) {
                    m.setValueAt(f, i, 0);
                }
                ((JTableHeader) e.getSource()).repaint();
            }
        }
    });
}

@Override
public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
		int row, int column) {
	// TODO Auto-generated method stub
	return null;
}
/*
@Override
public Component getTableCellRendererComponent(
        JTable tbl, Object val, boolean isS, boolean hasF, int row, int col) {
    TableCellRenderer r = tbl.getTableHeader().getDefaultRenderer();
    //JLabel l = (JLabel) r.getTableCellRendererComponent(tbl, val, isS, hasF, row, col);
    //l.setIcon(new CheckBoxIcon(check));
    return l;
}
private static class CheckBoxIcon implements Icon {

    private final JCheckBox check;

    public CheckBoxIcon(JCheckBox check) {
        this.check = check;
    }

    @Override
    public int getIconWidth() {
        return check.getPreferredSize().width;
    }

    @Override
    public int getIconHeight() {
        return check.getPreferredSize().height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        SwingUtilities.paintComponent(
                g, check, (Container) c, x, y, getIconWidth(), getIconHeight());
    }
}*/


}



