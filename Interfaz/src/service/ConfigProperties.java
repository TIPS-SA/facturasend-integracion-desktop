package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigProperties {

	private Properties prop = null;
	
	private FileReader reader = null;
	private FileWriter writer = null;
	
	private File dbFile = new File(System.getProperty("user.dir") + File.separator + "resource/config.properties");
	private File logFile = new File(System.getProperty("user.dir") + File.separator + "target/resource/log4j.properties");
	//private File fsFile = new File(System.getProperty("user.dir") + File.separator + "srcConfig/config/facturasend.properties");
	
	private InputStream dbProperties= null;
	private InputStream logProperties= null;
	//private InputStream fsProperties= null;
	private OutputStream dbPropertiesSave = null;
	//private OutputStream fsPropertiesSave = null;
	
	
	public Map readDbProperties() {
		prop = new Properties();
		Map properties = new HashMap<String, String>();
		try {
			 
			dbProperties = new FileInputStream(dbFile);
			prop.load(dbProperties);
			properties = prop;
		}catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}
	
	public String readLogProperties() {
		String log= null;
		String linea;
		BufferedReader br = null;
		prop = new Properties();
		Map properties = new HashMap<String, String>();
		try {
			 
			logProperties = new FileInputStream(logFile);
			prop.load(logProperties);
			properties = prop;
			logFile = new File(System.getProperty("user.dir") + File.separator + "target"+File.separator + properties.get("log4j.appender.logfile.File"));
			FileReader fr = new FileReader(logFile);
			br = new BufferedReader(fr);
	        try {
				while((linea=br.readLine())!=null) {
				   log = log +" " + linea +"\n";
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
		         // En el finally cerramos el fichero, para asegurarnos
		         // que se cierra tanto si todo va bien como si salta 
		         // una excepcion.
		         try{                    
		            if( null != fr ){   
		               fr.close();     
		            }                  
		         }catch (Exception e2){ 
		            e2.printStackTrace();
		         }
			}
		}catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return log;
	}
	
	public void writeDbProperties(Map<String, String> values) {
		try {
			dbPropertiesSave = new FileOutputStream(dbFile);
			prop = new SortedStoreProperties();
			reader = new FileReader(dbFile);
			writer = new FileWriter(dbFile);
			
			prop.load(reader);
			//Ordenar MAP
			Map<String, String> ordenado = sortMapByKey(values);
			System.out.println("-----------Ordendado------------\n"+ ordenado);
			
			for (Map.Entry<String, String> entry : ordenado.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(dbPropertiesSave, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private Map<String, String> sortMapByKey(Map<String, String> map)
	{
	    Map<String, String> sortedMap =  map.entrySet().stream()
	            .sorted(Entry.comparingByKey())
	            .collect(Collectors.toMap(
	                    Map.Entry::getKey,
	                    Map.Entry::getValue,
	                    (a, b) -> { throw new AssertionError();},
	                    LinkedHashMap::new
	            ));
	    return sortedMap;
	}
	
	/*public Map readFsProperties() {
		prop = new Properties();
		Map properties = new HashMap<String, String>();
		try {
			fsProperties = new FileInputStream(fsFile);
			prop.load(fsProperties);
			properties = prop;
		}catch(FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}*/
	
	/*public void writeFsProperties(Map<String, String> values) {
		try {
			fsPropertiesSave = new FileOutputStream(fsFile);
			prop = new Properties();
			reader = new FileReader(fsFile);
			writer = new FileWriter(fsFile);
			
			for (Map.Entry<String, String> entry : values.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(fsPropertiesSave, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}*/
}
