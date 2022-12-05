package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import service.xml.XmlTextPane;

import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.awt.event.ActionEvent;

public class ShowXMLDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private static String xml;
	private JButton okButton;
	private JPanel buttonPane;
	private JScrollPane scrollPane;
	private JTextArea txtAXML;
	private XmlTextPane xmlTextPane;
	private KeyboardFocusManager kb;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ShowXMLDialog dialog = new ShowXMLDialog(xml);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Ocurrio un problema inesperado\n"+e);
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ShowXMLDialog(String xml) {
		setModal(true);
		this.xml = xml;
		setBounds(100, 100, 500, 650);
		init(xml);
		events();
	}
	
	private void init(String xml) {
		kb = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				txtAXML = new JTextArea();
				txtAXML.setLineWrap(true);
				txtAXML.setEditable(false);
				
				
				txtAXML.setText(toPrettyString(xml,2));
				
				xmlTextPane = new XmlTextPane();
				xmlTextPane.setEditable(false);
				xmlTextPane.setBackground(new Color(238, 238, 238));
				xmlTextPane.setText(toPrettyString(xml,2));
				
				scrollPane.setViewportView(xmlTextPane);
			}
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
	
	public static String toPrettyString(String xml, int indent) {
	    try {
	        // Turn xml string into a document
	        Document document = DocumentBuilderFactory.newInstance()
	                .newDocumentBuilder()
	                .parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

	        // Remove whitespaces outside tags
	        document.normalize();
	        XPath xPath = XPathFactory.newInstance().newXPath();
	        NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
	                                                      document,
	                                                      XPathConstants.NODESET);

	        for (int i = 0; i < nodeList.getLength(); ++i) {
	            Node node = nodeList.item(i);
	            node.getParentNode().removeChild(node);
	        }

	        // Setup pretty print options
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", indent);
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

	        // Return pretty print xml string
	        StringWriter stringWriter = new StringWriter();
	        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
	        return stringWriter.toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}
}
