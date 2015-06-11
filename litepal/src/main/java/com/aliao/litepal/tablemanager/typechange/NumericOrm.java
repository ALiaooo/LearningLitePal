package com.aliao.litepal.tablemanager.typechange;

/**
 * Created by 丽双 on 2015/6/11.
 */
public class NumericOrm extends OrmChange {

    /**
     * 如果传入的字段类型是int,long或者short，则把他转换为integer类型作为列的数据类型
     * @param className
     * @param fieldName
     * @param fieldType
     * @return
     */
    @Override
    public String[] object2Relation(String className, String fieldName, String fieldType) {
        if (fieldName != null && fieldType != null) {
            String[] relations = new String[2];
            relations[0] = fieldName;
            if (fieldType.equals("int") || fieldType.equals("java.lang.Integer")) {
                relations[1] = "INTEGER";
                return relations;
            }
            if (fieldType.equals("long") || fieldType.equals("java.lang.Long")) {
                relations[1] = "INTEGER";
                return relations;
            }
            if (fieldType.equals("short") || fieldType.equals("java.lang.Short")) {
                relations[1] = "INTEGER";
                return relations;
            }
        }
        return null;
    }

}
