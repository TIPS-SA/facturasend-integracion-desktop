package views.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.text.NumberFormat;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;


public class TableDesign {

	public void addCheckBox(int columna, JTable table) {
		TableColumn tc = table.getColumnModel().getColumn(columna);
//		tc.setHeaderRenderer(new HeaderRenderer2(table.getTableHeader()));//Esta es la linea que trata de poner el chk pero da error
		tc.setCellEditor(table.getDefaultEditor(Boolean.class));
		tc.setCellRenderer(table.getDefaultRenderer(Boolean.class));
		tc.setPreferredWidth(50);
	}
	
	public String getEstadoDescripcion(int valor) {
		String returnValue;
		switch (valor) {
		case 0:
			returnValue= "Generado";
			break;
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
			returnValue= "No integrado";
			break;
		}
		return returnValue;
	}
	
	public void setPrincipalTableCellsStyle(JTable table) {
		final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalTextPosition(0);
		table.setDefaultRenderer(getClass(), defaultTableCellRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(5).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(6).setCellRenderer(defaultTableCellRenderer);
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
	Map  attributes = font.getAttributes();

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
		Component componente = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
		if(column == 7) {
			//String estadoStr = this.getText().toString();
			switch (this.getText()) {
			case "Generado":
				componente.setBackground(Color.white);
				componente.setForeground(Color.gray);
				break;
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
			case "No integrado":
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


}
