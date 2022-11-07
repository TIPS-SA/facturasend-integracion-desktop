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
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class InfoMovimiento extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private static BigDecimal transaccionId;
	private JButton okButton;
	private JTable table;
	private FacturasendService fs;
	private JPanel infoCdcPane;
	private JLabel lblCdc;
	private JLabel lblError;
	private JTextField txtCdc;
	private JTextField txtEstado;
	private JTextArea txtAError;
	private JLabel lblCodigoQr;
	private KeyboardFocusManager kb;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			InfoMovimiento dialog = new InfoMovimiento(transaccionId);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public InfoMovimiento(BigDecimal transaccionId) {
		this.transaccionId = transaccionId;

		setModal(true);
		setBounds(100, 100, 800, 550);
		setMinimumSize(new Dimension(800,410));
		setTitle("Detalles del Movimiento #"+transaccionId);
		initialize(transaccionId);
		events();
		
	}

	private void initialize(BigDecimal transaccionId) {
		kb = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		fs = new FacturasendService();
		// TODO Auto-generated method stub
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		//{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			//{
			table = new JTable();
			List<Map<String, Object>> transacconesItem = fs.populateTransactionDetailsTable(table, transaccionId);
			scrollPane.setViewportView(table);
			//}
		//}
		//{
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
			
			lblCodigoQr = new JLabel("Codigo QR");
			
			txtAError = new JTextArea();
			txtAError.setLineWrap(true);
			txtAError.setEditable(false);
			
			//Asignar valores
			if (transacconesItem.size() > 0) {
				txtCdc.setText( transacconesItem.get(0).get("CDC") + "");
				txtEstado.setText( transacconesItem.get(0).get("ESTADO") + "");
				txtAError.setText( transacconesItem.get(0).get("ERROR") + "");
				
			}

			GroupLayout gl_infoCdcPane = new GroupLayout(infoCdcPane);
			gl_infoCdcPane.setHorizontalGroup(
				gl_infoCdcPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_infoCdcPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_infoCdcPane.createParallelGroup(Alignment.LEADING, false)
							.addGroup(gl_infoCdcPane.createSequentialGroup()
								.addComponent(lblError)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(txtAError))
							.addGroup(gl_infoCdcPane.createSequentialGroup()
								.addGroup(gl_infoCdcPane.createParallelGroup(Alignment.LEADING)
									.addComponent(lblCdc)
									.addComponent(lblEstado))
								.addGap(6)
								.addGroup(gl_infoCdcPane.createParallelGroup(Alignment.LEADING, false)
									.addComponent(txtEstado)
									.addComponent(txtCdc, GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE))))
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
							.addComponent(txtEstado, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_infoCdcPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblError)
							.addComponent(txtAError, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(67, Short.MAX_VALUE))
			);
			infoCdcPane.setLayout(gl_infoCdcPane);
			
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
				dispose();
			}
		});
	}
}
