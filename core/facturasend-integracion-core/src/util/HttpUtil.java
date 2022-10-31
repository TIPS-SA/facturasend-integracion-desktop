package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HttpUtil {
	
	public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create(); 
	public static Log log = LogFactory.getLog(HttpUtil.class);
	
	static {
        System.setProperty("javax.net.debug", "all");
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
        System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
        //=
        //log.info("Seteando protocolo" + "TLSv1,TLSv1.1,TLSv1.2");
    }
	
	public static Map invocarRest(String urlName, String method, String jsonData, Map header) {
		if (urlName.startsWith("https://")){
			return invocarSSLRest(urlName, method, jsonData, header);
		} else {
			return invocarNoSSLRest(urlName, method, jsonData, header);
		}
	}
	
	public static Map invocarNoSSLRest(String urlName, String method, String jsonData, Map header) {
        String outPut = "";

        try {
        	log.info("Invocando " + urlName);
        	log.info("Method " + method);
        	log.info("jsonData " + jsonData);
        	int postDataLength = 0;
        	if (jsonData != null){
        		byte[] postData = jsonData.getBytes(Charset.forName("UTF-8"));
        		postDataLength = postData.length;
        	}
        	
            URL url = new URL(urlName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method);
     
            if (header != null) {
	            Iterator itr = header.entrySet().iterator();
	    		while (itr.hasNext()) {
	    			Map.Entry e = (Map.Entry)itr.next();
	    			log.info(e.getKey() + ", " + e.getValue());
		            conn.setRequestProperty(e.getKey()+"", e.getValue()+"");
	    		}
            }
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        	if (jsonData != null)
        		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            conn.setUseCaches(false);
            conn.connect();
            
            if (jsonData != null){
	            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	            
	            wr.write(jsonData.toString());
	            wr.flush();
            }
            

            InputStream inputStream;
            System.err.println("Response Code: " + conn.getResponseCode());
        	System.err.println("Response Message: " + conn.getResponseMessage());
            if (conn.getResponseCode() < HttpsURLConnection.HTTP_BAD_REQUEST) {
            	
            	inputStream = conn.getInputStream();
            	
            	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while (((line = reader.readLine()) != null)){
                    outPut += line;
                }
                conn.disconnect();
                
                Map<String, Object> result = new HashMap<String, Object>();
                if (!outPut.equals("true") && !outPut.equals("false")){
                	if (outPut.startsWith("[") || outPut.startsWith("{")){
	                	log.info(outPut);
		                result = gson.fromJson(outPut, Map.class);
		                //log.info("Resultado:");
		                //log.info(outPut);
                	} else {
                    	result.put("success", true);
                    	result.put("value", outPut);
                	}
                } else {
                	result.put("success", true);
                }
                return result;
            } else if (conn.getResponseCode() == HttpsURLConnection.HTTP_NOT_FOUND) {
            	
            	Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", false);
                result.put("error", "No se encontró el Recurso (404)");
                return result;
            } else if (conn.getResponseCode() == HttpsURLConnection.HTTP_BAD_REQUEST) {
            	
            	Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", false);
                result.put("error", "Sin Authorización (400)");
                return result;
            } else if (conn.getResponseCode() == HttpsURLConnection.HTTP_FORBIDDEN) {
            	
            	Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", false);
                result.put("error", "Sin Authorización (403)");
                return result;
            } else {
                 /* error from server */
            	System.err.println("Error");
            	inputStream = conn.getErrorStream();
            	
            	System.err.println(inputStream);
            	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while (((line = reader.readLine()) != null)){
                    outPut += line;
                }
                
                conn.disconnect();
                
                System.err.println(outPut);
                return null;
            }
           
            

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//https://stackoverflow.com/questions/36298394/java-invalidalgorithmparameterexception-prime-size-must-be-multiple-of-64
	public static Map invocarSSLRest(String urlName, String method, String jsonData, Map header) {
        String outPut = "";

        try {
        	log.info("Invocando " + urlName);
        	log.info("Method " + method);
        	log.info("jsonData " + jsonData);
        	int postDataLength = 0;
        	if (jsonData != null){
        		byte[] postData = jsonData.getBytes(Charset.forName("UTF-8"));
        		postDataLength = postData.length;
        	}
            
            URL url = new URL(urlName);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(new TLSSocketConnectionFactory());
            
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method);

            if (header != null) {
	            Iterator itr = header.entrySet().iterator();
	    		while (itr.hasNext()) {
	    			Map.Entry e = (Map.Entry)itr.next();
	    			log.info(e.getKey() + ", " + e.getValue());
		            conn.setRequestProperty(e.getKey()+"", e.getValue()+"");
	    		}
            }
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        	if (jsonData != null)
        		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            conn.setUseCaches(false);
            conn.connect();
            
            if (jsonData != null){
            	OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	            wr.write(jsonData.toString());
	            wr.flush();
            }

            InputStream inputStream;
            System.err.println("Message Code: " + conn.getResponseCode());
        	System.err.println("Message Error: " + conn.getResponseMessage());
            if (conn.getResponseCode() < HttpsURLConnection.HTTP_BAD_REQUEST) {
            	inputStream = conn.getInputStream();
            	
            	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while (((line = reader.readLine()) != null)){
                    outPut += line;
                }
                conn.disconnect();
                Map<String, Object> result = new HashMap<String, Object>();
                if (!outPut.equals("true") && !outPut.equals("false")){
                	if (outPut.startsWith("[") || outPut.startsWith("{")){
	                	log.info(outPut);
		                result = gson.fromJson(outPut, Map.class);
		                //log.info("Resultado:");
		                //log.info(outPut);
                	} else {
                    	result.put("success", true);
                    	result.put("value", outPut);
                	}
                } else {
                	result.put("success", true);
                }
                return result;
            } else if (conn.getResponseCode() == HttpsURLConnection.HTTP_NOT_FOUND) {
            	Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", false);
                result.put("error", "No se encontró el Recurso (404)");
                return result;
            } else if (conn.getResponseCode() == HttpsURLConnection.HTTP_BAD_REQUEST) {
            	
            	Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", false);
                result.put("error", "Sin Authorización (400)");
                return result;
            } else if (conn.getResponseCode() == HttpsURLConnection.HTTP_FORBIDDEN) {
            	Map<String, Object> result = new HashMap<String, Object>();
                result.put("success", false);
                result.put("error", "Sin Authorización (403)");
                return result;
            } else {
                 /* error from server */
            	System.err.println("Error");
            	inputStream = conn.getErrorStream();
            	
            	System.err.println(inputStream);
            	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while (((line = reader.readLine()) != null)){
                    outPut += line;
                }
                
                conn.disconnect();
                
                System.err.println(outPut);
                return null;
            }
           
            

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<Map<String, Object>> invocarRestList(String urlName, String method, String jsonData, Map header) {
		if (urlName.startsWith("https://")){
			return invocarSSLRestList(urlName, method, jsonData, header);
		} else {
			return invocarNoSSLRestList(urlName, method, jsonData, header);
		}
	}
	
	public static List<Map<String, Object>> invocarNoSSLRestList(String urlName, String method, String jsonData, Map header) {
        String outPut = "";

        try {
        	log.info("Invocando " + urlName);
        	log.info("Method " + method);
        	log.info("jsonData " + jsonData);
        	int postDataLength = 0;
        	if (jsonData != null){
        		byte[] postData = jsonData.getBytes(Charset.forName("UTF-8"));
        		postDataLength = postData.length;
        	}
        	
            URL url = new URL(urlName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method);

            Iterator itr = header.entrySet().iterator();
    		while (itr.hasNext()) {
    			Map.Entry e = (Map.Entry)itr.next();
    			log.info(e.getKey() + ", " + e.getValue());
	            conn.setRequestProperty(e.getKey()+"", e.getValue()+"");
    		}
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        	if (jsonData != null)
        		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            conn.setUseCaches(false);
            conn.connect();
            
            /*DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.flush();                                                                 
            wr.close();  */
            if (jsonData != null){
	            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	            
	            wr.write(jsonData.toString());
	            wr.flush();
            }
            

            InputStream inputStream;
            System.err.println("Code: " + conn.getResponseCode());
        	System.err.println("Code: " + conn.getResponseMessage());
            if (conn.getResponseCode() < HttpsURLConnection.HTTP_BAD_REQUEST) {
            	inputStream = conn.getInputStream();
            	
            	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while (((line = reader.readLine()) != null)){
                    outPut += line;
                }
                conn.disconnect();
                List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
                
                result = gson.fromJson(outPut, result.getClass());
                log.info("Resultado:");
                log.info(outPut);
                return result;
            } else {
                 /* error from server */
            	System.err.println("Error");
            	inputStream = conn.getErrorStream();
            	
            	System.err.println(inputStream);
            	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while (((line = reader.readLine()) != null)){
                    outPut += line;
                }
                
                conn.disconnect();
                
                System.err.println(outPut);
                return null;
            }
           
            

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//https://stackoverflow.com/questions/36298394/java-invalidalgorithmparameterexception-prime-size-must-be-multiple-of-64
	public static List<Map<String, Object>> invocarSSLRestList(String urlName, String method, String jsonData, Map header) {
        String outPut = "";

        try {
        	log.info("Invocando " + urlName);
        	log.info("Method " + method);
        	log.info("jsonData " + jsonData);
        	int postDataLength = 0;
        	if (jsonData != null){
        		byte[] postData = jsonData.getBytes(Charset.forName("UTF-8"));
        		postDataLength = postData.length;
        	}
            
            URL url = new URL(urlName);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(method);

            Iterator itr = header.entrySet().iterator();
    		while (itr.hasNext()) {
    			Map.Entry e = (Map.Entry)itr.next();
    			log.info(e.getKey() + ", " + e.getValue());
	            conn.setRequestProperty(e.getKey()+"", e.getValue()+"");
    		}
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
        	if (jsonData != null)
        		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            conn.setUseCaches(false);
            conn.connect();
            
            if (jsonData != null){
            	OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	            wr.write(jsonData.toString());
	            wr.flush();
            }

            InputStream inputStream;
            System.err.println("Code: " + conn.getResponseCode());
        	System.err.println("Code: " + conn.getResponseMessage());
            if (conn.getResponseCode() < HttpsURLConnection.HTTP_BAD_REQUEST) {
            	inputStream = conn.getInputStream();
            	
            	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while (((line = reader.readLine()) != null)){
                    outPut += line;
                }
                conn.disconnect();
                
                List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
                result = gson.fromJson(outPut, result.getClass());
                log.info("Resultado:");
                log.info(outPut);
                return result;
            } else {
                 /* error from server */
            	System.err.println("Error");
            	inputStream = conn.getErrorStream();
            	
            	System.err.println(inputStream);
            	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while (((line = reader.readLine()) != null)){
                    outPut += line;
                }
                
                conn.disconnect();
                
                System.err.println(outPut);
                return null;
            }
           
            

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getMagentoToken(String urlName, String user, String passwd) {
        String outPut = "";

        try {
        	log.info("Invocando " + urlName);
        	log.info("Method " + user);
        	log.info("jsonData " + passwd);
        	
        	Map ojsonData = new HashMap();
        	ojsonData.put("username", user);
        	ojsonData.put("password", passwd);
        	
        	String jsonData = gson.toJson(ojsonData);
            byte[] postData = jsonData.getBytes(Charset.forName("UTF-8"));

            int postDataLength = postData.length;
            
            URL url = new URL(urlName);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            if (user != null && passwd != null){
	            String authString = user + ":" + passwd;
				
				//byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
				//String authStringEnc = new String(authEncBytes);
	            //conn.setRequestProperty("Authorization",  "Basic " + authStringEnc);
            }
            
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            conn.setUseCaches(false);
            conn.connect();

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(jsonData.toString());
            wr.flush();
            

            InputStream inputStream;
            if (conn.getResponseCode() < HttpsURLConnection.HTTP_BAD_REQUEST) {
            	inputStream = conn.getInputStream();
            	
            	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while (((line = reader.readLine()) != null)){
                    outPut += line;
                }
                conn.disconnect();
                
                String retorno = null;
                if (outPut != null){
                	retorno = outPut.substring(1, outPut.length()-1);
                	log.info("Token: " + retorno);		
                	return retorno;	
                }
                
            } else {
                 /* error from server */
            	System.err.println("Error");
            	inputStream = conn.getErrorStream();
            	
            	System.err.println(inputStream);
            	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while (((line = reader.readLine()) != null)){
                    outPut += line;
                }
                
                conn.disconnect();
                
                System.err.println(outPut);
                return null;
            }
           
            

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
