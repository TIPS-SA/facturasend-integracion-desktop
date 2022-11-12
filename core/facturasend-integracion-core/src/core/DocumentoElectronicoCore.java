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

	/**
	 * Retorna un Map con 2 listas independientes, pero con el mismo indice, para su uso posterior
	 * 	1 el array de Jsons que se deben enviar a facturasend
	 * 	2 el array de transacciones que esta relacionado con cada documento electronico, del array
	 * 
	 * @param transactionIds
	 * @param documentosParaEnvioAllList
	 * @param parmentViewAllList
	 * @param databaseProperties
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> generarJSONLote(String[] transactionIds, List<Map<String, Object>> documentosParaEnvioAllList, List<Map<String, Object>> parmentViewAllList, Map<String, String> databaseProperties) throws Exception{
		 
		Map<String, Object> result = new HashMap<String, Object>();
		List<Map<String, Object>> jsonDEs = new ArrayList<Map<String,Object>>();
		List<List<Map<String,Object>>> transacctionsView = new ArrayList<List<Map<String,Object>>>();
			
		for (int i = 0; i < transactionIds.length; i++) {
			if (!transactionIds[i].trim().isEmpty()) {
				Integer transaccionId = Integer.valueOf(transactionIds[i].trim());
				
				//---
				List<Map<String, Object>> documentosParaEnvioFiltradoList = documentosParaEnvioAllList.stream().filter( map -> {
					String transaccionIdString = CoreService.getValueForKey(map, "transaccion_id", "tra_id") + "";
					
					Integer transaccionIdIterate = new BigDecimal(transaccionIdString).intValue();
					
					return transaccionIdIterate == transaccionId.intValue();
				}).collect(Collectors.toList());
				
				//---
				//System.out.println("parmentViewAllList " + parmentViewAllList);
				List<Map<String, Object>> parmentViewFiltradoList = parmentViewAllList.stream().filter( map -> {
					String transaccionIdString = CoreService.getValueForKey(map, "transaccion_id", "tra_id") + "";
					
					Integer transaccionIdIterate = new BigDecimal(transaccionIdString).intValue();
					
					return transaccionIdIterate == transaccionId.intValue();
				}).collect(Collectors.toList());

				//---
				if (documentosParaEnvioFiltradoList.size() > 0) {
					Map<String, Object> jsonDE = invocarDocumentoElectronicoDesdeView(documentosParaEnvioFiltradoList, parmentViewFiltradoList, databaseProperties);
					
					jsonDEs.add(jsonDE);
					transacctionsView.add(documentosParaEnvioFiltradoList);
				}
			}
		}
		result.put("jsonDEs", jsonDEs);
		result.put("documentosParaEnvioFiltradoList", transacctionsView);
		return result;
	}
			
	/**
	 * Prepara los datos para enviar al Sistema de Facturación electronica, teniendo en cuenta los datos de una AutoFactura de Compra.
	 * 
	 * @param timbradoFacturacionMovimiento
	 * @param usuario
	 * @param sucursal
	 * @throws InfoException
	 */
	public static Map<String, Object> invocarDocumentoElectronicoDesdeView(List<Map<String, Object>> transaccionMap, List<Map<String, Object>> paymentViewMap, Map<String, String> databaseProperties) throws Exception{
		Map<String, Object> dataMap = new HashMap<String, Object>();

		System.out.println("Procesando... " + gson.toJson(transaccionMap));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (transaccionMap != null && transaccionMap.size() > 0) {
			System.out.println("1");
			Map<String, Object> transaccionCabecera = transaccionMap.get(0);
			System.out.println("2");

			Integer transaccionId = Integer.valueOf(CoreService.getValueForKey(transaccionCabecera, "transaccion_id", "tra_id") + "");
			Integer tipoDocumento = Integer.valueOf(CoreService.getValueForKey(transaccionCabecera, "tipo_documento", "tip_doc") + "");

			dataMap.put("tipoDocumento", tipoDocumento);
			String cdcGenerado = (String) CoreService.getValueForKey(transaccionCabecera, "cdc");
			System.out.println("3");

			if (cdcGenerado != null && cdcGenerado.length() == 44) {
				dataMap.put("cdc", cdcGenerado); //Si ya fue generado con un CDC, entonces envía para utilizar el mismo.
			}

			dataMap.put("establecimiento", CoreService.getValueForKey(transaccionCabecera, "establecimiento", "estable"));
			dataMap.put("punto", CoreService.getValueForKey(transaccionCabecera, "punto"));
			dataMap.put("numero", CoreService.getValueForKey(transaccionCabecera, "numero"));
			dataMap.put("serie", CoreService.getValueForKey(transaccionCabecera, "serie"));
			if (CoreService.getValueForKey(transaccionCabecera,"descripcion","descrip") != null) {
				dataMap.put("descripcion", CoreService.getValueForKey(transaccionCabecera, "descripcion", "descrip").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"observacion","observa") != null) {
				dataMap.put("observacion", CoreService.getValueForKey(transaccionCabecera,"observacion","observa").toString().trim());
			}

			if ( CoreService.getValueForKey(transaccionCabecera,"fecha") instanceof Date) {
				dataMap.put("fecha", sdf.format((Date) CoreService.getValueForKey(transaccionCabecera,"fecha")) );
			} else {
				dataMap.put("fecha", CoreService.getValueForKey(transaccionCabecera,"fecha") );
			}
			dataMap.put("tipoEmision", CoreService.getValueForKey(transaccionCabecera, "tipo_emision", "tip_emi"));
			dataMap.put("tipoTransaccion", CoreService.getValueForKey(transaccionCabecera, "tipo_transaccion", "tip_tra"));
			dataMap.put("tipoImpuesto", CoreService.getValueForKey(transaccionCabecera, "tipo_impuesto", "tip_imp"));
			
			if (CoreService.getValueForKey(transaccionCabecera,"moneda") != null) {
				dataMap.put("moneda", CoreService.getValueForKey(transaccionCabecera, "moneda").toString().trim());
			}
			dataMap.put("condicionAnticipo", CoreService.getValueForKey(transaccionCabecera, "anticipo"));
			dataMap.put("descuentoGlobal", CoreService.getValueForKey(transaccionCabecera, "descuento_global", "des_glo"));
			dataMap.put("anticipoGlobal", CoreService.getValueForKey(transaccionCabecera, "anticipo_global", "ant_glo"));
			dataMap.put("total", CoreService.getValueForKey(transaccionCabecera, "total"));

			dataMap.put("condicionTipoCambio", CoreService.getValueForKey(transaccionCabecera,"tipo_cambio","tip_cam"));			
			dataMap.put("cambio", CoreService.getValueForKey(transaccionCabecera,"cambio"));
			
			// Cliente
			Map<String, Object> cliente = new HashMap<String, Object>();
			
			cliente.put("contribuyente", Boolean.valueOf(CoreService.getValueForKey(transaccionCabecera,"cliente_contribuyente","c_contribu")+""));
			
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_ruc","c_ruc") != null) {
				cliente.put("ruc", CoreService.getValueForKey(transaccionCabecera,"cliente_ruc","c_ruc").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_razon_social","c_raz_soc") != null) {
				cliente.put("razonSocial", CoreService.getValueForKey(transaccionCabecera,"cliente_razon_social","c_raz_soc").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_nombre_fantasia","c_nom_fan") != null) {
				cliente.put("nombreFantasia", CoreService.getValueForKey(transaccionCabecera,"cliente_nombre_fantasia","c_nom_fan").toString().trim());
			}

			cliente.put("tipoOperacion", CoreService.getValueForKey(transaccionCabecera,"cliente_tipo_operacion","c_tip_ope"));
			
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_direccion","c_direcc") != null) {
				cliente.put("direccion", CoreService.getValueForKey(transaccionCabecera,"cliente_direccion","c_direcc").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_numero_casa","c_num_cas") != null) {
				cliente.put("numeroCasa", CoreService.getValueForKey(transaccionCabecera,"cliente_numero_casa","c_num_cas").toString().trim());
			}

			cliente.put("ciudad", CoreService.getValueForKey(transaccionCabecera,"cliente_ciudad","c_ciudad"));
			cliente.put("departamento", CoreService.getValueForKey(transaccionCabecera,"cliente_departamento","c_depart"));
			cliente.put("distrito", CoreService.getValueForKey(transaccionCabecera,"cliente_distrito","c_distri"));
			cliente.put("pais", CoreService.getValueForKey(transaccionCabecera,"cliente_pais","c_pais"));

			cliente.put("tipoContribuyente", CoreService.getValueForKey(transaccionCabecera,"cliente_tipo_contribuyente","c_tip_con"));
					
			cliente.put("documentoTipo", CoreService.getValueForKey(transaccionCabecera,"cliente_documento_tipo","c_doc_tip"));
			
			cliente.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"cliente_documento_numero","c_doc_num"));
			
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_telefono","c_tel") != null) {
				cliente.put("telefono", CoreService.getValueForKey(transaccionCabecera,"cliente_telefono","c_tel").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_celular","c_cel") != null) {
				cliente.put("celular", CoreService.getValueForKey(transaccionCabecera,"cliente_celular","c_cel").toString().trim() );
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_email","c_ema") != null) {
				cliente.put("email", CoreService.getValueForKey(transaccionCabecera,"cliente_email","c_ema").toString().trim() );
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_codigo","c_cod") != null) {
				cliente.put("codigo", CoreService.getValueForKey(transaccionCabecera,"cliente_codigo","c_cod").toString().trim() );
			}
			
			dataMap.put("cliente", cliente );
			//FIN CLIENTE
			
			
			// inicioUsuario
			Map<String, Object> dataMapUsuario = new HashMap<String, Object>();

			dataMapUsuario.put("documentoTipo", CoreService.getValueForKey(transaccionCabecera,"usuario_documento_tipo","u_doc_tip"));
			
			if (CoreService.getValueForKey(transaccionCabecera,"usuario_documento_numero","u_doc_num") != null) {
				dataMapUsuario.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"usuario_documento_numero","u_doc_num").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"usuario_nombre","u_nom") != null) {
				dataMapUsuario.put("nombre", CoreService.getValueForKey(transaccionCabecera,"usuario_nombre","u_nom").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"usuario_cargo","u_car") != null) {
				dataMapUsuario.put("cargo", CoreService.getValueForKey(transaccionCabecera,"usuario_cargo","u_car").toString().trim());
			}
			dataMap.put("usuario", dataMapUsuario);
			//finUsuario
			System.out.println("55555");

			//Factura
			if (tipoDocumento == 1) {
				Map<String, Object> dataMapFactura1 = new HashMap<String, Object>();
				dataMapFactura1.put("presencia", CoreService.getValueForKey(transaccionCabecera,"factura_presencia","fa_pre"));
				dataMapFactura1.put("fechaEnvio", CoreService.getValueForKey(transaccionCabecera,"factura_fecha_envio","fa_fec_env"));
				dataMapFactura1.put("ticket", CoreService.getValueForKey(transaccionCabecera,"factura_ticket","fa_ticket"));				
				dataMap.put("factura", dataMapFactura1);
			}
			
			//AutoFactura
			if (tipoDocumento == 4) {
				Map<String, Object> dataMapFactura = new HashMap<String, Object>();
				dataMapFactura.put("tipoVendedor", CoreService.getValueForKey(transaccionCabecera,"autofactura_tipo_vendedor","af_tip_ven"));
				dataMapFactura.put("documentoTipo", CoreService.getValueForKey(transaccionCabecera,"autofactura_documento_tipo","af_doc_tip"));
				dataMapFactura.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"autofactura_documento_numero","af_doc_num"));
				dataMapFactura.put("nombre", CoreService.getValueForKey(transaccionCabecera,"autofactura_nombre","af_tip_ven"));				
				dataMapFactura.put("direccion", CoreService.getValueForKey(transaccionCabecera,"autofactura_direccion","af_tip_ven"));
				dataMapFactura.put("numeroCasa", CoreService.getValueForKey(transaccionCabecera,"autofactura_numero_casa","af_num_cas"));
				dataMapFactura.put("departamento", CoreService.getValueForKey(transaccionCabecera,"autofactura_departamento","af_depart"));
				dataMapFactura.put("distrito", CoreService.getValueForKey(transaccionCabecera,"autofactura_distrito","af_distri"));
				dataMapFactura.put("ciudad", CoreService.getValueForKey(transaccionCabecera,"autofactura_ciudad","af_ciudad"));
				dataMapFactura.put("pais", CoreService.getValueForKey(transaccionCabecera,"autofactura_pais","af_pais"));
				
				Map<String, Object> ubicacion = new HashMap<String, Object>();
				
				//Ubicacion de la Autofactura, local del cliente.
				ubicacion.put("lugar", CoreService.getValueForKey(transaccionCabecera,"cliente_direccion","c_direcc"));
				ubicacion.put("ciudad", CoreService.getValueForKey(transaccionCabecera,"cliente_ciudad","c_ciudad"));
				ubicacion.put("distrito", CoreService.getValueForKey(transaccionCabecera,"cliente_distrito","c_distri"));				
				ubicacion.put("departamento", CoreService.getValueForKey(transaccionCabecera,"cliente_departamento","c_depart"));
				dataMapFactura.put("ubicacion", ubicacion);
				
				dataMap.put("autoFactura", dataMapFactura);
			}				
				
			//NotaCredito y Debito
			if (tipoDocumento == 5) {
				Map<String, Object> dataMapNotaCreditoDebito = new HashMap<String, Object>();
				dataMapNotaCreditoDebito.put("motivo", CoreService.getValueForKey(transaccionCabecera,"nc_motivo","nc_motivo"));
			
				dataMap.put("notaCreditoDebito", dataMapNotaCreditoDebito);		
			}
			
			//NotaRemision			
			if (tipoDocumento == 7) {
				Map<String, Object> dataMapNotaRemision = new HashMap<String, Object>();
				dataMapNotaRemision.put("motivo", CoreService.getValueForKey(transaccionCabecera,"nota_remision_motivo","nr_motivo"));
				dataMapNotaRemision.put("tipoResponsable", CoreService.getValueForKey(transaccionCabecera,"nota_remision_tipo_responsable","nr_tip_res"));
				dataMapNotaRemision.put("kms", CoreService.getValueForKey(transaccionCabecera,"nota_remision_kms","nr_kms"));
	
				dataMap.put("remision", dataMapNotaRemision);
			}
			
			// Items de la compra
			List<Map<String, Object>> lista = new ArrayList<Map<String, Object>>();

			System.out.println("6666");

			for (int i = 0; i < transaccionMap.size(); i++) {
				System.out.println("ciclo .. " + i + " size " + transaccionMap.size());

				// Items de la compra
				Map<String, Object> dataMapProducto = new HashMap<String, Object>();
				Map<String, Object> transaccionItems = transaccionMap.get(i);
				
				if (CoreService.getValueForKey(transaccionItems,"item_codigo","i_codigo") != null) {
					dataMapProducto.put("codigo", CoreService.getValueForKey(transaccionItems,"item_codigo","i_codigo").toString().trim());
				}
				if (CoreService.getValueForKey(transaccionItems,"item_descripcion","i_descrip") != null) {
					dataMapProducto.put("descripcion", CoreService.getValueForKey(transaccionItems,"item_descripcion","i_descrip").toString().trim());
				}
				
				if (CoreService.getValueForKey(transaccionItems,"item_observacion","i_obs") != null) {
					dataMapProducto.put("observacion", CoreService.getValueForKey(transaccionItems,"item_observacion","i_obs").toString().trim());
				}
				
				dataMapProducto.put("partidaArancelaria", CoreService.getValueForKey(transaccionItems,"item_partida_arancelaria","i_par_ara"));
				dataMapProducto.put("ncm", CoreService.getValueForKey(transaccionItems,"item_ncm","i_ncm"));
				dataMapProducto.put("unidadMedida", CoreService.getValueForKey(transaccionItems,"item_unidad_medida","i_uni_med"));
				dataMapProducto.put("cantidad", CoreService.getValueForKey(transaccionItems,"item_cantidad","i_cantidad"));
				dataMapProducto.put("precioUnitario", CoreService.getValueForKey(transaccionItems,"item_precio_unitario","i_pre_uni"));
				dataMapProducto.put("cambio", CoreService.getValueForKey(transaccionItems,"item_cambio", "i_cambio"));
				dataMapProducto.put("descuento", CoreService.getValueForKey(transaccionItems,"item_descuento","i_descue"));
				dataMapProducto.put("anticipo", CoreService.getValueForKey(transaccionItems,"item_anticipo","i_anti"));
				dataMapProducto.put("pais", CoreService.getValueForKey(transaccionItems,"item_pais","i_pais"));
				dataMapProducto.put("tolerancia", CoreService.getValueForKey(transaccionItems,"item_tolerancia","i_tol"));
				dataMapProducto.put("toleranciaCantidad", CoreService.getValueForKey(transaccionItems,"item_tolerancia_cantidad","i_tol_can"));
				dataMapProducto.put("toleranciaPorcentaje", CoreService.getValueForKey(transaccionItems,"item_tolerancia_porcentaje","i_tol_por"));
				dataMapProducto.put("cdcAnticipo", CoreService.getValueForKey(transaccionItems,"item_cdc_anticipo","i_cdc_ant"));
				
				dataMapProducto.put("ivaTipo", CoreService.getValueForKey(transaccionItems,"item_iva_tipo","i_iva_tip"));
				dataMapProducto.put("ivaBase", CoreService.getValueForKey(transaccionItems,"item_iva_base","i_iva_bas"));
				dataMapProducto.put("iva", CoreService.getValueForKey(transaccionItems,"item_iva","i_iva"));
				dataMapProducto.put("lote", CoreService.getValueForKey(transaccionItems,"item_lote","i_lote"));
				
				dataMapProducto.put("vencimiento", CoreService.getValueForKey(transaccionItems,"item_vencimiento","i_venci"));
				dataMapProducto.put("numeroSerie", CoreService.getValueForKey(transaccionItems,"item_numero_serie","i_num_ser"));
				dataMapProducto.put("numeroPedido", CoreService.getValueForKey(transaccionItems,"item_numero_pedido","i_num_ped"));
				dataMapProducto.put("numeroSeguimiento", CoreService.getValueForKey(transaccionItems,"item_numero_seguimiento","i_num_seg"));
				dataMapProducto.put("registroSenave", CoreService.getValueForKey(transaccionItems,"item_registro_senave","i_reg_sen"));
				dataMapProducto.put("registroEntidadComercial", CoreService.getValueForKey(transaccionItems,"item_registro_entidad_comercial","i_reg_ent"));
				
				Map<String, Object> dataMapDncp = new HashMap<String, Object>();
				dataMapDncp.put("codigoNivelGeneral", CoreService.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_general","i_dncp_cng"));
				dataMapDncp.put("codigoNivelEspecifico", CoreService.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_especifico","i_dncp_cne"));
				dataMapDncp.put("codigoGtinProducto", CoreService.getValueForKey(transaccionItems,"item_dncp_codigo_gtin_producto","i_dncp_cgp"));
				dataMapDncp.put("codigoNivelPaquete", CoreService.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_paquete","i_dncp_cnp"));

				if (CoreService.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_general","i_dncp_cng") != null) {
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
			System.out.println("termino ... ");

			dataMap.put("items", lista);
			
			System.out.println("termino ... 2 ");

			Map<String, Object> condicionMap = recuperarFormasDePagoParaCondicion(tipoDocumento, paymentViewMap, databaseProperties);
			
			System.out.println("termino ... 3 ");

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
			
			System.out.println("termino ... 4 ");

			//DocumentoAsociado
			Map<String, Object> documentoAsociadoMap = new HashMap<String, Object>();
			documentoAsociadoMap.put("formato", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_formato","d_aso_for"));
			
			if (CoreService.getValueForKey(transaccionCabecera,"documento_asociado_cdc","d_aso_cdc") != null) {
				documentoAsociadoMap.put("cdc", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_cdc","d_aso_cdc").toString().trim());
			}

			documentoAsociadoMap.put("tipoDocumentoImpreso", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_cdc","d_aso_tdi"));
			documentoAsociadoMap.put("timbrado", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_timbrado","d_aso_tim"));
			documentoAsociadoMap.put("establecimiento", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_establecimiento","d_aso_est"));
			documentoAsociadoMap.put("punto", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_punto","d_aso_pun"));
			documentoAsociadoMap.put("numero", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_numero","d_aso_num"));
			documentoAsociadoMap.put("fecha", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_fecha","d_aso_fec"));
			documentoAsociadoMap.put("numeroRetencion", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_numero_retencion","d_aso_ret"));
			documentoAsociadoMap.put("resolucionCreditoFiscal", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_resolucion_credito_fiscal","d_aso_rcf"));
			documentoAsociadoMap.put("constanciaTipo", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_constancia_tipo","d_aso_cti"));
			documentoAsociadoMap.put("constanciaNumero", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_constancia_numero","d_aso_cnu"));
			documentoAsociadoMap.put("constanciaControl", CoreService.getValueForKey(transaccionCabecera,"documento_asociado_constancia_control","d_aso_cco"));
			
			if (CoreService.getValueForKey(transaccionCabecera,"documento_asociado_formato","d_aso_for") != null) {
				dataMap.put("documentoAsociado", documentoAsociadoMap);	
			}
			
			System.out.println("termino ... 5 ");


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
	private static Map<String, Object> recuperarFormasDePagoParaCondicion(Integer tipoDocumento, List<Map<String, Object>> paymentViewMap, Map<String, String> databaseProperties) throws Exception {
		
		//List<Map<String, Object>> paymentViewMap = Core.formasPagosByTransaccion(tipoDocumento, transaccionId, databaseProperties);
		System.out.println("Despues de la llamada " + paymentViewMap);
		if (paymentViewMap.size() <= 0) {
			return null;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		boolean esACredito = false;
		boolean yaEntroEnCredito = false;
		// Averiguar si hay forma de pago a credito
		for (int i = 0; i < paymentViewMap.size(); i++) {
			Map<String, Object> formaCobro = paymentViewMap.get(i);

			Integer tipo = Integer.valueOf(CoreService.getValueForKey(formaCobro,"tipo")+"");
			
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
		Integer tipoFormaPagoAnterior = Integer.valueOf(CoreService.getValueForKey(paymentViewMap.get(0),"tipo") + "");
		//Integer monedaFormaPagoAnterior = Integer.valueOf(Core.getValueForKey(paymentViewMap.get(0),"moneda") + "");
		double sumatoriaMontoEntrega = 0.0;
		for (int i = 0; i < paymentViewMap.size(); i++) {
			Map<String, Object> formaCobro = paymentViewMap.get(i);

			Integer tipo = Integer.valueOf( CoreService.getValueForKey(formaCobro,"tipo") + "" );
			Integer creditoTipo = Integer.valueOf( CoreService.getValueForKey(formaCobro,"credito_tipo","c_tipo") + "" );
			
			if (tipo == 1) {
				Map<String, Object> efectivoMap = new HashMap<String, Object>();

				double monto = Double.valueOf(CoreService.getValueForKey(formaCobro,"monto")+"");
				efectivoMap.put("tipo", 1);
				efectivoMap.put("monto", monto);
				efectivoMap.put("moneda", CoreService.getValueForKey(formaCobro,"moneda"));
				efectivoMap.put("cambio", CoreService.getValueForKey(formaCobro,"cambio"));
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
					creditoMap.put("plazo", CoreService.getValueForKey(formaCobro,"credito_plazo","c_plazo") + "");
					
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
