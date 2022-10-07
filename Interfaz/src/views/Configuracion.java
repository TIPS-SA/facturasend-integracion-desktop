package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

import enums.DatabaseType;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JFormattedTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.JPasswordField;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;

public class Configuracion extends JFrame {

	private JPanel contentPane;
	private JTextField txtHost;
	private JTextField txtPuerto;
	private JTextField txtDatabase;
	private JTextField txtUsername;
	private JScrollPane scrollPane;
	private JPanel paneFacturasend;
	private JPanel paneBaseDatos;
	private JTabbedPane tabbedPane;
	private JComboBox cbDriver;
	private JButton btnTest;
	private JButton btnOkBd;
	private JButton btnCancelarBd;
	private JLabel lblPassword;
	private JLabel lblDriver;
	private JLabel lblDatabase;
	private JLabel lblUsername;
	private JLabel lblTipo;
	private JLabel lblHost;
	private JLabel lblPuerto;
	private JComboBox cbTipoDb;
	private JPasswordField pTxtPasswordBd;
	private JPanel paneConfigFacturasend;
	private JPanel paneConfigFacturasendSouth;
	private JPanel paneConfigFacturasendFacElectronica;
	private JCheckBox chkIntegracionFacturasend;
	private JLabel lblUrlApi;
	private JCheckBox chkComunicacionSincrona;
	private JCheckBox chkHabilitadoEnviar;
	private JLabel lblAuthorization;
	private JTextField txtUrlApi;
	private JTextField txtAuthorization;
	private JTextField txtEmails;
	private JPanel paneConfigFacturaSendArchivos;
	private JLabel lblUbicacinDePdfkude;
	private JLabel lblUbicacinDeXmlFactura;
	private JTextField txtUbicacionPdf;
	private JTextField txtUbicacionXml;
	private JPanel paneConfigFacturasendCenter;
	private JPanel paneBtns;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Configuracion frame = new Configuracion();
					frame.setVisible(true);
					frame.setLocationRelativeTo(null); 
					frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Configuracion() {
		setBounds(100, 100, 600, 410);
		initialize();
		events();
	}
	
	private void initialize() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		paneBaseDatos = new JPanel();
		tabbedPane.addTab("Base de Datos", null, paneBaseDatos, null);
		
		lblTipo = new JLabel("Tipo:");
		
		lblHost = new JLabel("Host:");
		
		lblPuerto = new JLabel("Puerto:");
		
		lblDatabase = new JLabel("Database:");
		
		lblUsername = new JLabel("Username:");
		
		lblPassword = new JLabel("Password:");
		
		lblDriver = new JLabel("Driver:");
		
		cbTipoDb = new JComboBox();
		cbTipoDb.addItem("Seleccione un Gestor de Base de Datos");
		cbTipoDb.addItem(DatabaseType.POSTGRES.name);
		cbTipoDb.addItem(DatabaseType.MYSQL.name);
		cbTipoDb.addItem(DatabaseType.ORACLE.name);
		
		txtHost = new JTextField();
		
		txtPuerto = new JTextField();
		txtPuerto.setColumns(10);
		
		txtDatabase = new JTextField();
		txtDatabase.setColumns(10);
		
		txtUsername = new JTextField();
		txtUsername.setColumns(10);
		
		cbDriver = new JComboBox();
		cbDriver.addItem("Seleccione el Driver para su Base de Datos");
		cbDriver.addItem(DatabaseType.POSTGRES.defaultDriver);
		cbDriver.addItem(DatabaseType.MYSQL.defaultDriver);
		cbDriver.addItem(DatabaseType.ORACLE.defaultDriver);
		
		btnTest = new JButton("Test");
		
		btnOkBd = new JButton("Ok");
		
		btnCancelarBd = new JButton("Cancelar");
		
		pTxtPasswordBd = new JPasswordField();
		GroupLayout gl_paneBaseDatos = new GroupLayout(paneBaseDatos);
		gl_paneBaseDatos.setHorizontalGroup(
			gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneBaseDatos.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_paneBaseDatos.createSequentialGroup()
							.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
								.addComponent(lblDatabase)
								.addComponent(lblUsername)
								.addComponent(lblTipo)
								.addComponent(lblHost)
								.addComponent(lblPuerto)
								.addComponent(lblPassword)
								.addComponent(lblDriver))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
								.addComponent(pTxtPasswordBd, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
								.addComponent(cbDriver, 0, 194, Short.MAX_VALUE)
								.addComponent(txtHost, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
								.addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
								.addComponent(txtPuerto, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
								.addComponent(txtDatabase, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
								.addComponent(cbTipoDb, 0, 481, Short.MAX_VALUE))
							.addContainerGap())
						.addGroup(gl_paneBaseDatos.createSequentialGroup()
							.addComponent(btnTest)
							.addPreferredGap(ComponentPlacement.RELATED, 361, Short.MAX_VALUE)
							.addComponent(btnOkBd)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnCancelarBd)
							.addContainerGap())))
		);
		gl_paneBaseDatos.setVerticalGroup(
			gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneBaseDatos.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTipo)
						.addComponent(cbTipoDb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHost)
						.addComponent(txtHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPuerto)
						.addComponent(txtPuerto, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDatabase)
						.addComponent(txtDatabase, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUsername)
						.addComponent(txtUsername, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPassword)
						.addComponent(pTxtPasswordBd, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDriver)
						.addComponent(cbDriver, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnTest)
						.addComponent(btnCancelarBd)
						.addComponent(btnOkBd))
					.addContainerGap())
		);
		paneBaseDatos.setLayout(gl_paneBaseDatos);
		
		paneFacturasend = new JPanel();
		tabbedPane.addTab("FacturaSend", null, paneFacturasend, null);
		paneFacturasend.setLayout(new BorderLayout(0, 0));
		
		scrollPane = new JScrollPane();
		paneFacturasend.add(scrollPane, BorderLayout.CENTER);
		
		paneConfigFacturasend = new JPanel();
		scrollPane.setViewportView(paneConfigFacturasend);
		paneConfigFacturasend.setLayout(new BorderLayout(0, 0));
		
		paneConfigFacturasendCenter = new JPanel();
		paneConfigFacturasend.add(paneConfigFacturasendCenter, BorderLayout.CENTER);
		paneConfigFacturasendCenter.setLayout(new BorderLayout(0, 0));
		
		paneConfigFacturasendFacElectronica = new JPanel();
		paneConfigFacturasendCenter.add(paneConfigFacturasendFacElectronica, BorderLayout.CENTER);
		paneConfigFacturasendFacElectronica.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Facturaci\u00F3n Electronica - FacturaSend", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		paneConfigFacturasendFacElectronica.setPreferredSize(new Dimension(150,160));
		
		chkIntegracionFacturasend = new JCheckBox("Integración con Facturación Electrónica Habilitada");
		
		lblUrlApi = new JLabel("UrlApi");
		
		lblAuthorization = new JLabel("Authorization");
		
		chkHabilitadoEnviar = new JCheckBox("Habilitado para Enviar");
		
		chkComunicacionSincrona = new JCheckBox("Utilizar Comunicacion Sincrona");
		
		JLabel lblEmailsParaMensajes = new JLabel("Copiar Mensajes a estos Emails");
		
		txtUrlApi = new JTextField();
		txtUrlApi.setColumns(10);
		
		txtAuthorization = new JTextField();
		txtAuthorization.setColumns(10);
		
		txtEmails = new JTextField();
		txtEmails.setColumns(10);
		GroupLayout gl_paneConfigFacturasendFacElectronica = new GroupLayout(paneConfigFacturasendFacElectronica);
		gl_paneConfigFacturasendFacElectronica.setHorizontalGroup(
			gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneConfigFacturasendFacElectronica.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_paneConfigFacturasendFacElectronica.createSequentialGroup()
							.addGroup(gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.LEADING)
								.addComponent(lblUrlApi)
								.addComponent(lblAuthorization))
							.addGap(103))
						.addGroup(gl_paneConfigFacturasendFacElectronica.createSequentialGroup()
							.addComponent(lblEmailsParaMensajes)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.LEADING)
						.addComponent(chkComunicacionSincrona)
						.addComponent(txtAuthorization, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
						.addComponent(txtEmails, GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
						.addComponent(txtUrlApi, GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
						.addComponent(chkIntegracionFacturasend)
						.addComponent(chkHabilitadoEnviar))
					.addContainerGap())
		);
		gl_paneConfigFacturasendFacElectronica.setVerticalGroup(
			gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneConfigFacturasendFacElectronica.createSequentialGroup()
					.addComponent(chkIntegracionFacturasend)
					.addGap(10)
					.addGroup(gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_paneConfigFacturasendFacElectronica.createSequentialGroup()
							.addGroup(gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblUrlApi)
								.addComponent(txtUrlApi, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblAuthorization))
						.addGroup(gl_paneConfigFacturasendFacElectronica.createSequentialGroup()
							.addGap(27)
							.addComponent(txtAuthorization, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chkHabilitadoEnviar)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(chkComunicacionSincrona)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtEmails, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblEmailsParaMensajes))
					.addContainerGap())
		);
		paneConfigFacturasendFacElectronica.setLayout(gl_paneConfigFacturasendFacElectronica);
		
		paneConfigFacturasendSouth = new JPanel();
		paneConfigFacturasendCenter.add(paneConfigFacturasendSouth, BorderLayout.SOUTH);
		paneConfigFacturasendSouth.setPreferredSize(new Dimension(150,100));
		paneConfigFacturasendSouth.setLayout(new BorderLayout(0, 0));
		
		paneConfigFacturaSendArchivos = new JPanel();
		paneConfigFacturaSendArchivos.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "Ubicaci\u00F3n de Archivos KUDE-PDF/XML para env\u00EDo de Email", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		paneConfigFacturasendSouth.add(paneConfigFacturaSendArchivos, BorderLayout.CENTER);
		
		lblUbicacinDePdfkude = new JLabel("Ubicación de  PDF-KUDE");
		
		lblUbicacinDeXmlFactura = new JLabel("Ubicación de XML Factura");
		
		txtUbicacionPdf = new JTextField();
		txtUbicacionPdf.setColumns(10);
		
		txtUbicacionXml = new JTextField();
		txtUbicacionXml.setColumns(10);
		GroupLayout gl_paneConfigFacturaSendArchivos = new GroupLayout(paneConfigFacturaSendArchivos);
		gl_paneConfigFacturaSendArchivos.setHorizontalGroup(
			gl_paneConfigFacturaSendArchivos.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneConfigFacturaSendArchivos.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneConfigFacturaSendArchivos.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_paneConfigFacturaSendArchivos.createSequentialGroup()
							.addComponent(lblUbicacinDePdfkude)
							.addGap(28)
							.addComponent(txtUbicacionPdf, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
						.addGroup(gl_paneConfigFacturaSendArchivos.createSequentialGroup()
							.addComponent(lblUbicacinDeXmlFactura)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(txtUbicacionXml, GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_paneConfigFacturaSendArchivos.setVerticalGroup(
			gl_paneConfigFacturaSendArchivos.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneConfigFacturaSendArchivos.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneConfigFacturaSendArchivos.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUbicacinDePdfkude)
						.addComponent(txtUbicacionPdf, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneConfigFacturaSendArchivos.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblUbicacinDeXmlFactura)
						.addComponent(txtUbicacionXml, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(74, Short.MAX_VALUE))
		);
		paneConfigFacturaSendArchivos.setLayout(gl_paneConfigFacturaSendArchivos);
		
		paneBtns = new JPanel();
		paneBtns.setPreferredSize(new Dimension(150,50));
		paneConfigFacturasend.add(paneBtns, BorderLayout.SOUTH);
		
		JButton btnOkFacturaSend = new JButton("Ok");
		
		JButton btnCacncelarFacturaSend = new JButton("Cacncelar");
		GroupLayout gl_paneBtns = new GroupLayout(paneBtns);
		gl_paneBtns.setHorizontalGroup(
			gl_paneBtns.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_paneBtns.createSequentialGroup()
					.addContainerGap(411, Short.MAX_VALUE)
					.addComponent(btnOkFacturaSend)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnCacncelarFacturaSend)
					.addContainerGap())
		);
		gl_paneBtns.setVerticalGroup(
			gl_paneBtns.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneBtns.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneBtns.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCacncelarFacturaSend)
						.addComponent(btnOkFacturaSend))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		paneBtns.setLayout(gl_paneBtns);
	}

	private void events() {
		cbTipoDb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(cbTipoDb.getSelectedItem());
				if (cbTipoDb.getSelectedItem()==DatabaseType.POSTGRES.name) {
					txtDatabase.setText(DatabaseType.POSTGRES.defaultDatabase);
					txtUsername.setText(DatabaseType.POSTGRES.defaultUsername);
					txtHost.setText(DatabaseType.POSTGRES.defaultHost);
					txtPuerto.setText(DatabaseType.POSTGRES.defaultPort);
					cbDriver.setSelectedItem(DatabaseType.POSTGRES.defaultDriver);
				}
				if (cbTipoDb.getSelectedItem()==DatabaseType.MYSQL.name) {
					txtDatabase.setText(DatabaseType.MYSQL.defaultDatabase);
					txtUsername.setText(DatabaseType.MYSQL.defaultUsername);
					txtHost.setText(DatabaseType.MYSQL.defaultHost);
					txtPuerto.setText(DatabaseType.MYSQL.defaultPort);
					cbDriver.setSelectedItem(DatabaseType.MYSQL.defaultDriver);
				}
				if (cbTipoDb.getSelectedItem()==DatabaseType.ORACLE.name) {
					txtDatabase.setText(DatabaseType.ORACLE.defaultDatabase);
					txtUsername.setText(DatabaseType.ORACLE.defaultUsername);
					txtHost.setText(DatabaseType.ORACLE.defaultHost);
					txtPuerto.setText(DatabaseType.ORACLE.defaultPort);
					cbDriver.setSelectedItem(DatabaseType.ORACLE.defaultDriver);
				}
			}
		});
	}
}
