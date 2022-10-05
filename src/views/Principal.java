package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

public class Principal extends JFrame {

	private JFrame frmFacturaSend;
	private JPanel paneSouth;
	private JPanel paneNorth;
	private JTextField tfBuscar;
	private JButton btnFacturas;
	private JButton btnAutoFactura;
	private JButton btnNotaCredito;
	private JButton btnNotaDebito;
	private JButton btnRemision;
	private JLabel lblBuscar;
	private JButton btnBuscar;
	private JButton btnLogs;
	private JButton btnCondiguracion;
	private JButton btnNoEnviar;
	private JButton btnReintegrar;
	private JButton btnVerXml;
	private JButton btnVerkude;
	private JButton btnEnviarEmail;
	private JScrollPane scrollPaneCenter;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Principal window = new Principal();
					window.frmFacturaSend.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Principal() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmFacturaSend = new JFrame();
		frmFacturaSend.setTitle("Factura Send - Integration Tool");
		frmFacturaSend.setBounds(100, 100, 731, 478);
		frmFacturaSend.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		paneSouth = new JPanel();
		paneSouth.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		paneSouth.setPreferredSize(new Dimension(150,60));
		frmFacturaSend.getContentPane().add(paneSouth, BorderLayout.SOUTH);
		
		btnLogs = new JButton("Logs");
		btnLogs.setPreferredSize(new Dimension(15, 15));
		
		btnCondiguracion = new JButton("Configuracion");
		
		btnNoEnviar = new JButton("No Enviar");
		
		btnReintegrar = new JButton("Reintegrar");
		
		btnVerXml = new JButton("Ver XML");
		
		btnVerkude = new JButton("verKUDE");
		
		btnEnviarEmail = new JButton("Enviar Email");
		GroupLayout gl_paneSouth = new GroupLayout(paneSouth);
		gl_paneSouth.setHorizontalGroup(
			gl_paneSouth.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_paneSouth.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnLogs, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCondiguracion, GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNoEnviar, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnReintegrar, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnVerXml, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnVerkude, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnEnviarEmail, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
					.addGap(18))
		);
		gl_paneSouth.setVerticalGroup(
			gl_paneSouth.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneSouth.createSequentialGroup()
					.addGap(13)
					.addGroup(gl_paneSouth.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnLogs, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCondiguracion)
						.addComponent(btnNoEnviar)
						.addComponent(btnReintegrar)
						.addComponent(btnVerXml)
						.addComponent(btnVerkude)
						.addComponent(btnEnviarEmail))
					.addGap(14))
		);
		paneSouth.setLayout(gl_paneSouth);
		
		paneNorth = new JPanel();
		paneNorth.setPreferredSize(new Dimension(150, 100));
		frmFacturaSend.getContentPane().add(paneNorth, BorderLayout.NORTH);
		
		lblBuscar = new JLabel("Buscar:");
		
		tfBuscar = new JTextField();
		tfBuscar.setColumns(10);
		
		btnBuscar = new JButton("Buscar");
		
		btnFacturas = new JButton("Facturas");
		
		btnAutoFactura = new JButton("<html><p style='text-align: center;'>Auto</p><p>Factura</p></html>");
		
		btnNotaCredito = new JButton("<html><p style='text-align: center;'>Nota</p><p>Credito</p></html>");
		
		btnNotaDebito = new JButton("<html><p style='text-align: center;'>Nota</p><p>Debito</p></html>");
		
		btnRemision = new JButton("Remision");
		
		JButton btnFacturaExportacion = new JButton("<html><p style='text-align:center;'>Factura</p><p>Exportacion</p></html>");
		
		JButton btnNewButton = new JButton("<html><p style='text-align:center;'>Factura</p><p>Importación</p></html>");
		GroupLayout gl_paneNorth = new GroupLayout(paneNorth);
		gl_paneNorth.setHorizontalGroup(
			gl_paneNorth.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneNorth.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneNorth.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_paneNorth.createSequentialGroup()
							.addComponent(btnFacturas)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnFacturaExportacion, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 109, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAutoFactura, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNotaCredito)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNotaDebito))
						.addGroup(gl_paneNorth.createSequentialGroup()
							.addComponent(lblBuscar)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfBuscar, GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
							.addGap(15)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneNorth.createParallelGroup(Alignment.LEADING)
						.addComponent(btnRemision)
						.addComponent(btnBuscar, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_paneNorth.setVerticalGroup(
			gl_paneNorth.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneNorth.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneNorth.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblBuscar)
						.addComponent(tfBuscar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBuscar))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneNorth.createParallelGroup(Alignment.LEADING, false)
						.addComponent(btnFacturas, GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
						.addComponent(btnRemision, GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
						.addGroup(gl_paneNorth.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnNotaCredito, GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
							.addComponent(btnNotaDebito, GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE))
						.addComponent(btnAutoFactura, GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
						.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnFacturaExportacion, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
		);
		paneNorth.setLayout(gl_paneNorth);
		
		scrollPaneCenter = new JScrollPane();
		frmFacturaSend.getContentPane().add(scrollPaneCenter, BorderLayout.CENTER);
		
		table = new JTable();
		scrollPaneCenter.setViewportView(table);
	}
}
