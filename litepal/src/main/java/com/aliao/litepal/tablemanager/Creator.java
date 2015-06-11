package com.aliao.litepal.tablemanager;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.aliao.litepal.exceptions.DatabaseGenerateException;
import com.aliao.litepal.parser.LitePalAttr;
import com.aliao.litepal.tablemanager.model.TableModel;
import com.aliao.litepal.tablemanager.typechange.BooleanOrm;
import com.aliao.litepal.tablemanager.typechange.DateOrm;
import com.aliao.litepal.tablemanager.typechange.DecimalOrm;
import com.aliao.litepal.tablemanager.typechange.NumericOrm;
import com.aliao.litepal.tablemanager.typechange.OrmChange;
import com.aliao.litepal.tablemanager.typechange.TextOrm;
import com.aliao.litepal.util.BaseUtility;
import com.aliao.litepal.util.Const;
import com.aliao.litepal.util.DBUtility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 丽双 on 2015/6/10.
 */
public class Creator {

    private Collection<TableModel> mTableModels;

    /**
     * 根据表的映射对象来创建表-批量创建表
     * @param db
     * @param force
     */
    protected void createOrUpgradeTable(SQLiteDatabase db, boolean force){
        for (TableModel tableModel:getAllTableModels()){
            execute(getCreateTableSQLs(tableModel, db, force), db);
            giveTableSchemaACopy(tableModel.getTableName(), Const.TableSchema.NORMAL_TABLE, db);
        }
    }

    /**
     * 批量执行sql语句来创建表
     * @param sqls
     * @param db
     */
    protected void execute(String[] sqls, SQLiteDatabase db) {
        String throwSQL = "";
        try{
            if (sqls != null){
                for (String sql:sqls){
                    db.execSQL(BaseUtility.changeCase(sql));
                }
            }
        }catch (SQLException e){
            throw new DatabaseGenerateException(DatabaseGenerateException.SQL_ERROR + throwSQL);
        }
    }

    /**
     * 获取创建表的sql语句
     * @param tableModel
     * @param db
     * @param force
     * @return
     */
    private String[] getCreateTableSQLs(TableModel tableModel, SQLiteDatabase db, boolean force) {
        //force用来判断，是否要先删除已存在的表再新建
        if (force){
            return new String[]{
              generateDropTableSQL(tableModel),
              generateCreateTableSQL(tableModel)
            };
        }else {
            if (DBUtility.isTableExists(tableModel.getTableName(), db)) {
                return null;
            } else {
                return new String[] { generateCreateTableSQL(tableModel) };
            }
        }
    }

    /**
     * 生成一个删除表的sql语句
     * @param tableModel
     * @return
     */
    private String generateDropTableSQL(TableModel tableModel) {
        return generateDropTableSQL(tableModel.getTableName());
    }
    protected String generateDropTableSQL(String tableName) {
        return "drop table if exists " + tableName;
    }

    /**
     * 生成一个创建表的sql语句
     * @param tableModel
     * @return
     */
    private String generateCreateTableSQL(TableModel tableModel) {
        return generateCreateTableSQL(tableModel.getTableName(), tableModel.getColumns(), true);
    }
    protected String generateCreateTableSQL(String tableName, Map<String, String> columnsMap,boolean autoIncrementId) {
        Set<String> columnNames = columnsMap.keySet();
        //判断集合里是有含有id或_id的列名，如果有则删除。防止生成两个id列，因为每个sql语句会自己创建id列作为主键
        removeId(columnNames);
        StringBuilder createTableSQL = new StringBuilder("create table ");
        createTableSQL.append(tableName).append(" (");
        //创建id作为主键，autoIncrementId总是true
        if (autoIncrementId){
            createTableSQL.append("id integer primary key autoincrement,");
        }
        //判断有没有列名，没有列名就把刚拼接的sql语句最后那个逗号给去了
        Iterator<String> i = columnNames.iterator();
        if (!i.hasNext()){
            createTableSQL.deleteCharAt(createTableSQL.length() - 1);
        }
        boolean needSeparator = false;
        while (i.hasNext()){
            if (needSeparator){
                createTableSQL.append(", ");
            }
            needSeparator = true;
            String columnName = i.next();
            createTableSQL.append(columnName).append(" ").append(columnsMap.get(columnName));
        }
        createTableSQL.append(")");
        return createTableSQL.toString();
    }

    private void removeId(Set<String> columnNames) {
        String idName = "";
        for (String columnName : columnNames) {
            if (isIdColumn(columnName)) {
                idName = columnName;
                break;
            }
        }
        if (!TextUtils.isEmpty(idName)) {
            columnNames.remove(idName);
        }
    }
    protected boolean isIdColumn(String columnName) {
        return "_id".equalsIgnoreCase(columnName) || "id".equalsIgnoreCase(columnName);
    }


    /**
     * 通过所有关系映射的对象类名集合来组装表模型集合
     * @return
     */
    private Collection<TableModel> getAllTableModels() {
        if (mTableModels == null){
            mTableModels = new ArrayList<>();
        }
        if(!canUseCache()){
            mTableModels.clear();
            for (String className : LitePalAttr.getIntance().getClassNames()){
                mTableModels.add(getTableModel(className));
            }
        }

        return mTableModels;
    }

    private OrmChange[] typeChangeRules = { new NumericOrm(), new TextOrm(), new BooleanOrm(),
            new DecimalOrm(), new DateOrm() };

    /**
     * 根据类名来创建表模型
     * @param className
     * @return
     */
    private TableModel getTableModel(String className) {
        String tableName = DBUtility.getTableNameByClassName(className);
        TableModel tableModel = new TableModel();
        tableModel.setTableName(tableName);//设置表名
        tableModel.setClassName(className);//设置类名
        //获取类的所有字段
        List<Field> supportedFields = getSupportedFields(className);
        //根据字段获取该字段的名字及数据类型，添加到‘字段名-数据类型’的哈希表columnMap里
        for (Field field : supportedFields){
            String fieldName = field.getName();
            Class<?> fieldTypeClass = field.getType();
            String columnName = null;
            String columnType = null;
            for (OrmChange ormChange:typeChangeRules){

            }
        }

        return null;
    }

    /**
     * 利用反射机制获取该类所有的字段
     * 1.要求添加的字段的修饰符是私有的且被static的，
     * 2.字段的类型要符合基本数据类型
     * @param className
     * @return
     */
    private List<Field> getSupportedFields(String className) {

        List<Field> supportedFields = new ArrayList<>();
        Class<?> dynamicClass = null;
        try {
            dynamicClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = dynamicClass.getDeclaredFields();
        for (Field field:fields){
            int modifiers = field.getModifiers();
            if (Modifier.isPrivate(modifiers) && !Modifier.isStatic(modifiers)){
                Class<?> fieldTypeClass = field.getType();
                String fieldType = fieldTypeClass.getName();
                if (BaseUtility.isFieldTypeSupported(fieldType)){
                    supportedFields.add(field);
                }
            }
        }
        return supportedFields;
    }

    private boolean canUseCache() {
        return false;
    }

    /**
     * 将新创建的表的表名备份到table_schema中
     * @param tableName
     * @param tableType
     * @param db
     */
    protected void giveTableSchemaACopy(String tableName, int tableType, SQLiteDatabase db) {

    }
}
