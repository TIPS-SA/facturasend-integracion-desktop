package core;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

public class CoreDocumentoElectronico {
	
	private static Gson gson = new Gson();
	public static Log log = LogFactory.getLog(CoreDocumentoElectronico.class);

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

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (transaccionMap != null && transaccionMap.size() > 0) {
			
			Map<String, Object> transaccionCabecera = transaccionMap.get(0);
			
			Integer transaccionId = Integer.valueOf(CoreService.getValueForKey(transaccionCabecera, "transaccion_id", "tra_id") + "");
			Integer tipoDocumento = Integer.valueOf(CoreService.getValueForKey(transaccionCabecera, "tipo_documento", "tip_doc") + "");

			dataMap.put("tipoDocumento", tipoDocumento);
			String cdcGenerado = (String) CoreService.getValueForKey(transaccionCabecera, "cdc");
			
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
				dataMapNotaCreditoDebito.put("motivo", CoreService.getValueForKey(transaccionCabecera,"nota_credito_motivo","nc_motivo"));
			
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

			for (int i = 0; i < transaccionMap.size(); i++) {
				
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
			
			dataMap.put("items", lista);
			
			Map<String, Object> condicionMap = recuperarFormasDePagoParaCondicion(tipoDocumento, paymentViewMap, databaseProperties);
			
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
			documentoAsociadoMap.put("formato", CoreService.getValueForKey(transaccionCabecera,"doc_aso_formato","d_aso_for"));
			
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_cdc","d_aso_cdc") != null) {
				documentoAsociadoMap.put("cdc", CoreService.getValueForKey(transaccionCabecera,"doc_aso_cdc","d_aso_cdc").toString().trim());
			}

			documentoAsociadoMap.put("tipoDocumentoImpreso", CoreService.getValueForKey(transaccionCabecera,"doc_aso_cdc","d_aso_tdi"));
			documentoAsociadoMap.put("timbrado", CoreService.getValueForKey(transaccionCabecera,"doc_aso_timbrado","d_aso_tim"));
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_establecimiento","d_aso_est") != null) {
				documentoAsociadoMap.put("establecimiento", CoreService.getValueForKey(transaccionCabecera,"doc_aso_establecimiento","d_aso_est").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_punto","d_aso_pun") != null) {
				documentoAsociadoMap.put("punto", CoreService.getValueForKey(transaccionCabecera,"doc_aso_punto","d_aso_pun").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"doc_aso_numero","d_aso_num") != null){
				documentoAsociadoMap.put("numero", CoreService.getValueForKey(transaccionCabecera,"doc_aso_numero","d_aso_num").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_fecha","d_aso_fec") != null) {
				documentoAsociadoMap.put("fecha", CoreService.getValueForKey(transaccionCabecera,"doc_aso_fecha","d_aso_fec").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"doc_aso_numero_retencion","d_aso_ret") != null) {
				documentoAsociadoMap.put("numeroRetencion", CoreService.getValueForKey(transaccionCabecera,"doc_aso_numero_retencion","d_aso_ret").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"doc_aso_reso_credito_fiscal","d_aso_rcf") != null) {
				documentoAsociadoMap.put("resolucionCreditoFiscal", CoreService.getValueForKey(transaccionCabecera,"doc_aso_reso_credito_fiscal","d_aso_rcf").toString().trim());
			}
			documentoAsociadoMap.put("constanciaTipo", CoreService.getValueForKey(transaccionCabecera,"doc_aso_constancia_tipo","d_aso_cti"));
			documentoAsociadoMap.put("constanciaNumero", CoreService.getValueForKey(transaccionCabecera,"doc_aso_constancia_numero","d_aso_cnu"));
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_constancia_control","d_aso_cco") != null) {
				documentoAsociadoMap.put("constanciaControl", CoreService.getValueForKey(transaccionCabecera,"doc_aso_constancia_control","d_aso_cco").toString().trim());
			}
			
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_formato","d_aso_for") != null) {
				dataMap.put("documentoAsociado", documentoAsociadoMap);	
			}
			
			Map<String, Object> transporteMap = new HashMap<String, Object>();
			transporteMap.put("tipo", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip"));
			transporteMap.put("modalidad", CoreService.getValueForKey(transaccionCabecera,"tra_modalidad","t_mod"));
			transporteMap.put("tipoResponsable", CoreService.getValueForKey(transaccionCabecera,"tra_tipo_responsable","t_tip_res"));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_condicion_negociacion","t_con_neg") != null) {
				transporteMap.put("condicionNegociacion", CoreService.getValueForKey(transaccionCabecera,"tra_condicion_negociacion","t_con_neg").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip") != null &&
					((BigDecimal)CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip")).intValue() != 0 &&
					CoreService.getValueForKey(transaccionCabecera,"tra_modalidad","t_mod") != null &&
					((BigDecimal)CoreService.getValueForKey(transaccionCabecera,"tra_modalidad","t_mod")).intValue() != 0) {
				
				dataMap.put("transporte", transporteMap);	//Agrega transporte si tiene datos básicos	
			}
				
			
			
			/*transporteMap.put("NumeroManifiesto", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip"));
			transporteMap.put("numeroDespachoImportacion", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip"));
			transporteMap.put("inicioEstimadoTranslado", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip"));
			transporteMap.put("finEstimadoTranslado", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip"));
			transporteMap.put("paisDestino", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip"));
			transporteMap.put("paisDestinoNombre", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip"));*/
			
			
			Map<String, Object> transporteSalidaMap = new HashMap<String, Object>();
			if (CoreService.getValueForKey(transaccionCabecera,"tra_sali_direccion","t_sal_dir") != null) {
				transporteSalidaMap.put("direccion", CoreService.getValueForKey(transaccionCabecera,"tra_sali_direccion","t_sal_dir").toString().trim());
			}
			transporteSalidaMap.put("numeroCasa", CoreService.getValueForKey(transaccionCabecera,"tra_sali_numero_casa","t_sal_nca"));
			if(CoreService.getValueForKey(transaccionCabecera,"tra_sali_comple_direccion1","t_sal_cd1")!= null) {
				transporteSalidaMap.put("complementoDireccion1", CoreService.getValueForKey(transaccionCabecera,"tra_sali_comple_direccion1","t_sal_cd1").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_sali_comple_direccion2","t_sal_cd2")!= null) {
				transporteSalidaMap.put("complementoDireccion2", CoreService.getValueForKey(transaccionCabecera,"tra_sali_comple_direccion2","t_sal_cd2").toString().trim());
			}
			transporteSalidaMap.put("departamento", CoreService.getValueForKey(transaccionCabecera,"tra_sali_departamento","t_sal_dep"));
			transporteSalidaMap.put("distrito", CoreService.getValueForKey(transaccionCabecera,"tra_sali_distrito","t_sal_dis"));
			transporteSalidaMap.put("ciudad", CoreService.getValueForKey(transaccionCabecera,"tra_sali_ciudad","t_sal_ciu"));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_sali_pais","t_sal_pai") != null) {				
				transporteSalidaMap.put("pais", CoreService.getValueForKey(transaccionCabecera,"tra_sali_pais","t_sal_pai").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_sali_telefono_contacto","t_sal_tel") != null) {
				transporteSalidaMap.put("telefonoContacto", CoreService.getValueForKey(transaccionCabecera,"tra_sali_telefono_contacto","t_sal_tel").toString().trim());
			}
			
			
			Map<String, Object> transporteEntregaMap = new HashMap<String, Object>();
			if(CoreService.getValueForKey(transaccionCabecera,"tra_entre_direccion","t_ent_dir") != null) {
				transporteEntregaMap.put("direccion", CoreService.getValueForKey(transaccionCabecera,"tra_entre_direccion","t_ent_dir").toString().trim());
			}
			transporteEntregaMap.put("numeroCasa", CoreService.getValueForKey(transaccionCabecera,"tra_entre_numero_casa","t_ent_nca"));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_entre_comple_direccion1","t_ent_cd1") != null) { 
				transporteEntregaMap.put("complementoDireccion1", CoreService.getValueForKey(transaccionCabecera,"tra_entre_comple_direccion1","t_ent_cd1").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_entre_comple_direccion2","t_ent_cd2") != null) {
				transporteEntregaMap.put("complementoDireccion2", CoreService.getValueForKey(transaccionCabecera,"tra_entre_comple_direccion2","t_ent_cd2").toString().trim());
			}
			transporteEntregaMap.put("departamento", CoreService.getValueForKey(transaccionCabecera,"tra_entre_departamento","t_ent_dep"));
			transporteEntregaMap.put("distrito", CoreService.getValueForKey(transaccionCabecera,"tra_entre_distrito","t_ent_dis"));
			transporteEntregaMap.put("ciudad", CoreService.getValueForKey(transaccionCabecera,"tra_entre_ciudad","t_ent_ciu"));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_entre_pais","t_ent_pai") != null) {
				transporteEntregaMap.put("pais", CoreService.getValueForKey(transaccionCabecera,"tra_entre_pais","t_ent_pai").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_entre_telefono_contacto","t_ent_tel") != null) {
				transporteEntregaMap.put("telefonoContacto", CoreService.getValueForKey(transaccionCabecera,"tra_entre_telefono_contacto","t_ent_tel").toString().trim());
			}
			
			
			Map<String, Object> transporteVehiculoMap = new HashMap<String, Object>();
			if (CoreService.getValueForKey(transaccionCabecera,"tra_vehi_tipo","t_veh_tip") != null) {
				transporteVehiculoMap.put("tipo", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_tipo","t_veh_tip").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_vehi_marca","t_veh_mar") != null) {
				transporteVehiculoMap.put("marca", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_marca","t_veh_mar").toString().trim());
			}
			transporteVehiculoMap.put("documentoTipo", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_documento_tipo","t_veh_dti"));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_vehi_documento_numero","t_veh_dnu")!=null) {
				transporteVehiculoMap.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_documento_numero","t_veh_dnu").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_vehi_obs","t_veh_obs") != null) {
				transporteVehiculoMap.put("obs", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_obs","t_veh_obs").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_vehi_numero_matricula","t_veh_mat") != null) {
				transporteVehiculoMap.put("numeroMatricula", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_numero_matricula","t_veh_mat").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_vehi_numero_vuelo","t_veh_vue")!= null) {
				transporteVehiculoMap.put("numeroVuelo", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_numero_vuelo","t_veh_vue").toString().trim());
			}
			
			
			Map<String, Object> transporteTransportistaMap = new HashMap<String, Object>();
			transporteTransportistaMap.put("contribuyente", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_contribuyente","t_t_con"));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_transpor_nombre","t_t_nom")!= null) {
				transporteTransportistaMap.put("nombre", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_nombre","t_t_nom").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_ruc","t_t_ruc")!=null) {
				transporteTransportistaMap.put("ruc", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_ruc","t_t_ruc").toString().trim());
			}
			transporteTransportistaMap.put("documentoTipo", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_documento_tipo","t_t_dti"));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_transpor_documento_numero","t_t_dnu") != null) {
				transporteTransportistaMap.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_documento_numero","t_t_dnu").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_transpor_direccion","t_t_dir")!= null) {
				transporteTransportistaMap.put("direccion", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_direccion","t_t_dir").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_transpor_obs","t_t_obs") != null) {
				transporteTransportistaMap.put("obs", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_obs","t_t_obs").toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_transpor_pais","t_t_pai") != null) {
				transporteTransportistaMap.put("pais", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_pais","t_t_pai").toString().trim());
			}
			
			
			Map<String, Object> transporteTransportistaChoferMap = new HashMap<String, Object>();
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_doc_numero","t_t_c_dnu")!= null) {
				transporteTransportistaChoferMap.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_doc_numero","t_t_c_dnu").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_nombre","t_t_c_nom") != null) {
				transporteTransportistaChoferMap.put("nombre", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_nombre","t_t_c_nom").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_direccion","t_t_c_dir") != null) {
				transporteTransportistaChoferMap.put("direccion", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_direccion","t_t_c_dir").toString().trim());
			}
			
			Map<String, Object> transporteTransportistaAgenteMap = new HashMap<String, Object>();
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_nombre","t_t_a_nom") != null) {
				transporteTransportistaAgenteMap.put("nombre", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_nombre","t_t_a_nom").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_ruc","t_t_a_ruc") != null) {
				transporteTransportistaAgenteMap.put("ruc", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_ruc","t_t_a_ruc").toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_direccion","t_t_a_dir") != null) {
				transporteTransportistaAgenteMap.put("direccion", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_direccion","t_t_a_dir").toString().trim());
			}
			
			
			
			transporteTransportistaMap.put("chofer",transporteTransportistaChoferMap );
			transporteTransportistaMap.put("agente", transporteTransportistaAgenteMap);
			
			transporteMap.put("salida", transporteSalidaMap);
			transporteMap.put("entrega", transporteEntregaMap);
			transporteMap.put("vehiculo", transporteVehiculoMap);
			transporteMap.put("transportista", transporteTransportistaMap);
			

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

						creditoMap.put("montoEntrega", sumatoriaMontoEntrega);
						// infoCuotas
						
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
