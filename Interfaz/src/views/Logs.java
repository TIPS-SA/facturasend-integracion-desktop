package views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import service.ConfigProperties;

import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.awt.event.ActionEvent;

public class Logs extends JDialog {
	private JPanel buttonPane;
	private JScrollPane contentPane;
	private JButton okButton;
	private JTextArea txtAreaLogs;
	private KeyboardFocusManager kb;
	private ConfigProperties cp = new ConfigProperties();

	/**
	 * Launch the application.
	 * commit
	 */
	public static void main(String[] args) {
		try {
			Logs dialog = new Logs();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			dialog.setLocationRelativeTo(null); 
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Ocurrio un problema inesperado\n"+e);
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Logs() {
		setModal(true);
		setBounds(100, 100, 650, 500);
		initialize();
		events();
	}

	private void initialize() {
		kb = KeyboardFocusManager.getCurrentKeyboardFocusManager();
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
				txtAreaLogs.setEditable(false);
				txtAreaLogs.setLineWrap(true);
				txtAreaLogs.setText(cp.readLogProperties());
				contentPane.setViewportView(txtAreaLogs);
			}
		}
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

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
}
