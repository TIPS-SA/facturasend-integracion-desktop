package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import service.FacturasendService;

import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class InfoMovimiento extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private static BigDecimal transaccionId;
	private JButton okButton;
	private JTable table;
	private FacturasendService fs;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			InfoMovimiento dialog = new InfoMovimiento(transaccionId);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public InfoMovimiento(BigDecimal transaccionId) {
		this.transaccionId = transaccionId;

		setModal(true);
		setBounds(100, 100, 800, 410);
		setMinimumSize(new Dimension(800,410));
		setTitle("Detalles del Movimiento #"+transaccionId);
		initialize(transaccionId);
		events();
		
	}

	private void initialize(BigDecimal nroMov) {
		fs = new FacturasendService();
		// TODO Auto-generated method stub
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				table = new JTable();
				fs.populateTransactionDetailsTable(table, nroMov);
				scrollPane.setViewportView(table);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	private void events() {
		// TODO Auto-generated method stub
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}

}
