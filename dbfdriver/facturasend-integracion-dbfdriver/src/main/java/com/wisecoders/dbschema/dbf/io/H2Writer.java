package com.wisecoders.dbschema.dbf.io;


import com.wisecoders.dbschema.dbf.schema.DataTypeUtil;
import com.wisecoders.dbschema.dbf.schema.Db;
import com.wisecoders.dbschema.dbf.schema.Table;
import com.linuxense.javadbf.DBFWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import static com.wisecoders.dbschema.dbf.JdbcDriver.LOGGER;

/**
 * Copyright Wise Coders GmbH https://wisecoders.com
 * Driver is used in the DbSchema Database Designer https://dbschema.com
 * Free to be used by everyone.
 * Code modifications allowed only to GitHub repository https://github.com/wise-coders/dbf-jdbc-driver
 */
public class H2Writer {


    public H2Writer(final Connection h2Connection, final File outputFolder, String [] aTables, final String charset ) throws Exception {

        final Db db = new Db();

        final ResultSet rsColumns = h2Connection.getMetaData().getColumns( null, null, null, null );
        while( rsColumns.next() ){
            String schemaName = rsColumns.getString( 2 );
            String tableName = rsColumns.getString( 3 );
            if ( !"INFORMATION_SCHEMA".equalsIgnoreCase( schemaName) && !DataTypeUtil.isH2SystemTable(tableName ) ) { //
                String columnName = rsColumns.getString(4);
                db.getOrCreateTable(tableName).createDBFField(columnName, rsColumns.getString(6), rsColumns.getInt(7), rsColumns.getInt(9));
            }
        }

        for (int i = 0; i < aTables.length; i++) {
	        String path = aTables[i].trim();
	        if ( ( path.startsWith("'") || path.endsWith("'") ) || ( path.startsWith("\"") || path.endsWith("\"") )){
	            path = path.substring(1, path.length()-1);
	        }
	        aTables[i] = path;
		}
        
        for ( Table table : db.getTables() ){
        	boolean contains = Arrays.stream(aTables).anyMatch(table.name::equalsIgnoreCase);
        	
        	if (contains) {
                final File outputFile = new File( outputFolder.toURI().resolve( table.name + ".dbf"));
                LOGGER.info("Saving " + table + " outputFile: " + outputFile);

                final FileOutputStream os;
                final DBFWriter writer;
                if (outputFile.exists()) {
                	//Escribira sobre el archivo existente.
                	writer = charset != null ? new DBFWriter(outputFile, Charset.forName(charset)) : new DBFWriter(outputFile);
                } else {
                	//Creara un nuevo archivo
                	os = new FileOutputStream(outputFile);
                	writer = charset != null ? new DBFWriter(os, Charset.forName(charset)) : new DBFWriter(os);
                	writer.setFields( table.getDBFFields() );
                }
                //final FileOutputStream os = new FileOutputStream(outputFile);
                //final DBFWriter writer = charset != null ? new DBFWriter(os, Charset.forName(charset)) : new DBFWriter(os);
                //Averiguar si la tabla no existe, para hacer el setFields.
                //Si es existente abrir con File. 
                //writer.setFields( table.getDBFFields() );

                try ( Statement st = h2Connection.createStatement()) {
                    ResultSet rs = st.executeQuery("SELECT * FROM " + table.name);
                    int recCount = 0;
                    while (rs.next()) {
                        int columnCount = rs.getMetaData().getColumnCount();
                        Object[] data = new Object[columnCount];
                        for (int i = 0; i < columnCount; i++) {
                            data[i] = rs.getObject(i + 1);
                        }
                        try {
                            writer.addRecord(data);
                        } catch ( Throwable ex ){
                            StringBuilder sb = new StringBuilder();
                            sb.append("Error saving ").append( outputFile.getAbsolutePath() ).append( " record : [");
                            for ( Object obj: data){
                                if ( obj == null ){
                                    sb.append("null");
                                } else {
                                    sb.append("'").append( obj.toString()).append("'");
                                }
                                sb.append(",");
                            }
                            sb.append(" ]");
                            throw new SQLException(sb.toString() + ex.getLocalizedMessage(), ex );
                        }
                        recCount++;
                    }
                    rs.close();
                    writer.close();
                    LOGGER.info("Saved " + table.name + " " + recCount + " records." );
                    H2Loader.saveFileIntoFilesMeta( table, outputFile, h2Connection );
                }        		
        	} else {
                LOGGER.info("Ignore " + table.name );
        	}

        }
    }




}
