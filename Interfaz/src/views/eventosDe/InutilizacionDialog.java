package views.eventosDe;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import core.CoreService;
import service.FacturasendService;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InutilizacionDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JButton btnInutilizar;
	private JButton btnCancelar;
	private JLabel lblTipoDeDocumento;
	private JTextField txtEstablecimiento;
	private JTextField txtPunto;
	private JTextField txtNumeracionDesde;
	private JTextField txtNumeracionHasta;
	private JTextField txtSerie;
	private JComboBox cbTipoDocumento;
	private KeyboardFocusManager kb;
	private JTextField txtMotivo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			InutilizacionDialog dialog = new InutilizacionDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public InutilizacionDialog() {
		setTitle("Evento de Inutilizacion");
		setModal(true);
		setBounds(100, 100, 434, 366);
		init();
		events();
	}
	
	private void init() {
		kb = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		lblTipoDeDocumento = new JLabel("Tipo de Documento:");
		
		JLabel lblEstablecimiento = new JLabel("Establecimiento:");
		
		JLabel lblPunto = new JLabel("Punto: ");
		
		JLabel lblDesde = new JLabel("Desde:");
		
		JLabel lblHasta = new JLabel("Hasta:");
		
		JLabel lblSerie = new JLabel("Serie:");
		
		JLabel lblMotivo = new JLabel("Motivo:");
		
		cbTipoDocumento = new JComboBox();
		cbTipoDocumento.addItem("Factura Electronica");
		cbTipoDocumento.addItem("Auto Factura Electronica");
		cbTipoDocumento.addItem("Nota Credito");
		cbTipoDocumento.addItem("Nota Debito");
		cbTipoDocumento.addItem("Nota Remision");
		
		txtEstablecimiento = new JTextField();
		txtEstablecimiento.setColumns(10);
		
		txtPunto = new JTextField();
		txtPunto.setColumns(10);
		
		txtNumeracionDesde = new JTextField();
		txtNumeracionDesde.setColumns(10);
		
		txtNumeracionHasta = new JTextField();
		txtNumeracionHasta.setColumns(10);
		
		txtSerie = new JTextField();
		txtSerie.setColumns(10);
		
		txtMotivo = new JTextField();
		txtMotivo.setColumns(10);
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTipoDeDocumento)
						.addComponent(lblMotivo)
						.addComponent(lblEstablecimiento)
						.addComponent(lblPunto)
						.addComponent(lblDesde)
						.addComponent(lblHasta)
						.addComponent(lblSerie))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(txtMotivo, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
						.addComponent(cbTipoDocumento, 0, 265, Short.MAX_VALUE)
						.addComponent(txtEstablecimiento, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
						.addComponent(txtPunto, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
						.addComponent(txtNumeracionDesde, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
						.addComponent(txtNumeracionHasta, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
						.addComponent(txtSerie, GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTipoDeDocumento)
						.addComponent(cbTipoDocumento, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblEstablecimiento)
						.addComponent(txtEstablecimiento, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPunto)
						.addComponent(txtPunto, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDesde)
						.addComponent(txtNumeracionDesde, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHasta)
						.addComponent(txtNumeracionHasta, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSerie)
						.addComponent(txtSerie, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMotivo)
						.addComponent(txtMotivo, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		contentPanel.setLayout(gl_contentPanel);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		btnInutilizar = new JButton("Inutilizar");
		
		buttonPane.add(btnInutilizar);
		btnCancelar = new JButton("Cancelar");

		buttonPane.add(btnCancelar);
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
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnInutilizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int tipoDocumento = CoreService.getTipoDocumentoNro(cbTipoDocumento.getSelectedItem().toString());
				
				Map<String, Object> result = FacturasendService.ejecutarEventoInutilizacion(null, null, txtSerie.getText(), tipoDocumento, txtEstablecimiento.getText(), txtPunto.getText(), txtNumeracionHasta.getText(), txtNumeracionHasta.getText(), txtMotivo.getText());
				if (Boolean.valueOf(result.get("success").toString())==true) {
					if(result.get("result") != null) {
						Map<String, Object> respuesta = (Map<String, Object>)result.get("result");
						if(respuesta.get("ns2:rRetEnviEventoDe")!=null) {
							Map<String, Object> rRetEnviEventoDe = (Map<String, Object>)respuesta.get("ns2:rRetEnviEventoDe");
							if ( rRetEnviEventoDe.get("ns2:gResProcEVe")!=null) {
								Map<String, Object> gResProcEVe = (Map<String, Object>)rRetEnviEventoDe.get("ns2:gResProcEVe");
								Map<String, Object>  gResProc = (Map<String, Object>)gResProcEVe.get("ns2:gResProc");
								if (((String) gResProcEVe.get("ns2:dEstRes")).equalsIgnoreCase("Aprobado")) {
									JOptionPane.showMessageDialog(null, "Inutilizacion Existosa\nEstado: "+((String) gResProcEVe.get("ns2:dEstRes"))+"\nCodigo: "+ ((String) gResProc.get("ns2:dCodRes"))+"\nMensaje:  "+((String) gResProc.get("ns2:dMsgRes")));
								}else {
									JOptionPane.showMessageDialog(null, "Se rechazo la inutilizacion\nEstado: "+((String) gResProcEVe.get("ns2:dEstRes"))+"\nCodigo de rechazo: "+ ((String) gResProc.get("ns2:dCodRes"))+"\nMensaje:  "+((String) gResProc.get("ns2:dMsgRes")));
								}
							}else {
								JOptionPane.showMessageDialog(null, "Hubo Errores en la inutilizacion 3");
							}
							
						}else {
							JOptionPane.showMessageDialog(null, "Hubo Errores en la inutilizacion 2");
						}
						
					}else {
						JOptionPane.showMessageDialog(null, "Hubo Errores en la inutilizacion 1");
					}
					
				}else {
					JOptionPane.showMessageDialog(null, "Hubo Errores en la inutilizacion: \n"+result.get("error") + "");
				}
				dispose();
			}
		});
	}
}
