package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import com.google.gson.Gson;

import service.FacturasendService;
import util.HttpUtil;
import views.commons.JComboCheckBox;
import views.commons.Paginacion;
import views.commons.PaginacionListener;
import views.commons.Paginacion;

import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import javax.swing.border.EtchedBorder;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JSeparator;

public class Principal extends JFrame {

	private static Gson gson = new Gson();

	private int filas;
	private int alto;
	private JScrollBar barra;
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
	private JTable jTableTransaction;
	private JPanel paneCenter;
	private JPanel paneSouthTable;
	private JPanel tableBtns;
	private JButton btnSiguiente;
	private JButton btnAnterior;
	private JButton btnFacturaImportacion;
	private JPanel paneSouthTableCenter;
	InfoMovimiento movDetails;
	Paginacion paginacion;
	FacturasendService fs;
	
	private Integer rowsPerPage = 10;
	private Integer tipoDocumento = 1;
	private JMenuBar menuBar;
	private JMenu mnFiltrosDeTabla;
	private JCheckBoxMenuItem chkMenuMovNro;
	private JCheckBoxMenuItem chkMenuCliente;
	private JCheckBoxMenuItem chkMenuFecha;
	private JCheckBoxMenuItem chkMenuNroFactura;
	private JCheckBoxMenuItem chkMenuMoneda;
	private JCheckBoxMenuItem chkMenuTotal;
	private JCheckBoxMenuItem chkMenuEstado;
	private JCheckBoxMenuItem chkMenuCdc;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Principal window = new Principal();
					window.frmFacturaSend.setVisible(true);
					window.setLocationRelativeTo(null); 
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
		fs = new FacturasendService();
		initialize();
		events();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		if (FacturasendService.readDBProperties().get("database.rows_per_page") != null) {
			rowsPerPage = Integer.valueOf(FacturasendService.readDBProperties().get("database.rows_per_page")+"");	
		}
		
		frmFacturaSend = new JFrame();
		frmFacturaSend.setTitle("Factura Send - Integration Tool");
		frmFacturaSend.setBounds(0, 0, 1024, 680);
		frmFacturaSend.setMinimumSize(new Dimension(1024,680));
		frmFacturaSend.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		paneSouth = new JPanel();
		paneSouth.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		paneSouth.setPreferredSize(new Dimension(150,60));
		frmFacturaSend.getContentPane().add(paneSouth, BorderLayout.SOUTH);
		
		btnLogs = new JButton("Logs");
		btnLogs.setIcon(new ImageIcon(Principal.class.getResource("/resources/kterm.png")));
		btnLogs.setPreferredSize(new Dimension(15, 15));
		
		btnCondiguracion = new JButton("Configuracion");
		btnCondiguracion.setIcon(new ImageIcon(Principal.class.getResource("/resources/agt_softwareD.png")));
		
		btnNoEnviar = new JButton("Pausar/Enviar");
		btnNoEnviar.setIcon(new ImageIcon(Principal.class.getResource("/resources/agt_stop.png")));
		
		
		btnReintegrar = new JButton("Reintegrar");
		btnReintegrar.setIcon(new ImageIcon(Principal.class.getResource("/resources/reload.png")));
		
		
		btnVerXml = new JButton("Ver XML");
		btnVerXml.setIcon(new ImageIcon(Principal.class.getResource("/resources/txt.png")));
		
		
		btnVerkude = new JButton("verKUDE");
		btnVerkude.setIcon(new ImageIcon(Principal.class.getResource("/resources/pdf.png")));
		
		
		btnEnviarEmail = new JButton("Enviar Email");
		btnEnviarEmail.setIcon(new ImageIcon(Principal.class.getResource("/resources/folder_outbox.png")));
		GroupLayout gl_paneSouth = new GroupLayout(paneSouth);
		gl_paneSouth.setHorizontalGroup(
			gl_paneSouth.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_paneSouth.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnLogs, GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnCondiguracion, GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNoEnviar, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnReintegrar, GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnVerXml, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnVerkude, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnEnviarEmail, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(19))
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
		
		
		btnBuscar = new JButton("");
		btnBuscar.setIcon(new ImageIcon(Principal.class.getResource("/resources/search.png")));
		
		
		
		btnFacturas = new JButton("Facturas");
		
		btnFacturas.setIcon(new ImageIcon(Principal.class.getResource("/resources/FacturaElectronica.png")));
		btnFacturas.setToolTipText("Facturas");
		
		
		btnAutoFactura = new JButton("<html><p>Auto</p>Factura<p></p></html>");
		btnAutoFactura.setIcon(new ImageIcon(Principal.class.getResource("/resources/AutoFactura.png")));
		btnAutoFactura.setToolTipText("Auto Factura");
		btnAutoFactura.setPreferredSize(new Dimension(50,30));
		
		
		btnNotaCredito = new JButton("<html><p>Nota</p><p>Credito</p></html>");
		btnNotaCredito.setIcon(new ImageIcon(Principal.class.getResource("/resources/NotaCredito.png")));
		btnNotaCredito.setToolTipText("");
		
		
		btnNotaDebito = new JButton("<html><p>Nota</p><p>Debito</p></html>");
		btnNotaDebito.setIcon(new ImageIcon(Principal.class.getResource("/resources/NotaDebito.png")));
		btnNotaDebito.setToolTipText("");
		
		
		btnRemision = new JButton("<html><p>Nota</p><p>Remision</p></html>");
		btnRemision.setIcon(new ImageIcon(Principal.class.getResource("/resources/NotaRemision.png")));
		btnRemision.setToolTipText("Nota de Remision");
		
		
		JButton btnFacturaExportacion = new JButton("<html><p>Nota</p><p>Exportacion</p></html>");
		btnFacturaExportacion.setIcon(new ImageIcon(Principal.class.getResource("/resources/NotaExportacion.png")));
		btnFacturaExportacion.setToolTipText("Factura Exportacion");
		btnFacturaExportacion.setEnabled(false);
		
		btnFacturaImportacion = new JButton("<html><p>Nota</p>Importacion<p></p></html>");
		btnFacturaImportacion.setIcon(new ImageIcon(Principal.class.getResource("/resources/NotaImportacion.png")));
		btnFacturaImportacion.setToolTipText("");
		btnFacturaImportacion.setEnabled(false);
		GroupLayout gl_paneNorth = new GroupLayout(paneNorth);
		gl_paneNorth.setHorizontalGroup(
			gl_paneNorth.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneNorth.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneNorth.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_paneNorth.createSequentialGroup()
							.addComponent(btnFacturas, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnFacturaExportacion, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnFacturaImportacion, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAutoFactura, GroupLayout.PREFERRED_SIZE, 136, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNotaCredito, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNotaDebito, GroupLayout.PREFERRED_SIZE, 140, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnRemision, GroupLayout.PREFERRED_SIZE, 140, Short.MAX_VALUE))
						.addGroup(gl_paneNorth.createSequentialGroup()
							.addComponent(lblBuscar)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfBuscar, GroupLayout.DEFAULT_SIZE, 885, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnBuscar)))
					.addContainerGap())
		);
		gl_paneNorth.setVerticalGroup(
			gl_paneNorth.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneNorth.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneNorth.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_paneNorth.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblBuscar)
							.addComponent(tfBuscar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(btnBuscar))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_paneNorth.createParallelGroup(Alignment.LEADING)
						.addComponent(btnFacturas, GroupLayout.PREFERRED_SIZE, 44, Short.MAX_VALUE)
						.addComponent(btnAutoFactura, GroupLayout.PREFERRED_SIZE, 44, Short.MAX_VALUE)
						.addComponent(btnFacturaExportacion, GroupLayout.PREFERRED_SIZE, 44, Short.MAX_VALUE)
						.addComponent(btnRemision, GroupLayout.PREFERRED_SIZE, 44, Short.MAX_VALUE)
						.addComponent(btnNotaDebito, GroupLayout.PREFERRED_SIZE, 44, Short.MAX_VALUE)
						.addComponent(btnNotaCredito, GroupLayout.PREFERRED_SIZE, 44, Short.MAX_VALUE)
						.addComponent(btnFacturaImportacion, GroupLayout.PREFERRED_SIZE, 44, Short.MAX_VALUE))
					.addContainerGap())
		);
		paneNorth.setLayout(gl_paneNorth);
		
		FacturasendService fs = new FacturasendService();
		
		filas = 8;//para que se pueda tener como opcion tambien
		
		paneCenter = new JPanel();
		paneCenter.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		frmFacturaSend.getContentPane().add(paneCenter, BorderLayout.CENTER);
		paneCenter.setLayout(new BorderLayout(0, 0));
		
		jTableTransaction = new JTable();
		//fs.cargar_tabla(table);
		
		scrollPaneCenter = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		paneCenter.add(scrollPaneCenter, BorderLayout.CENTER);
		scrollPaneCenter.setPreferredSize(new Dimension(jTableTransaction.getPreferredSize().width, jTableTransaction.getRowHeight()*filas));
		
		scrollPaneCenter.setViewportView(jTableTransaction);
		
//		tableBtns = new JPanel();
//		tableBtns.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
//		tableBtns.setPreferredSize(new Dimension(100,35));
		paneSouthTable = new JPanel();
		paneSouthTable.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		paneSouthTable.setLayout(new BorderLayout(0, 0));
		
		paginacion = new Paginacion(365, 45);
		paginacion.addActionListener(new PaginacionListener() {
			@Override
			public void goTo(Integer currentPage) {
				paginacion.setTotal(fs.populateTransactionTable(jTableTransaction, tfBuscar.getText(), tipoDocumento, currentPage, rowsPerPage));
			}
		});
		paginacion.setRowsPerPage(rowsPerPage);
		paginacion.setCurrentPage(1);	//Ejecuta la Consulta
		//paginacion.setTotal(100);

		paneCenter.add(paneSouthTable, BorderLayout.SOUTH);
		paneSouthTable.add(paginacion, BorderLayout.EAST);
		
		menuBar = new JMenuBar();
		frmFacturaSend.setJMenuBar(menuBar);
		
		mnFiltrosDeTabla = new JMenu("Filtros de Tabla");
		menuBar.add(mnFiltrosDeTabla);
		
		chkMenuMovNro = new JCheckBoxMenuItem("Mov #");
		
		
		mnFiltrosDeTabla.add(chkMenuMovNro);
		
		JSeparator separator = new JSeparator();
		mnFiltrosDeTabla.add(separator);
		
		chkMenuFecha = new JCheckBoxMenuItem("Fecha");
		mnFiltrosDeTabla.add(chkMenuFecha);
		
		JSeparator separator_1 = new JSeparator();
		mnFiltrosDeTabla.add(separator_1);
		
		chkMenuCliente = new JCheckBoxMenuItem("Cliente");
		mnFiltrosDeTabla.add(chkMenuCliente);
		
		JSeparator separator_2 = new JSeparator();
		mnFiltrosDeTabla.add(separator_2);
		
		chkMenuNroFactura = new JCheckBoxMenuItem("Nro. Factura");
		mnFiltrosDeTabla.add(chkMenuNroFactura);
		
		JSeparator separator_3 = new JSeparator();
		mnFiltrosDeTabla.add(separator_3);
		
		chkMenuMoneda = new JCheckBoxMenuItem("Moneda");
		mnFiltrosDeTabla.add(chkMenuMoneda);
		
		JSeparator separator_4 = new JSeparator();
		mnFiltrosDeTabla.add(separator_4);
		
		chkMenuTotal = new JCheckBoxMenuItem("Total");
		mnFiltrosDeTabla.add(chkMenuTotal);
		
		JSeparator separator_5 = new JSeparator();
		mnFiltrosDeTabla.add(separator_5);
		
		chkMenuEstado = new JCheckBoxMenuItem("Estado");
		mnFiltrosDeTabla.add(chkMenuEstado);
		
		JSeparator separator_6 = new JSeparator();
		mnFiltrosDeTabla.add(separator_6);
		
		chkMenuCdc = new JCheckBoxMenuItem("CDC");
		mnFiltrosDeTabla.add(chkMenuCdc);
		
	}
	
	private void events() {
		chkMenuMovNro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chkMenuMovNro.isSelected()) {
					ocultarColumnas(jTableTransaction, 0);
				}else {
					mostrarColumnas(jTableTransaction, 0);
				}
			}
		});
		chkMenuFecha.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chkMenuFecha.isSelected()) {
					ocultarColumnas(jTableTransaction, 1);
				}else {
					mostrarColumnas(jTableTransaction, 1);
				}
			}
		});
		chkMenuCliente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chkMenuCliente.isSelected()) {
					ocultarColumnas(jTableTransaction, 2);
				}else {
					mostrarColumnas(jTableTransaction, 2);
				}
			}
		});
		chkMenuNroFactura.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chkMenuNroFactura.isSelected()) {
					ocultarColumnas(jTableTransaction, 3);
				}else {
					mostrarColumnas(jTableTransaction, 3);
				}
			}
		});
		chkMenuMoneda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chkMenuMoneda.isSelected()) {
					ocultarColumnas(jTableTransaction, 4);
				}else {
					mostrarColumnas(jTableTransaction, 4);
				}
			}
		});
		chkMenuTotal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chkMenuTotal.isSelected()) {
					ocultarColumnas(jTableTransaction, 5);
				}else {
					mostrarColumnas(jTableTransaction, 5);
				}
			}
		});
		chkMenuEstado.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chkMenuEstado.isSelected()) {
					ocultarColumnas(jTableTransaction, 6);
				}else {
					mostrarColumnas(jTableTransaction, 6);
				}
			}
		});
		chkMenuCdc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chkMenuCdc.isSelected()) {
					ocultarColumnas(jTableTransaction, 7);
				}else {
					mostrarColumnas(jTableTransaction, 7);
				}
			}
		});
		
		btnNoEnviar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Integer[] transacciones = new Integer[] {1,2};
					fs.pausarIniciar(transacciones);	
				} catch (Exception e2) {
					System.out.println("Mostrar error en pantalla, " + e2);
				}
			}
		});

		
		btnReintegrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					fs.iniciarIntegracion(tipoDocumento);	
				} catch (Exception e2) {
					System.out.println("Mostrar error en pantalla, " + e2);
				}
				
			}
		});
		
		tfBuscar.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if(e.getKeyCode() == KeyEvent.VK_ENTER){
	               paginacion.setCurrentPage(1);
	            }
	        }

	    });
		
		btnBuscar.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
               paginacion.setCurrentPage(1);
	        }

	    });
		
		btnVerkude.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Generar JSON de documentos electronicos.

				String cdc = "";

				int row = jTableTransaction.getSelectedRow();
				if (jTableTransaction.getSelectedRow() >= 0) {
					DefaultTableModel model = (DefaultTableModel) jTableTransaction.getModel();
					
	                cdc = (String) model.getValueAt(row, 7);
	
	                List<Map<String, Object>> deList = new ArrayList<Map<String,Object>>();
					Map data = new HashMap();
					data.put("type", "base64");
					data.put("cdcList", deList);
					
					Map cdcMap = new HashMap();
					cdcMap.put("cdc", cdc);
	
					deList.add(cdcMap);
					
					Map header = new HashMap();
					header.put("Authorization", "Bearer api_key_" + FacturasendService.readDBProperties().get("facturasend.token"));
					String url = FacturasendService.readDBProperties().get("facturasend.url")+"";
					url += "/de/pdf";
					
					try {
						Map<String, Object> resultadoJson = HttpUtil.invocarRest(url, "POST", gson.toJson(data), header);
						
						//probar con un pdf fijo, del folder
						if (resultadoJson != null) {
							if (Boolean.valueOf(resultadoJson.get("success") + "") == true) {

								ByteArrayOutputStream out = new ByteArrayOutputStream();

								byte[] decoder = Base64.getDecoder().decode(resultadoJson.get("value") + "");

								out.write(decoder);

								ByteArrayInputStream inStream = new ByteArrayInputStream(out.toByteArray());

								ShowKUDEDialog showPdf = new ShowKUDEDialog(inStream);
								showPdf.setVisible(true);
							}
	
						}
											
					} catch (Exception e2) {
						// TODO: handle exception
						e2.printStackTrace();
					}
				}
			}
		});
		
		btnVerXml.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String cdc = "";

				int row = jTableTransaction.getSelectedRow();
				if (jTableTransaction.getSelectedRow() >= 0) {
	                DefaultTableModel model = (DefaultTableModel) jTableTransaction.getModel();
	                
	                cdc = (String) model.getValueAt(row, 7);
	                
					Map header = new HashMap();
					header.put("Authorization", "Bearer api_key_" + FacturasendService.readDBProperties().get("facturasend.token"));
					String url = FacturasendService.readDBProperties().get("facturasend.url")+"";
					url += "/de/xml/" + cdc;
					
					try {
						Map<String, Object> resultadoJson = HttpUtil.invocarRest(url, "GET", null, header);
						
						if (resultadoJson != null) {
							if (Boolean.valueOf(resultadoJson.get("success")+"") == true) {
								
								//Imlementar el dialog para ver el xml 
								String xml = resultadoJson.get("value")+"";
								ShowXMLDialog xmlView = new ShowXMLDialog(xml);
								xmlView.setVisible(true);
							} else {
								//Lucas mostrar error en pantalla
								System.out.println("error" + resultadoJson.get("error")+"");
							}
		
						}
					} catch (Exception e2) {
						//Lucas Mostrar error e npantalla
						e2.printStackTrace();
					}
					
				}
			}
		});
		
		jTableTransaction.addMouseListener(new java.awt.event.MouseAdapter() {
			 public void mouseClicked(java.awt.event.MouseEvent evt) {
				 if(evt.getClickCount()>1) {
					//obtener la fila
		            int row = jTableTransaction.getSelectedRow();
	                DefaultTableModel model = (DefaultTableModel) jTableTransaction.getModel();
	                BigDecimal transaccionId = (BigDecimal) model.getValueAt(row, 0);
	                movDetails = new InfoMovimiento(transaccionId);
	                movDetails.setVisible(true);
				 }
		    }
		});

		btnLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Logs logsDialog = new Logs();
				logsDialog.setVisible(true);
			}
		});

		btnCondiguracion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config confView = new Config();
				confView.setVisible(true);
			}
		});
		btnFacturas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton("Factura");
				tipoDocumento = 1;
				getPaginacion().setCurrentPage(1);
				//paginacion.setTotal(fs.populateTable(table, tfBuscar.getText(), tipoDocumento, getPaginacion().getCurrentPage(), rowsPerPage));
			}
		});
		btnAutoFactura.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton("Auto Factura");
				tipoDocumento = 4;
				getPaginacion().setCurrentPage(1);
				//paginacion.setTotal(fs.populateTable(table, tfBuscar.getText(), tipoDocumento, getPaginacion().getCurrentPage(), rowsPerPage));
			}
		});
		btnNotaCredito.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton("Nota Credito");
				tipoDocumento = 5;
				getPaginacion().setCurrentPage(1);
				//paginacion.setTotal(fs.populateTable(table, tfBuscar.getText(), tipoDocumento, getPaginacion().getCurrentPage(), rowsPerPage));
			}
		});
		btnNotaDebito.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton("Nota Debito");
				tipoDocumento = 6;
				getPaginacion().setCurrentPage(1);
				//paginacion.setTotal(fs.populateTable(table, tfBuscar.getText(), tipoDocumento, getPaginacion().getCurrentPage(), rowsPerPage));
			}
		});
		btnRemision.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedButton("Nota Remision");
				tipoDocumento = 7;
				getPaginacion().setCurrentPage(1);
				//paginacion.setTotal(fs.populateTable(table, tfBuscar.getText(), tipoDocumento, getPaginacion().getCurrentPage(), rowsPerPage));
			}
		});
	}
	
	private void selectedButton(String  btn) {
		switch (btn) {
		case "Factura":
			btnFacturas.setBorder(BorderFactory.createLoweredBevelBorder());
			btnAutoFactura.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnNotaCredito.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnNotaDebito.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnRemision.setBorder(BorderFactory.createLineBorder(Color.gray));
			break;
		case "Auto Factura":
			btnAutoFactura.setBorder(BorderFactory.createLoweredBevelBorder());
			btnFacturas.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnNotaCredito.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnNotaDebito.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnRemision.setBorder(BorderFactory.createLineBorder(Color.gray));
			break;
		case "Nota Credito":
			btnNotaCredito.setBorder(BorderFactory.createLoweredBevelBorder());
			btnAutoFactura.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnFacturas.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnNotaDebito.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnRemision.setBorder(BorderFactory.createLineBorder(Color.gray));
			break;
		case "Nota Debito":
			btnNotaDebito.setBorder(BorderFactory.createLoweredBevelBorder());
			btnAutoFactura.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnNotaCredito.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnFacturas.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnRemision.setBorder(BorderFactory.createLineBorder(Color.gray));
			break;
		case "Nota Remision":
			btnRemision.setBorder(BorderFactory.createLoweredBevelBorder());
			btnAutoFactura.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnNotaCredito.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnNotaDebito.setBorder(BorderFactory.createLineBorder(Color.gray));
			btnFacturas.setBorder(BorderFactory.createLineBorder(Color.gray));
			break;

		default:
			break;
		}
	}

	public Paginacion getPaginacion() {
		return paginacion;
	}
	
	public void ocultarColumnas (JTable table, int col) {
		table.getColumnModel().getColumn(col).setMaxWidth(0);
		table.getColumnModel().getColumn(col).setMinWidth(0);
		table.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(0);
		table.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
	}
	
	public void mostrarColumnas(JTable table, int col) {
		int colPreferredWidth  =  col == 0?60:col==1?80:col==2?280:col==3?65:col==4?50:col==5?65:col ==6?80:100;
		int colHeaderPreferredWidth = col == 0?60:col==1?80:col==2?280:col==3?65:col==4?50:col==5?65:col ==6?80:100;
		
		
		table.getColumnModel().getColumn(col).setMaxWidth(200);
		table.getColumnModel().getColumn(col).setMinWidth(0);
		table.getColumnModel().getColumn(col).setPreferredWidth(colPreferredWidth);
		table.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(200);
		table.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
		table.getTableHeader().getColumnModel().getColumn(col).setPreferredWidth(colHeaderPreferredWidth);

	}
}
