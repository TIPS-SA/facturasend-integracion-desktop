package views.eventosDe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import service.FacturasendService;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CancelacionDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtCdc;
	private KeyboardFocusManager kb;
	private JLabel lblCdc;
	private JLabel lblMotivo;
	private JButton btnCancelacion;
	private JButton btnCerrar;
	private JTextField txtMotivo;
	private static String cdc;
	private static Integer tipoDocumento;
	private static BigDecimal transaccionId;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CancelacionDialog dialog = new CancelacionDialog(cdc, tipoDocumento, transaccionId);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CancelacionDialog(String cdc, Integer tipoDocumento, BigDecimal transaccionId) {
		this.cdc = cdc;
		this.tipoDocumento = tipoDocumento;
		this.transaccionId = transaccionId;
		setTitle("Evento de Cancelacion");
		setModal(true);
		setBounds(100, 100, 450, 238);
		init();
		events();
	}
	
	private void init() {
		kb = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		lblCdc = new JLabel("CDC:");
		
		txtCdc = new JTextField();
		txtCdc.setEditable(false);
		//asignar valor del cdc
		txtCdc.setText(cdc);
		txtCdc.setColumns(10);
		
		lblMotivo = new JLabel("Motivo:");
		
		txtMotivo = new JTextField();
		txtMotivo.setColumns(10);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addGap(41)
							.addComponent(txtMotivo, GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addComponent(lblCdc)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtCdc, GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
						.addComponent(lblMotivo, Alignment.LEADING))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblCdc)
						.addComponent(txtCdc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblMotivo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtMotivo, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(90, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		btnCancelacion = new JButton("Cancelacion");
		
		buttonPane.add(btnCancelacion);
		btnCerrar = new JButton("Cerrar");
		buttonPane.add(btnCerrar);
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
		btnCerrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancelacion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Map<String, Object> result = FacturasendService.ejecutarEventoCancelacion(tipoDocumento, null, transaccionId.intValue(), txtCdc.getText(), txtMotivo.getText());
				
				if (Boolean.valueOf(result.get("success") + "") == true) {
					Map<String, Object> resultadoJsonMap = (Map<String, Object>)result.get("result");
					if  (resultadoJsonMap.get("ns2:rRetEnviEventoDe") != null) {
						Map<String, Object> rRetEnviEventoDe = (Map<String, Object>)resultadoJsonMap.get("ns2:rRetEnviEventoDe");
						
						if  (rRetEnviEventoDe.get("ns2:gResProcEVe") != null) {
							Map<String, Object> gResProcEVe = (Map<String, Object>)rRetEnviEventoDe.get("ns2:gResProcEVe");
							
							if  (gResProcEVe.get("ns2:dEstRes") != null) {
								String dEstRes = (String)gResProcEVe.get("ns2:dEstRes");
								Map<String, Object>  gResProc = (Map<String, Object>)gResProcEVe.get("ns2:gResProc");
								if  (dEstRes.equalsIgnoreCase("Aprobado")) {
									JOptionPane.showMessageDialog(null, "Cancelacion Exitosa\nEstado: "+dEstRes+"\nCodigo: "+ ((String) gResProc.get("ns2:dCodRes"))+"\nMensaje:  "+((String) gResProc.get("ns2:dMsgRes")));
								}else {
									JOptionPane.showMessageDialog(null, "Se rechazo la Cancelacion\nEstado: "+dEstRes+"\nCodigo de rechazo: "+ ((String) gResProc.get("ns2:dCodRes"))+"\nMensaje:  "+((String) gResProc.get("ns2:dMsgRes")));
								}
							}else {
								JOptionPane.showMessageDialog(null, "Error en la cancelacion 3");
							}
						}else {
							JOptionPane.showMessageDialog(null, "Error en la cancelacion 2");
						}
					}else {
						JOptionPane.showMessageDialog(null, "Error en la cancelacion 1");
					}
				}else {
					
					JOptionPane.showMessageDialog(null, "Hubo Errores en la cancelacion: \n"+result.get("error") + "");
				}
			}
		});
	}
}
