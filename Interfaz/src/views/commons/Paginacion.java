package views.commons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Paginacion extends JPanel {
    private List<IPaginacionListener> listeners = new ArrayList<IPaginacionListener>();

	private JButton btnFirstPage;
	private JTextField txtPagina;
	private JButton btnAnterior;
	private JLabel lblPagina;
	private JLabel lblCantidadPaginas;
	private JButton btnSiguiente;
	private JButton btnLastPage;
	private int width;
	private int height;

	private Integer currentPage;

	private Integer rowsPerPage = 10;
	private Integer pages;	//Total de Páginas, calculado de acuerdo al total de registros / rowsPerPage
	private Integer total;
	
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
		txtPagina.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if(e.getKeyCode() == KeyEvent.VK_ENTER){
	               setCurrentPage(Integer.valueOf(txtPagina.getText()));
	            }
	        }

	    });
		lblCantidadPaginas = new JLabel("De " + pages);
		
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

		btnFirstPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentPage = 1;
				setCurrentPage(currentPage);
			}
		});
		btnAnterior.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentPage > 1) {
					currentPage --;	
				}
				
				setCurrentPage(currentPage);
			}
		});
		btnSiguiente.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentPage++;
				setCurrentPage(currentPage);
			}
		});
		btnLastPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentPage = pages;
				setCurrentPage(currentPage);
			}
		});
	}
	
	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
		txtPagina.setText(this.currentPage + "");
		this.listeners.stream().forEach( l -> {l.goTo(currentPage);} );
	}

	public void refresh() {
		setCurrentPage(this.currentPage);
	}
	
	public Integer getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(Integer rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		Integer calculateTotalPages = (Integer) (total / rowsPerPage);
		
		if (total.doubleValue() % rowsPerPage.doubleValue() != 0) {
			calculateTotalPages ++;	//Si la DIV no es entera, agrega mas una pagina
		}
		this.total = total;
		this.pages = calculateTotalPages;
		this.lblCantidadPaginas.setText("de " + pages);
	}

	public void addActionListener(IPaginacionListener paginacionListener) {
		listeners.add(paginacionListener);
	}

	public List<IPaginacionListener> getListeners() {
		return listeners;
	}
}



