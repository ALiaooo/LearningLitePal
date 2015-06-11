package com.aliao.litepal.tablemanager.typechange;

/**
 * Created by 丽双 on 2015/6/11.
 */
public abstract class OrmChange {

    public abstract String[] object2Relation(String className, String fieldName, String fieldType);
}
