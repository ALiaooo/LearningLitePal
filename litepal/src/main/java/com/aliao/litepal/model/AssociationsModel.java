package com.aliao.litepal.model;

/**
 * Created by 丽双 on 2015/6/15.
 * 这是一个表示表间关联关系的模型类。
 * 他包含表名，关联表的表名，持有外键的表名，和关联的类型
 * 关联类型有三种，分别是一对一，多对一，多对多
 * 如果关联关系是一对一或者多对一，外键将会被放在持有外键的表里
 * 如果关联关系是多对多，会创建一个中间表来表示两表间的关联关系，该中间表以两个表名中间连接下划线来命名
 */
public class AssociationsModel {

    /**
     * Table name.
     */
    private String tableName;

    /**
     * Associated table name.
     */
    private String associatedTableName;

    /**
     * The table which holds foreign key.
     */
    private String tableHoldsForeignKey;

    /**
     * The association type, including
     * {@link com.aliao.litepal.util.Const.Model#ONE_TO_ONE},
     * {@link com.aliao.litepal.util.Const.Model#MANY_TO_MANY},
     * {@link com.aliao.litepal.util.Const.Model#ONE_TO_ONE}.
     */
    private int associationType;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableHoldsForeignKey() {
        return tableHoldsForeignKey;
    }

    public void setTableHoldsForeignKey(String tableHoldsForeignKey) {
        this.tableHoldsForeignKey = tableHoldsForeignKey;
    }

    public String getAssociatedTableName() {
        return associatedTableName;
    }

    public void setAssociatedTableName(String associatedTableName) {
        this.associatedTableName = associatedTableName;
    }

    public int getAssociationType() {
        return associationType;
    }

    public void setAssociationType(int associationType) {
        this.associationType = associationType;
    }
}
