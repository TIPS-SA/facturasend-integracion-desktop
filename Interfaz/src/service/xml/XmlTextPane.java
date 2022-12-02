package service.xml;

import javax.swing.JTextPane;

public class XmlTextPane extends JTextPane {

    private static final long serialVersionUID = 6270183148379328084L;

    public XmlTextPane() {

        // Set editor kit
        this.setEditorKitForContentType("text/xml", new XmlEditorKit());
        this.setContentType("text/xml");
    }

}