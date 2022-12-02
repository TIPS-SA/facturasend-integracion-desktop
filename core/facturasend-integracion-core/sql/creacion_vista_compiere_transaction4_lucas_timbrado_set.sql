


-- Compiere Vista para Integracion
--DROP VIEW RV_C_Invoice_FacturaSend

CREATE OR REPLACE VIEW RV_C_Invoice_FacturaSend AS 

	SELECT
	ci.C_INVOICE_ID 											AS transaccion_id,
	1 															AS tipo_documento,
--	SUBSTR(ci.MOLI_PREIMPNO, 1, 3) 								AS establecimiento,
	translate ('001' using nchar_cs)							AS establecimiento,
	translate ('003' using nchar_cs)							AS punto,
--	SUBSTR(ci.MOLI_PREIMPNO, 9, 7) 								AS numero,
	TRANSLATE(CAST(ci.MOLI_NUMERACION_PRUEBA AS VARCHAR(7)) USING nchar_cs)					AS numero,
	NULL 														AS serie,
	NULL 														AS descripcion,
	ci.description 												AS observacion,
--	ci.DATEINVOICED 											AS fecha,
	CAST(TRANSLATE('2022-11-29T00:00:00' USING nchar_cs) AS VARCHAR(19))								AS fecha,
	1 															AS tipo_emision,	-- 1=Normal
	1 															AS tipo_transicion,	-- 1=Venta de mercadería (Por defecto).
	1 															AS tipo_impuesto,	-- 1=IVA. 2=ISC. 3=Renta. 4=Ninguno. 5=IVA - Renta.
	cc2.ISO_CODE 												AS moneda,
	1 															AS tipo_cambio, 	-- 1- GLOBAL 2- Item 

	round(currencyrate(ci.c_currency_id,'344', ci.DATEINVOICED, ci.C_CONVERSIONTYPE_ID, ci.AD_CLIENT_ID, ci.AD_ORG_ID), 0) 
																AS cambio, 			--Valor de la Cotizacion
	--
	-- Datos de Cliente
	--
	CASE WHEN (cb.TAXID IS NULL OR cb.TAXID = '') THEN
	    'false' 
	ELSE 
	    'true' 
	END 														AS cliente_contribuyente,
	
	CAST(cb.TAXID AS VARCHAR(20)) 								AS cliente_ruc,
	cb.NAME 													AS cliente_razon_social,
	cb.NAME 													AS cliente_nombre_fantasia,
	CASE WHEN ((cb.TAXID IS NULL OR cb.TAXID = '') OR (cb.MOLI_BPARTNERTYPE IS NOT NULL AND cb.MOLI_BPARTNERTYPE = 'F')) THEN
		2
	ELSE
		1
	END															AS cliente_tipo_operacion, --1= B2B, 2= B2C, 3= B2G, 4= B2F

	cbl.NAME 													AS cliente_direccion,
	'0'
--	ADDRESS2 
	
																AS cliente_numero_casa,
	--AS cliente_departamento,
	--AS cliente_distrito,
	--NULL AS cliente_ciudad,
	3383 														AS cliente_ciudad,
	
	'PRY'
	--(SELECT cc.MOLI_ALP3COUNTRYCODE FROM C_COUNTRY cc WHERE cc.C_COUNTRY_ID = (SELECT  first_value(cl.C_COUNTRY_ID )over (partition BY cl.C_COUNTRY_ID ORDER BY cl.CREATED DESC) FROM C_LOCATION cl WHERE cl.C_LOCATION_ID = cbl.C_LOCATION_ID ) ) 
																AS cliente_pais,
	CASE WHEN (cb.MOLI_BPARTNERTYPE IS NOT NULL AND cb.MOLI_BPARTNERTYPE = 'F') THEN
	    1 
	ELSE 
	    2 
	END 														AS cliente_tipo_contribuyente,
	cb.moli_typeDocument 				 						AS cliente_documento_tipo,
	CAST(cb.MOLI_PHOTOID AS VARCHAR2(20)) 						AS cliente_documento_numero,

	/*CASE WHEN cb.TAXID IS NULL OR cb.TAXID = '' THEN
	    NULL 
	ELSE
	    
	END AS cliente_tipo_contribuyente,*/
	--AS cliente_documento_tipo,
	cbl.PHONE 													AS cliente_telefono,
	COALESCE(cbl.PHONE2, n'') 									AS cliente_celular,
	--COALESCE((SELECT EMAIL FROM AD_USER au WHERE au.C_BPARTNER_ID = cb.C_BPARTNER_ID ORDER BY au.UPDATED DESC LIMIT 1), '') 
	NULL
																AS cliente_email,
	cb.VALUE 													AS cliente_codigo,
	--usuario_documento_tipo,
	--
	-- Datos de Usuario
	--
	1 															AS usuario_documento_tipo,	--1= Cédula paraguaya, 2= Pasaporte, 3= Cédula extranjera, 4= Carnet de residencia
	cbu.moli_photoid 											AS usuario_documento_numero,
	cbu.name 													AS usuario_nombre,
	u.title 													AS usuario_cargo,
	--
	-- Datos de la Factura
	--
	1 															AS factura_presencia, -- 1= Operación presencial, 2= Operación electrónica, 3= Operación telemarketing, 4= Venta a domicilio, 5= Operación bancaria
	NULL 														AS factura_fecha_envio,
	'false'														AS factura_ticket,
	--
	-- Datos de la Nota de Credito
	--
	NULL 														AS nota_credito_motivo, 
	--
	-- Datos de la Nota de Remision
	--
	NULL 														AS nota_remision_motivo,
	NULL 														AS nota_remision_tipo_responsable,
	NULL 														AS nota_remision_kms,
	NULL 														AS nota_remision_fecha_factura,
	NULL 														AS nota_remision_costo_flete,

	--
	-- Totales y Otros
	--
	ci.totallines 												AS total,
	ci.moli_cdc 												AS cdc,	
	TO_NUMBER(ci.moli_fsstatus)									AS estado,
	ci.moli_fspaused 											AS pausado,
	ci.moli_fsError 											AS error,
	--
	-- Items
	--
	mp.VALUE 													AS item_codigo,
	mp.NAME 													AS item_descripcion,
	NULL														AS item_observacion,
	77 															AS item_unidad_medida,
	cil.QTYENTERED 												AS item_cantidad,
	cil.PRICELIST 												AS item_precio_unitario,
	-- AS item_cambio,
	(cil.PRICELIST - cil.PRICEENTERED) 							AS item_descuento,
	
	CASE WHEN cil.PRICELIST = (cil.PRICELIST - cil.PRICEENTERED) THEN 
		-- Cuando el precio unitario y descuento son iguales
		3
	ELSE 
		CASE WHEN ct.RATE = 5 OR ct.RATE = 10 THEN  
	    	1
		WHEN ct.RATE = 0 THEN 
		    3
		ELSE 
		    4   
		END
	END 														AS item_iva_tipo,
	
	CASE WHEN cil.PRICELIST = (cil.PRICELIST - cil.PRICEENTERED) THEN 
		-- Cuando el precio unitario y descuento son iguales
		0
	ELSE 
		CASE WHEN ct.RATE = 5 OR ct.RATE = 10 THEN
		    100
		WHEN ct.RATE = 0 THEN 
		    0
		ELSE 
		    ct.RATE * 10
		END
	END 														AS item_iva_base,
	
	CASE WHEN cil.PRICELIST = (cil.PRICELIST - cil.PRICEENTERED) THEN 
		-- Cuando el precio unitario y descuento son iguales
		0
	ELSE 
		CASE WHEN ct.RATE = 0 THEN
		    0
		WHEN ct.RATE = 5 THEN 
		    5
		ELSE 
		    10
		END
	END 														AS item_iva,
	NULL  														AS item_lote,
	NULL  														AS item_vencimiento,
	NULL  														AS item_numero_serie,
	NULL  														AS item_numero_pedido,
	NULL  														AS item_numero_seguimiento,
	
	--
	-- Datos de Documento Asociado
	--
	NULL 														AS doc_aso_formato,
	NULL 														AS doc_aso_cdc,
	NULL 														AS doc_aso_tipo_documento_impreso,
	NULL 														AS doc_aso_timbrado,
	NULL 														AS doc_aso_establecimiento,
	NULL 														AS doc_aso_punto,
	NULL 														AS doc_aso_numero,
	NULL 														AS doc_aso_fecha,
	NULL 														AS doc_aso_numero_retencion,
	NULL 														AS doc_aso_reso_credito_fiscal,
	NULL 														AS doc_aso_constancia_tipo,
	NULL 														AS doc_aso_constancia_numero,
	NULL 														AS doc_aso_constancia_control,
	
	--
	-- Datos de Transporte
	--
	NULL 														AS tra_tipo,
	NULL 														AS tra_modalidad,
	NULL 														AS tra_tipo_responsable,
	NULL 														AS tra_condicion_negociacion,
	NULL 														AS tra_sali_direccion,
	NULL 														AS tra_sali_numero_casa,
	NULL 														AS tra_sali_comple_direccion1,
	NULL 														AS tra_sali_comple_direccion2,
	NULL 														AS tra_sali_departamento,
	NULL 														AS tra_sali_distrito,
	NULL 														AS tra_sali_ciudad,
	NULL 														AS tra_sali_pais,
	NULL 														AS tra_entre_direccion,
	NULL 														AS tra_entre_numero_casa,
	NULL 														AS tra_entre_comple_direccion1,
	NULL 														AS tra_entre_comple_direccion2,
	NULL 														AS tra_entre_departamento,
	NULL 														AS tra_entre_distrito,
	NULL 														AS tra_entre_ciudad,
	NULL 														AS tra_entre_pais,
	NULL 														AS tra_entre_telefono_contacto,
	NULL 														AS tra_vehi_tipo,
	NULL 														AS tra_vehi_marca,
	NULL 														AS tra_vehi_documento_tipo,
	NULL 														AS tra_vehi_documento_numero,
	NULL 														AS tra_vehi_obs,
	NULL 														AS tra_vehi_numero_matricula,
	NULL 														AS tra_vehi_numero_vuelo,
	NULL 														AS tra_transpor_contribuyente,
	NULL 														AS tra_transpor_nombre,
	NULL 														AS tra_transpor_ruc,
	NULL 														AS tra_transpor_documento_tipo,
	NULL 														AS tra_transpor_documento_numero,
	NULL 														AS tra_transpor_direccion,
	NULL 														AS tra_transpor_obs,
	NULL 														AS tra_transpor_pais,
	NULL 														AS tra_transpor_chofer_doc_numero,
	NULL 														AS tra_transpor_chofer_nombre,
	NULL 														AS tra_transpor_chofer_direccion,
	NULL 														AS tra_transpor_agente_nombre,
	NULL 														AS tra_transpor_agente_ruc,
	NULL 														AS tra_transpor_agente_direccion,

	---
	-- Campos informativos
	---
	ci.DOCACTION,
	ci.DOCSTATUS,
	ci.MOLI_PREIMPNO,
	ci.AD_ORG_ID,
	ci.AD_CLIENT_ID,
	ci.AD_USER_ID

	FROM 
		C_Invoice ci
		INNER JOIN C_InvoiceLine cil ON
			(ci.C_Invoice_ID = cil.C_Invoice_ID)
		LEFT OUTER JOIN M_Product mp ON
			(cil.M_Product_ID = mp.M_Product_ID)
		INNER JOIN C_Currency cc2 ON
			(ci.C_Currency_ID = cc2.C_Currency_ID)
		INNER JOIN C_DocType cd ON
			(ci.C_DocType_ID = cd.C_DocType_ID AND cd.name = 'AR Invoice')
		INNER JOIN C_PaymentTerm pt ON
			(ci.C_PaymentTerm_ID = pt.C_PaymentTerm_ID)
		INNER JOIN C_BPartner cb ON
			(ci.C_BPartner_ID = cb.C_BPartner_ID)
		LEFT OUTER JOIN AD_User bpc ON
			(ci.AD_User_ID = bpc.AD_User_ID)
		INNER JOIN C_BPartner_Location cbl ON
			(ci.C_BPartner_Location_ID = cbl.C_BPartner_Location_ID)
		INNER JOIN C_Location l ON
			(cbl.C_Location_ID = l.C_Location_ID)
		LEFT OUTER JOIN AD_User u ON
			(ci.SalesRep_ID = u.AD_User_ID)
		INNER JOIN C_BPartner cbu ON
			(u.C_BPartner_ID = cbu.C_BPartner_ID)
		INNER JOIN C_TAX ct ON
			(cil.C_TAX_ID = ct.C_TAX_ID)
		INNER JOIN C_TAXCATEGORY tc ON
			(tc.C_TAXCATEGORY_ID = ct.C_TAXCATEGORY_ID)
		INNER JOIN M_PRICELIST mp ON 
			(mp.m_pricelist_id = ci.M_PRICELIST_ID AND mp.NAME = 'Mayorista')
	WHERE 1=1
	
		AND ci.AD_ORG_ID = 1000005
--		AND ci.DOCACTION = 'CL'
		AND ci.DOCSTATUS = 'CO'	--Completed
		--Condiciones Temporales
		AND MOLI_PREIMPNO IS NOT NULL
--		AND LENGTH (MOLI_PREIMPNO) = 15
--		AND MOLI_PREIMPNO LIKE '002-003-%'
		
		--Los que ya no debe traer por que ya se integró.
		--AND CAST((SELECT MOLI_VALUE FROM MOLI_invoiceData mid WHERE mid.C_INVOICE_ID = ci.C_INVOICE_ID 
		--	AND mid.MOLI_NAME = 'CDC') AS VARCHAR2(50)) IS NULL,
		
		/*CAST((SELECT MOLI_VALUE FROM MOLI_invoiceData mid WHERE mid.C_INVOICE_ID = ci.C_INVOICE_ID 
			AND mid.MOLI_NAME = 'PAUSADO') AS VARCHAR2(10)) AS PAUSADO,
		*/
		
	--
	--
	--
	UNION ALL	
	
			
	SELECT
	ci.C_INVOICE_ID 											AS transaccion_id,
	5 															AS tipo_documento,
	SUBSTR(ci.MOLI_PREIMPNO, 1, 3) 								AS establecimiento,
	SUBSTR(ci.MOLI_PREIMPNO, 5, 3) 								AS punto,
	SUBSTR(ci.MOLI_PREIMPNO, 9, 7) 								AS numero,
	NULL 														AS serie,
	NULL 														AS descripcion,
	ci.description 												AS observacion,
--	ci.DATEINVOICED 											AS fecha,
	CAST(TRANSLATE('2022-11-29T00:00:00' USING nchar_cs) AS VARCHAR(19)) AS fecha,
	1 															AS tipo_emision,	-- 1=Normal
	1 															AS tipo_transicion,	-- 1=Venta de mercadería (Por defecto).
	1 															AS tipo_impuesto,	-- 1=IVA. 2=ISC. 3=Renta. 4=Ninguno. 5=IVA - Renta.
	cc2.ISO_CODE 												AS moneda,
	1 															AS tipo_cambio, 	-- 1- GLOBAL 2- Item 

	round(currencyrate(ci.c_currency_id,'344', ci.DATEINVOICED, ci.C_CONVERSIONTYPE_ID, ci.AD_CLIENT_ID, ci.AD_ORG_ID), 0) 
																AS cambio, 			--Valor de la Cotizacion
	--
	-- Datos de Cliente
	--
	CASE WHEN (cb.TAXID IS NULL OR cb.TAXID = '') THEN
	    'false' 
	ELSE 
	    'true' 
	END 														AS cliente_contribuyente,
	
	CAST(cb.TAXID AS VARCHAR(20)) 								AS cliente_ruc,
	cb.NAME 													AS cliente_razon_social,
	cb.NAME 													AS cliente_nombre_fantasia,
	CASE WHEN ((cb.TAXID IS NULL OR cb.TAXID = '') OR (cb.MOLI_BPARTNERTYPE IS NOT NULL AND cb.MOLI_BPARTNERTYPE = 'F')) THEN
		2
	ELSE
		1
	END															AS cliente_tipo_operacion, --1= B2B, 2= B2C, 3= B2G, 4= B2F

	cbl.NAME 													AS cliente_direccion,
	'0'
--	ADDRESS2 
	
																AS cliente_numero_casa,
	--AS cliente_departamento,
	--AS cliente_distrito,
	--NULL AS cliente_ciudad,
	3383 														AS cliente_ciudad,
	
	'PRY'
	--(SELECT cc.MOLI_ALP3COUNTRYCODE FROM C_COUNTRY cc WHERE cc.C_COUNTRY_ID = (SELECT  first_value(cl.C_COUNTRY_ID )over (partition BY cl.C_COUNTRY_ID ORDER BY cl.CREATED DESC) FROM C_LOCATION cl WHERE cl.C_LOCATION_ID = cbl.C_LOCATION_ID ) ) 
																AS cliente_pais,
	CASE WHEN (cb.MOLI_BPARTNERTYPE IS NOT NULL AND cb.MOLI_BPARTNERTYPE = 'F') THEN
	    1 
	ELSE 
	    2 
	END 														AS cliente_tipo_contribuyente,
	cb.moli_typeDocument 				 						AS cliente_documento_tipo,
	CAST(cb.MOLI_PHOTOID AS VARCHAR2(20)) 						AS cliente_documento_numero,

	/*CASE WHEN cb.TAXID IS NULL OR cb.TAXID = '' THEN
	    NULL 
	ELSE
	    
	END AS cliente_tipo_contribuyente,*/
	--AS cliente_documento_tipo,
	cbl.PHONE 													AS cliente_telefono,
	COALESCE(cbl.PHONE2, n'') 									AS cliente_celular,
	--COALESCE((SELECT EMAIL FROM AD_USER au WHERE au.C_BPARTNER_ID = cb.C_BPARTNER_ID ORDER BY au.UPDATED DESC LIMIT 1), '') 
	NULL
																AS cliente_email,
	cb.VALUE 													AS cliente_codigo,
	--usuario_documento_tipo,
	--
	-- Datos de Usuario
	--
	1 															AS usuario_documento_tipo,	--1= Cédula paraguaya, 2= Pasaporte, 3= Cédula extranjera, 4= Carnet de residencia
	cbu.moli_photoid 											AS usuario_documento_numero,
	cbu.name 													AS usuario_nombre,
	u.title 													AS usuario_cargo,
	--
	-- Datos de la Factura
	--
	1 															AS factura_presencia, -- 1= Operación presencial, 2= Operación electrónica, 3= Operación telemarketing, 4= Venta a domicilio, 5= Operación bancaria
	NULL 														AS factura_fecha_envio,
	'false'														AS factura_ticket,
	--
	-- Datos de la Nota de Credito
	--
	NULL 														AS nota_credito_motivo, 
	--
	-- Datos de la Nota de Remision
	--
	NULL 														AS nota_remision_motivo,
	NULL 														AS nota_remision_tipo_responsable,
	NULL 														AS nota_remision_kms,
	NULL 														AS nota_remision_fecha_factura,
	NULL 														AS nota_remision_costo_flete,

	--
	-- Totales y Otros
	--
	ci.GRANDTOTAL 												AS total,
	ci.moli_cdc 												AS cdc,	
	TO_NUMBER(ci.moli_fsstatus)									AS estado,
	ci.moli_fsPaused											AS pausado,
	ci.moli_fsError 											AS error,
	--
	-- Items
	--
	mp.VALUE 													AS item_codigo,
	mp.NAME 													AS item_descripcion,
	NULL														AS item_observacion,
	77 															AS item_unidad_medida,
	cil.QTYENTERED 												AS item_cantidad,
	cil.PRICELIST 												AS item_precio_unitario,
	-- AS item_cambio,
	(cil.PRICELIST - cil.PRICEENTERED) 							AS item_descuento,
	
	CASE WHEN cil.PRICELIST = (cil.PRICELIST - cil.PRICEENTERED) THEN 
		-- Cuando el precio unitario y descuento son iguales
		3
	ELSE 
		CASE WHEN ct.RATE = 5 OR ct.RATE = 10 THEN  
	    	1
		WHEN ct.RATE = 0 THEN 
		    3
		ELSE 
		    4   
		END
	END 														AS item_iva_tipo,
	
	CASE WHEN cil.PRICELIST = (cil.PRICELIST - cil.PRICEENTERED) THEN 
		-- Cuando el precio unitario y descuento son iguales
		0
	ELSE 
		CASE WHEN ct.RATE = 5 OR ct.RATE = 10 THEN
		    100
		WHEN ct.RATE = 0 THEN 
		    0
		ELSE 
		    ct.RATE * 10
		END
	END 														AS item_iva_base,
	
	CASE WHEN cil.PRICELIST = (cil.PRICELIST - cil.PRICEENTERED) THEN 
		-- Cuando el precio unitario y descuento son iguales
		0
	ELSE 
		CASE WHEN ct.RATE = 0 THEN
		    0
		WHEN ct.RATE = 5 THEN 
		    5
		ELSE 
		    10
		END
	END 														AS item_iva,
	NULL  														AS item_lote,
	NULL  														AS item_vencimiento,
	NULL  														AS item_numero_serie,
	NULL  														AS item_numero_pedido,
	NULL  														AS item_numero_seguimiento,
	
	--
	-- Datos de Documento Asociado
	--
	NULL 														AS doc_aso_formato,
	NULL 														AS doc_aso_cdc,
	NULL 														AS doc_aso_tipo_documento_impreso,
	NULL 														AS doc_aso_timbrado,
	NULL 														AS doc_aso_establecimiento,
	NULL 														AS doc_aso_punto,
	NULL 														AS doc_aso_numero,
	NULL 														AS doc_aso_fecha,
	NULL 														AS doc_aso_numero_retencion,
	NULL 														AS doc_aso_reso_credito_fiscal,
	NULL 														AS doc_aso_constancia_tipo,
	NULL 														AS doc_aso_constancia_numero,
	NULL 														AS doc_aso_constancia_control,
	
	--
	-- Datos de Transporte
	--
	NULL 														AS tra_tipo,
	NULL 														AS tra_modalidad,
	NULL 														AS tra_tipo_responsable,
	NULL 														AS tra_condicion_negociacion,
	NULL 														AS tra_sali_direccion,
	NULL 														AS tra_sali_numero_casa,
	NULL 														AS tra_sali_comple_direccion1,
	NULL 														AS tra_sali_comple_direccion2,
	NULL 														AS tra_sali_departamento,
	NULL 														AS tra_sali_distrito,
	NULL 														AS tra_sali_ciudad,
	NULL 														AS tra_sali_pais,
	NULL 														AS tra_entre_direccion,
	NULL 														AS tra_entre_numero_casa,
	NULL 														AS tra_entre_comple_direccion1,
	NULL 														AS tra_entre_comple_direccion2,
	NULL 														AS tra_entre_departamento,
	NULL 														AS tra_entre_distrito,
	NULL 														AS tra_entre_ciudad,
	NULL 														AS tra_entre_pais,
	NULL 														AS tra_entre_telefono_contacto,
	NULL 														AS tra_vehi_tipo,
	NULL 														AS tra_vehi_marca,
	NULL 														AS tra_vehi_documento_tipo,
	NULL 														AS tra_vehi_documento_numero,
	NULL 														AS tra_vehi_obs,
	NULL 														AS tra_vehi_numero_matricula,
	NULL 														AS tra_vehi_numero_vuelo,
	NULL 														AS tra_transpor_contribuyente,
	NULL 														AS tra_transpor_nombre,
	NULL 														AS tra_transpor_ruc,
	NULL 														AS tra_transpor_documento_tipo,
	NULL 														AS tra_transpor_documento_numero,
	NULL 														AS tra_transpor_direccion,
	NULL 														AS tra_transpor_obs,
	NULL 														AS tra_transpor_pais,
	NULL 														AS tra_transpor_chofer_doc_numero,
	NULL 														AS tra_transpor_chofer_nombre,
	NULL 														AS tra_transpor_chofer_direccion,
	NULL 														AS tra_transpor_agente_nombre,
	NULL 														AS tra_transpor_agente_ruc,
	NULL 														AS tra_transpor_agente_direccion,

	---
	-- Campos informativos
	---
	ci.DOCACTION,
	ci.DOCSTATUS,
	ci.MOLI_PREIMPNO,
	ci.AD_ORG_ID,
	ci.AD_CLIENT_ID,
	ci.AD_USER_ID

	FROM 
		C_Invoice ci
		INNER JOIN C_InvoiceLine cil ON
			(ci.C_Invoice_ID = cil.C_Invoice_ID)
		LEFT OUTER JOIN M_Product mp ON
			(cil.M_Product_ID = mp.M_Product_ID)
		INNER JOIN C_Currency cc2 ON
			(ci.C_Currency_ID = cc2.C_Currency_ID)
		INNER JOIN C_DocType cd ON
			(ci.C_DocType_ID = cd.C_DocType_ID AND cd.name = 'AR Credit Memo')
		INNER JOIN C_PaymentTerm pt ON
			(ci.C_PaymentTerm_ID = pt.C_PaymentTerm_ID)
		INNER JOIN C_BPartner cb ON
			(ci.C_BPartner_ID = cb.C_BPartner_ID)
		LEFT OUTER JOIN AD_User bpc ON
			(ci.AD_User_ID = bpc.AD_User_ID)
		INNER JOIN C_BPartner_Location cbl ON
			(ci.C_BPartner_Location_ID = cbl.C_BPartner_Location_ID)
		INNER JOIN C_Location l ON
			(cbl.C_Location_ID = l.C_Location_ID)
		LEFT OUTER JOIN AD_User u ON
			(ci.SalesRep_ID = u.AD_User_ID)
		INNER JOIN C_BPartner cbu ON
			(u.C_BPartner_ID = cbu.C_BPartner_ID)
		INNER JOIN C_TAX ct ON
			(cil.C_TAX_ID = ct.C_TAX_ID)
		INNER JOIN C_TAXCATEGORY tc ON
			(tc.C_TAXCATEGORY_ID = ct.C_TAXCATEGORY_ID)
		INNER JOIN M_PRICELIST mp ON 
			(mp.m_pricelist_id = ci.M_PRICELIST_ID AND mp.NAME = 'Mayorista')
	WHERE 1=1
	
		AND ci.AD_ORG_ID = 1000005
--		AND ci.DOCACTION = 'CL'
		AND ci.DOCSTATUS = 'CO'	--Completed
		--Condiciones Temporales
		AND MOLI_PREIMPNO IS NOT NULL
--		AND LENGTH (MOLI_PREIMPNO) = 15
--		AND MOLI_PREIMPNO LIKE '002-003-%'
		
		--Los que ya no debe traer por que ya se integró.
		--AND CAST((SELECT MOLI_VALUE FROM MOLI_invoiceData mid WHERE mid.C_INVOICE_ID = ci.C_INVOICE_ID 
		--	AND mid.MOLI_NAME = 'CDC') AS VARCHAR2(50)) IS NULL,
		
		/*CAST((SELECT MOLI_VALUE FROM MOLI_invoiceData mid WHERE mid.C_INVOICE_ID = ci.C_INVOICE_ID 
			AND mid.MOLI_NAME = 'PAUSADO') AS VARCHAR2(10)) AS PAUSADO,
		*/
		
	--
	--
	--
		
	
			
				
		
		
	ORDER BY 1 DESC;	







	
SELECT ci.salesrep_id FROM C_INVOICE ci 

	







SELECT * FROM 
( SELECT 
      ROWNUM rn, a.* 
  FROM 
   ( SELECT transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, 
cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
establecimiento, punto, numero, serie, total, cdc, estado, error, pausado 
FROM RV_C_Invoice_FacturaSend 
WHERE ( 
	(establecimiento || '-' || punto || '-' || numero || COALESCE(serie, '')) LIKE '%%' 
	OR UPPER(COALESCE(cliente_ruc, '')) LIKE '%%' 
	OR UPPER(COALESCE(cliente_documento_numero, '')) LIKE '%%' 
	OR UPPER(cliente_razon_social) LIKE '%%' 
) 
AND tipo_documento = 1 
GROUP BY transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, 
cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
establecimiento, punto, numero, serie, total, cdc, estado, error, pausado 
ORDER BY establecimiento DESC, punto DESC, numero DESC 
 ) a 
WHERE 
ROWNUM <= 10 
) 
WHERE 
rn  >= 1



SELECT * FROM RV_C_Invoice_FacturaSend 
WHERE transaccion_id = 1566797





SELECT count(*) FROM (SELECT * FROM RV_C_Invoice_FacturaSend) 





SELECT INSTR('Marcos Adrian Jara Rodriguez','ar', 0, 2)
"Reversed Instring"
     FROM DUAL;
    
    

SELECT * FROM ( SELECT       ROWNUM rn, a.*   FROM    ( SELECT * FROM RV_C_Invoice_FacturaSend ) a WHEREROWNUM <= 20 ) WHERE rn  >= 1




SELECT transaccion_id, tipo_documento, descripcion, observacion, fecha, 
		cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
		establecimiento, punto, numero, serie, total 
FROM RV_C_Invoice_FacturaSend

--FILTRO
WHERE (establecimiento || '-' || punto || '-' || numero || COALESCE(serie, '')) LIKE '%002-001-0026746%'

GROUP BY transaccion_id, tipo_documento, descripcion, observacion, fecha, 
		cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
		establecimiento, punto, numero, serie, total
ORDER BY numero;



SELECT * FROM C_INVOICE ci WHERE AD_ORG_ID = 1000005
SELECT C_DOCTYPE_ID, NAME, PRINTNAME FROM C_DOCTYPE cd WHERE C_DOCTYPE_ID IN (116, 117) 
SELECT C_DOCTYPE_ID, NAME, PRINTNAME FROM C_DOCTYPE cd WHERE UPPER(NAME) LIKE 'AR I%';
SELECT C_DOCTYPE_ID, NAME, PRINTNAME FROM C_DOCTYPE cd WHERE C_DOCTYPE_ID IN (1000002, 1000005) 
SELECT C_DOCTYPE_ID, NAME, PRINTNAME FROM C_DOCTYPE cd WHERE C_DOCTYPE_ID IN (116, 117) 

SELECT C_DOCTYPE_ID, NAME, PRINTNAME FROM C_DOCTYPE cd WHERE name = 'AR Credit Memo'
SELECT * FROM C_DOCTYPE cd WHERE name = 'AR Credit Memo'

SELECT * FROM C_INVOICE ci 
WHERE AD_ORG_ID = 1000005
AND DOCACTION = 'CL'
AND DOCSTATUS = 'CO'
AND MOLI_PREIMPNO IS NOT NULL
AND LENGTH (MOLI_PREIMPNO) = 15
AND MOLI_PREIMPNO LIKE '002-001-%'
 
ORDER BY MOLI_PREIMPNO 




SELECT SUBSTR('001-002-1234567', 1, 3) "Substring" FROM DUAL;
SELECT SUBSTR('001-002-1234567', 5, 3) "Substring" FROM DUAL;
SELECT SUBSTR('001-002-1234567', 9, 7) "Substring" FROM DUAL;
    
    

SELECT * FROM C_BPARTNER

SELECT TAXID FROM C_BPARTNER





https://docs.google.com/document/d/1Xcew0aYj4bgxIApuPDFGHi9BP592XLv-yOJuTwbgW6s/edit


SELECT transaccion_id 
FROM RV_C_Invoice_FacturaSend 
WHERE tipo_documento = 1 
GROUP BY transaccion_id, numero 
ORDER BY numero DESC 


SELECT * FROM 
( SELECT 
      ROWNUM rn, a.* 
  FROM 
   ( SELECT * 
FROM C_INVOICE ci 

 ) a 
WHERE 
ROWNUM <= 50 
) 
WHERE 
rn  >= 1












-- Compiere Vista para Integracion
-- DROP TABLE IF EXISTS RV_C_Invoice_FacturaSend;
DROP VIEW RV_C_Paymentterm_FacturaSend;
CREATE OR REPLACE VIEW RV_C_Paymentterm_FacturaSend AS 
	SELECT 
	
	ci.C_INVOICE_ID AS transaccion_id,
	2 AS tipo,			-- Siempre a Credito
	1 AS credito_tipo,	-- Siempre a plazo
	1 AS tipo_documento,	-- 1=Ventas, 5=Nota de credito, ver para hacer por el documento TYPE
	SUBSTR(cp.NAME,1,15) AS credito_plazo
--	cp.NAME AS credito_plazo
	FROM C_INVOICE ci
	--, C_ORDER co
	, C_PAYMENTTERM cp 
	WHERE cp.C_PAYMENTTERM_ID = ci.C_PAYMENTTERM_ID
	--AND ci.C_ORDER_ID = co.C_ORDER_ID  
	--AND co.C_PAYMENTTERM_ID IS NOT NULL;


SELECT C_PAYMENTTERM_ID FROM C_INVOICE

SELECT * FROM RV_C_Paymentterm_FacturaSend
WHERE transaccion_id = 1566797


select 1, 2, 3 FROM dual
SELECT * FROM MOLI_invoiceData
ad_client_id, ad_org_id, isactive

SELECT 1 FROM DUAL GROUP BY 1
CURRENTNEXT
INCREMENTNO

SELECT MAX(CURRENTNEXT) FROM AD_SEQUENCE as2 WHERE name = 'MOLI_invoiceData' GROUP BY CURRENTNEXT 
HAVING COUNT(CURRENTNEXT) > 1

(SELECT MAX(ad.CURRENTNEXT) as MAXIMO FROM AD_SEQUENCE ad WHERE ad.name = 'MOLI_invoiceData' HAVING MAX(ad.CURRENTNEXT) > 1)

SELECT CAST(MAX(CURRENTNEXT) AS NUMBER(10)) FROM AD_SEQUENCE as2 WHERE name = 'MOLI_invoiceData' GROUP BY CURRENTNEXT


   SELECT * FROM MOLI_invoiceData ORDER BY moli_invoicedata_id DESC;

   SELECT * FROM MOLI_invoiceData WHERE C_INVOICE_ID = 1315268 ORDER BY moli_invoicedata_id DESC;
 --delete FROM MOLI_invoiceData WHERE C_INVOICE_ID = 1566797 
UPDATE AD_SEQUENCE SET CURRENTNEXT = (MAX(CURRENTNEXT) + INCREMENTNO) WHERE name = 'MOLI_invoiceData'


INSERT INTO MOLI_invoiceData (c_invoice_id, moli_invoicedata_id, ad_org_id, ad_client_id, moli_name, moli_value) VALUES ( '1566797', (SELECT MAX(ad.CURRENTNEXT) as MAXIMO FROM AD_SEQUENCE ad WHERE ad.name = 'MOLI_invoiceData' HAVING MAX(ad.CURRENTNEXT) > 1), '1000005', '1000000', 'error', 'Debe informar el valor del Cambio en data.cambio; ' )
SELECT cc.MOLI_ALP3COUNTRYCODE FROM C_COUNTRY cc 



SELECT * FROM C_BUSI


SELECT bp.MOLI_BPARTNERTYPE FROM C_BPARTNER bp

SELECT count (*) FROM C_BPARTNER bp

SELECT count (*) FROM C_BPARTNER bp

CASE WHEN cb.TAXID IS NULL OR cb.TAXID = '' THEN
	    'false' 
	ELSE 
	    'true' 
	END
	
	
	
UPDATE C_BPARTNER SET MOLI_BPARTNERTYPE = 'F';

UPDATE C_BPARTNER SET MOLI_BPARTNERTYPE = 'J'
WHERE TAXID IS NOT NULL OR TAXID <> '';



	(SELECT DISTINCT first_value(ADDRESS2) over (partition BY ADDRESS2  ORDER BY lct.CREATED DESC)FROM C_LOCATION lct WHERE lct.C_LOCATION_ID = cbl.C_LOCATION_ID) 
	AS cliente_numero_casa,

	
	





	
	



UPDATE AD_SEQUENCE SET CURRENTNEXT = (CURRENTNEXT + INCREMENTNO) WHERE name = 'MOLI_invoiceData'

SELECT * FROM C_INVOICELINE










SELECT * FROM 
( SELECT 
      ROWNUM rn, a.* 
  FROM 
   ( SELECT transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, 
cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
establecimiento, punto, numero, serie, total
--, cdc, estado, error, pausado 
FROM RV_C_Invoice_FacturaSend 
WHERE ( 
	(establecimiento || '-' || punto || '-' || numero || COALESCE(serie, '')) LIKE '%%' 
	OR UPPER(COALESCE(cliente_ruc, '')) LIKE '%%' 
	OR UPPER(COALESCE(cliente_documento_numero, '')) LIKE '%%' 
	OR UPPER(cliente_razon_social) LIKE '%%' 
) 
AND tipo_documento = 1 
GROUP BY transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, 
cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
establecimiento, punto, numero, serie, total
--, cdc, estado, error, pausado 
ORDER BY establecimiento DESC, punto DESC, numero DESC 
 ) a 
WHERE 
ROWNUM <= 20 
) 
WHERE 
rn  >= 1











SELECT ci.DOCUMENTNO FROM C_INVOICE_HEADER_FACTURASEND_V ci WHERE ROWNUM <= 5;


SELECT ci.C_INVOICE_ID FROM rv_C_INVOICELINE_FACTURASEND ci WHERE 
ci.C_INVOICE_ID = 1315268
ROWNUM <= 5


SELECT documentno, dateinvoiced FROM compiere3811.c_invoice WHERE dateinvoiced >= sysdate-30;





SELECT * FROM C_Greeting


DELETE FROM MOLI_invoiceData WHERE C_INVOICE_ID  = 1231549  AND TRIM(UPPER(moli_name)) IN ('ERROR', 'ESTADO', 'PAUSADO', 'XML', 'JSON', 'QR', 'CDC', 'TIPO')






SELECT * FROM 
( SELECT 
      ROWNUM rn, a.* 
  FROM 
   ( SELECT transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, 
cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
establecimiento, punto, numero, serie, total, cdc, estado, error, pausado 
FROM RV_C_Invoice_FacturaSend 
WHERE ( 
	(establecimiento || '-' || punto || '-' || numero || COALESCE(serie, '')) LIKE '%%' 
	OR UPPER(COALESCE(cliente_ruc, '')) LIKE '%%' 
	OR UPPER(COALESCE(cliente_documento_numero, '')) LIKE '%%' 
	OR UPPER(cliente_razon_social) LIKE '%%' 
) 
AND cdc IS NOT null
--AND tipo_documento = 1 
GROUP BY transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, 
cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
establecimiento, punto, numero, serie, total, cdc, estado, error, pausado 
ORDER BY establecimiento DESC, punto DESC, numero DESC 
 ) a 
WHERE 
ROWNUM <= 5720 
) 
WHERE 
rn  >= 5701



SELECT * FROM 
( SELECT 
      ROWNUM rn, a.* 
  FROM 
   ( SELECT transaccion_id 
FROM RV_C_Invoice_FacturaSend 
WHERE 1=1 
AND tipo_documento = 1 
AND pausado IS NULL 
AND ( 
CDC IS NULL 
OR 
TRIM(ESTADO) = 4 
) 
GROUP BY transaccion_id, establecimiento, punto, numero 
ORDER BY establecimiento, punto, numero 
 ) a 
WHERE 
ROWNUM <= 50 
) 
WHERE 
rn  >= 1




-- Limpieza
UPDATE C_INVOICE ci
SET 
	ci.MOLI_CDC = NULL,
	ci.moli_fsError = NULL, 
	ci.moli_fsstatus = NULL,
	ci.moli_fspaused = NULL
WHERE 
	ci.AD_ORG_ID = 1000005
	AND ci.DOCACTION = 'CL'
	AND ci.DOCSTATUS = 'CO'
	--Condiciones Temporales
	AND MOLI_PREIMPNO IS NOT NULL
	AND moli_fsstatus != 2 
	AND moli_fsstatus !=3
--	AND LENGTH (MOLI_PREIMPNO) = 15
--	AND MOLI_PREIMPNO LIKE '002-003-%';
--	AND cd.NAME LIKE 'AR Invoice%';

SELECT * FROM MOLI_invoiceData 
WHERE AD_ORG_ID = 1000005;
--UPDATE MOLI_invoiceData SET moli_value = '0' WHERE MOLI_NAME = 'ESTADO' AND AD_ORG_ID = 1000005;

DELETE FROM MOLI_invoiceData 
WHERE AD_ORG_ID = 1000005;


SELECT cdc, 
moli_fsstatus,
estado FROM RV_C_Invoice_FacturaSend WHERE cdc IS NULL 

--UPDATE C_INVOICE SET moli_fsestado = '0' WHERE moli_fsestado = 'null'

SELECT transaccion_id, fecha, tipo_documento, descripcion, observacion, fecha, moneda, 
cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
establecimiento, punto, numero, serie, total, cdc, estado, error, pausado 
FROM RV_C_Invoice_FacturaSend 
WHERE ( 
	(establecimiento || '-' || punto || '-' || numero || COALESCE(serie, '')) LIKE '%%' 
	OR UPPER(COALESCE(cliente_ruc, '')) LIKE '%%' 
	OR UPPER(COALESCE(cliente_documento_numero, '')) LIKE '%%' 
	OR UPPER(cliente_razon_social) LIKE '%%' 
) 
AND tipo_documento = 1
--AND TO_DATE(FECHA, 'YYYY-MM-DD H24:MI:SS') >= TO_DATE('2021-09-01', 'YYYY-MM-DD') 
--AND TO_DATE(FECHA, 'YYYY-MM-DD H24:MI:SS') <= TO_DATE('2021-09-30', 'YYYY-MM-DD')
AND FECHA >= TO_DATE('2022-09-01', 'YYYY-MM-DD') 
AND FECHA <= TO_DATE('2022-09-30', 'YYYY-MM-DD')
GROUP BY transaccion_id, fecha, tipo_documento, descripcion, observacion, fecha, moneda, 
cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
establecimiento, punto, numero, serie, total, cdc, estado, error, pausado 
ORDER BY establecimiento , punto , numero  



SELECT name FROM M_PRICELIST mp 
SELECT * FROM C_INVOICE ci WHERE ci.M_PRICELIST_ID 


SELECT * FROM C_BPARTNER cb WHERE rownum < 10






SELECT * FROM MOLI_invoiceData

INSERT INTO MOLI_invoiceData ("moli_invoicedata_id", "ad_org_id", "c_invoice_id", "ad_client_id", "MOLI_NAME", "MOLI_VALUE") VALUES ((SELECT MAX(ad.CURRENTNEXT) as MAXIMO FROM AD_SEQUENCE ad WHERE ad.name = 'MOLI_invoiceData' HAVING MAX(ad.CURRENTNEXT) > 1), 
1956837, 1000003, 1000000, 'test', 'test' ); 





--lucas

SELECT * FROM C_INVOICE ci WHERE ROWNUM <5

ALTER TABLE C_INVOICE ADD MOLI_NUMERACION_PRUEBA number(10) DEFAULT 'nextval.TIMBRADO_PRUEBA_SEQ' NOT NULL

SELECT TIMBRADO_PRUEBA_SEQ.currval FROM dual 


DECLARE 
integer c;
BEGIN 
	c:= 32001;
	WHILE (c <= 35000) LOOP 
		UPDATE C_INVOICE SET MOLI_NUMERACION_PRUEBA = TIMBRADO_PRUEBA_SEQ.currval 
		WHERE ci.AD_ORG_ID = 1000005
		AND ci.DOCSTATUS = 'CO'
		AND MOLI_NUMERACION_PRUEBA IS NULL
		AND ROWNUM < 2;
		c:=c+1;
		SELECT TIMBRADO_PRUEBA_SEQ.nextval FROM dual
	END LOOP;
END


UPDATE C_INVOICE ci SET MOLI_NUMERACION_PRUEBA = TIMBRADO_PRUEBA_SEQ.nextval 
WHERE ci.AD_ORG_ID = 1000005
AND ci.DOCSTATUS = 'CO'
AND MOLI_NUMERACION_PRUEBA IS NULL

SELECT count(*) FROM C_INVOICE ci WHERE ci.AD_ORG_ID = 1000005
AND ci.DOCSTATUS = 'CO'
AND MOLI_NUMERACION_PRUEBA IS NULL


