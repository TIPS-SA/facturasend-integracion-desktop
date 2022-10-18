package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ConfigProperties {

	private Properties prop = null;
	
	private FileReader reader = null;
	private FileWriter writer = null;
	
	private File dbFile = new File("/home/lucasf/pgfxDev/git/facturasend-integracion-desktop/Interfaz/srcConfig/config/database.properties");
	private File fsFile = new File("/home/lucasf/pgfxDev/git/facturasend-integracion-desktop/Interfaz/srcConfig/config/facturasend.properties");
	
	private InputStream dbProperties= null;
	private InputStream fsProperties= null;
	
	public Map readDbProperties() {
		prop = new Properties();
		Map properties = new HashMap<String, String>();
		Set<String> values = prop.stringPropertyNames();
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
			prop = new Properties();
			reader = new FileReader(dbFile);
			writer = new FileWriter(dbFile);
			
			prop.load(reader);
			
			for (Map.Entry<String, String> entry : values.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public Map readFsProperties() {
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
	}
	
	public void writeFsProperties(Map<String, String> values) {
		try {
			prop = new Properties();
			reader = new FileReader(fsFile);
			writer = new FileWriter(fsFile);
			
			prop.load(reader);
			
			for (Map.Entry<String, String> entry : values.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		ConfigProperties cp = new ConfigProperties();
		Map<String, String> p = new HashMap<String, String>();
		p = cp.readDbProperties();
		for (Map.Entry<String,String> entry : p.entrySet()) {
			System.out.println(entry.getKey()+ " - " +entry.getValue());
		}
		System.out.println("\n\n\n");
		p = cp.readFsProperties();
		for (Map.Entry<String,String> entry : p.entrySet()) {
			System.out.println(entry.getKey()+ " - " +entry.getValue());
		}
	}
}
