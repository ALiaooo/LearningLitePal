package com.aliao.litepal.tablemanager.typechange;

/**
 * Created by 丽双 on 2015/6/11.
 */
public class TextOrm extends OrmChange {

    /**
     * 如果传入的字段类型是char或者String，则把他转换为text类型作为列的数据类型
     * @param className
     * @param fieldName
     * @param fieldType
     * @return
     */
    @Override
    public String[] object2Relation(String className, String fieldName, String fieldType) {
        if (fieldName != null && fieldType != null){
            String[] relations = new String[2];
            relations[0] = fieldName;
            if (fieldType.equals("char") || fieldType.equals("java.lang.Character")){
                relations[1] = "TEXT";
                return relations;
            }
            if (fieldType.equals("java.lang.String")){
                relations[1] = "TEXT";
                return relations;
            }
        }
        return null;
    }
}
