package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import core.CoreService;
import service.FacturasendService;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;

public class InfoMovimiento extends JDialog {

	private final JPanel contentPanel = new JPanel();
	//---
	private static BigDecimal transaccionId;
	private static Integer tipoDocumento;
	//---
	private JButton okButton;
	private JTable jTableTransaccionesItems;
	private FacturasendService fs;
	private JPanel infoCdcPane;
	private JLabel lblCdc;
	private JLabel lblError;
	private JTextField txtCdc;
	private JTextField txtEstado;
	private JTextArea txtAError;
	private JLabel lblCodigoQr;
	private KeyboardFocusManager kb;
	private JTextField txtEstadoDescripcion;
	private JPanel paneSouth;
	private JLabel lblItemsTotal;
	private JLabel lblCantidadItemsTotal;
	
	private Principal parent;
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		try {
			InfoMovimiento dialog = new InfoMovimiento(transaccionId);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Ocurrio un problema inesperado\n"+e);
			e.printStackTrace();
		}
	}*/

	/**
	 * Create the dialog.
	 */
	public InfoMovimiento(BigDecimal transaccionId, Integer tipoDocumento, Principal parent) {
		this.transaccionId = transaccionId;
		this.tipoDocumento = tipoDocumento;
		
		setModal(true);
		setBounds(100, 100, 800, 550);
		setMinimumSize(new Dimension(800,410));
		setTitle("Detalles del Movimiento #"+transaccionId);
		initialize(transaccionId, tipoDocumento);
		events();
		
	}

	private void initialize(BigDecimal transaccionId, Integer tipoDocumento) {
		kb = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		fs = new FacturasendService();
		// TODO Auto-generated method stub
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
	
		JScrollPane scrollPane = new JScrollPane();
		contentPanel.add(scrollPane, BorderLayout.CENTER);
	
		jTableTransaccionesItems = new JTable();
		List<Map<String, Object>> transacconesItem = fs.populateTransactionDetailsTable(jTableTransaccionesItems, transaccionId, tipoDocumento);
		
		scrollPane.setViewportView(jTableTransaccionesItems);

		infoCdcPane = new JPanel();
		infoCdcPane.setPreferredSize(new Dimension(100, 150));
		contentPanel.add(infoCdcPane, BorderLayout.NORTH);
		
		lblCdc = new JLabel("CDC: ");
		
		lblError = new JLabel("Error:");
		
		JLabel lblEstado = new JLabel("Estado:");
		
		txtCdc = new JTextField();
		txtCdc.setEditable(false);
		txtCdc.setColumns(10);
		
		
		txtEstado = new JTextField();
		txtEstado.setEditable(false);
		txtEstado.setColumns(10);
		
		txtEstadoDescripcion = new JTextField();
		txtEstadoDescripcion.setEditable(false);
		txtEstadoDescripcion.setColumns(10);

		lblCodigoQr = new JLabel("Codigo QR");
		
		txtAError = new JTextArea();
		txtAError.setLineWrap(true);
		txtAError.setEditable(false);
		
		//Asignar valores
		if (transacconesItem.size() > 0) {
			String cdc = (String)CoreService.getValueForKey(transacconesItem.get(0), "CDC");
			if (cdc!= null) {
				txtCdc.setText( cdc.trim() );
			}
			
			Object fieldEstado = CoreService.getValueForKey(transacconesItem.get(0), "estado");
			
			//String valueEstadoStr = (String) rs.get(i).get( fieldEstado );
			Integer valueEstadoInt = -99;	//Sin estado	
			
			if (fieldEstado != null) {
				valueEstadoInt = Integer.valueOf( (fieldEstado+"").trim() );
				txtEstado.setText( valueEstadoInt + "");
				txtEstadoDescripcion.setText(CoreService.getEstadoDescripcion(valueEstadoInt));

			}
			
			/*String estado = (String)Core.getValueForKey(transacconesItem.get(0), "ESTADO");
			if (estado != null) {
				txtEstado.setText( estado.trim() );
			}*/
			
			String error = (String)CoreService.getValueForKey(transacconesItem.get(0), "ERROR");
			
			if (error != null) {
				txtAError.setText( error.trim());
			}
		}
			

			GroupLayout gl_infoCdcPane = new GroupLayout(infoCdcPane);
			gl_infoCdcPane.setHorizontalGroup(
				gl_infoCdcPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_infoCdcPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_infoCdcPane.createParallelGroup(Alignment.LEADING, false)
							.addGroup(gl_infoCdcPane.createSequentialGroup()
								.addGroup(gl_infoCdcPane.createParallelGroup(Alignment.LEADING)
									.addComponent(lblCdc)
									.addComponent(lblEstado))
								.addGap(6)
								.addGroup(gl_infoCdcPane.createParallelGroup(Alignment.LEADING, false)
									.addGroup(gl_infoCdcPane.createSequentialGroup()
										.addComponent(txtEstado, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(txtEstadoDescripcion))
									.addComponent(txtCdc, GroupLayout.PREFERRED_SIZE, 396, GroupLayout.PREFERRED_SIZE)))
							.addGroup(gl_infoCdcPane.createSequentialGroup()
								.addComponent(lblError)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(txtAError, 0, 0, Short.MAX_VALUE)))
						.addGap(32)
						.addComponent(lblCodigoQr)
						.addContainerGap(234, Short.MAX_VALUE))
			);
			gl_infoCdcPane.setVerticalGroup(
				gl_infoCdcPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_infoCdcPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_infoCdcPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblCdc)
							.addComponent(txtCdc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblCodigoQr))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_infoCdcPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblEstado)
							.addComponent(txtEstado, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(txtEstadoDescripcion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_infoCdcPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblError)
							.addComponent(txtAError, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
			infoCdcPane.setLayout(gl_infoCdcPane);
			
			paneSouth = new JPanel();
			paneSouth.setPreferredSize(new Dimension(100, 20));
			contentPanel.add(paneSouth, BorderLayout.SOUTH);
			
			lblItemsTotal = new JLabel("Item(s):");
			
			lblCantidadItemsTotal = new JLabel(transacconesItem.size()+"");
			GroupLayout gl_paneSouth = new GroupLayout(paneSouth);
			gl_paneSouth.setHorizontalGroup(
				gl_paneSouth.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_paneSouth.createSequentialGroup()
						.addComponent(lblItemsTotal, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(lblCantidadItemsTotal)
						.addContainerGap(669, Short.MAX_VALUE))
			);
			gl_paneSouth.setVerticalGroup(
				gl_paneSouth.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_paneSouth.createSequentialGroup()
						.addGroup(gl_paneSouth.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblItemsTotal)
							.addComponent(lblCantidadItemsTotal))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
			paneSouth.setLayout(gl_paneSouth);
			
		//}
		//{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		//}
	}
	
	private void events() {
		kb.addKeyEventPostProcessor(new KeyEventPostProcessor(){
            public boolean postProcessKeyEvent(KeyEvent e){
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && this != null){
                    dispose();
                    return false;
                }
                return true;
            }
		});
		// TODO Auto-generated method stub
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//parent.paginacion.refresh();
				dispose();
			}
		});
	}
}
