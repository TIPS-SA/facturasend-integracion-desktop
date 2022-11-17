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
import javax.swing.LayoutStyle.ComponentPlacement;

public class Paginacion extends JPanel {
    private List<IPaginacionListener> listeners = new ArrayList<IPaginacionListener>();

	private JButton btnFirstPage;
	private JButton btnAnterior;
	private JTextField txtPagina;
	private JLabel lblPagina;
	private JLabel lblCantidadPaginas;
	private JButton btnSiguiente;
	private JButton btnLastPage;
	
	private JButton btnReload;
	
	private JLabel lblRegistrosPorPagina;
	
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
		
		btnReload = new JButton("");
		btnReload.setIcon(new ImageIcon(Paginacion.class.getResource("/resources/icons8-update-left-rotation-16.png")));
		
		lblRegistrosPorPagina = new JLabel("");
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnFirstPage, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAnterior, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					.addGap(1)
					.addComponent(lblPagina, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addComponent(txtPagina, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addComponent(lblCantidadPaginas, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
					.addGap(6)
					.addComponent(btnSiguiente, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnLastPage, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnReload, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(lblRegistrosPorPagina)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblRegistrosPorPagina, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
						.addComponent(btnReload, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
						.addComponent(btnLastPage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnAnterior, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnFirstPage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnSiguiente, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblPagina)
							.addComponent(txtPagina, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblCantidadPaginas)))
					.addGap(1))
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
					setCurrentPage(currentPage);
				}
				
				
			}
		});
		btnSiguiente.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentPage < pages) {
					currentPage++;
					setCurrentPage(currentPage);
				}
			}
		});
		btnLastPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentPage = pages;
				setCurrentPage(currentPage);
			}
		});
		btnReload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
	}
	
	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
		txtPagina.setText(this.currentPage + "");
		Integer desde = (this.currentPage == 1 ? this.currentPage : (this.currentPage * getRowsPerPage()));
		lblRegistrosPorPagina.setText( desde + " a " + (desde-1 + getRowsPerPage()) + " de " + getTotal());
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
		
		Integer desde = (this.currentPage == 1 ? this.currentPage : ( ((this.currentPage-1) * getRowsPerPage())+1) );
		lblRegistrosPorPagina.setText( desde + " a " + (desde + getRowsPerPage()-1) + " de " + getTotal());

	}

	public void addActionListener(IPaginacionListener paginacionListener) {
		listeners.add(paginacionListener);
	}

	public List<IPaginacionListener> getListeners() {
		return listeners;
	}
}



