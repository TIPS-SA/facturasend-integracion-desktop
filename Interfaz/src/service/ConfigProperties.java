package service;

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
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ConfigProperties {

	private Properties prop = null;
	
	private FileReader reader = null;
	private FileWriter writer = null;
	
	private File dbFile = new File(System.getProperty("user.dir") + File.separator + "srcConfig/config/config.properties");
	//private File fsFile = new File(System.getProperty("user.dir") + File.separator + "srcConfig/config/facturasend.properties");
	
	private InputStream dbProperties= null;
	private InputStream fsProperties= null;
	private OutputStream dbPropertiesSave = null;
	private OutputStream fsPropertiesSave = null;
	
	
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
	
	public void writeDbProperties(Map<String, String> values) {
		try {
			dbPropertiesSave = new FileOutputStream(dbFile);
			prop = new Properties();
			reader = new FileReader(dbFile);
			writer = new FileWriter(dbFile);
			
			prop.load(reader);
			
			for (Map.Entry<String, String> entry : values.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(dbPropertiesSave, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
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
