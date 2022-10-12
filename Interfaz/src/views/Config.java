package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import enums.DatabaseType;
import java.awt.GridLayout;

public class Config extends JDialog {

	private final JPanel contentPanel = new JPanel();
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
	private JPanel buttonsPane;
	private JButton btnCancelar;
	private JButton btnOk;
	private JLabel lblUtilizarComunicacionSincrona;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Config dialog = new Config();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Config() {
		setModal(true);
		setBounds(100, 100, 600, 410);
		setMinimumSize(new Dimension(600,410));
		setTitle("Configuracion");
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
								.addComponent(pTxtPasswordBd, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(cbDriver, 0, 481, Short.MAX_VALUE)
								.addComponent(txtHost, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(txtPuerto, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(txtDatabase, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(cbTipoDb, 0, 481, Short.MAX_VALUE)))
						.addComponent(btnTest))
					.addContainerGap())
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
					.addGap(18)
					.addComponent(btnTest)
					.addContainerGap(41, Short.MAX_VALUE))
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
		
		chkIntegracionFacturasend = new JCheckBox("");
		
		lblUrlApi = new JLabel("UrlApi");
		
		lblAuthorization = new JLabel("Authorization");
		
		chkComunicacionSincrona = new JCheckBox("");
		
		JLabel lblEmailsParaMensajes = new JLabel("Copiar Mensajes a estos Emails");
		
		txtUrlApi = new JTextField();
		txtUrlApi.setColumns(10);
		
		txtAuthorization = new JTextField();
		txtAuthorization.setColumns(10);
		
		txtEmails = new JTextField();
		txtEmails.setColumns(10);
		
		JLabel lblIntegracinConFacturacin = new JLabel("<html><p>Integración con Facturación</p><p>Electrónica Habilitada</p>");
		
		lblUtilizarComunicacionSincrona = new JLabel("Utilizar Comunicacion Sincrona");
		GroupLayout gl_paneConfigFacturasendFacElectronica = new GroupLayout(paneConfigFacturasendFacElectronica);
		gl_paneConfigFacturasendFacElectronica.setHorizontalGroup(
			gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneConfigFacturasendFacElectronica.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.LEADING)
						.addComponent(lblEmailsParaMensajes)
						.addComponent(lblUrlApi)
						.addComponent(lblAuthorization)
						.addComponent(lblIntegracinConFacturacin)
						.addComponent(lblUtilizarComunicacionSincrona))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.LEADING)
						.addComponent(chkIntegracionFacturasend)
						.addComponent(chkComunicacionSincrona)
						.addComponent(txtEmails, GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
						.addComponent(txtAuthorization, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
						.addComponent(txtUrlApi, GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_paneConfigFacturasendFacElectronica.setVerticalGroup(
			gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_paneConfigFacturasendFacElectronica.createSequentialGroup()
					.addContainerGap(12, Short.MAX_VALUE)
					.addGroup(gl_paneConfigFacturasendFacElectronica.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_paneConfigFacturasendFacElectronica.createSequentialGroup()
							.addComponent(chkIntegracionFacturasend)
							.addGap(10)
							.addComponent(txtUrlApi, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtAuthorization, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(chkComunicacionSincrona)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtEmails, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_paneConfigFacturasendFacElectronica.createSequentialGroup()
							.addComponent(lblIntegracinConFacturacin)
							.addGap(10)
							.addComponent(lblUrlApi)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblAuthorization)
							.addGap(16)
							.addComponent(lblUtilizarComunicacionSincrona)
							.addGap(6)
							.addComponent(lblEmailsParaMensajes)))
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
		
		buttonsPane = new JPanel();
		contentPane.add(buttonsPane, BorderLayout.SOUTH);
		buttonsPane.setPreferredSize(new Dimension(100, 50));
		
		btnCancelar = new JButton("Cancelar");
		
		btnOk = new JButton("Ok");
		GroupLayout gl_buttonsPane = new GroupLayout(buttonsPane);
		gl_buttonsPane.setHorizontalGroup(
			gl_buttonsPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_buttonsPane.createSequentialGroup()
					.addContainerGap(377, Short.MAX_VALUE)
					.addComponent(btnOk)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCancelar)
					.addContainerGap())
		);
		gl_buttonsPane.setVerticalGroup(
			gl_buttonsPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_buttonsPane.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_buttonsPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCancelar)
						.addComponent(btnOk))
					.addContainerGap())
		);
		buttonsPane.setLayout(gl_buttonsPane);
	}

	private void events() {
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
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