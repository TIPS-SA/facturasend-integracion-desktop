package service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import core.CoreIntegracionService;

public class IntegradorTest {

	public static Log log = LogFactory.getLog(IntegradorTest.class);

	/**
	 * Launch integración
	 */
	public static void main(String[] args) {
		
		CoreIntegracionService.iniciarIntegracion(FacturasendService.readDBProperties());
		
	}
}
