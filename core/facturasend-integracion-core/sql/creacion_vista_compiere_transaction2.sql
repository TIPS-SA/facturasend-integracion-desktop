
-- Compiere Vista para Integracion
--DROP VIEW IF EXISTS RV_C_Invoice_FacturaSend;

CREATE OR REPLACE VIEW RV_C_Invoice_FacturaSend AS 

	SELECT
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
	7100 AS cambio, --obtener de la BD
	
	-- Datos de Clientes
	CASE WHEN (cb.TAXID IS NULL OR cb.TAXID = '') THEN
	    'false' 
	ELSE 
	    'true' 
	END AS cliente_contribuyente,
	
	CASE WHEN (cb.MOLI_BPARTNERTYPE IS NULL AND cb.MOLI_BPARTNERTYPE = 'F') THEN
	    1 
	ELSE 
	    2 
	END AS cliente_tipo_contribuyente,
	
	
	CAST(cb.TAXID AS VARCHAR(20)) AS cliente_ruc,
	CAST(cb.MOLI_PHOTOID AS VARCHAR2(20)) AS cliente_documento_numero,
	cb.NAME AS cliente_razon_social,
	cb.NAME AS cliente_nombre_fantasia,
	--AS cliente_tipo_operacion,
	cbl.NAME AS cliente_direccion,
	
	COALESCE((SELECT DISTINCT first_value(ADDRESS2) over (partition BY ADDRESS2  ORDER BY lct.CREATED DESC)FROM C_LOCATION lct WHERE lct.C_LOCATION_ID = cbl.C_LOCATION_ID), n'0') 
--	(SELECT DISTINCT first_value(ADDRESS2) over (partition BY ADDRESS2  ORDER BY lct.CREATED DESC) FROM C_LOCATION lct WHERE lct.C_LOCATION_ID = cbl.C_LOCATION_ID)
	
	AS cliente_numero_casa,
	--AS cliente_departamento,
	--AS cliente_distrito,
	--NULL AS cliente_ciudad,
	3383 AS cliente_ciudad,
	(SELECT cc.MOLI_ALP3COUNTRYCODE FROM C_COUNTRY cc WHERE cc.C_COUNTRY_ID = (SELECT  first_value(cl.C_COUNTRY_ID )over (partition BY cl.C_COUNTRY_ID ORDER BY cl.CREATED DESC)
	FROM C_LOCATION cl WHERE cl.C_LOCATION_ID = cbl.C_LOCATION_ID ) ) 
	AS cliente_pais,
	/*CASE WHEN cb.TAXID IS NULL OR cb.TAXID = '' THEN
	    NULL 
	ELSE
	    
	END AS cliente_tipo_contribuyente,*/
	--AS cliente_documento_tipo,
	 cbl.PHONE AS cliente_telefono,
	 COALESCE(cbl.PHONE2, n'') AS cliente_celular,
	 --COALESCE((SELECT EMAIL FROM AD_USER au WHERE au.C_BPARTNER_ID = cb.C_BPARTNER_ID ORDER BY au.UPDATED DESC LIMIT 1), '') 
	 NULL
	 AS cliente_email,
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
	ci.MOLI_PREIMPNO,
	ci.AD_ORG_ID,
	ci.AD_CLIENT_ID,
	ci.AD_USER_ID,
	
	-- Campos de integracion previa
	CAST((SELECT MOLI_VALUE FROM MOLI_invoiceData mid WHERE mid.C_INVOICE_ID = ci.C_INVOICE_ID 
		AND mid.MOLI_NAME = 'CDC') AS VARCHAR2(50))  AS CDC,
	CAST((SELECT MOLI_VALUE FROM MOLI_invoiceData mid WHERE mid.C_INVOICE_ID = ci.C_INVOICE_ID 
		AND mid.MOLI_NAME = 'ESTADO') AS VARCHAR2(10)) AS ESTADO,
	CAST((SELECT MOLI_VALUE FROM MOLI_invoiceData mid WHERE mid.C_INVOICE_ID = ci.C_INVOICE_ID 
		AND mid.MOLI_NAME = 'PAUSADO') AS VARCHAR2(10)) AS PAUSADO,
	CAST((SELECT MOLI_VALUE FROM MOLI_invoiceData mid WHERE mid.C_INVOICE_ID = ci.C_INVOICE_ID 
		AND mid.MOLI_NAME = 'ERROR') AS VARCHAR2(254)) AS ERROR

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
		
		--Los que ya no debe traer por que ya se integró.
		AND CAST((SELECT MOLI_VALUE FROM MOLI_invoiceData mid WHERE mid.C_INVOICE_ID = ci.C_INVOICE_ID 
			AND mid.MOLI_NAME = 'CDC') AS VARCHAR2(50)) IS NULL,
		
		/*CAST((SELECT MOLI_VALUE FROM MOLI_invoiceData mid WHERE mid.C_INVOICE_ID = ci.C_INVOICE_ID 
			AND mid.MOLI_NAME = 'PAUSADO') AS VARCHAR2(10)) AS PAUSADO,
		*/
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












-- Compiere Vista para Integracion
-- DROP VIEW IF EXISTS RV_C_Invoice_FacturaSend;

CREATE OR REPLACE VIEW RV_C_Paymentterm_FacturaSend AS 
	SELECT 
	
	ci.C_INVOICE_ID AS id,
	2 AS tipo,			-- Siempre a Credito
	1 AS credito_tipo,	-- Siempre a plazo
	1 AS tipo_documento,	-- 1=Ventas, 5=Nota de credito, ver para hacer por el documento type
	cp.NAME AS credito_plazo
	FROM C_INVOICE ci, C_ORDER co, C_PAYMENTTERM cp 
	WHERE ci.C_ORDER_ID = co.C_ORDER_ID  
	AND cp.C_PAYMENTTERM_ID = co.C_PAYMENTTERM_ID 
	AND co.C_PAYMENTTERM_ID IS NOT NULL;




SELECT * FROM RV_C_Paymentterm_FacturaSend



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

   
SELECT * FROM MOLI_invoiceData WHERE C_INVOICE_ID = 1566797 ORDER BY moli_invoicedata_id DESC;
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

	
	
	
	SELECT * FROM 
SELECT 
	(SELECT DISTINCT first_value(ADDRESS2) over (partition BY ADDRESS2  ORDER BY lct.CREATED DESC) 
		FROM C_LOCATION lct 
		WHERE lct.C_LOCATION_ID = cbl.C_LOCATION_ID
	) 
	AS cliente_numero_casa

FROM C_BPARTNER bp, C_BPARTNER_LOCATION cbl
WHERE bp.C_BPARTNER_ID = cbl.C_BPARTNER_ID 












SELECT * FROM 
( SELECT 
      ROWNUM rn, a.* 
  FROM 
   ( SELECT transaccion_id, tipo_documento, descripcion, observacion, fecha, moneda, 
cliente_contribuyente, cliente_ruc, cliente_documento_numero, cliente_razon_social, 
establecimiento, punto, numero, serie, total 
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
ORDER BY establecimiento DESC, punto DESC, numero DESC 
 ) a 
WHERE 
ROWNUM <= 5160 
) 
WHERE 
rn  >= 5141
el ultimo transacction es 	1000861	2018-05-31 00:00:00.0	AMERICANA S.A	002-001-0026444	USD	706.44	Desconocido







