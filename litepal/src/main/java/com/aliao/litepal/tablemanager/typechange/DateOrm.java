package com.aliao.litepal.tablemanager.typechange;

/**
 * Created by 丽双 on 2015/6/11.
 */
public class DateOrm extends OrmChange {
    @Override
    public String[] object2Relation(String className, String fieldName, String fieldType) {
        return new String[0];
    }
}
