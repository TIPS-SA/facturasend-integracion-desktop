package views.commons;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ImageIcon;

public class Paginacion{

	private JPanel panel;
	private JPanel contentPane;
	private JButton btnAnterior;
	private JButton btnFirstPage;
	private JTextField lblPaginaNumero;
	private JButton btnLastPage;
	private JButton btnSiguiente;
	private JLabel lblCantidadpaginas;
	private JLabel lblPagina;
	private int width;
	private int heigth;
	public Paginacion(int w, int h) {
		this.width=w;
		this.heigth=h;
		init();
	}
	
	public JPanel getPaginacion() {
		return this.panel;
	}
	
	public void init() {
//		contentPane = new JPanel();
//		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//		contentPane.setLayout(new BorderLayout(0, 0));
		//setContentPane(contentPane);
		
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(width,heigth));
		//contentPane.add(panel, BorderLayout.CENTER);
		
		btnFirstPage = new JButton("");
		btnFirstPage.setIcon(new ImageIcon(Paginacion.class.getResource("/resources/2leftarrow.png")));
		
		btnAnterior = new JButton("");
		btnAnterior.setIcon(new ImageIcon(Paginacion.class.getResource("/resources/1leftarrow.png")));
		
		lblPagina = new JLabel("Pagina");
		
		lblPaginaNumero = new JTextField();
		lblPaginaNumero.setColumns(10);
		
		lblCantidadpaginas = new JLabel("de 10");
		
		btnSiguiente = new JButton("");
		btnSiguiente.setIcon(new ImageIcon(Paginacion.class.getResource("/resources/1rightarrow.png")));
		
		btnLastPage = new JButton("");
		btnLastPage.setIcon(new ImageIcon(Paginacion.class.getResource("/resources/2rightarrow.png")));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnFirstPage, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAnterior, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPagina, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblPaginaNumero, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblCantidadpaginas, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnSiguiente, GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnLastPage, GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
					.addGap(10))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnFirstPage)
						.addComponent(btnAnterior)
						.addComponent(lblPagina)
						.addComponent(lblPaginaNumero, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblCantidadpaginas)
						.addComponent(btnSiguiente)
						.addComponent(btnLastPage))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
	}
}
