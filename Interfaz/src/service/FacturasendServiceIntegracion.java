package service;

import java.util.Map;

import core.CoreServiceIntegracion;

/**
 * Clase del Front que se dedica exclusivamente para invocar a los servicios de integración de Documentos electronicos
 * 
 * Contiene un metodo main, para que se pueda invocar desde la linea de comandos.
 * 
 */
public class FacturasendServiceIntegracion {

	public static void main(String[] args) {
		
	}

	/**
	 * Ejecuta el proceso de integración desde el CoreServiceIntegracion, el 
	 * cual tiene su logica propia para mantener las transacciones
	 * sincronizadas.
	 * 
	 * Conviene llamar a éste metodo desde la linea de comandos y no desde un 
	 * boton dentro de la pantalla ya que puede ralentizar el render de los objetos
	 *  
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	public static void iniciarIntegracion() throws Exception {
		
		//Llamar a la consulta de Datos
		
		Map<String, Object> returnData1 = CoreServiceIntegracion.iniciarIntegracion(FacturasendService.readDBProperties());

		/*System.out.println(returnData);
		if (Boolean.valueOf(returnData.get("success")+"") == true) {
			return returnData;
		} else {
			throw new Exception(returnData.get("error")+"");
		}*/
	}	

}




