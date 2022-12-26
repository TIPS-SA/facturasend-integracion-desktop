package com.wisecoders.dbschema.dbf.io;

import com.linuxense.javadbf.DBFField;

public class DBFUtil {

    public static String getFieldDescription(DBFField field) {
        return  field.getName().toLowerCase() + " " +
                field.getType().name() + "(" +
                field.getLength() + "," +
                field.getDecimalCount() + ")";
    }
    
    public static String getFieldDescription2(DBFField field) {
        return  field.getName().toLowerCase() + " " +
                field.getType().name() + "(" +
                field.getLength() + "," +
                field.getDecimalCount() + ") " + (field.isNullable() ? "NULL": "") + "R1:" + field.getReserv1() + " R2:"  + field.getReserv2() + " R3:" +  field.getReserv3() + " R4:" + field.getReserv4()  ;
    }
}
