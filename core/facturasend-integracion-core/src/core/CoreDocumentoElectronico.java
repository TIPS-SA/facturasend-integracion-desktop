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
					String transaccionIdString = CoreService.getValueForKey(map, "transaccion_id", "tra_id", databaseProperties) + "";
					
					Integer transaccionIdIterate = new BigDecimal(transaccionIdString).intValue();
					
					return transaccionIdIterate == transaccionId.intValue();
				}).collect(Collectors.toList());
				
				//---
				List<Map<String, Object>> parmentViewFiltradoList = parmentViewAllList.stream().filter( map -> {
					String transaccionIdString = CoreService.getValueForKey(map, "transaccion_id", "tra_id", databaseProperties) + "";
					
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

		SimpleDateFormat sdfYyyyMMddHHmmss = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		SimpleDateFormat sdfYyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
		if (transaccionMap != null && transaccionMap.size() > 0) {
			
			Map<String, Object> transaccionCabecera = transaccionMap.get(0);
			
			Integer transaccionId = Integer.valueOf(CoreService.getValueForKey(transaccionCabecera, "transaccion_id", "tra_id", databaseProperties) + "");
			Integer tipoDocumento = Integer.valueOf(CoreService.getValueForKey(transaccionCabecera, "tipo_documento", "tip_doc", databaseProperties) + "");

			dataMap.put("tipoDocumento", tipoDocumento);
			String cdcGenerado = (String) CoreService.getValueForKey(transaccionCabecera, "cdc", databaseProperties);
			
			if (cdcGenerado != null && cdcGenerado.length() == 44) {
				dataMap.put("cdc", cdcGenerado); //Si ya fue generado con un CDC, entonces envía para utilizar el mismo.
			}

			dataMap.put("establecimiento", CoreService.getValueForKey(transaccionCabecera, "establecimiento", "estable", databaseProperties));
			dataMap.put("punto", CoreService.getValueForKey(transaccionCabecera, "punto", databaseProperties));
			if (CoreService.getValueForKey(transaccionCabecera,"numero", databaseProperties) != null) {
				dataMap.put("numero", CoreService.getValueForKey(transaccionCabecera, "numero", databaseProperties).toString().trim());
			}
			dataMap.put("serie", CoreService.getValueForKey(transaccionCabecera, "serie", databaseProperties));
			if (CoreService.getValueForKey(transaccionCabecera,"descripcion","descrip", databaseProperties) != null) {
				dataMap.put("descripcion", CoreService.getValueForKey(transaccionCabecera, "descripcion", "descrip", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"observacion","observa", databaseProperties) != null) {
				dataMap.put("observacion", CoreService.getValueForKey(transaccionCabecera,"observacion","observa", databaseProperties).toString().trim());
			}

			if ( CoreService.getValueForKey(transaccionCabecera, "fecha", databaseProperties) instanceof Date) {
				dataMap.put("fecha", sdfYyyyMMddHHmmss.format((Date) CoreService.getValueForKey(transaccionCabecera,"fecha", databaseProperties)) );
			} else {
				dataMap.put("fecha", CoreService.getValueForKey(transaccionCabecera,"fecha", databaseProperties) );
			}
			dataMap.put("tipoEmision", CoreService.getValueForKey(transaccionCabecera, "tipo_emision", "tip_emi", databaseProperties));
			dataMap.put("tipoTransaccion", CoreService.getValueForKey(transaccionCabecera, "tipo_transaccion", "tip_tra", databaseProperties));
			dataMap.put("tipoImpuesto", CoreService.getValueForKey(transaccionCabecera, "tipo_impuesto", "tip_imp", databaseProperties));
			
			if (CoreService.getValueForKey(transaccionCabecera,"moneda", databaseProperties) != null) {
				dataMap.put("moneda", CoreService.getValueForKey(transaccionCabecera, "moneda", databaseProperties).toString().trim());
			}
			dataMap.put("condicionAnticipo", CoreService.getValueForKey(transaccionCabecera, "anticipo", databaseProperties));
			dataMap.put("descuentoGlobal", CoreService.getValueForKey(transaccionCabecera, "descuento_global", "des_glo", databaseProperties));
			dataMap.put("anticipoGlobal", CoreService.getValueForKey(transaccionCabecera, "anticipo_global", "ant_glo", databaseProperties));
			dataMap.put("total", CoreService.getValueForKey(transaccionCabecera, "total", databaseProperties));

			dataMap.put("condicionTipoCambio", CoreService.getValueForKey(transaccionCabecera,"tipo_cambio","tip_cam", databaseProperties));			
			dataMap.put("cambio", CoreService.getValueForKey(transaccionCabecera,"cambio", databaseProperties));
			dataMap.put("format", CoreService.getValueForKey(transaccionCabecera,"format", databaseProperties));
			
			// Cliente
			Map<String, Object> cliente = new HashMap<String, Object>();
			
			cliente.put("contribuyente", Boolean.valueOf(CoreService.getValueForKey(transaccionCabecera,"cliente_contribuyente","c_contribu", databaseProperties)+""));
			
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_ruc","c_ruc", databaseProperties) != null) {
				cliente.put("ruc", CoreService.getValueForKey(transaccionCabecera,"cliente_ruc","c_ruc", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_razon_social","c_raz_soc", databaseProperties) != null) {
				cliente.put("razonSocial", CoreService.getValueForKey(transaccionCabecera,"cliente_razon_social","c_raz_soc", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_nombre_fantasia","c_nom_fan", databaseProperties) != null) {
				cliente.put("nombreFantasia", CoreService.getValueForKey(transaccionCabecera,"cliente_nombre_fantasia","c_nom_fan", databaseProperties).toString().trim());
			}

			cliente.put("tipoOperacion", CoreService.getValueForKey(transaccionCabecera,"cliente_tipo_operacion","c_tip_ope", databaseProperties));
			
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_direccion","c_direcc", databaseProperties) != null) {
				cliente.put("direccion", CoreService.getValueForKey(transaccionCabecera,"cliente_direccion","c_direcc", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_numero_casa","c_num_cas", databaseProperties) != null) {
				cliente.put("numeroCasa", CoreService.getValueForKey(transaccionCabecera,"cliente_numero_casa","c_num_cas", databaseProperties).toString().trim());
			}

			cliente.put("ciudad", CoreService.getValueForKey(transaccionCabecera,"cliente_ciudad","c_ciudad", databaseProperties));
			cliente.put("departamento", CoreService.getValueForKey(transaccionCabecera,"cliente_departamento","c_depart", databaseProperties));
			cliente.put("distrito", CoreService.getValueForKey(transaccionCabecera,"cliente_distrito","c_distri", databaseProperties));
			cliente.put("pais", CoreService.getValueForKey(transaccionCabecera,"cliente_pais","c_pais", databaseProperties));

			cliente.put("tipoContribuyente", CoreService.getValueForKey(transaccionCabecera,"cliente_tipo_contribuyente","c_tip_con", databaseProperties));
					
			cliente.put("documentoTipo", CoreService.getValueForKey(transaccionCabecera,"cliente_documento_tipo","c_doc_tip", databaseProperties));
			
			cliente.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"cliente_documento_numero","c_doc_num", databaseProperties));
			
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_telefono","c_tel", databaseProperties) != null) {
				cliente.put("telefono", CoreService.getValueForKey(transaccionCabecera,"cliente_telefono","c_tel", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_celular","c_cel", databaseProperties) != null) {
				cliente.put("celular", CoreService.getValueForKey(transaccionCabecera,"cliente_celular","c_cel", databaseProperties).toString().trim() );
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_email","c_ema", databaseProperties) != null) {
				cliente.put("email", CoreService.getValueForKey(transaccionCabecera,"cliente_email","c_ema", databaseProperties).toString().trim() );
			}
			if (CoreService.getValueForKey(transaccionCabecera,"cliente_codigo","c_cod", databaseProperties) != null) {
				cliente.put("codigo", CoreService.getValueForKey(transaccionCabecera,"cliente_codigo","c_cod", databaseProperties).toString().trim() );
			}
			
			dataMap.put("cliente", cliente );
			//FIN CLIENTE
			
			
			// inicioUsuario
			Map<String, Object> dataMapUsuario = new HashMap<String, Object>();

			dataMapUsuario.put("documentoTipo", CoreService.getValueForKey(transaccionCabecera,"usuario_documento_tipo","u_doc_tip", databaseProperties));
			
			if (CoreService.getValueForKey(transaccionCabecera,"usuario_documento_numero","u_doc_num", databaseProperties) != null) {
				dataMapUsuario.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"usuario_documento_numero","u_doc_num", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"usuario_nombre","u_nom", databaseProperties) != null) {
				dataMapUsuario.put("nombre", CoreService.getValueForKey(transaccionCabecera,"usuario_nombre","u_nom", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"usuario_cargo","u_car", databaseProperties) != null) {
				dataMapUsuario.put("cargo", CoreService.getValueForKey(transaccionCabecera,"usuario_cargo","u_car", databaseProperties).toString().trim());
			}
			dataMap.put("usuario", dataMapUsuario);
			//finUsuario

			//Factura
			if (tipoDocumento == 1) {
				Map<String, Object> dataMapFactura1 = new HashMap<String, Object>();
				dataMapFactura1.put("presencia", CoreService.getValueForKey(transaccionCabecera,"factura_presencia","fa_pre", databaseProperties));
				dataMapFactura1.put("fechaEnvio", CoreService.getValueForKey(transaccionCabecera,"factura_fecha_envio","fa_fec_env", databaseProperties));
				dataMapFactura1.put("ticket", CoreService.getValueForKey(transaccionCabecera,"factura_ticket","fa_ticket", databaseProperties));				
				dataMap.put("factura", dataMapFactura1);
			}
			
			//AutoFactura
			if (tipoDocumento == 4) {
				Map<String, Object> dataMapFactura = new HashMap<String, Object>();
				dataMapFactura.put("tipoVendedor", CoreService.getValueForKey(transaccionCabecera,"autofactura_tipo_vendedor","af_tip_ven", databaseProperties));
				dataMapFactura.put("documentoTipo", CoreService.getValueForKey(transaccionCabecera,"autofactura_documento_tipo","af_doc_tip", databaseProperties));
				dataMapFactura.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"autofactura_documento_numero","af_doc_num", databaseProperties));
				dataMapFactura.put("nombre", CoreService.getValueForKey(transaccionCabecera,"autofactura_nombre","af_tip_ven", databaseProperties));				
				dataMapFactura.put("direccion", CoreService.getValueForKey(transaccionCabecera,"autofactura_direccion","af_tip_ven", databaseProperties));
				dataMapFactura.put("numeroCasa", CoreService.getValueForKey(transaccionCabecera,"autofactura_numero_casa","af_num_cas", databaseProperties));
				dataMapFactura.put("departamento", CoreService.getValueForKey(transaccionCabecera,"autofactura_departamento","af_depart", databaseProperties));
				dataMapFactura.put("distrito", CoreService.getValueForKey(transaccionCabecera,"autofactura_distrito","af_distri", databaseProperties));
				dataMapFactura.put("ciudad", CoreService.getValueForKey(transaccionCabecera,"autofactura_ciudad","af_ciudad", databaseProperties));
				dataMapFactura.put("pais", CoreService.getValueForKey(transaccionCabecera,"autofactura_pais","af_pais", databaseProperties));
				
				Map<String, Object> ubicacion = new HashMap<String, Object>();
				
				//Ubicacion de la Autofactura, local del cliente.
				ubicacion.put("lugar", CoreService.getValueForKey(transaccionCabecera,"cliente_direccion","c_direcc", databaseProperties));
				ubicacion.put("ciudad", CoreService.getValueForKey(transaccionCabecera,"cliente_ciudad","c_ciudad", databaseProperties));
				ubicacion.put("distrito", CoreService.getValueForKey(transaccionCabecera,"cliente_distrito","c_distri", databaseProperties));				
				ubicacion.put("departamento", CoreService.getValueForKey(transaccionCabecera,"cliente_departamento","c_depart", databaseProperties));
				dataMapFactura.put("ubicacion", ubicacion);
				
				dataMap.put("autoFactura", dataMapFactura);
			}				
				
			//NotaCredito y Debito
			if (tipoDocumento == 5) {
				Map<String, Object> dataMapNotaCreditoDebito = new HashMap<String, Object>();
				dataMapNotaCreditoDebito.put("motivo", CoreService.getValueForKey(transaccionCabecera,"nota_credito_motivo","nc_motivo", databaseProperties));
			
				dataMap.put("notaCreditoDebito", dataMapNotaCreditoDebito);		
			}
			
			//NotaRemision			
			if (tipoDocumento == 7) {
				Map<String, Object> dataMapNotaRemision = new HashMap<String, Object>();
				dataMapNotaRemision.put("motivo", CoreService.getValueForKey(transaccionCabecera,"nota_remision_motivo","nr_motivo", databaseProperties));
				dataMapNotaRemision.put("tipoResponsable", CoreService.getValueForKey(transaccionCabecera,"nota_remision_tipo_responsable","nr_tip_res", databaseProperties));
				dataMapNotaRemision.put("kms", CoreService.getValueForKey(transaccionCabecera,"nota_remision_kms","nr_kms", databaseProperties));
	
				dataMap.put("remision", dataMapNotaRemision);
			}
			
			// Items de la compra
			List<Map<String, Object>> lista = new ArrayList<Map<String, Object>>();

			for (int i = 0; i < transaccionMap.size(); i++) {
				
				// Items de la compra
				Map<String, Object> dataMapProducto = new HashMap<String, Object>();
				Map<String, Object> transaccionItems = transaccionMap.get(i);
				
				if (CoreService.getValueForKey(transaccionItems,"item_codigo","i_codigo", databaseProperties) != null) {
					dataMapProducto.put("codigo", CoreService.getValueForKey(transaccionItems,"item_codigo","i_codigo", databaseProperties).toString().trim());
				}
				if (CoreService.getValueForKey(transaccionItems,"item_descripcion","i_descrip", databaseProperties) != null) {
					dataMapProducto.put("descripcion", CoreService.getValueForKey(transaccionItems,"item_descripcion","i_descrip", databaseProperties).toString().trim());
				}
				
				if (CoreService.getValueForKey(transaccionItems,"item_observacion","i_obs", databaseProperties) != null) {
					dataMapProducto.put("observacion", CoreService.getValueForKey(transaccionItems,"item_observacion","i_obs", databaseProperties).toString().trim());
				}
				
				dataMapProducto.put("partidaArancelaria", CoreService.getValueForKey(transaccionItems,"item_partida_arancelaria","i_par_ara", databaseProperties));
				dataMapProducto.put("ncm", CoreService.getValueForKey(transaccionItems,"item_ncm","i_ncm", databaseProperties));
				dataMapProducto.put("unidadMedida", CoreService.getValueForKey(transaccionItems,"item_unidad_medida","i_uni_med", databaseProperties));
				dataMapProducto.put("cantidad", CoreService.getValueForKey(transaccionItems,"item_cantidad","i_cantidad", databaseProperties));
				dataMapProducto.put("precioUnitario", CoreService.getValueForKey(transaccionItems,"item_precio_unitario","i_pre_uni", databaseProperties));
				dataMapProducto.put("cambio", CoreService.getValueForKey(transaccionItems,"item_cambio", "i_cambio", databaseProperties));
				dataMapProducto.put("descuento", CoreService.getValueForKey(transaccionItems,"item_descuento","i_descue", databaseProperties));
				dataMapProducto.put("anticipo", CoreService.getValueForKey(transaccionItems,"item_anticipo","i_anti", databaseProperties));
				dataMapProducto.put("pais", CoreService.getValueForKey(transaccionItems,"item_pais","i_pais", databaseProperties));
				dataMapProducto.put("tolerancia", CoreService.getValueForKey(transaccionItems,"item_tolerancia","i_tol", databaseProperties));
				dataMapProducto.put("toleranciaCantidad", CoreService.getValueForKey(transaccionItems,"item_tolerancia_cantidad","i_tol_can", databaseProperties));
				dataMapProducto.put("toleranciaPorcentaje", CoreService.getValueForKey(transaccionItems,"item_tolerancia_porcentaje","i_tol_por", databaseProperties));
				dataMapProducto.put("cdcAnticipo", CoreService.getValueForKey(transaccionItems,"item_cdc_anticipo","i_cdc_ant", databaseProperties));
				
				dataMapProducto.put("ivaTipo", CoreService.getValueForKey(transaccionItems,"item_iva_tipo","i_iva_tip", databaseProperties));
				dataMapProducto.put("ivaBase", CoreService.getValueForKey(transaccionItems,"item_iva_base","i_iva_bas", databaseProperties));
				dataMapProducto.put("iva", CoreService.getValueForKey(transaccionItems,"item_iva","i_iva", databaseProperties));
				dataMapProducto.put("lote", CoreService.getValueForKey(transaccionItems,"item_lote","i_lote", databaseProperties));
				dataMapProducto.put("tolerancia", CoreService.getValueForKey(transaccionItems,"item_tolerancia","i_tol", databaseProperties));
				
				dataMapProducto.put("vencimiento", CoreService.getValueForKey(transaccionItems,"item_vencimiento","i_venci", databaseProperties));
				dataMapProducto.put("numeroSerie", CoreService.getValueForKey(transaccionItems,"item_numero_serie","i_num_ser", databaseProperties));
				dataMapProducto.put("numeroPedido", CoreService.getValueForKey(transaccionItems,"item_numero_pedido","i_num_ped", databaseProperties));
				dataMapProducto.put("numeroSeguimiento", CoreService.getValueForKey(transaccionItems,"item_numero_seguimiento","i_num_seg", databaseProperties));
				dataMapProducto.put("registroSenave", CoreService.getValueForKey(transaccionItems,"item_registro_senave","i_reg_sen", databaseProperties));
				dataMapProducto.put("registroEntidadComercial", CoreService.getValueForKey(transaccionItems,"item_registro_entidad_comercial","i_reg_ent", databaseProperties));
				
				Map<String, Object> dataMapDncp = new HashMap<String, Object>();
				dataMapDncp.put("codigoNivelGeneral", CoreService.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_general","i_dncp_cng", databaseProperties));
				dataMapDncp.put("codigoNivelEspecifico", CoreService.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_especifico","i_dncp_cne", databaseProperties));
				dataMapDncp.put("codigoGtinProducto", CoreService.getValueForKey(transaccionItems,"item_dncp_codigo_gtin_producto","i_dncp_cgp", databaseProperties));
				dataMapDncp.put("codigoNivelPaquete", CoreService.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_paquete","i_dncp_cnp", databaseProperties));

				if (CoreService.getValueForKey(transaccionItems,"item_dncp_codigo_nivel_general","i_dncp_cng", databaseProperties) != null) {
					dataMapProducto.put("dncp", dataMapDncp);	
				}
				
				/*Map<String, Object> dataMapImportador = new HashMap<String, Object>();
				
				dataMapImportador.put("nombre", Core.getValueForKey(transaccionItems,"item_importador_nombre","i_imp_nom"));
				dataMapImportador.put("direccion", Core.getValueForKey(transaccionItems,"item_importador_direccion","i_imp_dir"));
				dataMapImportador.put("registroImportador", Core.getValueForKey(transaccionItems,"item_registro_importador","i_reg_imp"));
				
				if (Core.getValueForKey(transaccionItems,"item_importador_nombre","i_imp_nom", databaseProperties) != null) {
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
			documentoAsociadoMap.put("formato", CoreService.getValueForKey(transaccionCabecera,"doc_aso_formato","d_aso_for", databaseProperties));
			
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_cdc","d_aso_cdc", databaseProperties) != null) {
				documentoAsociadoMap.put("cdc", CoreService.getValueForKey(transaccionCabecera,"doc_aso_cdc","d_aso_cdc", databaseProperties).toString().trim());
			}

			documentoAsociadoMap.put("tipoDocumentoImpreso", CoreService.getValueForKey(transaccionCabecera,"doc_aso_cdc","d_aso_tdi", databaseProperties));
			documentoAsociadoMap.put("timbrado", CoreService.getValueForKey(transaccionCabecera,"doc_aso_timbrado","d_aso_tim", databaseProperties));
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_establecimiento","d_aso_est", databaseProperties) != null) {
				documentoAsociadoMap.put("establecimiento", CoreService.getValueForKey(transaccionCabecera,"doc_aso_establecimiento","d_aso_est", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_punto","d_aso_pun", databaseProperties) != null) {
				documentoAsociadoMap.put("punto", CoreService.getValueForKey(transaccionCabecera,"doc_aso_punto","d_aso_pun", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"doc_aso_numero","d_aso_num", databaseProperties) != null){
				documentoAsociadoMap.put("numero", CoreService.getValueForKey(transaccionCabecera,"doc_aso_numero","d_aso_num", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_fecha","d_aso_fec", databaseProperties) != null) {
				documentoAsociadoMap.put("fecha", CoreService.getValueForKey(transaccionCabecera,"doc_aso_fecha","d_aso_fec", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"doc_aso_numero_retencion","d_aso_ret", databaseProperties) != null) {
				documentoAsociadoMap.put("numeroRetencion", CoreService.getValueForKey(transaccionCabecera,"doc_aso_numero_retencion","d_aso_ret", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"doc_aso_reso_credito_fiscal","d_aso_rcf", databaseProperties) != null) {
				documentoAsociadoMap.put("resolucionCreditoFiscal", CoreService.getValueForKey(transaccionCabecera,"doc_aso_reso_credito_fiscal","d_aso_rcf", databaseProperties).toString().trim());
			}
			documentoAsociadoMap.put("constanciaTipo", CoreService.getValueForKey(transaccionCabecera,"doc_aso_constancia_tipo","d_aso_cti", databaseProperties));
			documentoAsociadoMap.put("constanciaNumero", CoreService.getValueForKey(transaccionCabecera,"doc_aso_constancia_numero","d_aso_cnu", databaseProperties));
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_constancia_control","d_aso_cco", databaseProperties) != null) {
				documentoAsociadoMap.put("constanciaControl", CoreService.getValueForKey(transaccionCabecera,"doc_aso_constancia_control","d_aso_cco", databaseProperties).toString().trim());
			}
			
			if (CoreService.getValueForKey(transaccionCabecera,"doc_aso_formato","d_aso_for", databaseProperties) != null) {
				dataMap.put("documentoAsociado", documentoAsociadoMap);	
			}
			
			Map<String, Object> transporteMap = new HashMap<String, Object>();
			transporteMap.put("tipo", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip", databaseProperties));
			transporteMap.put("modalidad", CoreService.getValueForKey(transaccionCabecera,"tra_modalidad","t_mod", databaseProperties));
			transporteMap.put("nombre", CoreService.getValueForKey(transaccionCabecera,"tra_nombre","t_nom", databaseProperties));
			if ( CoreService.getValueForKey(transaccionCabecera,"tra_fecha_inicio", "t_f_ini", databaseProperties) instanceof Date) {
				transporteMap.put("inicioEstimadoTranslado", sdfYyyyMMdd.format((Date) CoreService.getValueForKey(transaccionCabecera, "tra_fecha_inicio","t_f_ini", databaseProperties)));
			} else {
				dataMap.put("inicioEstimadoTranslado", CoreService.getValueForKey(transaccionCabecera, "tra_fecha_inicio", "t_f_ini", databaseProperties));
			}
			if ( CoreService.getValueForKey(transaccionCabecera,"tra_fecha_fin", "t_f_fin", databaseProperties) instanceof Date) {
				transporteMap.put("finEstimadoTranslado", sdfYyyyMMdd.format((Date) CoreService.getValueForKey(transaccionCabecera, "tra_fecha_fin","t_f_fin", databaseProperties)));
			} else {
				dataMap.put("finEstimadoTranslado", CoreService.getValueForKey(transaccionCabecera, "tra_fecha_fin", "t_f_fin", databaseProperties));
			}
			//transporteMap.put("inicioEstimadoTranslado", sdf.format((Date) CoreService.getValueForKey(transaccionCabecera,"tra_fecha_inicio","t_f_ini", databaseProperties)));
			//transporteMap.put("finEstimadoTranslado", sdf.format((Date) CoreService.getValueForKey(transaccionCabecera,"tra_fecha_fin","t_f_fin", databaseProperties)));
			transporteMap.put("tipoResponsable", CoreService.getValueForKey(transaccionCabecera,"tra_tipo_responsable","t_tip_res", databaseProperties));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_condicion_negociacion","t_con_neg", databaseProperties) != null) {
				transporteMap.put("condicionNegociacion", CoreService.getValueForKey(transaccionCabecera,"tra_condicion_negociacion","t_con_neg", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip", databaseProperties) != null &&
					((BigDecimal)CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip", databaseProperties)).intValue() != 0 &&
					CoreService.getValueForKey(transaccionCabecera,"tra_modalidad","t_mod", databaseProperties) != null &&
					((BigDecimal)CoreService.getValueForKey(transaccionCabecera,"tra_modalidad","t_mod", databaseProperties)).intValue() != 0) {
				
				dataMap.put("transporte", transporteMap);	//Agrega transporte si tiene datos básicos	
			}
				
			
			
			/*transporteMap.put("NumeroManifiesto", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip", databaseProperties));
			transporteMap.put("numeroDespachoImportacion", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip", databaseProperties));
			transporteMap.put("inicioEstimadoTranslado", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip", databaseProperties));
			transporteMap.put("finEstimadoTranslado", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip", databaseProperties));
			transporteMap.put("paisDestino", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip", databaseProperties));
			transporteMap.put("paisDestinoNombre", CoreService.getValueForKey(transaccionCabecera,"tra_tipo","t_tip", databaseProperties));*/
			
			
			Map<String, Object> transporteSalidaMap = new HashMap<String, Object>();
			if (CoreService.getValueForKey(transaccionCabecera,"tra_sali_direccion","t_sal_dir", databaseProperties) != null) {
				transporteSalidaMap.put("direccion", CoreService.getValueForKey(transaccionCabecera,"tra_sali_direccion","t_sal_dir", databaseProperties).toString().trim());
			}
			transporteSalidaMap.put("numeroCasa", CoreService.getValueForKey(transaccionCabecera,"tra_sali_numero_casa","t_sal_nca", databaseProperties));
			if(CoreService.getValueForKey(transaccionCabecera,"tra_sali_comple_direccion1","t_sal_cd1", databaseProperties) != null) {
				transporteSalidaMap.put("complementoDireccion1", CoreService.getValueForKey(transaccionCabecera,"tra_sali_comple_direccion1","t_sal_cd1", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_sali_comple_direccion2","t_sal_cd2", databaseProperties) != null) {
				transporteSalidaMap.put("complementoDireccion2", CoreService.getValueForKey(transaccionCabecera,"tra_sali_comple_direccion2","t_sal_cd2", databaseProperties).toString().trim());
			}
			transporteSalidaMap.put("departamento", CoreService.getValueForKey(transaccionCabecera,"tra_sali_departamento","t_sal_dep", databaseProperties));
			transporteSalidaMap.put("distrito", CoreService.getValueForKey(transaccionCabecera,"tra_sali_distrito","t_sal_dis", databaseProperties));
			transporteSalidaMap.put("ciudad", CoreService.getValueForKey(transaccionCabecera,"tra_sali_ciudad","t_sal_ciu", databaseProperties));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_sali_pais","t_sal_pai", databaseProperties) != null) {				
				transporteSalidaMap.put("pais", CoreService.getValueForKey(transaccionCabecera,"tra_sali_pais","t_sal_pai", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_sali_telefono_contacto","t_sal_tel", databaseProperties) != null) {
				transporteSalidaMap.put("telefonoContacto", CoreService.getValueForKey(transaccionCabecera,"tra_sali_telefono_contacto","t_sal_tel", databaseProperties).toString().trim());
			}
			
			
			Map<String, Object> transporteEntregaMap = new HashMap<String, Object>();
			if(CoreService.getValueForKey(transaccionCabecera,"tra_entre_direccion","t_ent_dir", databaseProperties) != null) {
				transporteEntregaMap.put("direccion", CoreService.getValueForKey(transaccionCabecera,"tra_entre_direccion","t_ent_dir", databaseProperties).toString().trim());
			}
			transporteEntregaMap.put("numeroCasa", CoreService.getValueForKey(transaccionCabecera,"tra_entre_numero_casa","t_ent_nca", databaseProperties));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_entre_comple_direccion1","t_ent_cd1", databaseProperties) != null) { 
				transporteEntregaMap.put("complementoDireccion1", CoreService.getValueForKey(transaccionCabecera,"tra_entre_comple_direccion1","t_ent_cd1", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_entre_comple_direccion2","t_ent_cd2", databaseProperties) != null) {
				transporteEntregaMap.put("complementoDireccion2", CoreService.getValueForKey(transaccionCabecera,"tra_entre_comple_direccion2","t_ent_cd2", databaseProperties).toString().trim());
			}
			transporteEntregaMap.put("departamento", CoreService.getValueForKey(transaccionCabecera,"tra_entre_departamento","t_ent_dep", databaseProperties));
			transporteEntregaMap.put("distrito", CoreService.getValueForKey(transaccionCabecera,"tra_entre_distrito","t_ent_dis", databaseProperties));
			transporteEntregaMap.put("ciudad", CoreService.getValueForKey(transaccionCabecera,"tra_entre_ciudad","t_ent_ciu", databaseProperties));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_entre_pais","t_ent_pai", databaseProperties) != null) {
				transporteEntregaMap.put("pais", CoreService.getValueForKey(transaccionCabecera,"tra_entre_pais","t_ent_pai", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_entre_telefono_contacto","t_ent_tel", databaseProperties) != null) {
				transporteEntregaMap.put("telefonoContacto", CoreService.getValueForKey(transaccionCabecera,"tra_entre_telefono_contacto","t_ent_tel", databaseProperties).toString().trim());
			}
			
			
			Map<String, Object> transporteVehiculoMap = new HashMap<String, Object>();
			if (CoreService.getValueForKey(transaccionCabecera,"tra_vehi_tipo","t_veh_tip", databaseProperties) != null) {
				transporteVehiculoMap.put("tipo", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_tipo","t_veh_tip", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_vehi_marca","t_veh_mar", databaseProperties) != null) {
				transporteVehiculoMap.put("marca", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_marca","t_veh_mar", databaseProperties).toString().trim());
			}
			transporteVehiculoMap.put("documentoTipo", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_documento_tipo","t_veh_dti", databaseProperties));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_vehi_documento_numero","t_veh_dnu", databaseProperties) != null) {
				transporteVehiculoMap.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_documento_numero","t_veh_dnu", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_vehi_obs","t_veh_obs", databaseProperties) != null) {
				transporteVehiculoMap.put("obs", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_obs","t_veh_obs", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_vehi_numero_matricula","t_veh_mat", databaseProperties) != null) {
				transporteVehiculoMap.put("numeroMatricula", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_numero_matricula","t_veh_mat", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_vehi_numero_vuelo","t_veh_vue", databaseProperties) != null) {
				transporteVehiculoMap.put("numeroVuelo", CoreService.getValueForKey(transaccionCabecera,"tra_vehi_numero_vuelo","t_veh_vue", databaseProperties).toString().trim());
			}
			
			
			Map<String, Object> transporteTransportistaMap = new HashMap<String, Object>();
			transporteTransportistaMap.put("contribuyente", Boolean.valueOf(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_contribuyente","t_t_con", databaseProperties) +""));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_transpor_nombre","t_t_nom", databaseProperties) != null) {
				transporteTransportistaMap.put("nombre", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_nombre","t_t_nom", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_ruc","t_t_ruc", databaseProperties) != null) {
				transporteTransportistaMap.put("ruc", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_ruc","t_t_ruc", databaseProperties).toString().trim());
			}
			transporteTransportistaMap.put("documentoTipo", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_documento_tipo","t_t_dti", databaseProperties));
			if (CoreService.getValueForKey(transaccionCabecera,"tra_transpor_documento_numero","t_t_dnu", databaseProperties) != null) {
				transporteTransportistaMap.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_documento_numero","t_t_dnu", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_transpor_direccion","t_t_dir", databaseProperties) != null) {
				transporteTransportistaMap.put("direccion", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_direccion","t_t_dir", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_transpor_obs","t_t_obs", databaseProperties) != null) {
				transporteTransportistaMap.put("obs", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_obs","t_t_obs", databaseProperties).toString().trim());
			}
			if (CoreService.getValueForKey(transaccionCabecera,"tra_transpor_pais","t_t_pai", databaseProperties) != null) {
				transporteTransportistaMap.put("pais", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_pais","t_t_pai", databaseProperties).toString().trim());
			}
			
			
			Map<String, Object> transporteTransportistaChoferMap = new HashMap<String, Object>();
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_doc_numero","t_t_c_dnu", databaseProperties) != null) {
				transporteTransportistaChoferMap.put("documentoNumero", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_doc_numero","t_t_c_dnu", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_nombre","t_t_c_nom", databaseProperties) != null) {
				transporteTransportistaChoferMap.put("nombre", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_nombre","t_t_c_nom", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_direccion","t_t_c_dir", databaseProperties) != null) {
				transporteTransportistaChoferMap.put("direccion", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_chofer_direccion","t_t_c_dir", databaseProperties).toString().trim());
			}
			
			Map<String, Object> transporteTransportistaAgenteMap = new HashMap<String, Object>();
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_nombre","t_t_a_nom", databaseProperties) != null) {
				transporteTransportistaAgenteMap.put("nombre", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_nombre","t_t_a_nom", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_ruc","t_t_a_ruc", databaseProperties) != null) {
				transporteTransportistaAgenteMap.put("ruc", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_ruc","t_t_a_ruc", databaseProperties).toString().trim());
			}
			if(CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_direccion","t_t_a_dir", databaseProperties) != null) {
				transporteTransportistaAgenteMap.put("direccion", CoreService.getValueForKey(transaccionCabecera,"tra_transpor_agente_direccion","t_t_a_dir", databaseProperties).toString().trim());
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

			Integer tipo = Integer.valueOf(CoreService.getValueForKey(formaCobro, "tipo", databaseProperties)+"");
			
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
		Integer tipoFormaPagoAnterior = Integer.valueOf(CoreService.getValueForKey(paymentViewMap.get(0), "tipo", databaseProperties) + "");
		//Integer monedaFormaPagoAnterior = Integer.valueOf(Core.getValueForKey(paymentViewMap.get(0),"moneda") + "");
		double sumatoriaMontoEntrega = 0.0;
		for (int i = 0; i < paymentViewMap.size(); i++) {
			Map<String, Object> formaCobro = paymentViewMap.get(i);

			Integer tipo = Integer.valueOf( CoreService.getValueForKey(formaCobro, "tipo", databaseProperties) + "" );
			Integer creditoTipo = Integer.valueOf( CoreService.getValueForKey(formaCobro, "credito_tipo", "c_tipo", databaseProperties) + "" );
			
			if (tipo == 1) {
				Map<String, Object> efectivoMap = new HashMap<String, Object>();

				double monto = Double.valueOf(CoreService.getValueForKey(formaCobro, "monto", databaseProperties)+"");
				efectivoMap.put("tipo", 1);
				efectivoMap.put("monto", monto);
				efectivoMap.put("moneda", CoreService.getValueForKey(formaCobro,"moneda", databaseProperties));
				efectivoMap.put("cambio", CoreService.getValueForKey(formaCobro,"cambio", databaseProperties));
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
					creditoMap.put("plazo", CoreService.getValueForKey(formaCobro,"credito_plazo","c_plazo", databaseProperties) + "");
					
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
