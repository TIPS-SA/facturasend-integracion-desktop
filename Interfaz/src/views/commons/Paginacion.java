package views.commons;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.FlowLayout;
import javax.swing.JTextField;

public class Paginacion extends JPanel {
	private JButton btnFirstPage;
	private JTextField txtPagina;
	private JButton btnAnterior;
	private JLabel lblPagina;
	private JLabel lblCantidadPaginas;
	private JButton btnSiguiente;
	private JButton btnLastPage;
	private int width;
	private int height;

	/**
	 * Create the panel.
	 */
	public Paginacion(int w, int h) {
		this.width=w;
		this.height=h;
		//para editar componente, comentar Este setPreferredSize
		//setPreferredSize(new  Dimension(width, height));
		init();	
	}
	
	private void init() {
		btnFirstPage = new JButton("");
		btnFirstPage.setIcon(new ImageIcon(Paginacion.class.getResource("/resources/2leftarrow.png")));
		
		btnAnterior = new JButton("");
		btnAnterior.setIcon(new ImageIcon(Paginacion.class.getResource("/resources/1leftarrow.png")));
		
		lblPagina = new JLabel("Pagina");
		
		txtPagina = new JTextField();
		txtPagina.setColumns(3);
		
		lblCantidadPaginas = new JLabel("De 10");
		
		btnSiguiente = new JButton("");
		btnSiguiente.setIcon(new ImageIcon(Paginacion.class.getResource("/resources/1rightarrow.png")));
		
		btnLastPage = new JButton("");
		btnLastPage.setIcon(new ImageIcon(Paginacion.class.getResource("/resources/2rightarrow.png")));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(9)
					.addComponent(btnFirstPage, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
					.addGap(5)
					.addComponent(btnAnterior, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
					.addGap(5)
					.addComponent(lblPagina, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
					.addGap(5)
					.addComponent(txtPagina, GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
					.addGap(5)
					.addComponent(lblCantidadPaginas, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
					.addGap(5)
					.addComponent(btnSiguiente, GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
					.addGap(5)
					.addComponent(btnLastPage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(10))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(btnFirstPage))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(btnAnterior))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(9)
					.addComponent(lblPagina))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(7)
					.addComponent(txtPagina, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(9)
					.addComponent(lblCantidadPaginas))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(btnSiguiente))
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(btnLastPage))
		);
		setLayout(groupLayout);

	}
}
