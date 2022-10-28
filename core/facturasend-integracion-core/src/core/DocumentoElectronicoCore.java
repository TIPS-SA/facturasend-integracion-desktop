package core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.javaeext.ejb.base.entidad.Ciudad;
import org.javaeext.ejb.base.entidad.Persona;
import org.javaeext.ejb.exception.InfoException;
import org.javaeext.ejb.util.StringUtil;

import com.google.gson.Gson;

import archivoCliente.entidades.Cliente;
import connect.BDConnect;
import connect.FSConnect;
import connect.SQLConnection;
import facturacion.entidades.mov.TimbradoFacturacionMovimiento;
import grupos.clasificadorGenerico.entidades.ClasificadorGenerico;
import multiMoneda.entidad.Cotizacion;
import multiMoneda.entidad.Moneda;
import sucursal.entidades.Sucursal;

public class DocumentoElectronicoCore {
	
	private Gson gson = new Gson();

	public static List<Map<String, Object>> generarJSONLote(String[] transactionIds, List<Map<String, Object>> documentosParaEnvioList, Map<String, String> databaseProperties) throws Exception{
		
		List<Map<String, Object>> jsonDEs = new ArrayList<Map<String,Object>>();
			
		for (int i = 0; i < transactionIds.length; i++) {
			Integer transaccionId = Integer.valueOf(transactionIds[i]);
			
			List<Map<String, Object>> documentosParaEnvioFiltradoList = documentosParaEnvioList.stream().filter( map -> map.get(Core.getFieldName("transaccion_id", databaseProperties)) == transaccionId).collect(Collectors.toList());
			
			Map<String, Object> jsonDE = invocarDocumentoElectronicoAutoFacturaCompra(documentosParaEnvioFiltradoList);
			jsonDEs.add(jsonDE);
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
	public static Map<String, Object> invocarDocumentoElectronicoAutoFacturaCompra(List<Map<String, Object>> transaccionMap) throws Exception{
		Map<String, Object> resultado = new HashMap<String, Object>();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			if (transaccionMap != null && transaccionMap.size() > 0) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				
				Map<String, Object> compraItemCabecera = transaccionMap.get(0);
				
				dataMap.put("tipoDocumento", 4); // AutoFactura
				if (cdcGenerado != null && cdcGenerado.length() == 44) {
					dataMap.put("cdc", cdcGenerado); //Si ya fue generado con un CDC, entonces envía para utilizar el mismo.
				}
				dataMap.put("ruc", compraItemCabecera[0]);
				dataMap.put("establecimiento", compraItemCabecera[1]);
				dataMap.put("punto", compraItemCabecera[2]);
				dataMap.put("numero", compraItemCabecera[3]);// <-----cambiar
				dataMap.put("numeroSerie", compraItemCabecera[4]);
				dataMap.put("descripcion", compraItemCabecera[5]);
				dataMap.put("observacion", compraItemCabecera[6]);
				dataMap.put("fecha", sdf.format((Date) compraItemCabecera[7]));
				dataMap.put("tipoEmision", compraItemCabecera[8]);
				dataMap.put("tipoTransaccion", compraItemCabecera[9]);
				dataMap.put("tipoImpuesto", compraItemCabecera[10]);
				dataMap.put("moneda", compraItemCabecera[11]);
				dataMap.put("condicionAnticipo", compraItemCabecera[12]);
				dataMap.put("condicionTipoCambio", compraItemCabecera[13]);
				//dataMap.put("cambio", compraItemCabecera[14]);
				if (!compraItemCabecera[11].equals("PYG")) {
					//dataMap.put("condicionTipoCambio", operacionIngresoCabecera[13]);
					dataMap.put("condicionTipoCambio", 1);
					Cotizacion cotizacion = cfr.consultarCotizacionFechaMoneda((Date)compraItemCabecera[7],
																				Integer.valueOf (String.valueOf(compraItemCabecera[81])),
																				monedaPredeterminada.getCodigo());
					if (cotizacion == null) {
						throw new Exception("No existe cotización en fecha '" 
								+ new SimpleDateFormat("dd/MM/yyyy").format((Date)compraItemCabecera[7])
								+ "' entre la Moneda (" + compraItemCabecera[81] + "" + ") y la Moneda (" 
								+ monedaPredeterminada.getCodigo() + ")");

					}
					
					dataMap.put("cambio",Double.valueOf(cotizacion.getCompra()));
				}
				// Cliente
				
				Map<String, Object> cliente = new HashMap<String, Object>();
				
				if (compraItemCabecera[78] == null) {
					throw new Exception("Debe especificar la Empresa en Opciones del Sistema");
				}

				Persona clienteAFacturar = em.find(Cliente.class, compraItemCabecera[78]);
				
				if (clienteAFacturar == null) {
					throw new Exception("Debe cargar a la persona con codigo #" + compraItemCabecera[78] + " como Cliente");
				}
				if (clienteAFacturar.getTipoDocumento() != null ) {
					if (clienteAFacturar.getTipoDocumento() == 1) {
						cliente.put("contribuyente", true);
					} else {
						cliente.put("contribuyente", false);
					}
				}
				cliente.put("ruc", clienteAFacturar.getDocumento());
				cliente.put("razonSocial", clienteAFacturar.getNombre());
				cliente.put("nombreFantasia", clienteAFacturar.getNombreFantasia());

				cliente.put("tipoOperacion", 2);//D202
				cliente.put("direccion", clienteAFacturar.getDireccion());
				
				if (clienteAFacturar.getNumeroCasa() != null && clienteAFacturar.getNumeroCasa() != "" ) {
					cliente.put("numeroCasa", clienteAFacturar.getNumeroCasa());

				} else {
					cliente.put("numeroCasa", 0);
				}
				
				if (clienteAFacturar.getCiudad() != null && clienteAFacturar.getCiudad().getCodigo() != null) {

					Ciudad ciudad = em.find(Ciudad.class, clienteAFacturar.getCiudad().getCodigo());
					if (ciudad != null) {
						cliente.put("ciudad", ciudad.getCodigo());
						cliente.put("ciudadDescripcion", ciudad.getNombre());

						if (ciudad.getDistrito() != null && ciudad.getDistrito().getCodigo() != null) {
							ClasificadorGenerico distrito = em.find(ClasificadorGenerico.class,
									ciudad.getDistrito().getCodigo());
							if (distrito.getClasificador() != null && distrito.getClasificador().getCodigo() != null) {
								ClasificadorGenerico departamento = em.find(ClasificadorGenerico.class,
										distrito.getClasificador().getCodigo());
								if (departamento != null) {
									cliente.put("departamento", departamento.getReferencia());
									cliente.put("departamentoDescripcion", departamento.getNombre());
								}
							}
							cliente.put("distrito", distrito.getReferencia());
							cliente.put("distritoDescripcion", distrito.getNombre());
						}
					}
					if (clienteAFacturar.getNacionalidad() != null
							&& clienteAFacturar.getNacionalidad().getCodigo() != null) {
						ClasificadorGenerico pais = em.find(ClasificadorGenerico.class,
								clienteAFacturar.getNacionalidad().getCodigo());
						cliente.put("pais", pais.getReferencia());
						cliente.put("pais_descripcion", pais.getNombre());

					}

				}

				// String tipoPersona = clienteAFacturar.getTipo();
				if (clienteAFacturar.getTipo().equals("F")) // D205 1=Fisica, 2=Juridica
				{
					cliente.put("tipoContribuyente", 1);
				} else {
					cliente.put("tipoContribuyente", 2);
				}
				
				if (clienteAFacturar.getTipoDocumento().intValue() == 0) {
					cliente.put("documentoTipo", 1);// Cedula de identidad
				} else if (clienteAFacturar.getTipoDocumento().intValue() == 2
						|| clienteAFacturar.getTipoDocumento().intValue() == 4) {
					cliente.put("documentoTipo", 3);// Cedula Extranjera
				} else if (clienteAFacturar.getTipoDocumento().intValue() == 3) {
					cliente.put("documentoTipo", 2);// Pasaporte
				} else if (clienteAFacturar.getTipoDocumento().intValue() == 5) {
					cliente.put("documentoTipo", 4);// Carnet de Residencia
				} else if (clienteAFacturar.getTipoDocumento().intValue() == 6) {
					cliente.put("documentoTipo", 6);// Tarjeta diplomatica
				} else if (clienteAFacturar.getTipoDocumento().intValue() == 7) {
					cliente.put("documentoTipo", 5);// Innominado
				} else {
					cliente.put("documentoTipo", 9);// Otros
				}
				cliente.put("documentoNumero", clienteAFacturar.getDocumento());
				cliente.put("telefono", clienteAFacturar.getTelefono());
				cliente.put("celular", clienteAFacturar.getCelular());
				cliente.put("email", clienteAFacturar.getEmail());
				cliente.put("codigo", StringUtil.leftPadding(clienteAFacturar.getCodigo() + "", 3));
				
				dataMap.put("cliente", cliente );
				//FIN CLIENTE
				
				// inicioUsuario
				Map<String, Object> dataMapUsuario = new HashMap<String, Object>();

				Integer documentoTipoUsuario = Integer.valueOf(compraItemCabecera[36] + "");

				if (documentoTipoUsuario == 0 || documentoTipoUsuario == 1) {
					dataMapUsuario.put("documentoTipo", 1);// Cedula de identidad
				} else if (documentoTipoUsuario == 2 || documentoTipoUsuario == 4) {
					dataMapUsuario.put("documentoTipo", 3);// Cedula Extranjera
				} else if (documentoTipoUsuario == 3) {
					dataMapUsuario.put("documentoTipo", 2);// Pasaporte
				} else if (documentoTipoUsuario == 5) {
					dataMapUsuario.put("documentoTipo", 4);// Carnet de Residencia
				} else if (documentoTipoUsuario == 6) {
					dataMapUsuario.put("documentoTipo", 6);// Tarjeta diplomatica
				} else if (documentoTipoUsuario == 7) {
					dataMapUsuario.put("documentoTipo", 5);// Innominado
				} else {
					dataMapUsuario.put("documentoTipo", 9);// Otros
				}
				dataMapUsuario.put("documentoNumero", compraItemCabecera[37]);
				dataMapUsuario.put("nombre", compraItemCabecera[38]);
				dataMapUsuario.put("cargo", compraItemCabecera[39]);
				dataMap.put("usuario", dataMapUsuario);
				//finUsuario
				
				//DATOS DEL VENDEDOR
				Map<String, Object> dataMapFactura = new HashMap<String, Object>();
				dataMapFactura.put("tipoVendedor", 1);	//Tiene que ser dinamico
				dataMapFactura.put("documentoNumero", compraItemCabecera[17]);
				dataMapFactura.put("nombre", compraItemCabecera[18]);				
				Integer documentoTipoVendedor = Integer.valueOf(compraItemCabecera[16] + "");
				Integer institucionEstado = 0;
				
				
				if (compraItemCabecera[29] == null) {
					throw new Exception("Debe asignar un PAIS al Proveedor");
				}
				String paisVnd = String.valueOf(compraItemCabecera[29] + "");
				String tipoPersonaVnd = String.valueOf(compraItemCabecera[31] + "");

				if (documentoTipoVendedor != null && documentoTipoVendedor == 1 ) {
					throw new Exception("Imposible Autofacturar, El Tipo de Documento del Vendedor es RUC");
				}
				dataMapFactura.put("tipoOperacion", 2); // B2C
				/*if (paisVnd.equals("PRY")) {
					dataMapFactura.put("tipoOperacion", 2); // B2C
				} else if (!paisVnd.equals("PRY")) {
					dataMapFactura.put("tipoOperacion", 4); // B2F
				}*/

				dataMapFactura.put("direccion", compraItemCabecera[21]);

				if (compraItemCabecera[22] != null && compraItemCabecera[22] != "") {
					dataMapFactura.put("numeroCasa", compraItemCabecera[22]);
				} else {
					dataMapFactura.put("numeroCasa", 0);
				}

				//dataMapFactura.put("numeroCasa", compraItemItemCabecera[22]);
				dataMapFactura.put("departamento", compraItemCabecera[23]);
				dataMapFactura.put("departamentoDescripcion", compraItemCabecera[24]);
				dataMapFactura.put("distrito", compraItemCabecera[25]);
				dataMapFactura.put("distritoDescripcion", compraItemCabecera[26]);
				dataMapFactura.put("ciudad", compraItemCabecera[27]);
				dataMapFactura.put("ciudadDescripcion", compraItemCabecera[28]);
				dataMapFactura.put("pais", paisVnd);
				dataMapFactura.put("pais_descripcion", compraItemCabecera[30]);
				
				
				if (tipoPersonaVnd.equals("F")) // D205 1=Fisica, 2=Juridica
				{
					dataMapFactura.put("tipoContribuyente", 1);
				} else {
					dataMapFactura.put("tipoContribuyente", 2);
				}
				if (documentoTipoVendedor != 1) {
					if (documentoTipoVendedor == 0) {
						dataMapFactura.put("documentoTipo", 1);// Cedula de identidad
					} else if (documentoTipoVendedor == 2 || documentoTipoVendedor == 4) {
						dataMapFactura.put("documentoTipo", 3);// Cedula Extranjera
					} else if (documentoTipoVendedor == 3) {
						dataMapFactura.put("documentoTipo", 2);// Pasaporte
					} else if (documentoTipoVendedor == 5) {
						dataMapFactura.put("documentoTipo", 4);// Carnet de Residencia
					} else if (documentoTipoVendedor == 6) {
						dataMapFactura.put("documentoTipo", 6);// Tarjeta diplomatica
					} else if (documentoTipoVendedor == 7) {
						dataMapFactura.put("documentoTipo", 5);// Innominado
					} else {
						dataMapFactura.put("documentoTipo", 9);// Otros
					}
				}

				dataMapFactura.put("documentoNumero", compraItemCabecera[17]);
				dataMapFactura.put("telefono", compraItemCabecera[32]);
				dataMapFactura.put("celular", compraItemCabecera[33]);
				dataMapFactura.put("email", compraItemCabecera[34]);
				dataMapFactura.put("codigo", compraItemCabecera[35] + "");
				
				Map<String, Object> ubicacion = new HashMap<String, Object>();
				//Ubicacion de la Autofactura, local del cliente.
				ubicacion.put("lugar", clienteAFacturar.getDireccion());
				if (clienteAFacturar.getCiudad() != null && clienteAFacturar.getCiudad().getCodigo() != null) {
					
					Ciudad ciudad = em.find(Ciudad.class, clienteAFacturar.getCiudad().getCodigo());
					if (ciudad != null) {
						ubicacion.put("ciudad", ciudad.getCodigo());
						ubicacion.put("ciudadDescripcion", ciudad.getNombre());
						
						if (ciudad.getDistrito() != null && ciudad.getDistrito().getCodigo() != null) {
							ClasificadorGenerico distrito = em.find(ClasificadorGenerico.class, ciudad.getDistrito().getCodigo());
							if (distrito.getClasificador() != null && distrito.getClasificador().getCodigo() != null) {
								ClasificadorGenerico departamento = em.find(ClasificadorGenerico.class, distrito.getClasificador().getCodigo());
								if (departamento != null) {
									ubicacion.put("departamento", departamento.getReferencia());
									ubicacion.put("departamentoDescripcion", departamento.getNombre());
								}
							}
							ubicacion.put("distrito", distrito.getReferencia());
							ubicacion.put("distritoDescripcion", distrito.getNombre());
						}	
					}
				}
				
				dataMapFactura.put("ubicacion", ubicacion);
				
				
				dataMap.put("autoFactura", dataMapFactura);
				// Items de la compra
				List<Map<String, Object>> lista = new ArrayList<Map<String, Object>>();

				for (int i = 0; i < comprasItems.size(); i++) {
					// Items de la compra
					Map<String, Object> dataMapProducto = new HashMap<String, Object>();
					Object[] comprasCompraItems = comprasItems.get(i);
					dataMapProducto.put("codigo", comprasCompraItems[42]);
					//dataMapProducto.put("descripcion", comprasCompraItems[43]);
					String descripcion = String.valueOf(comprasCompraItems[43] + "");
					if(descripcion.length()> 120) {
						dataMapProducto.put("descripcion", descripcion.substring(0, 120));
					} else {
						dataMapProducto.put("descripcion", descripcion);
					}
					dataMapProducto.put("observacion", comprasCompraItems[44]);
					dataMapProducto.put("partidaArancelaria", comprasCompraItems[49]);
					dataMapProducto.put("ncm", comprasCompraItems[50]);
					dataMapProducto.put("unidadMedida", comprasCompraItems[51]);
					dataMapProducto.put("cantidad", comprasCompraItems[45]);
					dataMapProducto.put("precioUnitario", comprasCompraItems[46]);
					dataMapProducto.put("cambio", comprasCompraItems[52]);
					dataMapProducto.put("descuento", comprasCompraItems[47]);
					//dataMapProducto.put("descuentoPorcentaje", comprasCompraItems[48]);
					dataMapProducto.put("anticipo", comprasCompraItems[53]);
					dataMapProducto.put("subtotal", comprasCompraItems[54]);
					dataMapProducto.put("pais", comprasCompraItems[55]);
					dataMapProducto.put("paisDescripcion", comprasCompraItems[56]);
					dataMapProducto.put("tolerancia", comprasCompraItems[57]);
					dataMapProducto.put("toleranciaCantidad", comprasCompraItems[58]);
					dataMapProducto.put("toleranciaPorcentaje", comprasCompraItems[59]);
					dataMapProducto.put("cdcAnticipo", comprasCompraItems[60]);
					Map<String, Object> dataMapDncp = new HashMap<String, Object>();

					dataMapDncp.put("codigoNivelGeneral", comprasCompraItems[61]);
					dataMapDncp.put("codigoNivelEspecifico", comprasCompraItems[62]);
					dataMapDncp.put("codigoGtinProducto", comprasCompraItems[63]);
					dataMapDncp.put("codigoNivelPaquete", comprasCompraItems[64]);

					dataMapProducto.put("dncp", dataMapDncp);
					// IVA
					int ivaBase = Double.valueOf(comprasCompraItems[66] + "").intValue();
					int iva = Integer.valueOf(comprasCompraItems[65] + "").intValue();
					
					if (iva == 0) {
						
						dataMapProducto.put("ivaTipo", 3);
						
					} else if (iva == 5 || iva == 10) {

						if (ivaBase == 100) {
							
							dataMapProducto.put("ivaTipo", 1);
							
						} else {
							
							dataMapProducto.put("ivaTipo", 4);
						}

					}
					dataMapProducto.put("ivaBase", ivaBase);
					dataMapProducto.put("iva", iva);
					dataMapProducto.put("lote", comprasCompraItems[67]);
					dataMapProducto.put("vencimiento", comprasCompraItems[68]);
					dataMapProducto.put("numeroSerie", comprasCompraItems[69]);
					dataMapProducto.put("numeroPedido", comprasCompraItems[70]);
					dataMapProducto.put("numeroSeguimiento", comprasCompraItems[71]);
					
					Map<String, Object> dataMapImportador = new HashMap<String, Object>();
					
					dataMapImportador.put("nombre", comprasCompraItems[72]);
					dataMapImportador.put("direccion", comprasCompraItems[73]);
					dataMapImportador.put("registroImportador", comprasCompraItems[74]);
					dataMapImportador.put("registroSenave", comprasCompraItems[75]);
					dataMapImportador.put("registroEntidadComercial", comprasCompraItems[76]);
					dataMapProducto.put("importador", dataMapImportador);
				
					dataMapProducto.put("sectorAutomotor", null);
					
					lista.add(dataMapProducto);

				}

				dataMap.put("items", lista);
				Integer egresoNumero = timbradoFacturacionMovimiento.getIngresoEgresoNumero() ;
				Integer egreso = timbradoFacturacionMovimiento.getEgreso().getEgreso();
				
				Map<String, Object> condicionMap = documentoElectronicoFinancieroFacade.recuperarFormasDePagoParaCondicion(egreso, egresoNumero);
				dataMap.put("condicion", condicionMap);

				dataMap.put("sectorEnergiaElectrica", null);
				dataMap.put("sectorSeguros", null);
				dataMap.put("sectorSupermercados", null);
				dataMap.put("sectorAdicional", null);
				dataMap.put("detalleTransporte", null);
				dataMap.put("complementarios", null);
				
				Map<String, Object> documentoAsociadoMap = new HashMap<String, Object>();
				documentoAsociadoMap.put("formato", 3);
				documentoAsociadoMap.put("constanciaTipo", 1);
				//documentoAsociadoMap.put("constanciaNumero", Integer.valueOf(compraItemCabecera[79]+""));
				documentoAsociadoMap.put("constanciaNumero", compraItemCabecera[79]+"");
				documentoAsociadoMap.put("constanciaControl", compraItemCabecera[80]+"");
				
				dataMap.put("documentoAsociado", documentoAsociadoMap);

				
				//--- - - - - - - - - - - 
				//--- - - - - - - - - - - 
				//--- - - - - - - - - - - 
				resultado = documentoElectronicoFinancieroFacade.crearDocumentoElectronicoEnFacturaSend(dataMap, false);
				//--- - - - - - - - - - - 
				//--- - - - - - - - - - - 
				//--- - - - - - - - - - - 
				
			}
			
			return resultado;
			
		} catch (Exception e) {
			e.printStackTrace();
			//throw new InfoException(e);
			resultado.put("success", false);
			resultado.put("error", e.getMessage());
			
			return resultado;
		}
	}
}
