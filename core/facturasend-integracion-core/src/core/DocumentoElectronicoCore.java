package core;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;

public class DocumentoElectronicoCore {
	
	private static Gson gson = new Gson();

	public static List<Map<String, Object>> generarJSONLote(String[] transactionIds, List<Map<String, Object>> documentosParaEnvioList, Map<String, String> databaseProperties) throws Exception{
		 
		List<Map<String, Object>> jsonDEs = new ArrayList<Map<String,Object>>();
			
		for (int i = 0; i < transactionIds.length; i++) {
			if (!transactionIds[i].trim().isEmpty()) {
				Integer transaccionId = Integer.valueOf(transactionIds[i].trim());
				
				List<Map<String, Object>> documentosParaEnvioFiltradoList = documentosParaEnvioList.stream().filter( map -> {
					String transaccionIdString = Core.getValueForKey(map, "transaccion_id", "tra_id") + "";
					
					Integer transaccionIdIterate = new BigDecimal(transaccionIdString).intValue();
					
					return transaccionIdIterate == transaccionId.intValue();
				}).collect(Collectors.toList());
				
				if (documentosParaEnvioFiltradoList.size() > 0) {
					Map<String, Object> jsonDE = invocarDocumentoElectronicoDesdeView(documentosParaEnvioFiltradoList, databaseProperties);
					jsonDEs.add(jsonDE);					
				}
			}
		}
		
		return jsonDEs;
	}
			
	/**
	 * Prepara los datos para enviar al Sistema de Facturación electronica, teniendo en cuenta los datos de una AutoFactura de Compra.
	 * 
	 * @param timbradoFacturacionMovimiento
	 * @param usuario
	 * @param sucursal
	 * @throws InfoException
	 */
	public static Map<String, Object> invocarDocumentoElectronicoDesdeView(List<Map<String, Object>> transaccionMap, Map<String, String> databaseProperties) throws Exception{
		Map<String, Object> dataMap = new HashMap<String, Object>();

		System.out.println("Procesando... " + gson.toJson(transaccionMap));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (transaccionMap != null && transaccionMap.size() > 0) {
			
			Map<String, Object> transaccionCabecera = transaccionMap.get(0);
			
			Integer transaccionId = Integer.valueOf(Core.getValueForKey(transaccionCabecera, "transaccion_id", "tra_id") + "");
			Integer tipoDocumento = Integer.valueOf(Core.getValueForKey(transaccionCabecera, "tipo_documento", "tip_doc") + "");

			dataMap.put("tipoDocumento", tipoDocumento);
			String cdcGenerado = (String) Core.getValueForKey(transaccionCabecera, "cdc");
			
			if (cdcGenerado != null && cdcGenerado.length() == 44) {
				dataMap.put("cdc", cdcGenerado); //Si ya fue generado con un CDC, entonces envía para utilizar el mismo.
			}

			dataMap.put("establecimiento", Core.getValueForKey(transaccionCabecera, "establecimiento", "estable"));
			dataMap.put("punto", Core.getValueForKey(transaccionCabecera, "punto"));
			dataMap.put("numero", Core.getValueForKey(transaccionCabecera, "numero"));
			dataMap.put("serie", Core.getValueForKey(transaccionCabecera, "serie"));
			if (Core.getValueForKey(transaccionCabecera,"descripcion","descrip") != null) {
				dataMap.put("descripcion", Core.getValueForKey(transaccionCabecera, "descripcion", "descrip").toString().trim());
			}
			if (Core.getValueForKey(transaccionCabecera,"observacion","observa") != null) {
				dataMap.put("observacion", Core.getValueForKey(transaccionCabecera,"observacion","observa").toString().trim());
			}

			if ( Core.getValueForKey(transaccionCabecera,"fecha") instanceof Date) {
				dataMap.put("fecha", sdf.format((Date) Core.getValueForKey(transaccionCabecera,"fecha")) );
			} else {
				dataMap.put("fecha", Core.getValueForKey(transaccionCabecera,"fecha") );
			}
			dataMap.put("tipoEmision", Core.getValueForKey(transaccionCabecera, "tipo_emision", "tip_emi"));
			dataMap.put("tipoTransaccion", Core.getValueForKey(transaccionCabecera, "tipo_transaccion", "tip_tra"));
			dataMap.put("tipoImpuesto", Core.getValueForKey(transaccionCabecera, "tipo_impuesto", "tip_imp"));
			
			if (Core.getValueForKey(transaccionCabecera,"moneda") != null) {
				dataMap.put("moneda", Core.getValueForKey(transaccionCabecera, "moneda").toString().trim());
			}
			dataMap.put("condicionAnticipo", Core.getValueForKey(transaccionCabecera, "anticipo"));
			dataMap.put("descuentoGlobal", Core.getValueForKey(transaccionCabecera, "descuento_global", "des_glo"));
			dataMap.put("anticipoGlobal", Core.getValueForKey(transaccionCabecera, "anticipo_global", "ant_glo"));
			dataMap.put("total", Core.getValueForKey(transaccionCabecera, "total"));

			dataMap.put("condicionTipoCambio", Core.getValueForKey(transaccionCabecera,"tipo_cambio","tip_cam"));			
			dataMap.put("cambio", Core.getValueForKey(transaccionCabecera,"cambio"));
			
			// Cliente
			Map<String, Object> cliente = new HashMap<String, Object>();
			
			cliente.put("contribuyente", Boolean.valueOf(Core.getValueForKey(transaccionCabecera,"cliente_contribuyente","c_contribu")+""));
			
			if (Core.getValueForKey(transaccionCabecera,"cliente_ruc","c_ruc") != null) {
				cliente.put("ruc", Core.getValueForKey(transaccionCabecera,"cliente_ruc","c_ruc").toString().trim());
			}
			if (Core.getValueForKey(transaccionCabecera,"cliente_razon_social","c_raz_soc") != null) {
				cliente.put("razonSocial", Core.getValueForKey(transaccionCabecera,"cliente_razon_social","c_raz_soc").toString().trim());
			}
			if (Core.getValueForKey(transaccionCabecera,"cliente_nombre_fantasia","c_nom_fan") != null) {
				cliente.put("nombreFantasia", Core.getValueForKey(transaccionCabecera,"cliente_nombre_fantasia","c_nom_fan").toString().trim());
			}

			cliente.put("tipoOperacion", Core.getValueForKey(transaccionCabecera,"cliente_tipo_operacion","c_tip_ope"));
			
			if (Core.getValueForKey(transaccionCabecera,"cliente_direccion","c_direcc") != null) {
				cliente.put("direccion", Core.getValueForKey(transaccionCabecera,"cliente_direccion","c_direcc").toString().trim());
			}
			if (Core.getValueForKey(transaccionCabecera,"cliente_numero_casa","c_num_cas") != null) {
				cliente.put("numeroCasa", Core.getValueForKey(transaccionCabecera,"cliente_numero_casa","c_num_cas").toString().trim());
			}

			cliente.put("ciudad", Core.getValueForKey(transaccionCabecera,"cliente_ciudad","c_ciudad"));
			cliente.put("departamento", Core.getValueForKey(transaccionCabecera,"cliente_departamento","c_depart"));
			cliente.put("distrito", Core.getValueForKey(transaccionCabecera,"cliente_distrito","c_distri"));
			cliente.put("pais", Core.getValueForKey(transaccionCabecera,"cliente_pais","c_pais"));

			cliente.put("tipoContribuyente", Core.getValueForKey(transaccionCabecera,"cliente_tipo_contribuyente","c_tip_con"));
					
			cliente.put("documentoTipo", Core.getValueForKey(transaccionCabecera,"cliente_documento_tipo","c_doc_tip"));
			
			cliente.put("documentoNumero", Core.getValueForKey(transaccionCabecera,"cliente_documento_numero","c_doc_num"));
			
			if (Core.getValueForKey(transaccionCabecera,"cliente_telefono","c_tel") != null) {
				cliente.put("telefono", Core.getValueForKey(transaccionCabecera,"cliente_telefono","c_tel").toString().trim());
			}
			if (Core.getValueForKey(transaccionCabecera,"cliente_celular","c_cel") != null) {
				cliente.put("celular", Core.getValueForKey(transaccionCabecera,"cliente_celular","c_cel").toString().trim() );
			}
			if (Core.getValueForKey(transaccionCabecera,"cliente_email","c_ema") != null) {
				cliente.put("email", Core.getValueForKey(transaccionCabecera,"cliente_email","c_ema").toString().trim() );
			}
			if (Core.getValueForKey(transaccionCabecera,"cliente_codigo","c_cod") != null) {
				cliente.put("codigo", Core.getValueForKey(transaccionCabecera,"cliente_codigo","c_cod").toString().trim() );
			}
			
			dataMap.put("cliente", cliente );
			//FIN CLIENTE
			
			
			// inicioUsuario
			Map<String, Object> dataMapUsuario = new HashMap<String, Object>();

			dataMapUsuario.put("documentoTipo", Core.getValueForKey(transaccionCabecera,"usuario_documento_tipo","u_doc_tip"));
			
			if (Core.getValueForKey(transaccionCabecera,"usuario_documento_numero","u_doc_num") != null) {
				dataMapUsuario.put("documentoNumero", Core.getValueForKey(transaccionCabecera,"usuario_documento_numero","u_doc_num").toString().trim());
			}
			if (Core.getValueForKey(transaccionCabecera,"usuario_nombre","u_nom") != null) {
				dataMapUsuario.put("nombre", Core.getValueForKey(transaccionCabecera,"usuario_nombre","u_nom").toString().trim());
			}
			if (Core.getValueForKey(transaccionCabecera,"usuario_cargo","u_car") != null) {
				dataMapUsuario.put("cargo", Core.getValueForKey(transaccionCabecera,"usuario_cargo","u_car").toString().trim());
			}
			dataMap.put("usuario", dataMapUsuario);
			//finUsuario
			
			//Factura
			if (tipoDocumento == 1) {
				Map<String, Object> dataMapFactura1 = new HashMap<String, Object>();
				dataMapFactura1.put("presencia", Core.getValueForKey(transaccionCabecera,"factura_presencia","fa_pre"));
				dataMapFactura1.put("fechaEnvio", Core.getValueForKey(transaccionCabecera,"factura_fecha_envio","fa_fec_env"));
				dataMapFactura1.put("ticket", Core.getValueForKey(transaccionCabecera,"factura_ticket","fa_ticket"));				
				dataMap.put("factura", dataMapFactura1);
			}
			
			//AutoFactura
			if (tipoDocumento == 4) {
				Map<String, Object> dataMapFactura = new HashMap<String, Object>();
				dataMapFactura.put("tipoVendedor", Core.getValueForKey(transaccionCabecera,"autofactura_tipo_vendedor","af_tip_ven"));
				dataMapFactura.put("documentoTipo", Core.getValueForKey(transaccionCabecera,"autofactura_documento_tipo","af_doc_tip"));
				dataMapFactura.put("documentoNumero", Core.getValueForKey(transaccionCabecera,"autofactura_documento_numero","af_doc_num"));
				dataMapFactura.put("nombre", Core.getValueForKey(transaccionCabecera,"autofactura_nombre","af_tip_ven"));				
				dataMapFactura.put("direccion", Core.getValueForKey(transaccionCabecera,"autofactura_direccion","af_tip_ven"));
				dataMapFactura.put("numeroCasa", Core.getValueForKey(transaccionCabecera,"autofactura_numero_casa","af_num_cas"));
				dataMapFactura.put("departamento", Core.getValueForKey(transaccionCabecera,"autofactura_departamento","af_depart"));
				dataMapFactura.put("distrito", Core.getValueForKey(transaccionCabecera,"autofactura_distrito","af_distri"));
				dataMapFactura.put("ciudad", Core.getValueForKey(transaccionCabecera,"autofactura_ciudad","af_ciudad"));
				dataMapFactura.put("pais", Core.getValueForKey(transaccionCabecera,"autofactura_pais","af_pais"));
				
				Map<String, Object> ubicacion = new HashMap<String, Object>();
				
				//Ubicacion de la Autofactura, local del cliente.
				ubicacion.put("lugar", Core.getValueForKey(transaccionCabecera,"cliente_direccion","c_direcc"));
				ubicacion.put("ciudad", Core.getValueForKey(transaccionCabecera,"cliente_ciudad","c_ciudad"));
				ubicacion.put("distrito", Core.getValueForKey(transaccionCabecera,"cliente_distrito","c_distri"));				
				ubicacion.put("departamento", Core.getValueForKey(transaccionCabecera,"cliente_departamento","c_depart"));
				dataMapFactura.put("ubicacion", ubicacion);
				
				dataMap.put("autoFactura", dataMapFactura);
			}				
				
			//NotaCredito y Debito
			if (tipoDocumento == 5) {
				Map<String, Object> dataMapNotaCreditoDebito = new HashMap<String, Object>();
				dataMapNotaCreditoDebito.put("motivo", Core.getValueForKey(transaccionCabecera,"nc_motivo","nc_motivo"));
			
				dataMap.put("notaCreditoDebito", dataMapNotaCreditoDebito);		
			}
			
			//NotaRemision			
			if (tipoDocumento == 7) {
				Map<String, Object> dataMapNotaRemision = new HashMap<String, Object>();
				dataMapNotaRemision.put("motivo", Core.getValueForKey(transaccionCabecera,"nota_remision_motivo","nr_motivo"));
				dataMapNotaRemision.put("tipoResponsable", Core.getValueForKey(transaccionCabecera,"nota_remision_tipo_responsable","nr_tip_res"));
				dataMapNotaRemision.put("kms", Core.getValueForKey(transaccionCabecera,"nota_remision_kms","nr_kms"));
	
				dataMap.put("remision", dataMapNotaRemision);
			}
			
			// Items de la compra
			List<Map<String, Object>> lista = new ArrayList<Map<String, Object>>();

			for (int i = 0; i < transaccionMap.size(); i++) {
				// Items de la compra
				Map<String, Object> dataMapProducto = new HashMap<String, Object>();
				Map<String, Object> transaccionItems = transaccionMap.get(i);
				
				if (Core.getValueForKey(transaccionItems,"item_codigo","i_codigo") != null) {
					dataMapProducto.put("codigo", Core.getValueForKey(transaccionItems,"item_codigo","i_codigo").toString().trim());
				}
				if (Core.getValueForKey(transaccionItems,"item_descripcion","i_descrip") != null) {
					dataMapProducto.put("descripcion", Core.getValueForKey(transaccionItems,"item_descripcion","i_descrip").toString().trim());
				}
				
				if (Core.getValueForKey(transaccionItems,"item_observacion","i_obs") != null) {
					dataMapProducto.put("observacion", Core.getValueForKey(transaccionItems,"item_observacion","i_obs").toString().trim());
				}
				
				dataMapProducto.put("partidaArancelaria", Core.getValueForKey(transaccionItems,"item_partida_arancelaria","i_par_ara"));
				dataMapProducto.put("ncm", Core.getValueForKey(transaccionItems,"item_ncm","i_ncm"));
				dataMapProducto.put("unidadMedida", Core.getValueForKey(transaccionItems,"item_unidad_medida","i_uni_med"));
				dataMapProducto.put("cantidad", Core.getValueForKey(transaccionItems,"item_cantidad","i_cantidad"));
				dataMapProducto.put("precioUnitario", Core.getValueForKey(transaccionItems,"item_precio_unitario","i_pre_uni"));
				dataMapProducto.put("cambio", Core.getValueForKey(transaccionItems,"item_cambio", "i_cambio"));
				dataMapProducto.put("descuento", Core.getValueForKey(transaccionItems,"item_descuento","i_descue"));
				dataMapProducto.put("anticipo", Core.getValueForKey(transaccionItems,"item_anticipo","i_anti"));
				dataMapProducto.put("pais", Core.getValueForKey(transaccionItems,"item_pais","i_pais"));
				dataMapProducto.put("tolerancia", Core.getValueForKey(transaccionItems,"item_tolerancia","i_tol"));
				dataMapProducto.put("toleranciaCantidad", Core.getValueForKey(transaccionItems,"item_tolerancia_cantidad","i_tol_can"));
				dataMapProducto.put("toleranciaPorcentaje", Core.getValueForKey(transaccionItems,"item_tolerancia_porcentaje","i_tol_por"));
				dataMapProducto.put("cdcAnticipo", Core.getValueForKey(transaccionItems,"item_cdc_anticipo","i_cdc_ant"));
				
				dataMapProducto.put("ivaTipo", Core.getValueForKey(transaccionItems,"item_iva_tipo","i_iva_tip"));
				dataMapProducto.put("ivaBase", Core.getValueForKey(transaccionItems,"item_iva_base","i_iva_bas"));
				dataMapProducto.put("iva", Core.getValueForKey(transaccionItems,"item_iva","i_iva"));
				dataMapProducto.put("lote", Core.getValueForKey(transaccionItems,"item_lote","i_lote"));
				dataMapProducto.put("vencimiento", Core.getValueForKey(transaccionItems,"item_vencimiento","i_venci"));
				dataMapProducto.put("numeroSerie", Core.getValueForKey(transaccionItems,"item_numero_serie","i_num_ser"));
				dataMapProducto.put("numeroPedido", Core.getValueForKey(transaccionItems,"item_numero_pedido","i_num_ped"));
				dataMapProducto.put("numeroSeguimiento", Core.getValueForKey(transaccionItems,"item_numero_seguimiento","i_num_seg"));
				dataMapProducto.put("registroSenave", Core.getValueForKey(transaccionItems,"item_registro_senave","i_reg_sen"));
				dataMapProducto.put("registroEntidadComercial", Core.getValueForKey(transaccionItems,"item_registro_entidad_comercial","i_reg_ent"));
				
				Map<String, Object> dataMapDncp = new HashMap<String, Object>();
				dataMapDncp.put("codigoNivelGeneral", Core.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_general","i_dncp_cng"));
				dataMapDncp.put("codigoNivelEspecifico", Core.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_especifico","i_dncp_cne"));
				dataMapDncp.put("codigoGtinProducto", Core.getValueForKey(transaccionItems,"item_dncp_codigo_gtin_producto","i_dncp_cgp"));
				dataMapDncp.put("codigoNivelPaquete", Core.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_paquete","i_dncp_cnp"));

				if (Core.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_general","i_dncp_cng") != null) {
					dataMapProducto.put("dncp", dataMapDncp);	
				}
				
				/*Map<String, Object> dataMapImportador = new HashMap<String, Object>();
				
				dataMapImportador.put("nombre", Core.getValueForKey(transaccionItems,"item_importador_nombre","i_imp_nom"));
				dataMapImportador.put("direccion", Core.getValueForKey(transaccionItems,"item_importador_direccion","i_imp_dir"));
				dataMapImportador.put("registroImportador", Core.getValueForKey(transaccionItems,"item_registro_importador","i_reg_imp"));
				
				if (Core.getValueForKey(transaccionItems,"item_importador_nombre","i_imp_nom") != null) {
					dataMapProducto.put("importador", dataMapImportador);	
				}*/
			
				dataMapProducto.put("sectorAutomotor", null);
				
				lista.add(dataMapProducto);

			}

			dataMap.put("items", lista);
			
			Map<String, Object> condicionMap = recuperarFormasDePagoParaCondicion(tipoDocumento, transaccionId, databaseProperties);
			
			
			if (condicionMap != null) {
				dataMap.put("condicion", condicionMap);	
			} else {
				//Para teste temporoal
				Map<String, Object> credito = new HashMap<String, Object>();
				credito.put("tipo", 1);
				credito.put("plazo", "30 dias");
				
				Map<String, Object> condicion = new HashMap<String, Object>();
				condicion.put("tipo", 2);
				condicion.put("credito", credito);
				
				
				
				dataMap.put("condicion", condicion);	

			}
			

			//DocumentoAsociado
			Map<String, Object> documentoAsociadoMap = new HashMap<String, Object>();
			documentoAsociadoMap.put("formato", Core.getValueForKey(transaccionCabecera,"documento_asociado_formato","d_aso_for"));
			
			if (Core.getValueForKey(transaccionCabecera,"documento_asociado_cdc","d_aso_cdc") != null) {
				documentoAsociadoMap.put("cdc", Core.getValueForKey(transaccionCabecera,"documento_asociado_cdc","d_aso_cdc").toString().trim());
			}

			documentoAsociadoMap.put("tipoDocumentoImpreso", Core.getValueForKey(transaccionCabecera,"documento_asociado_cdc","d_aso_tdi"));
			documentoAsociadoMap.put("timbrado", Core.getValueForKey(transaccionCabecera,"documento_asociado_timbrado","d_aso_tim"));
			documentoAsociadoMap.put("establecimiento", Core.getValueForKey(transaccionCabecera,"documento_asociado_establecimiento","d_aso_est"));
			documentoAsociadoMap.put("punto", Core.getValueForKey(transaccionCabecera,"documento_asociado_punto","d_aso_pun"));
			documentoAsociadoMap.put("numero", Core.getValueForKey(transaccionCabecera,"documento_asociado_numero","d_aso_num"));
			documentoAsociadoMap.put("fecha", Core.getValueForKey(transaccionCabecera,"documento_asociado_fecha","d_aso_fec"));
			documentoAsociadoMap.put("numeroRetencion", Core.getValueForKey(transaccionCabecera,"documento_asociado_numero_retencion","d_aso_ret"));
			documentoAsociadoMap.put("resolucionCreditoFiscal", Core.getValueForKey(transaccionCabecera,"documento_asociado_resolucion_credito_fiscal","d_aso_rcf"));
			documentoAsociadoMap.put("constanciaTipo", Core.getValueForKey(transaccionCabecera,"documento_asociado_constancia_tipo","d_aso_cti"));
			documentoAsociadoMap.put("constanciaNumero", Core.getValueForKey(transaccionCabecera,"documento_asociado_constancia_numero","d_aso_cnu"));
			documentoAsociadoMap.put("constanciaControl", Core.getValueForKey(transaccionCabecera,"documento_asociado_constancia_control","d_aso_cco"));
			
			if (Core.getValueForKey(transaccionCabecera,"documento_asociado_formato","d_aso_for") != null) {
				dataMap.put("documentoAsociado", documentoAsociadoMap);	
			}
			

			//Implementar Pedro
			dataMap.put("transporte", null);

			//A futuro
			dataMap.put("sectorEnergiaElectrica", null);
			dataMap.put("sectorSeguros", null);
			dataMap.put("sectorSupermercados", null);
			dataMap.put("sectorAdicional", null);
			dataMap.put("complementarios", null);
			

			
			
			
		}
		
		return dataMap;
		

	}
	

	/**
	 * Busca en la Base de datos las forma de pago de la Transaccion 
	 * de acuerdo al Tipo de Documento y retorna un map de condicion.
	 * 
	 * @param tipoDocumento
	 * @param transaccionId
	 * @return
	 */
	private static Map<String, Object> recuperarFormasDePagoParaCondicion(Integer tipoDocumento, Integer transaccionId, Map<String, String> databaseProperties) throws Exception {
		
		List<Map<String, Object>> paymentViewMap = Core.formasPagosByTransaccion(tipoDocumento, transaccionId, databaseProperties);
		
		if (paymentViewMap.size() <= 0) {
			return null;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		boolean esACredito = false;
		boolean yaEntroEnCredito = false;
		// Averiguar si hay forma de pago a credito
		for (int i = 0; i < paymentViewMap.size(); i++) {
			Map<String, Object> formaCobro = paymentViewMap.get(i);

			Integer tipo = Integer.valueOf(Core.getValueForKey(formaCobro,"tipo")+"");
			
			if (tipo.intValue() == 2) {
				esACredito = true;
			}

		}
		
		Map<String, Object> condicionMap = new HashMap<String, Object>();
		List<Map<String, Object>> entregasListMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> creditoMap = new HashMap<String, Object>();
		List<Map<String, Object>> infoCuotasListMap = new ArrayList<Map<String, Object>>();
		
		//Map<String, Object> creditoMap = new HashMap<String, Object>();
	
		condicionMap.put("tipo", esACredito ? 2 : 1);
				
		// Forma de Cobro
		Integer tipoFormaPagoAnterior = Integer.valueOf(Core.getValueForKey(paymentViewMap.get(0),"tipo") + "");
		//Integer monedaFormaPagoAnterior = Integer.valueOf(Core.getValueForKey(paymentViewMap.get(0),"moneda") + "");
		double sumatoriaMontoEntrega = 0.0;
		for (int i = 0; i < paymentViewMap.size(); i++) {
			Map<String, Object> formaCobro = paymentViewMap.get(i);

			Integer tipo = Integer.valueOf( Core.getValueForKey(formaCobro,"tipo") + "" );
			Integer creditoTipo = Integer.valueOf( Core.getValueForKey(formaCobro,"credito_tipo","c_tipo") + "" );
			
			if (tipo == 1) {
				Map<String, Object> efectivoMap = new HashMap<String, Object>();

				double monto = Double.valueOf(Core.getValueForKey(formaCobro,"monto")+"");
				efectivoMap.put("tipo", 1);
				efectivoMap.put("monto", monto);
				efectivoMap.put("moneda", Core.getValueForKey(formaCobro,"moneda"));
				efectivoMap.put("cambio", Core.getValueForKey(formaCobro,"cambio"));
				entregasListMap.add(efectivoMap);

				sumatoriaMontoEntrega += monto;
			}
			/*if (tipo == 6) { // Tarjeta de Credito
				Map<String, Object> tarjetaCreditoMap = new HashMap<String, Object>();
				tarjetaCreditoMap.put("tipo", 3);
				tarjetaCreditoMap.put("monto", formaCobro[1]);
				tarjetaCreditoMap.put("moneda", formaCobro[2]);
				tarjetaCreditoMap.put("monedaDescripcion", formaCobro[3]);
				tarjetaCreditoMap.put("cambio", formaCobro[4]);

				tarjetaCreditoMap.put("tipoTarjeta",formaCobro[17] );//codigo
				tarjetaCreditoMap.put("tipoDescripcion", formaCobro[18]);//tipo
				tarjetaCreditoMap.put("numeroTarjeta", formaCobro[8]);
				tarjetaCreditoMap.put("titular", formaCobro[11]);
				tarjetaCreditoMap.put("ruc", formaCobro[12]);
				// dataMapCobro.put("razonSocial", formaCobros.get(j)[]); 0/1 se podria enviar nulo, hablar con marcos
				tarjetaCreditoMap.put("medioPago", 1);
				tarjetaCreditoMap.put("codigoAutorizacion", formaCobro[9]);
				entregasListMap.add(tarjetaCreditoMap);

				sumatoriaMontoEntrega += Double.valueOf(formaCobro[1] + "").doubleValue();

			}
			if (tipo == 3) { // Cheques
				Map<String, Object> chequeMap = new HashMap<String, Object>();
				chequeMap.put("tipo", 2);
				chequeMap.put("monto", formaCobro[1]);
				chequeMap.put("moneda", formaCobro[2]);
				chequeMap.put("monedaDescripcion", formaCobro[3]);
				chequeMap.put("cambio", formaCobro[4]);
				chequeMap.put("numeroCheque", formaCobros.get(0)[5]);
				chequeMap.put("banco", formaCobros.get(0)[6]);

				entregasListMap.add(chequeMap);

				sumatoriaMontoEntrega += Double.valueOf(formaCobro[1] + "").doubleValue();

			}*/

			if (tipo == 2) { 
				// CREDITO
				if (creditoTipo == 1) {
					//A Plazo
					//Map<String, Object> creditoMap = new HashMap<String, Object>();

					creditoMap.put("tipo", 1);	//1=Plazo,2=Cuotas
					creditoMap.put("plazo", Core.getValueForKey(formaCobro,"credito_plazo","c_plazo") + "");
					
					//creditoMap.put("credito", creditoMap);

				} else {
					//A Cuotas
					if (!yaEntroEnCredito) {
						//List<Object[]> creditos = filtrarElementosCredito(formaCobros);
						List<Object[]> creditos = new ArrayList<Object[]>();	//Para que no de error
						//Map<String, Object> creditoMap = new HashMap<String, Object>();

						creditoMap.put("tipo", 2); // Siempre fijo, en "Cuotas"
						creditoMap.put("cuotas", creditos.size());
						System.out.println("cantidad de cuotas " + creditos.size());
						creditoMap.put("montoEntrega", sumatoriaMontoEntrega);
						// infoCuotas
						System.out.println("creditos " + creditos.size());
						for (int j = 0; j < creditos.size(); j++) {
							Map<String, Object> dataMapCobroCredito = new HashMap<String, Object>();
							Object[] formasCobros = creditos.get(j);
							dataMapCobroCredito.put("moneda", formasCobros[14]);
							dataMapCobroCredito.put("monto", formasCobros[1]);
							dataMapCobroCredito.put("vencimiento", sdf.format((Date) formasCobros[15]));

							infoCuotasListMap.add(dataMapCobroCredito);

						}
						creditoMap.put("infoCuotas", entregasListMap);
						yaEntroEnCredito = true;
					}					
				}
			}

		}
		if (entregasListMap != null && entregasListMap.size() > 0) {
			condicionMap.put("entregas", entregasListMap);
		}
		if (esACredito) {
			condicionMap.put("credito", creditoMap);
		}
		
		return 	(condicionMap);
	}
	
	//Analizar como usar este aqui.
	private List<Object[]> filtrarElementosCredito(List<Object[]> formaCobrosOriginal) {
		List<Object[]> formaCobrosReplicados = new ArrayList<Object[]>();
		for (int i = 0; i < formaCobrosOriginal.size(); i++) {
			Object[] formaCobro = formaCobrosOriginal.get(i);
			if (Integer.valueOf(formaCobro[0] + "").intValue() == 2) {
				formaCobrosReplicados.add(formaCobro);
			}
		}
		return formaCobrosReplicados;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
