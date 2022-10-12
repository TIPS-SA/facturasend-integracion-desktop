package views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Logs extends JDialog {
	private JPanel buttonPane;
	private JScrollPane contentPane;
	private JButton okButton;
	private JTextArea txtAreaLogs;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Logs dialog = new Logs();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			dialog.setLocationRelativeTo(null); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Logs() {
		setModal(true);
		setBounds(100, 100, 450, 300);
		initialize();
		events();
	}

	private void initialize() {
		getContentPane().setLayout(new BorderLayout());
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
		{
			contentPane = new JScrollPane();
			getContentPane().add(contentPane, BorderLayout.CENTER);
			{
				txtAreaLogs = new JTextArea();
				contentPane.setViewportView(txtAreaLogs);
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
}
