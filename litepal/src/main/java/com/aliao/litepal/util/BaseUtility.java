package com.aliao.litepal.util;

import com.aliao.litepal.parser.LitePalAttr;

import java.util.Collection;
import java.util.Locale;

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

    /**
     * 根据litepal.xml配置的大小写来设置
     * @param string
     * @return
     */
    public static String changeCase(String string){
        if (string != null){
            LitePalAttr litePalAttr = LitePalAttr.getIntance();
            String cases = litePalAttr.getCases();
            if (Const.LitePal.CASES_KEEP.equals(cases)){
                return string;
            }else if (Const.LitePal.CASES_UPPER.equals(cases)){
                return string.toUpperCase(Locale.US);
            }
            return string.toLowerCase(Locale.US);
        }
        return null;
    }


    /**
     * This helper method makes up the shortage of contains method in Collection
     * to support the function of case insensitive contains. It only supports
     * the String generic type of collection, cause other types have no cases
     * concept.
     *
     * @param collection
     *            The collection contains string data.
     * @param string
     *            The string want to look for in the collection.
     * @return If the string is in the collection without case concern return
     *         true, otherwise return false. If the collection is null, return
     *         false.
     */
    public static boolean containsIgnoreCases(Collection<String> collection, String string) {
        if (collection == null) {
            return false;
        }
        if (string == null) {
            return collection.contains(null);
        }
        boolean contains = false;
        for (String element : collection) {
            if (string.equalsIgnoreCase(element)) {
                contains = true;
                break;
            }
        }
        return contains;
    }
}
