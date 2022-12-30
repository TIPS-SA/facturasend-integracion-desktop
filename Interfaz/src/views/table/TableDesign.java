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
import javax.swing.SwingConstants;
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
	
	public void setPrincipalTableCellsStyle(JTable table) {
		final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalTextPosition(0);
		table.setDefaultRenderer(getClass(), defaultTableCellRenderer);
		table.getColumnModel().getColumn(0).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(4).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(5).setCellRenderer(new CurrencyCellRenderer());
		table.getColumnModel().getColumn(6).setCellRenderer(new CeldaPersonalizada());
	}
	
	public void setItemsTableCellsStyle(JTable table) {
		final DefaultTableCellRenderer defaultTableCellRenderer = new DefaultTableCellRenderer();
		defaultTableCellRenderer.setHorizontalTextPosition(0);
		table.setDefaultRenderer(getClass(), defaultTableCellRenderer);
		table.getColumnModel().getColumn(0).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(defaultTableCellRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(new CurrencyCellRenderer());
		table.getColumnModel().getColumn(4).setCellRenderer(new CurrencyCellRenderer());
		table.getColumnModel().getColumn(5).setCellRenderer(new CurrencyCellRenderer());
	}
}
class CurrencyCellRenderer extends DefaultTableCellRenderer {
	 
    private static final NumberFormat FORMAT = NumberFormat.getCurrencyInstance();

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        final Component result = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        setHorizontalAlignment(SwingConstants.RIGHT);
        return result;
    }
}

class CeldaPersonalizada extends DefaultTableCellRenderer {
	 
	Font font = new Font("helvetica", Font.PLAIN, 12);
	//Map  attributes = font.getAttributes();

	/*
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
		Component componente = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
		if(column == 6) {
			//String estadoStr = this.getText().toString();
			switch (this.getText()) {
			case "Error":
				componente.setBackground(Color.red);
				componente.setForeground(Color.white);
				break;
			case "Error(Pausado)":
				componente.setBackground(Color.red);
				componente.setForeground(Color.white);
				break;
			case "Generado":
				componente.setBackground(Color.gray);
				componente.setForeground(Color.white);
				break;
			case "Borrador":
				componente.setBackground(Color.white);
				componente.setForeground(Color.gray);
				break;
			case  "Aprobado":
				componente.setBackground(Color.green);
				componente.setForeground(Color.black);
				break;
			case "Aprobado c/Obs":
				componente.setBackground(Color.green);
				componente.setForeground(Color.black);
				//resaltar o algo
				Map attributes = font.getAttributes();
				attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON); 
				componente.setFont( new Font(attributes));
				break;
			case "Rechazado":
				componente.setBackground(Color.DARK_GRAY);
				componente.setForeground(Color.white);
				Map attributesRechazado = font.getAttributes();
				attributesRechazado.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
				componente.setFont( new Font(attributesRechazado));
				break;
			case "Inexistente":
				componente.setBackground(Color.white);
				componente.setForeground(Color.black);
				//preguntar al profe
				Map attributesInexistente = font.getAttributes();
				attributesInexistente.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON); 
				componente.setFont( new Font(attributesInexistente));
				break;
			case "Cancelado":
				componente.setBackground(Color.red);
				componente.setForeground(Color.white);
				//tachar
				Map attributesCancelado = font.getAttributes();
				attributesCancelado.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON); 
				componente.setFont( new Font(attributesCancelado));
				break;
			case "No integrado":
				componente.setBackground(Color.white);
				componente.setForeground(Color.black);
				break;
			}
		}
		*/
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component componente = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(column == 6) {
			if (this.getText().startsWith("No integrado")) {
				componente.setBackground(Color.white);
				componente.setForeground(Color.black);
			}
			if (this.getText().startsWith("Error")) {
				componente.setBackground(Color.red);
				componente.setForeground(Color.white);				
			}
			if (this.getText().startsWith("Generado")) {
				componente.setBackground(Color.gray);
				componente.setForeground(Color.white);
			}
			if (this.getText().startsWith("Borrador")) {
				componente.setBackground(Color.white);
				componente.setForeground(Color.gray);
			}
			if (this.getText().equalsIgnoreCase("Aprobado")) {
				componente.setBackground(Color.green);
				componente.setForeground(Color.black);
			}else if (this.getText().startsWith("Aprobado c/Obs")) {
				componente.setBackground(Color.green);
				componente.setForeground(Color.black);
				//resaltar o algo
				Map attributes = font.getAttributes();
				attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON); 
				componente.setFont( new Font(attributes));
			}
			if (this.getText().startsWith("Rechazado")) {
				componente.setBackground(Color.DARK_GRAY);
				componente.setForeground(Color.white);
				Map attributesRechazado = font.getAttributes();
				attributesRechazado.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
				componente.setFont( new Font(attributesRechazado));
			}
			if (this.getText().startsWith("Inexistente")) {
				componente.setBackground(Color.white);
				componente.setForeground(Color.black);
				//preguntar al profe
				Map attributesInexistente = font.getAttributes();
				attributesInexistente.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON); 
				componente.setFont( new Font(attributesInexistente));
			}
			if (this.getText().startsWith("Cancelado")) {
				componente.setBackground(Color.red);
				componente.setForeground(Color.white);
				//tachar
				Map attributesCancelado = font.getAttributes();
				attributesCancelado.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON); 
				componente.setFont( new Font(attributesCancelado));
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
