package views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.icepdf.ri.common.ComponentKeyBinding;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

import java.awt.event.ActionListener;
import java.io.InputStream;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;

public class ShowKUDEDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JPanel buttonPane;
	private JScrollPane scrollPane;
	private static InputStream pdf;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ShowKUDEDialog dialog = new ShowKUDEDialog(pdf);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ShowKUDEDialog(InputStream pdf) {
		this.pdf = pdf;
		setModal(true);
		setBounds(100, 100, 800, 600);
		init();
		events();
		openPdf(pdf);
	}
	
	private void init() {
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
		}
		{
			buttonPane = new JPanel();
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
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	private void openPdf(InputStream is){
		  
	    try {
	           SwingController control=new SwingController();
	            SwingViewBuilder factry=new SwingViewBuilder(control);
	            JPanel veiwerCompntpnl=factry.buildViewerPanel();
	            ComponentKeyBinding.install(control, veiwerCompntpnl);
	            control.getDocumentViewController().setAnnotationCallback(
	                    new org.icepdf.ri.common.MyAnnotationCallback(
	                    control.getDocumentViewController()));
		                   control.openDocument(is, "Descripcion", "path");
		                   scrollPane.setViewportView(veiwerCompntpnl); 
	        } catch (Exception ex) {
	            JOptionPane.showMessageDialog(this,"No se pudo visualizar el PDF");
	        }
	}

}
