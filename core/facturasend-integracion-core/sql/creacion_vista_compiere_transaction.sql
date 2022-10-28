
-- Compiere Vista para Integracion
--DROP VIEW IF EXISTS RV_C_Invoice_FacturaSend;

CREATE OR REPLACE VIEW RV_C_Invoice_FacturaSend AS 

	SELECT
	ci.AD_ORG_ID,
	ci.C_INVOICE_ID AS transaccion_id,
	1 AS tipo_documento,
	SUBSTR(ci.MOLI_PREIMPNO, 1, 3) AS establecimiento,
	SUBSTR(ci.MOLI_PREIMPNO, 5, 3) AS punto,
	SUBSTR(ci.MOLI_PREIMPNO, 9, 7) AS numero,
	NULL AS serie,
	'' AS descripcion,
	ci.description AS observacion,
	-- AS tipo_contribuyente,
	ci.DATEINVOICED AS fecha,
	1 AS tipo_emision,
	1 AS tipo_transicion,
	1 AS tipo_impuesto,
	cc2.ISO_CODE AS moneda,
	1 AS tipo_cambio, -- 1- GLOBAL 2- Item 
	CASE WHEN cb.TAXID IS NULL OR cb.TAXID = '' THEN
	    'false' 
	ELSE 
	    'true' 
	END AS cliente_contribuyente,
	CAST(cb.TAXID AS VARCHAR(20)) AS cliente_ruc,
	CAST(cb.MOLI_PHOTOID AS VARCHAR2(20)) AS cliente_documento_numero,
	cb.NAME AS cliente_razon_social,
	cb.NAME AS cliente_nombre_fantasia,
	--AS cliente_tipo_operacion,
	cbl.NAME AS cliente_direccion,
	
	(SELECT DISTINCT first_value(ADDRESS2) over (partition BY ADDRESS2  ORDER BY lct.CREATED DESC)FROM C_LOCATION lct WHERE lct.C_LOCATION_ID = cbl.C_LOCATION_ID) 
	AS cliente_numero_casa,
	--AS cliente_departamento,
	--AS cliente_distrito,
	--AS cliente_ciudad,
	(SELECT cc.COUNTRYCODE FROM C_COUNTRY cc WHERE cc.C_COUNTRY_ID = (SELECT  first_value(cl.C_COUNTRY_ID )over (partition BY cl.C_COUNTRY_ID ORDER BY cl.CREATED DESC)
	FROM C_LOCATION cl WHERE cl.C_LOCATION_ID = cbl.C_LOCATION_ID ) ) 
	AS cliente_pais,
	/*CASE WHEN cb.TAXID IS NULL OR cb.TAXID = '' THEN
	    NULL 
	ELSE
	    
	END AS cliente_tipo_contribuyente,*/
	--AS cliente_documento_tipo,
	 cbl.PHONE AS cliente_telefono,
	 cbl.PHONE2 AS cliente_celular,
	 --(SELECT EMAIL FROM AD_USER au WHERE au.C_BPARTNER_ID = cb.C_BPARTNER_ID ORDER BY au.UPDATED DESC LIMIT 1) AS cliente_email,
	 cb.VALUE AS cliente_codigo,
	 --usuario_documento_tipo,
	 (SELECT cb1.MOLI_PHOTOID FROM AD_USER au, C_BPARTNER cb1 WHERE cb1.C_BPARTNER_ID = au.C_BPARTNER_ID AND au.AD_USER_ID = co.SALESREP_ID ) AS usuario_documento_numero,
	 (SELECT cb1.NAME FROM AD_USER au, C_BPARTNER cb1 WHERE cb1.C_BPARTNER_ID = au.C_BPARTNER_ID AND au.AD_USER_ID = co.SALESREP_ID ) usuario_nombre,
	 (SELECT au.TITLE FROM AD_USER au, C_BPARTNER cb1 WHERE cb1.C_BPARTNER_ID = au.C_BPARTNER_ID AND au.AD_USER_ID = co.SALESREP_ID ) AS usuario_cargo,
	1 AS factura_presencia,
	ci.GRANDTOTAL AS total,
	mp.VALUE AS item_codigo,
	mp.NAME AS item_descripcion,
	'' AS item_observacion,
	77 AS item_unidad_medida ,
	cil.QTYENTERED AS item_cantidad,
	cil.PRICELIST AS item_precio_unitario,
	-- AS item_cambio,
	(cil.PRICELIST - cil.PRICEENTERED ) AS item_descuento,
	CASE WHEN ct.RATE = 5 OR ct.RATE = 10 THEN  
	    1
	WHEN ct.RATE = 0 THEN 
	    3
	ELSE 
	    4   
	END AS  item_iva_tipo,
	CASE WHEN ct.RATE = 5 OR ct.RATE = 10 THEN
	    100
	WHEN ct.RATE = 0 THEN 
	    0
	ELSE 
	    ct.RATE * 10
	END AS item_iva_base,
	CASE WHEN ct.RATE = 0 THEN
	    0
	WHEN ct.RATE = 5 THEN 
	    5
	ELSE 
	    10
	END AS item_iva,
	ci.DOCACTION,
	ci.DOCSTATUS,
	ci.MOLI_PREIMPNO 
	FROM C_INVOICE ci, 
		C_DOCTYPE cd,
		C_BPARTNER cb, 
		C_BPARTNER_LOCATION cbl, 
		--C_CASHLINE cc, 
		C_CURRENCY cc2,
		C_INVOICELINE cil, 
		M_PRODUCT mp, 
		C_TAXCATEGORY tc, 
		C_TAX ct, 
		C_ORDER co
	WHERE 1=1
		--AND ci.C_DOCTYPE_ID IN( 116, 117 )
		AND cd.C_DOCTYPE_ID = ci.C_DOCTYPE_ID
		AND co.C_ORDER_ID = ci.C_ORDER_ID 
		AND ci.C_BPARTNER_ID = cb.C_BPARTNER_ID 
		AND ci.C_INVOICE_ID = cil.C_INVOICE_ID 
	--	AND ci.C_CASHLINE_ID = cc.C_CASHLINE_ID 
		AND cc2.C_CURRENCY_ID = ci.C_CURRENCY_ID 
		AND cb.C_BPARTNER_ID = cbl.C_BPARTNER_ID 
		AND mp.M_PRODUCT_ID = cil.M_PRODUCT_ID 
		AND cil.C_TAX_ID = ct.C_TAX_ID  
		AND tc.C_TAXCATEGORY_ID = ct.C_TAXCATEGORY_ID
		AND ci.AD_ORG_ID = 1000005
		AND ci.DOCACTION = 'CL'
		AND ci.DOCSTATUS = 'CO'
		--Condiciones Temporales
		AND MOLI_PREIMPNO IS NOT NULL
		AND LENGTH (MOLI_PREIMPNO) = 15
		AND MOLI_PREIMPNO LIKE '002-001-%'
		AND cd.NAME LIKE 'AR Invoice%'	-- Estira AR Invoice, AR Invoice Indirect, AR Invoice eCommerce
	ORDER BY ci.C_INVOICE_ID DESC;	

	
	
	
SELECT * FROM RV_C_Invoice_FacturaSend 






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
   ( SELECT transaccion_id 
FROM RV_C_Invoice_FacturaSend 
WHERE tipo_documento = 1 
GROUP BY transaccion_id 
ORDER BY numero DESC 

 ) a 
WHERE 
ROWNUM <= 50 
) 
WHERE 
rn  >= 1
