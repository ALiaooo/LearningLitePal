package com.aliao.litepal.tablemanager.typechange;

/**
 * Created by 丽双 on 2015/6/11.
 */
public class DecimalOrm extends OrmChange {
    /**
     * 如果传入的字段类型是float或者double，则把他转换为real类型作为列的数据类型
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
            if (fieldType.equals("float") || fieldType.equals("java.lang.Float")) {
                relations[1] = "REAL";
                return relations;
            }
            if (fieldType.equals("double") || fieldType.equals("java.lang.Double")) {
                relations[1] = "REAL";
                return relations;
            }
        }
        return null;
    }
}
