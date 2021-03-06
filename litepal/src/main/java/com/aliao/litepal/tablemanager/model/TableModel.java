package com.aliao.litepal.tablemanager.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by 丽双 on 2015/6/10.
 */
public class TableModel {

    //表名
    private String tableName;
    //列名是key，列的类型是value
    private Map<String, String> columnsMap = new HashMap<>();
    //类名。去掉了包名前缀的类名
    private String className;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getColumns() {
        return columnsMap;
    }

    public void addColumns(String columnName, String columnType) {
        columnsMap.put(columnName, columnType);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Set<String> getColumnNames(){
        return columnsMap.keySet();
    }
}
