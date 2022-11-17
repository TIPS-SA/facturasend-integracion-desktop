package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import enums.DatabaseType;
import service.ConfigProperties;

import java.awt.GridLayout;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;

public class Config extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private ConfigProperties cp = new ConfigProperties();
	private Map<String, String> propertiesDb = new HashMap<String, String>();
	//private Map<String, String> propertiesDb = new HashMap<String, String>();
	private JPanel contentPane;
	private JTextField txtHost;
	private JTextField txtPuerto;
	private JTextField txtDatabase;
	private JTextField txtSchema;
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
	private JLabel lblSchema;
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
	private JButton btndbfFacturasend;
	private JFileChooser fileChooser;
	private JButton btnDbfPayments;
	private JTextField txtDbfFacturasend;
	private JTextField txtDbfPayments;
	private KeyboardFocusManager kb;
	private JTextField txtTablaDestino;
	private JLabel lblTablaDeDestino;
	private JTextField txtPathDbfPayments;
	private JTextField txtPathDbfFacturasend;
	private JPanel paneOtros;
	private JTextField txtNombreImpresora;
	private JLabel lblNombreDeLa;
	private JCheckBox chkEnviarKudeImpresora;

	private Principal parent;
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		try {
			Config dialog = new Config();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Ocurrio un problema inesperado\n"+e);
			System.out.println(e);
		}
	}*/

	/**
	 * Create the dialog.
	 */
	public Config(Principal parent) {
		this.parent = parent;
		setModal(true);
		setBounds(100, 100, 600, 410);
		setMinimumSize(new Dimension(600,410));
		setTitle("Configuracion");
		initialize();
		events();
	}
	
	private void initialize() {
		kb = KeyboardFocusManager.getCurrentKeyboardFocusManager();
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
		
		lblSchema = new JLabel("Schema:");

		lblUsername = new JLabel("Username:");
		
		lblPassword = new JLabel("Password:");
		
		lblDriver = new JLabel("Driver:");
		
		cbTipoDb = new JComboBox();
		cbTipoDb.addItem("Seleccione un Gestor de Base de Datos");
		cbTipoDb.addItem(DatabaseType.POSTGRES.name);
		cbTipoDb.addItem(DatabaseType.MYSQL.name);
		cbTipoDb.addItem(DatabaseType.ORACLE.name);
		cbTipoDb.addItem(DatabaseType.DBF.name);
		
		txtHost = new JTextField();
		
		txtPuerto = new JTextField();
		txtPuerto.setColumns(10);
		
		txtDatabase = new JTextField();
		txtDatabase.setColumns(10);
		
		txtSchema = new JTextField();
		txtSchema.setColumns(10);
		
		txtUsername = new JTextField();
		txtUsername.setColumns(10);
		
		cbDriver = new JComboBox();
		cbDriver.addItem("Seleccione el Driver para su Base de Datos");
		cbDriver.addItem(DatabaseType.POSTGRES.defaultDriver);
		cbDriver.addItem(DatabaseType.MYSQL.defaultDriver);
		cbDriver.addItem(DatabaseType.ORACLE.defaultDriver);
		
		btnTest = new JButton("Test");
		
		pTxtPasswordBd = new JPasswordField();
		
		btndbfFacturasend = new JButton("Seleccionar Archivo Facturasend.dbf");
		btndbfFacturasend.setVisible(false);
		
		btnDbfPayments = new JButton("Seleccionar Archivo payments.dbf");
		
		btnDbfPayments.setVisible(false);
		
		txtDbfFacturasend = new JTextField();
		txtDbfFacturasend.setEditable(false);
		txtDbfFacturasend.setColumns(10);
		txtDbfFacturasend.setVisible(false);
		
		txtDbfPayments = new JTextField();
		txtDbfPayments.setEditable(false);
		txtDbfPayments.setColumns(10);
		txtDbfPayments.setVisible(false);
		
		lblTablaDeDestino = new JLabel("Tabla de Destino");
		
		txtTablaDestino = new JTextField();
		txtTablaDestino.setColumns(10);
		
		txtPathDbfPayments = new JTextField();
		txtPathDbfPayments.setEditable(false);
		txtPathDbfPayments.setColumns(10);
		txtPathDbfPayments.setVisible(false);
		
		txtPathDbfFacturasend = new JTextField();
		txtPathDbfFacturasend.setEditable(false);
		txtPathDbfFacturasend.setColumns(10);
		txtPathDbfFacturasend.setVisible(false);
		
		
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
								.addComponent(lblSchema)
								.addComponent(lblDriver))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
								.addComponent(pTxtPasswordBd, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(cbDriver, 0, 481, Short.MAX_VALUE)
								.addComponent(txtHost, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(txtUsername, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(txtPuerto, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(txtDatabase, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(txtSchema, GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
								.addComponent(cbTipoDb, 0, 481, Short.MAX_VALUE)))
						.addComponent(btnTest)
						.addGroup(gl_paneBaseDatos.createSequentialGroup()
							.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.TRAILING, false)
									.addComponent(btnDbfPayments, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(btndbfFacturasend, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(lblTablaDeDestino))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
								.addComponent(txtDbfFacturasend, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
								.addComponent(txtDbfPayments, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
								.addComponent(txtTablaDestino, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
								.addComponent(txtPathDbfPayments, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
								.addComponent(txtPathDbfFacturasend, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))))
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
						.addComponent(lblSchema)
						.addComponent(txtSchema, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.LEADING, false)
						.addComponent(txtDbfFacturasend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btndbfFacturasend, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDbfPayments)
						.addComponent(txtDbfPayments, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTablaDeDestino)
						.addComponent(txtTablaDestino, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(txtPathDbfPayments, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(txtPathDbfFacturasend, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
					.addComponent(btnTest)
					.addGap(14))
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
		txtEmails.setToolTipText("Colocar correos separados por comas");
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
		
		paneOtros = new JPanel();
		tabbedPane.addTab("Otros", null, paneOtros, null);
		
		chkEnviarKudeImpresora = new JCheckBox("Enviar KUDE del DE a impresora al integrar");
		
		lblNombreDeLa = new JLabel("Nombre de la Impresora");
		
		txtNombreImpresora = new JTextField();
		txtNombreImpresora.setColumns(10);
		GroupLayout gl_paneOtros = new GroupLayout(paneOtros);
		gl_paneOtros.setHorizontalGroup(
			gl_paneOtros.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneOtros.createSequentialGroup()
					.addGap(19)
					.addGroup(gl_paneOtros.createParallelGroup(Alignment.LEADING)
						.addComponent(chkEnviarKudeImpresora)
						.addGroup(Alignment.TRAILING, gl_paneOtros.createSequentialGroup()
							.addComponent(lblNombreDeLa)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(txtNombreImpresora, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_paneOtros.setVerticalGroup(
			gl_paneOtros.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneOtros.createSequentialGroup()
					.addContainerGap()
					.addComponent(chkEnviarKudeImpresora)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_paneOtros.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNombreDeLa)
						.addComponent(txtNombreImpresora, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(229, Short.MAX_VALUE))
		);
		paneOtros.setLayout(gl_paneOtros);
		
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
		
		leerProperties();
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
		btndbfFacturasend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser = new JFileChooser();
				FileNameExtensionFilter filtroArchivo=new FileNameExtensionFilter("DBF","dbf");
			    //fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    int r=fileChooser.showOpenDialog(null);
			    if(r==JFileChooser.APPROVE_OPTION){
			    	File f=fileChooser.getSelectedFile();
					//Aca se le da el tratamiento al archivo
			    	txtDbfFacturasend.setText(f.getName());
			    	txtPathDbfFacturasend.setText(f.getParent());
			    }
			}
		});
		btnDbfPayments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser = new JFileChooser();
				FileNameExtensionFilter filtroArchivo=new FileNameExtensionFilter("DBF","dbf");
			    fileChooser.setFileFilter(filtroArchivo);
			    int r=fileChooser.showOpenDialog(null);
			    if(r==JFileChooser.APPROVE_OPTION){
			    	File f=fileChooser.getSelectedFile();
					//Aca se le da el tratamiento al archivo
			    	txtDbfPayments.setText(f.getName());
			    	txtPathDbfPayments.setText(f.getParent());
			    }
			}
		});
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (validation() == null) {					
					save();
//					System.exit(0);
					parent.paginacion.refresh();
					dispose();
				}else {
					JOptionPane.showMessageDialog(null, validation());
				}
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
					isDBF(false);
					txtDatabase.setText((propertiesDb.get("database.postgres.name").equals("") || propertiesDb.get("database.postgres.name") == null) || !propertiesDb.containsKey("database.postgres.name") ?DatabaseType.POSTGRES.defaultDatabase:propertiesDb.get("database.postgres.name"));
					txtSchema.setText((propertiesDb.get("database.postgres.schema").equals("") || propertiesDb.get("database.postgres.schema") == null) || !propertiesDb.containsKey("database.postgres.schema") ?DatabaseType.POSTGRES.defaultSchema:propertiesDb.get("database.postgres.schema"));
					txtUsername.setText((propertiesDb.get("database.postgres.username").equals("") || propertiesDb.get("database.postgres.username") == null) || !propertiesDb.containsKey("database.postgres.username")?DatabaseType.POSTGRES.defaultUsername:propertiesDb.get("database.postgres.username"));
					txtHost.setText((propertiesDb.get("database.postgres.host").equals("") || propertiesDb.get("database.postgres.host") == null) || !propertiesDb.containsKey("database.postgres.host")?DatabaseType.POSTGRES.defaultHost: propertiesDb.get("database.postgres.host"));
					txtPuerto.setText((propertiesDb.get("database.postgres.port").equals("") || propertiesDb.get("database.postgres.port") == null) || !propertiesDb.containsKey("database.postgres.port")?DatabaseType.POSTGRES.defaultPort:propertiesDb.get("database.postgres.port"));
					cbDriver.setSelectedItem((propertiesDb.get("database.postgres.driver").equals("") || propertiesDb.get("database.postgres.driver") == null) || !propertiesDb.containsKey("database.postgres.driver")?DatabaseType.POSTGRES.defaultDriver:propertiesDb.get("database.postgres.driver"));
					pTxtPasswordBd.setText((propertiesDb.get("database.postgres.password").equals("") || propertiesDb.get("database.postgres.password") == null) || !propertiesDb.containsKey("database.postgres.password")?DatabaseType.POSTGRES.defaultPass:propertiesDb.get("database.postgres.password"));
				}
				if (cbTipoDb.getSelectedItem()==DatabaseType.MYSQL.name) {
					isDBF(false);
					txtDatabase.setText((propertiesDb.get("database.mysql.name").equals("") || propertiesDb.get("database.mysql.name") == null) || !propertiesDb.containsKey("database.mysql.name") ?DatabaseType.MYSQL.defaultDatabase:propertiesDb.get("database.mysql.name"));
					txtSchema.setText((propertiesDb.get("database.mysql.schema").equals("") || propertiesDb.get("database.mysql.schema") == null) || !propertiesDb.containsKey("database.mysql.schema")?DatabaseType.MYSQL.defaultSchema:propertiesDb.get("database.mysql.schema"));
					txtUsername.setText((propertiesDb.get("database.mysql.username").equals("") || propertiesDb.get("database.mysql.username") == null) || !propertiesDb.containsKey("database.mysql.username")?DatabaseType.MYSQL.defaultUsername:propertiesDb.get("database.mysql.username"));
					txtHost.setText((propertiesDb.get("database.mysql.host").equals("") || propertiesDb.get("database.mysql.host") == null) || !propertiesDb.containsKey("database.mysql.host")?DatabaseType.MYSQL.defaultHost: propertiesDb.get("database.mysql.host"));
					txtPuerto.setText((propertiesDb.get("database.mysql.port").equals("") || propertiesDb.get("database.mysql.port") == null) || !propertiesDb.containsKey("database.mysql.port")?DatabaseType.MYSQL.defaultPort:propertiesDb.get("database.mysql.port"));
					cbDriver.setSelectedItem((propertiesDb.get("database.mysql.driver").equals("") || propertiesDb.get("database.mysql.driver") == null) || !propertiesDb.containsKey("database.mysql.driver")?DatabaseType.MYSQL.defaultDriver:propertiesDb.get("database.mysql.driver"));
					pTxtPasswordBd.setText((propertiesDb.get("database.mysql.password").equals("") || propertiesDb.get("database.mysql.password") == null) || !propertiesDb.containsKey("database.mysql.password")?DatabaseType.MYSQL.defaultPass:propertiesDb.get("database.mysql.password"));
				}
				if (cbTipoDb.getSelectedItem()==DatabaseType.ORACLE.name) {
					isDBF(false);
					txtDatabase.setText((propertiesDb.get("database.oracle.name").equals("") || propertiesDb.get("database.oracle.name") == null) || !propertiesDb.containsKey("database.oracle.name")?DatabaseType.ORACLE.defaultDatabase:propertiesDb.get("database.oracle.name"));
					txtSchema.setText((propertiesDb.get("database.oracle.schema").equals("") || propertiesDb.get("database.oracle.schema") == null) || !propertiesDb.containsKey("database.oracle.schema")?DatabaseType.ORACLE.defaultSchema:propertiesDb.get("database.oracle.schema"));
					txtUsername.setText((propertiesDb.get("database.oracle.username").equals("") || propertiesDb.get("database.oracle.username") == null) || !propertiesDb.containsKey("database.oracle.username")?DatabaseType.ORACLE.defaultUsername:propertiesDb.get("database.oracle.username"));
					txtHost.setText((propertiesDb.get("database.oracle.host").equals("") || propertiesDb.get("database.oracle.host") == null) || !propertiesDb.containsKey("database.oracle.host")?DatabaseType.ORACLE.defaultHost: propertiesDb.get("database.oracle.host"));
					txtPuerto.setText((propertiesDb.get("database.oracle.port").equals("") || propertiesDb.get("database.oracle.port") == null) || !propertiesDb.containsKey("database.oracle.port")?DatabaseType.ORACLE.defaultPort:propertiesDb.get("database.oracle.port"));
					cbDriver.setSelectedItem((propertiesDb.get("database.oracle.driver").equals("") || propertiesDb.get("database.oracle.driver") == null) || !propertiesDb.containsKey("database.oracle.driver")?DatabaseType.ORACLE.defaultDriver:propertiesDb.get("database.oracle.driver"));
					pTxtPasswordBd.setText((propertiesDb.get("database.oracle.password").equals("") || propertiesDb.get("database.oracle.password") == null) || !propertiesDb.containsKey("database.oracle.password")?DatabaseType.ORACLE.defaultPass:propertiesDb.get("database.oracle.password"));
				}
				if (cbTipoDb.getSelectedItem()==DatabaseType.DBF.name) {
					isDBF(true);
					txtDbfFacturasend.setText(propertiesDb.get("database.dbf.transaccion_table"));
					txtDbfPayments.setText(propertiesDb.get("database.dbf.payment_view"));
					txtTablaDestino.setText(propertiesDb.get("database.dbf.facturasend_table"));
				}
			}
		});
	}
	
	private void isDBF(boolean flag) {
		//!flag == no visible cuando es DBF
		lblDatabase.setVisible(!flag);
		lblSchema.setVisible(!flag);
		lblUsername.setVisible(!flag);
		lblHost.setVisible(!flag);
		lblPuerto.setVisible(!flag);
		lblDriver.setVisible(!flag);
		lblPassword.setVisible(!flag);
		txtDatabase.setVisible(!flag);
		txtSchema.setVisible(!flag);
		txtUsername.setVisible(!flag);
		txtHost.setVisible(!flag);
		txtPuerto.setVisible(!flag);
		cbDriver.setVisible(!flag);
		pTxtPasswordBd.setVisible(!flag);
		btndbfFacturasend.setVisible(flag);
		btnDbfPayments.setVisible(flag);
		txtDbfPayments.setVisible(flag);
		txtDbfFacturasend.setVisible(flag);
		txtPathDbfFacturasend.setVisible(flag);
		txtPathDbfPayments.setVisible(flag);
		txtTablaDestino.setVisible(flag);
		lblTablaDeDestino.setVisible(flag);
		btnTest.setVisible(!flag);
	}
	
	private void save() {
		if (!cbTipoDb.getSelectedItem().toString().equals("Archivo DBF")) {	
			String password;
			char[] pass;
			String tipo = null;
			switch (cbTipoDb.getSelectedItem().toString()) {
				case "PostgreSQL":
					propertiesDb.put("database.type", DatabaseType.POSTGRES.value);
					tipo = DatabaseType.POSTGRES.value;
					break;
				case "Oracle":
					propertiesDb.put("database.type", DatabaseType.ORACLE.value);
					tipo = DatabaseType.ORACLE.value;
					break;
				case "MySQL":
					propertiesDb.put("database.type", DatabaseType.MYSQL.value);
					tipo = DatabaseType.MYSQL.value;
					break;
				default:
					break;
			}
			propertiesDb.put("database."+tipo+".name", txtDatabase.getText());
			propertiesDb.put("database."+tipo+".schema", txtSchema.getText());
			propertiesDb.put("database."+tipo+".username", txtUsername.getText());
			propertiesDb.put("database."+tipo+".host", txtHost.getText());
			propertiesDb.put("database."+tipo+".port", txtPuerto.getText());
			password="";
			pass=pTxtPasswordBd.getPassword();
			for (int i = 0; i < pass.length; i++) {
				password += pass[i];
			}
			propertiesDb.put("database."+tipo+".password", password);
			propertiesDb.put("database."+tipo+".driver", cbDriver.getSelectedItem().toString());
		}else {
			propertiesDb.put("database.type", DatabaseType.DBF.value);
			propertiesDb.put("database.dbf.payment_view", txtDbfPayments.getText());
			propertiesDb.put("database.dbf.transaccion_table", txtDbfFacturasend.getText());
			propertiesDb.put("database.dbf.parent_folder", txtPathDbfPayments.getText()==null ||txtPathDbfPayments.getText() == "" ? txtPathDbfFacturasend.getText():txtPathDbfPayments.getText());
			propertiesDb.put("database.dbf.facturasend_table",txtTablaDestino.getText());
		}
		//pestanha facturasend
		
		propertiesDb.put("facturasend.integracionSet", chkIntegracionFacturasend.isSelected()?"Y":"N");
		propertiesDb.put("facturasend.sincrono", chkComunicacionSincrona.isSelected()?"Y":"N");
		propertiesDb.put("facturasend.url",txtUrlApi.getText());
		propertiesDb.put("facturasend.token",txtAuthorization.getText());
		propertiesDb.put("facturasend.emails",txtEmails.getText());
		propertiesDb.put("facturasend.carpetaKude",txtUbicacionPdf.getText());
		propertiesDb.put("facturasend.carpetaXML",txtUbicacionXml.getText());
		
		
		//pestanha otros
		
		propertiesDb.put("config.otros.enviar_kude_impresora", chkEnviarKudeImpresora.isSelected()?"Y":"N");
		propertiesDb.put("config.otros.nombre_impresora", txtNombreImpresora.getText());
		
		cp.writeDbProperties(propertiesDb);
		//cp.writeFsProperties(propertiesDb);
	}
	
	private String validation() {
		String errMsg=null;
		if (cbTipoDb.getSelectedIndex() == 0) {
			return null;
		}
		if (!cbTipoDb.getSelectedItem().toString().equals("Archivo DBF")) {	
			if(txtDatabase.getText().contentEquals("") || txtDatabase.getText() == null) {
				errMsg = "No se especifico una Base de Datos";
			}
			if(txtSchema.getText().contentEquals("") || txtSchema.getText() == null) {
				errMsg ="Np se especifico un Esquema";
			}
			if(txtUsername.getText().contentEquals("") || txtUsername.getText() == null) {
				errMsg = "No se especifico el usuario para la conexion con la base de datos";
			}
			if(txtHost.getText().contentEquals("") || txtHost.getText() == null) {
				errMsg ="No se especifico la url de la base de datos";
			}
			if(txtPuerto.getText().contentEquals("") || txtPuerto.getText() == null) {
				errMsg ="No se especifico el puerto de la base de datos";
			}
			if(pTxtPasswordBd.getPassword().length <1) {
				errMsg ="No se especifico la contraseña del usuario de base de datos";
			}
			if(cbDriver.getSelectedIndex()== 0) {
				errMsg ="Por favor, seleccione un Driver";
			}
		}else {
			if(txtDbfFacturasend.getText().contentEquals("") || txtDbfFacturasend.getText() == null) {
				errMsg ="Por favor, seleccione un archivo DBF para la tabla FacturaSend";
			}
			if(txtDbfPayments.getText().contentEquals("") || txtDbfPayments.getText() == null) {
				errMsg ="Por favor, seleccione un archivo DBF para la tabla FacturaSend";
			}
			if(!txtPathDbfFacturasend.getText().equals(txtPathDbfPayments.getText())) {
				errMsg = "Los archivos deben estar dentro de la misma carpeta";
			}
			
		}
		if(txtUrlApi.getText().contentEquals("") || txtUrlApi.getText() == null) {
			errMsg ="No se especifico una url para la API";
		}
		if(txtAuthorization.getText().contentEquals("") || txtAuthorization.getText() == null) {
			errMsg ="Por favor, coloque su api key";
		}
		if(txtEmails.getText().contentEquals("") || txtEmails.getText() == null) {
			errMsg ="No se especifico ningun email a FacturaSend";
		}
		if(txtUbicacionPdf.getText().contentEquals("") || txtUbicacionPdf.getText() == null) {
			errMsg ="Por favor, especifique una ruta para poder guardar los PDF";
		}
		if(txtUbicacionXml.getText().contentEquals("") || txtUbicacionXml.getText() == null) {
			errMsg ="Por favor, especifique una ruta para poder guardar los XML";
		}
		
		
		return errMsg;
		
	}
	
	private void leerProperties() {
		propertiesDb = cp.readDbProperties();
		if(!propertiesDb.isEmpty()) {
			cbTipoDb.setSelectedItem(getDataBaseName());
			System.out.println(propertiesDb.get("database.type"));
			if ( propertiesDb.get("database.type").equals("dbf")) {
				isDBF(true);
			}
			txtDbfFacturasend.setText(propertiesDb.get("database.dbf.transaccion_table"));
			txtDbfPayments.setText(propertiesDb.get("database.dbf.payment_view"));
			txtPathDbfPayments.setText(propertiesDb.get("database.dbf.parent_folder"));
			txtPathDbfFacturasend.setText(propertiesDb.get("database.dbf.parent_folder"));
			txtTablaDestino.setText(propertiesDb.get("database.dbf.facturasend_table"));
			String tipo = propertiesDb.get("database.type").toString();
			txtDatabase.setText(propertiesDb.get("database."+tipo+".name"));
			txtSchema.setText(propertiesDb.get("database."+tipo+".schema"));
			txtUsername.setText(propertiesDb.get("database."+tipo+".username"));
			txtHost.setText(propertiesDb.get("database."+tipo+".host"));
			txtPuerto.setText(propertiesDb.get("database."+tipo+".port"));
			cbDriver.setSelectedItem(propertiesDb.get("database."+tipo+".driver"));
			pTxtPasswordBd.setText(propertiesDb.get("database."+tipo+".password"));
					
			//Pestanha FacturaSend
			chkIntegracionFacturasend.setSelected((propertiesDb.get("facturasend.integracionSet")).toString().equals("Y")?true:false);
			chkComunicacionSincrona.setSelected((propertiesDb.get("facturasend.sincrono")).toString().equals("Y")?true:false);
			txtUrlApi.setText(propertiesDb.get("facturasend.url"));
			txtAuthorization.setText(propertiesDb.get("facturasend.token"));
			txtEmails.setText(propertiesDb.get("facturasend.emails"));
			txtUbicacionPdf.setText(propertiesDb.get("facturasend.carpetaKude"));
			txtUbicacionXml.setText(propertiesDb.get("facturasend.carpetaXML"));
			
			//pestanha otros
			chkEnviarKudeImpresora.setSelected((propertiesDb.get("config.otros.enviar_kude_impresora")).toString().equals("Y")?true:false);
			txtNombreImpresora.setText(propertiesDb.get("config.otros.nombre_impresora"));
		}
	}
	
	private String getDataBaseName() {
		//Es para poder ver mejor la seleccion del nombre y dejar el codigo un poco mas limpio
		String name = propertiesDb.get("database.type").equals("dbf")?
				DatabaseType.DBF.name:(propertiesDb.get("database.type").equals("postgres")?
						DatabaseType.POSTGRES.name:(propertiesDb.get("database.type").equals("mysql")?
								DatabaseType.MYSQL.name:(propertiesDb.get("database.type").equals("oracle")?
										DatabaseType.ORACLE.name:null)));
		return name;
	}
}
