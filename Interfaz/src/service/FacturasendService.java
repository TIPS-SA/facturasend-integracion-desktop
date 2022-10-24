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

public class FacturasendService {

	public static List<DocumentoElectronico>  loadDocumentosElectronicos(Integer tipo) {
		
		List<DocumentoElectronico> returnData = new ArrayList<DocumentoElectronico>();
		/*
		DocumentoElectronico de1 = new DocumentoElectronico();
		DocumentoElectronico de2 = new DocumentoElectronico();
		DocumentoElectronico de3 = new DocumentoElectronico();
		DocumentoElectronico de4 = new DocumentoElectronico();
		DocumentoElectronico de5 = new DocumentoElectronico();
		DocumentoElectronico de6 = new DocumentoElectronico();
		
		//clientes
		Cliente cli1 = new Cliente();
		cli1.setId(1);
		cli1.setNombre("Lucas");
		
		Cliente cli2 = new Cliente();
		cli2.setId(2);
		cli2.setNombre("Pedro");
		
		Cliente cli3 = new Cliente();
		cli3.setId(3);
		cli3.setNombre("Marcos");
		

		Cliente cli4 = new Cliente();
		cli4.setId(4);
		cli4.setNombre("Sonia");
		
		//de1.setXXX
		de1.setNroven(1);
		de1.setFecha(null);
		de1.setCliente(cli1);
		de1.setNumeroFactura("001-001-000001");
		de1.setMoneda("GS");
		de1.setTotal(19000.00);
		de1.setEstado(-1);//Borrador
		
		//de2

		de2.setNroven(2);
		de2.setFecha(null);
		de2.setCliente(cli2);
		de2.setNumeroFactura("001-001-000002");
		de2.setMoneda("GS");
		de2.setTotal(19000.00);
		de2.setEstado(2);//Aprobao
		
		//de3
		de3.setNroven(3);
		de3.setFecha(null);
		de3.setCliente(cli3);
		de3.setNumeroFactura("001-001-000003");
		de3.setMoneda("GS");
		de3.setTotal(19000.00);
		de3.setEstado(3);//Aprobado
		
		//de4
		de4.setNroven(4);
		de4.setFecha(null);
		de4.setCliente(cli4);
		de4.setNumeroFactura("001-001-000004");
		de4.setMoneda("GS");
		de4.setTotal(19000.00);
		de4.setEstado(4);//Rechazado
		
		//de5
		de5.setNroven(5);
		de5.setFecha(null);
		de5.setCliente(cli1);
		de5.setNumeroFactura("001-001-000005");
		de5.setMoneda("GS");
		de5.setTotal(19000.00);
		de5.setEstado(98);//Inexistente
		
		//de6
		de6.setNroven(6);
		de6.setFecha(null);
		de6.setCliente(cli2);
		de6.setNumeroFactura("001-001-000006");
		de6.setMoneda("GS");
		de6.setTotal(19000.00);
		de6.setEstado(99);//Cancelado
		
		returnData.add(de1);
		returnData.add(de2);
		returnData.add(de3);
		returnData.add(de4);
		returnData.add(de5);
		returnData.add(de6);
		*/
		int estado = 0;
		for (int i = 0; i < 6; i++) {
			Cliente cli1 = new Cliente();
			cli1.setId(i);
			cli1.setNombre("Lucas"+i);
			DocumentoElectronico de1 = new DocumentoElectronico();
			de1.setNroven(i);
			de1.setFecha(null);
			de1.setCliente(cli1);
			de1.setNumeroFactura("001-001-0000"+i);
			de1.setMoneda("GS");
			de1.setTotal(19000.00 + i*1000);
			switch (i) {
			case 0:
				estado = -2;
				break;
			case 1:
				estado = 2;			
				break;
			case 2:
				estado = 3;
				break;
			case 3:
				estado = 4;
				break;
			case 4:
				estado = 98;
				break;
			case 5:
				estado = 99;
				break;

			default:
				break;
			}
			de1.setEstado(estado);
			returnData.add(de1);
		}
		
		for (int i = 6; i < 50; i++) {
			Cliente cli1 = new Cliente();
			cli1.setId(i);
			cli1.setNombre("Lucas"+i);
			DocumentoElectronico de1 = new DocumentoElectronico();
			de1.setNroven(i);
			de1.setFecha(null);
			de1.setCliente(cli1);
			de1.setNumeroFactura("001-001-0000"+i);
			de1.setMoneda("GS");
			de1.setTotal(19000.00 + i*1000);
			de1.setEstado(98);
			returnData.add(de1);
			
		}
		return returnData;
	}
	
	public void cargar_tabla(JTable table){
		Object [] titulos = {null,"Mov #", "Fecha","Cliente","N° Factura","Moneda", "Total", "Estado"};
		Object datos[] = {null, null, null, null,null, null, null, null};
		/*Object datos[][] = {
				{null, "1", "2022-07-09", "Lucas", "001-001-000001", "Gs", "19.000", "Sincronizado"},
				{null, "1", "2022-07-09", "Pedro", "001-001-000001", "Gs", "19.000", "Sincronizado"},
				{null, "1", "2022-07-09", "Marcos",  "001-001-000001", "Gs", "19.000", "Sincronizado"},
		};*/
		    
		DefaultTableModel model = new DefaultTableModel(null, titulos);
		try {
			List<DocumentoElectronico> rs = loadDocumentosElectronicos(0);
			for (int i = 0; i < rs.size(); i++) {
				datos[1] = rs.get(i).getNroven();
				datos[2] = rs.get(i).getFecha();
				datos[3] = rs.get(i).getCliente().getNombre();
				datos[4] = rs.get(i).getNumeroFactura();
				datos[5] = rs.get(i).getMoneda();
				datos[6] = rs.get(i).getTotal();
				datos[7] = getEstadoValue(rs.get(i).getEstado());
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
	}
		
		private void addCheckBox(int columna, JTable table) {
			TableColumn tc = table.getColumnModel().getColumn(columna);
//			tc.setHeaderRenderer(new HeaderRenderer2(table.getTableHeader()));//Esta es la linea que trata de poner el chk pero da error
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
	Map  attributes = null;

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
			case "Rechazado":
				componente.setBackground(Color.red);
				componente.setForeground(Color.white);
				break;
			case "Inexistente":
				attributes = font.getAttributes();
				componente.setBackground(Color.white);
				componente.setForeground(Color.black);
				//preguntar al profe
				attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON); 
				componente.setFont( new Font(attributes));
				break;
			case "Aprobado c/ Error":
				attributes = font.getAttributes();
				componente.setBackground(Color.green);
				componente.setForeground(Color.black);
				//resaltar o algo
				attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON); 
				componente.setFont( new Font(attributes));
				break;
			case "Cancelado":
				attributes = font.getAttributes();
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



