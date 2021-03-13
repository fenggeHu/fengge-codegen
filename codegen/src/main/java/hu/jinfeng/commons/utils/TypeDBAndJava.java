package hu.jinfeng.commons.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库字段类型与java类型对照
 *
 * @Author hujinfeng  @Date 2020/11/28
 **/
public class TypeDBAndJava {

//    private final static Map<String, String> mapping = new HashMap<>();
//
//    static {
//        mapping.put("BIT", "Boolean");
//        mapping.put("BIGINT", "Long");
//        mapping.put("FLOAT", "Double");
//        mapping.put("DOUBLE", "Double");
//        mapping.put("DECIMAL", "Double");
//        mapping.put("DATE", "Date");
//        mapping.put("DATETIME", "Date");
//        mapping.put("CHAR", "String");
//        mapping.put("VARCHAR", "String");
//        mapping.put("TEXT", "String");
//        mapping.put("BLOB", "String");
//    }

    /**
     * db - mysql
     */
    public static String getJavaType(String db, String type) {
        type = type.trim().toUpperCase();
        if (type.startsWith("BIT")) {
            return "Boolean";
        } else if (type.indexOf("BIGINT") >= 0) {
            return "Long";
        } else if (type.indexOf("INT") >= 0) {
            return "Integer";
        } else if (type.indexOf("FLOAT") >= 0 || type.indexOf("DOUBLE") >= 0 || type.indexOf("DECIMAL") >= 0) {
            return "Double";
        } else if (type.indexOf("DATE") >= 0) {
            return "Date";
        } else {
            return "String";
        }
    }
}
