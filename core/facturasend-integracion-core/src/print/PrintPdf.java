package print;
/**
 *
 * @author Jaime Alcides
 */
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

/**
 * Cuando se va a ejecutar desde el Eclipse, pasar éste argumento entre comillas:
 * Para ticket
 * "tipscloudPrintDirectly:// http://192.168.88.17:8081/TIPSCLOUD-WEB/imprimirTicketComun.action?venta.numero=67089&downloadDirectly=false&timbradoFacturacionMovimiento.ingresoEgresoNumero=67089&printer=TICKET&dk=false&virtualContext=carlitos&user=tips&translateX=15&translateY=12&scaleX=0.95&scaleY=0.95&cutter=29,86,0,48&dkc=27,112,48,55,121&format=TXT&"
 * 
 * Para factura 
 * "tipscloudPrintDirectly:// http://192.168.88.17:8081/TIPSCLOUD-WEB/imprimirFacturaLegalVenta.action?venta.numero=9569&downloadDirectly=false&format=PDF&timbradoFacturacionMovimiento.ingresoEgresoNumero=9569&timbradoFacturacionMovimiento.timbradoFacturacion.numero=13&timbradoFacturacionMovimiento.timbradoFacturacion.sucursalTimbrado=1&timbradoFacturacionMovimiento.timbradoFacturacion.puntoTimbrado=1&timbradoFacturacionMovimiento.numeracion=1144&timbradoFacturacionMovimiento.tipoFactura=0&timbradoFacturacionMovimiento.fecha=2018-05-02T00:00:00&timbradoFacturacionMovimiento.razonSocial=FLAVORS%20OF%20AMERICAS&timbradoFacturacionMovimiento.documento=80084022-4&timbradoFacturacionMovimiento.direccion=&timbradoFacturacionMovimiento.telefono=&printer=Microsoft%20XPS%20Document%20Writer&virtualContext=donruben&user=tips&scale=0.32"
 * http://192.168.0.50:8080/tipscloud/imprimirFacturaLegalVenta.action?venta.numero=72192&downloadDirectly=false&timbradoFacturacionMovimiento.ingresoEgresoNumero=72192&timbradoFacturacionMovimiento.timbradoFacturacion.numero=28&timbradoFacturacionMovimiento.timbradoFacturacion.sucursalTimbrado=1&timbradoFacturacionMovimiento.timbradoFacturacion.puntoTimbrado=1&timbradoFacturacionMovimiento.numeracion=23010&timbradoFacturacionMovimiento.tipoFactura=0&timbradoFacturacionMovimiento.fecha=2019-05-23T00:00:00&timbradoFacturacionMovimiento.razonSocial=FLAVORS%20OF%20AMERICAS%20S.A&timbradoFacturacionMovimiento.documento=80084022-4&timbradoFacturacionMovimiento.direccion=&timbradoFacturacionMovimiento.telefono=&timbradoFacturacionMovimiento.moneda.codigo=2&printer=FACTURA&dk=true&virtualContext=carlitos&user=dani&translateX=15&translateY=12&scaleX=0.95&scaleY=0.95&cutter=29,86,0,48&dkc=27,112,48,55,121&format=PDF&
 * 
 * Como instalar/utilizar
 * - Instalar el .exe en C:\Program Files\tipscloudPrintDirectly\tipscloudPrintDirectly.exe
 * - Llevar en C:\Program Files\tipscloudPrintDirectly\lib las librerías del proyecto  
 * - Ejecutar con 2click el archivo tipscloudPrintDirectly.reg en Windows, para que el navegador reconozca el protocolo de la aplicación al ejecutarse
 * - En el sistema, en Opciones/General/Parametros de Impresion marcar la opcion [x]Utilizar la impresion directa de comprobantes
 * - Opcionalmente en Opciones/Stock/Formato de Comprobantes se pueden añadir las opciones de impresion.
 * - Un log por cada impresion se genera en C:\tipscloudPrintDirectly\tipscloudPrintDirectly.log  
 * 
 *
 */
public class PrintPdf{
	protected static Log log = LogFactory.getLog(PrintPdf.class);

	private static Map paramVars = new HashMap();
	private PrinterJob pjob = null;
	
    static String imp = "";
    //static String beginConfigName = "_begin_config_name_";
    //static String endConfigName = "_end_config_name_";
    static String beginConfigName = "_begin_printer_name_";
    static String endConfigName = "_end_printer_name_";
    /**
     * args[0] = http://192.168.88.250:8081/TIPSCLOUD-WEB/imprimirTicketComun.action
     * virtualContext, args[1] = virgenparaguay
     * user, args[2] = tips
     * numero, args[3] = 656
     * @param args
     * @throws IOException
     * @throws PrinterException
     */
    public static void main(String[] args) {
    	log.info("ArgumentoSSSSS : " + args[0]);
    	String protocol = "tipscloudprintdirectly://";
    	String path = args[0].substring(args[0].indexOf(protocol) + protocol.length(), args[0].length() );
    	log.info("nueva impresión desde: " + path);
    	
    	String variablesPart = path.split("\\?")[1];
    	String[] variablesValues = variablesPart.split("&");
    	for (String variableValue : variablesValues) {
    		String[] valores = variableValue.split("=");
    		
    		String valor = "";
    		if (valores.length > 1 && valores[1] != null)
    			valor = valores[1];
    		
    		if (valores.length > 0)
    			paramVars.put(valores[0], valor);
		}
    	

		//Caso, el nombre de la impresora venga separado por comas, entonces quiere decir que debe imprimir dos veces en la misma impresora.
		String regex = "(?<!\\\\)" + Pattern.quote(",");
		String printersAndFormat[] = (paramVars.get("printer")+"").split(regex);
		log.info(printersAndFormat);

		String newPath = path.split("(?<!\\\\)" + Pattern.quote("?"))[0] + "?" + variablesToEncodedParams(paramVars);
			
		InputStream is = null;
		try {
	    	log.info("Invocar a " + newPath);
	    	String parte1 = newPath.substring(0, newPath.indexOf("?")) + "?";
	    	String parte2 = newPath.substring( newPath.indexOf("?")+1, newPath.length());
	    	String parte2Enc = "";
	    	String [] parametros = parte2.split("&");
	    	

	    	for (String parametro : parametros) {
	    		String [] parValue = parametro.split("=");
	    		
	    		if (parValue.length > 1){
		    		if (parValue[1].contains(" ")){	//Si tiene espacio, hace el encode
		    			parte2Enc += parValue[0] + "=" + java.net.URLEncoder.encode(parValue[1], "UTF-8") + "&";
		    		} else {
		    			parte2Enc += parValue[0] + "=" + parValue[1] + "&";
		    		}
	    		} else {
	    			parte2Enc += parValue[0] + "=&";
	    		}
			}
	    	//log.info("*** " + parte2Enc);
	    	String encodedURL = parte1 + parte2Enc;
	    	log.info("Encoded " + encodedURL);
	    	
			URL obj = new URL(encodedURL);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			
			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			int responseCode = con.getResponseCode();

			switch (responseCode) {
			  case 500: {
				  log.error("Estado: " + responseCode + " " + con.getResponseMessage());
				  is = con.getErrorStream();
				  log.error(getStringFromInputStream(is));
			  }
			  case 505: {
				  log.error("Estado: " + responseCode + " " + con.getResponseMessage());
				  is = con.getErrorStream();
				  log.error(getStringFromInputStream(is));
			  }
			  case 200: {
				  is = obj.openStream();	//Invoca la URL para recuperar el PDF o el TXT
			  }
			}
				
	    	
	    	log.info("paramVars " + paramVars);
	    	log.info("responseCode " + responseCode);
	    	if (paramVars.get("format") != null && paramVars.get("format").equals("PDF")){
	    		try {
	    			String printerAndFormat[] = (paramVars.get("printer")+"").split("(?<!\\\\)" + Pattern.quote("|"));
	    			
		    		PrintPdf printPDFFile = new PrintPdf(is, "tipsCloud - Impresión de Ticket", printerAndFormat[0], paramVars.get("format")+"");
		            printPDFFile.print();
					
				} catch (Exception e) {
					log.info(e);
					log.info("probando...");
					JOptionPane.showMessageDialog(null, e.getMessage());
				}
	    	} else {
	    		
		    	String resultadoImpresion = getStringFromInputStream(is);
		    	log.info("resultadoImpresion");
		    	log.info(resultadoImpresion );
		    	int cantidadImpresiones = ( resultadoImpresion.split(PrintPdf.beginConfigName, -1).length ) - 1;
		    	
		    	log.info("cantidadImpresiones = " + cantidadImpresiones);
		    	
		    	String errorMessage = "";
		    	int lastIndexOf = 0;
		    	for (int j = 0; j < cantidadImpresiones; j++) {
		    		lastIndexOf = resultadoImpresion.indexOf(PrintPdf.beginConfigName, lastIndexOf) + PrintPdf.beginConfigName.length();
			    	String printerNameWithFormat = resultadoImpresion.substring(lastIndexOf, resultadoImpresion.indexOf(PrintPdf.endConfigName, lastIndexOf));
			    	lastIndexOf = resultadoImpresion.indexOf("_begin_content_data_", lastIndexOf)+20;
			    	String contenidoImprimir = resultadoImpresion.substring(lastIndexOf, resultadoImpresion.indexOf("_end_content_data_", lastIndexOf));
			    	String printer[] = printerNameWithFormat.split("(?<!\\\\)" + Pattern.quote("|"));
					String printerName = printer[0];
					String format = "PDF";
					if (printer.length > 1){
						format = printer[1];
					}
	
					log.info("printerName " + printerName);
					log.info("contenidoImprimir " + contenidoImprimir);
					log.info("format " + format);
					
			    	try {
	
						if (format.equals("TXT")){
			    			printTxt(contenidoImprimir, "tipsCloud - Impresión de Comprobante", printerName, format);
						}
			    	} catch (Exception e) {
						log.error(e, e);
						if (e.getMessage().startsWith("Service cannot be null")){
							errorMessage += "Debe especificar un nombre correcto de Impresora...!" + "<br/>";
						}else if (e.getMessage().startsWith("Printer is not accepting job")){
							errorMessage += "La Impresora '" + printerName + "' no está Lista...!" + "<br/>";
						}else{ 
							errorMessage += e.getMessage() + "<br/>";
						}
					}
				}	//end-for
	
				if (errorMessage != null && !errorMessage.isEmpty()){
					log.info(errorMessage);
					log.info("marcos");
					JOptionPane.showMessageDialog(null, "<HTML><BODY><P>" + errorMessage + "</P></BODY></HTML>");
				}
				
	    	}
	    	if (is != null)
	    		is.close();
	        
		} catch (IOException e) {
			log.error(e);
			log.error("Marcos 222");
			//log.info(getStringFromInputStream(is));
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	//}
    			
		
    }

    public static String variablesToEncodedParams(Map variables){
    	String result = "";
		Iterator itr = variables.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry e = (Map.Entry)itr.next();
			
			result += e.getKey() + "=" + e.getValue()+"" + "&";
		}
    	return result;
    }
    public static void printTxt(String inputStream, String jobNames, String printerName, String format) throws Exception {
    	
    	//Extraer nombre de Impresora
        log.info("Va a imprimir un archivo TXT ===========" );

	    DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
	    //DocFlavor flavor = DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8;
	    InputStream stream = new ByteArrayInputStream(inputStream.getBytes());
        Doc simpleDoc = new SimpleDoc(stream, flavor, null);

        log.info("Buscar nombre de Impresora para imrimir txt.... " + printerName);
        PrintService printService = findPrintService(java.net.URLDecoder.decode(printerName, "UTF-8"));
        
        if (printService == null)
        	throw new Exception("Impresora '" + printerName + "' no encontrada");
        
        DocPrintJob printJob = printService.createPrintJob();
        printJob.addPrintJobListener(new PrintJobMonitor());
        
        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
        //attributeSet.add(OrientationRequested.PORTRAIT);
        JobName jobName = new JobName(jobNames, null);
        attributeSet.add(jobName);
        
        printJob.print(simpleDoc, attributeSet);
        
//		if (paramVars.get("cutter") != null && !(paramVars.get("cutter")+"").equals("null") && !(paramVars.get("cutter")+"").isEmpty()){
//			sendAsciiDecimalCommand(java.net.URLDecoder.decode(printerName, "UTF-8"), paramVars.get("cutter")+"");
//		}

    }
    
    public PrintPdf(InputStream inputStream, String jobName, String printerName, String format) throws Exception {
        byte[] pdfContent = new byte[inputStream.available()];
        inputStream.read(pdfContent, 0, inputStream.available());
        initialize(pdfContent, jobName, printerName, format);
    }
    
    public PrintPdf(byte[] content, String jobName, String printerName, String format) throws Exception
    {
        initialize(content, jobName, printerName, format);
    }
    
    private static PrintService findPrintService(String printerName) {
    	log.info("Buscando nombres de impresoras..........................xxxxxxxxxxxxxx ");
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
        	log.info("** printService " + printService);
        	log.info("** printService.getName() " + printService.getName());
            ///if (printService.getName().trim().equals(printerName)) {
        	///if (printService.getName().trim().contains(printerName)) {
        	if (printService.getName().toLowerCase().trim().endsWith(printerName.toLowerCase())) {
                return printService;
            }
        }
        return null;
    }
    
    private void initialize(byte[] pdfContent, String jobName, String printerName, String format) throws Exception {
        ByteBuffer bb = ByteBuffer.wrap(pdfContent);
        PDFFile pdfFile = new PDFFile(bb);
        PDFPrintPage pages = new PDFPrintPage(pdfFile, paramVars);

        pjob = PrinterJob.getPrinterJob();
        
        PrintService printService = findPrintService(java.net.URLDecoder.decode(printerName, "UTF-8"));
        
        if (printService == null)
        	throw new Exception("Impresora '" + printerName + "' no encontrada");
        
        //pjob.setPrintService(findPrintService( java.net.URLDecoder.decode(printerName, "UTF-8")));
        pjob.setPrintService(printService);
        PageFormat pf = PrinterJob.getPrinterJob().defaultPage();
        pjob.setJobName(jobName);
                
        Book book = new Book();
        book.append(pages, pf, pdfFile.getNumPages());
        
        pjob.setPageable(book);
        
        Paper paper = new Paper();
        paper.setSize(paper.getWidth(), pf.getPaper().getImageableHeight()+30);	//Tamaño de la Hoja
        paper.setImageableArea(0, 0, paper.getWidth(), pf.getPaper().getImageableHeight()+30);	//Tamaño maximo de elementos que se imprimen

        pf.setPaper(paper);
    	
    }
    
    public void print() throws PrinterException
    {
    	PrintRequestAttributeSet attr_set = new HashPrintRequestAttributeSet();
    	//attr_set.add(MediaSizeName.NA_LEGAL); 
    	//attr_set.add(MediaSizeName.ISO_A4);
    	//attr_set.add(new Copies(3)); 
    	pjob.setCopies(1);
    	
        //if (pjob.printDialog(attr_set) == true) {
        	pjob.print(attr_set);
        //}
    }
    
/*    public static void sendAsciiDecimalCommand(String printerName, String command) throws Exception{
        
    	String[] sCommand = command.split(",");
    	byte[] open = new byte[sCommand.length];
    	for (int i = 0; i < open.length; i++) {
			open[i] = Byte.valueOf(sCommand[i]);
		}
  
        PrintService pservice = findPrintService(java.net.URLDecoder.decode(printerName, "UTF-8"));
        
        if (pservice == null)
        	throw new Exception ("Impresora '" + printerName + "' no encontrada");
        
        DocPrintJob job = pservice.createPrintJob();
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        Doc doc = new SimpleDoc(open, flavor, null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();

        job.print(doc, aset);

    }*/
    
	// convert InputStream to String
	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

//			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			br = new BufferedReader(new InputStreamReader(is));
//			byte[] latin1 = new String(getBytesFromInputStream(is), "UTF-8").getBytes("ISO-8859-1");
			//byte[] latin1 = new String(getBytesFromInputStream(is), "UTF-8").getBytes("UTF-8");
			
			//br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(latin1), "UTF-8"));
			while ((line = br.readLine()) != null) {
				//log.info("Impresion temprana=> " + line+ "\n");
				sb.append(line+ "\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
	
	public static byte[] getBytesFromInputStream(InputStream is) throws IOException {
	    ByteArrayOutputStream os = new ByteArrayOutputStream(); 
	    byte[] buffer = new byte[0xFFFF];
	    for (int len = is.read(buffer); len != -1; len = is.read(buffer)) { 
	        os.write(buffer, 0, len);
	    }
	    return os.toByteArray();
	}
}
class PDFPrintPage implements Printable {
	protected static Log log = LogFactory.getLog(PDFPrintPage.class);

    private PDFFile file;
    private Map variables;
    PDFPrintPage(PDFFile file, Map variables)
    {
        this.file = file;
        this.variables = variables;
    }
    public int print(Graphics g, PageFormat format, int index) throws PrinterException {
        int pagenum = index + 1;
        if ((pagenum >= 1) && (pagenum <= file.getNumPages())) {
            Graphics2D g2 = (Graphics2D) g;
            PDFPage page = file.getPage(pagenum);
            
            Rectangle imageArea = new Rectangle((int)format.getImageableX(), (int)format.getImageableY(), (int) page.getBBox().getWidth(), (int) page.getBBox().getHeight());

            //---
            double translateX = 0.0;
            double translateY = 0.0;
            if (variables.get("translateX") != null)
            	translateX = Double.valueOf((variables.get("translateX")+""));
        	if (variables.get("translateY") != null)
        		translateY = Double.valueOf((variables.get("translateY")+""));
            
            g2.translate(translateX, translateY);
            
            //---
            double scalaX = 1.0;
            double scalaY = 1.0;
            if (variables.get("scaleX") != null)
            	scalaX = Double.valueOf((variables.get("scaleX")+""));
        	if (variables.get("scaleY") != null)
            	scalaY = Double.valueOf((variables.get("scaleY")+""));
        	
            g2.scale(scalaX, scalaY);
            	
            //---
            PDFRenderer pgs = new PDFRenderer(page, g2, imageArea, null, null);
            try {
                page.waitForFinish();
                pgs.run();
            } catch (InterruptedException ie) {
                throw new PrinterException(ie.getMessage());
            }
            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }
 
}




