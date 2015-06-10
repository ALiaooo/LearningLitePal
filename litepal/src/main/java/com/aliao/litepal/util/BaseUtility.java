package com.aliao.litepal.util;

/**
 * Created by 丽双 on 2015/6/10.
 */
public class BaseUtility {

    /**
     * 检查字段的类型是否是支持的类型，当前只支持基本数据类型和String类型
     * @param fieldType
     * @return
     */
    public static boolean isFieldTypeSupported(String fieldType){
        if ("boolean".equals(fieldType) || "java.lang.Boolean".equals(fieldType)) {
            return true;
        }
        if ("float".equals(fieldType) || "java.lang.Float".equals(fieldType)) {
            return true;
        }
        if ("double".equals(fieldType) || "java.lang.Double".equals(fieldType)) {
            return true;
        }
        if ("int".equals(fieldType) || "java.lang.Integer".equals(fieldType)) {
            return true;
        }
        if ("long".equals(fieldType) || "java.lang.Long".equals(fieldType)) {
            return true;
        }
        if ("short".equals(fieldType) || "java.lang.Short".equals(fieldType)) {
            return true;
        }
        if ("char".equals(fieldType) || "java.lang.Character".equals(fieldType)) {
            return true;
        }
        if ("java.lang.String".equals(fieldType) || "java.util.Date".equals(fieldType)) {
            return true;
        }
        return false;
    }
}
