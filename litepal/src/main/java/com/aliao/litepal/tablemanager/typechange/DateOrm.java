package com.aliao.litepal.tablemanager.typechange;

/**
 * Created by 丽双 on 2015/6/11.
 */
public class DateOrm extends OrmChange {
    /**
     * 如果传入的字段类型是java.util.Date，则把他转换为integer类型作为列的数据类型
     * @param className
     * @param fieldName
     * @param fieldType
     * @return
     */
    @Override
    public String[] object2Relation(String className, String fieldName, String fieldType) {
        if (fieldName != null && fieldType != null) {
            String columnName = fieldName;
            if (fieldType.equals("java.util.Date")) {
                String[] relations = { columnName, "INTEGER" };
                return relations;
            }
        }
        return null;
    }
}
