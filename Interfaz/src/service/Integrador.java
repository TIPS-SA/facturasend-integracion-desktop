package service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import core.CoreIntegracionService;

public class Integrador {

	/**
	 * Launch integración
	 */
	public static void main(String[] args) {
		
		Integer autoUpdateTableView = Integer.valueOf(FacturasendService.readDBProperties().get("database.autoupdate_millis.integracion")+"");
		
		//Parametros que se tiene en cuenta para integrar
		//database.autoupdate_millis.integracion
		//facturasend.rows_lote_request
		
		if (autoUpdateTableView != null && autoUpdateTableView > 0) {
			new Timer().schedule(new TimerTask() {
			    @Override
			    public void run() {
			        CoreIntegracionService.iniciarIntegracion(FacturasendService.readDBProperties());
			    }
			}, new Date(), autoUpdateTableView); //Cada N millis segundos						
		} else {
			System.out.println("No se inicio la integración, establezca 'database.autoupdate_millis.integracion' con un valor > 0 (1000=1seg)");
		}
	}
}
