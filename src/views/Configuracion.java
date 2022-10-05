package views;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

public class Configuracion extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Configuracion frame = new Configuracion();
					frame.setVisible(true);
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
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 589, 423);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel paneBaseDatos = new JPanel();
		tabbedPane.addTab("Base de Datos", null, paneBaseDatos, null);
		
		JLabel lblTipo = new JLabel("Tipo:");
		
		JLabel lblHost = new JLabel("Host:");
		
		JLabel lblPuerto = new JLabel("Puerto");
		GroupLayout gl_paneBaseDatos = new GroupLayout(paneBaseDatos);
		gl_paneBaseDatos.setHorizontalGroup(
			gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneBaseDatos.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTipo)
						.addComponent(lblHost)
						.addComponent(lblPuerto))
					.addContainerGap(498, Short.MAX_VALUE))
		);
		gl_paneBaseDatos.setVerticalGroup(
			gl_paneBaseDatos.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_paneBaseDatos.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblTipo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblHost)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPuerto)
					.addContainerGap(279, Short.MAX_VALUE))
		);
		paneBaseDatos.setLayout(gl_paneBaseDatos);
		
		JPanel paneFacturasend = new JPanel();
		tabbedPane.addTab("FacturaSend", null, paneFacturasend, null);
	}
}
