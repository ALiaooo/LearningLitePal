package com.aliao.litepal.tablemanager;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.aliao.litepal.exceptions.DatabaseGenerateException;
import com.aliao.litepal.model.AssociationsModel;
import com.aliao.litepal.parser.LitePalAttr;
import com.aliao.litepal.tablemanager.model.TableModel;
import com.aliao.litepal.util.BaseUtility;
import com.aliao.litepal.util.Const;
import com.aliao.litepal.util.DBUtility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 丽双 on 2015/6/15.
 */
public abstract class AssociationCreator extends Generator{

    public static final String TAG = "AssociationCreator";

    protected abstract void createOrUpgradeTable(SQLiteDatabase db, boolean force);
    /**
     * Action to get associations.
     */
    private static final int GET_ASSOCIATIONS_ACTION = 1;
    /**
     * The collection contains all association models.
     */
    private Collection<AssociationsModel> mAssociationModels;

    /**
     * The collection contains all association models.
     */
    private Collection<AssociationsModel> mAllRelationModels;

    @Override
    protected void addOrUpdateAssociation(SQLiteDatabase db, boolean force) {
        addAssociations(getAllAssociations(), db, force);
    }

    private void addAssociations(Collection<AssociationsModel> associatedModels, SQLiteDatabase db, boolean force) {
        for (AssociationsModel associationsModel : associatedModels){
            if (Const.Model.MANY_TO_ONE == associationsModel.getAssociationType() |
                    Const.Model.ONE_TO_ONE == associationsModel.getAssociationType()){
                addForeignKeyColumn(associationsModel, db);
            }else if (Const.Model.MANY_TO_MANY == associationsModel.getAssociationType()){
                createIntermediateTable(associationsModel.getTableName(), associationsModel.getAssociatedTableName(),db,force);
            }
        }
    }

    /**
     * 当关联关系是多对多时，数据库需要创建一个中间表来映射关联关系。该方法用来创建一个表
     * @param tableName
     * @param associatedTableName
     * @param db
     * @param force
     */
    private void createIntermediateTable(String tableName, String associatedTableName, SQLiteDatabase db, boolean force) {
        Map<String, String> columnsMap = new HashMap<>();
        columnsMap.put(tableName+"_id", "integer");
        columnsMap.put(associatedTableName+"_id", "integer");
        String intermediateTableName = DBUtility.getIntermediateTableName(tableName, associatedTableName);
        List<String> sqls = new ArrayList<>();
        if(DBUtility.isTableExists(intermediateTableName, db)){
            if (force){
                sqls.add(generateDropTableSQL(intermediateTableName));
                sqls.add(generateCreateTableSQL(intermediateTableName, columnsMap, false));
            }
        }else {
            sqls.add(generateCreateTableSQL(intermediateTableName, columnsMap, false));
        }
        execute(sqls.toArray(new String[0]), db);
        giveTableSchemaACopy(intermediateTableName, Const.TableSchema.INTERMEDIATE_JOIN_TABLE, db);
    }


    /**
     * 将新创建的表的表名备份到table_schema中
     * @param tableName
     * @param tableType
     * @param db
     */
    protected void giveTableSchemaACopy(String tableName, int tableType, SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder("select * from ");
        sql.append(Const.TableSchema.TABLE_NAME);

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql.toString(), null);
            if (isNeedtoGiveACopy(cursor, tableName)) {
                ContentValues values = new ContentValues();
                values.put(Const.TableSchema.COLUMN_NAME, BaseUtility.changeCase(tableName));
                values.put(Const.TableSchema.COLUMN_TYPE, tableType);
                db.insert(Const.TableSchema.TABLE_NAME, null, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



    /**
     * Save the name of a created table into table_schema, but there're some
     * extra rules. Each table name should be only saved once, and special
     * tables will not be saved.
     *
     * @param cursor
     *            The cursor used to iterator values in the table.
     * @param tableName
     *            The table name.
     * @return If all rules are passed return true, any of them failed return
     *         false.
     */
    private boolean isNeedtoGiveACopy(Cursor cursor, String tableName) {
        return !isValueExists(cursor, tableName) && !isSpecialTable(tableName);
    }

    /**
     * Judge the table name has already exist in the table_schema or not.
     *
     * @param cursor
     *            The cursor used to iterator values in the table.
     * @param tableName
     *            The table name.
     * @return If value exists return true, or return false.
     */
    private boolean isValueExists(Cursor cursor, String tableName) {
        boolean exist = false;
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor
                        .getColumnIndexOrThrow(Const.TableSchema.COLUMN_NAME));
                if (name.equalsIgnoreCase(tableName)) {
                    exist = true;
                    break;
                }
            } while (cursor.moveToNext());
        }
        return exist;
    }

    /**
     * Judge a table is a special table or not. Currently table_schema is a
     * special table.
     *
     * @param tableName
     *            The table name.
     * @return Return true if it's special table.
     */
    private boolean isSpecialTable(String tableName) {
        return Const.TableSchema.TABLE_NAME.equalsIgnoreCase(tableName);
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

    private void addForeignKeyColumn(AssociationsModel associationsModel, SQLiteDatabase db) {
        String tableName = associationsModel.getTableName();
        String associatedTableName = associationsModel.getAssociatedTableName();
        String tableHoldsForeignKey = associationsModel.getTableHoldsForeignKey();
        if (DBUtility.isTableExists(tableName, db)){
            if (DBUtility.isTableExists(associatedTableName, db)){
                String foreignKeyColumn = null;
                if (tableName.equals(tableHoldsForeignKey)){
                    foreignKeyColumn = getForeignKeyColumnName(associatedTableName);
                }else if (associatedTableName.equals(tableHoldsForeignKey)){
                    foreignKeyColumn = getForeignKeyColumnName(tableName);
                }

                if (!DBUtility.isColumnExists(foreignKeyColumn,tableHoldsForeignKey, db)){
                    String[] sqls = {
                            generateAddColunmSQL(tableHoldsForeignKey, foreignKeyColumn, "integer")
                    };
                    execute(sqls, db);
                }else {
                    Log.d(TAG, "column " + foreignKeyColumn
                            + " is already exist, no need to add one");
                }
            }else {
                throw new DatabaseGenerateException(DatabaseGenerateException.TABLE_DOES_NOT_EXIST
                        + associatedTableName);
            }
        }else {
            throw new DatabaseGenerateException(DatabaseGenerateException.TABLE_DOES_NOT_EXIST
                    + tableName);
        }
    }

    private String generateAddColunmSQL(String tableName, String colunmName, String columnType) {
        StringBuilder addColumnSQL = new StringBuilder();
        addColumnSQL.append("alter table ").
                append(tableName).
                append(" add column ").
                append(colunmName).
                append(" ").append(columnType);
        return addColumnSQL.toString();
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

    private String getForeignKeyColumnName(String tableName) {
        return BaseUtility.changeCase(tableName + "_id");
    }

    protected Collection<AssociationsModel> getAllAssociations(){
        if (mAllRelationModels == null || mAllRelationModels.isEmpty()){
            mAllRelationModels = getAssociations(LitePalAttr.getIntance().getClassNames());
        }
        return mAllRelationModels;
    }

    protected Collection<AssociationsModel> getAssociations(List<String> calssNames){
        if (mAssociationModels == null){
            mAssociationModels = new HashSet<>();
        }
        mAssociationModels.clear();
        for (String className: calssNames){
            analyzeClassFields(className, GET_ASSOCIATIONS_ACTION);
        }
        return mAssociationModels;
    }

    /**
     *
     * @param className
     * @param action
     */
    private void analyzeClassFields(String className, int action) {
        Class<?> dynamicClass = null;
        try {
            dynamicClass = Class.forName(className);
            Field[] fields = dynamicClass.getDeclaredFields();
            for (Field field : fields){
                if (isPrivateAndNonPrimitive(field)){
                    //字段类型是对象的
                    oneToAnyConditions(className, field, action);
                    //字段类型是一个列表或者集合
                    manyToAnyConditions(className, field, action);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new DatabaseGenerateException(DatabaseGenerateException.CLASS_NOT_FOUND + className);
        }
    }


    /**
     * 判断字段修饰符是否是private且不属于基本数据类型
     * @param field
     * @return
     */
    private boolean isPrivateAndNonPrimitive(Field field) {
        return Modifier.isPrivate(field.getModifiers()) && !field.getType().isPrimitive();
    }


    private void oneToAnyConditions(String className, Field field, int action) throws ClassNotFoundException {
        Class<?> fieldTypeClass = field.getType();
        //判断该字段的类名是否是映射表里包含的类名之一
        if (LitePalAttr.getIntance().getClassNames().contains(fieldTypeClass.getName())){
            Class<?> reverseDynamicClass = Class.forName(fieldTypeClass.getName());
            Field[] reverseFields = reverseDynamicClass.getDeclaredFields();
            // Look up if there's a reverse association
            // definition in the reverse class.
            boolean reverseAssociations = false;
            for (int i = 0; i<reverseFields.length; i++){
                Field reverseField = reverseFields[i];
                if (Modifier.isPrivate(reverseField.getModifiers())){
                    Class<?> reverseFieldTypeClass = reverseField.getType();
                    //双向的1对1关联关系
                    if (className.equals(reverseFieldTypeClass.getName())){
                        addIntoAssociationModelCollection(className, fieldTypeClass.getName(),fieldTypeClass.getName(), Const.Model.ONE_TO_ONE);
                        reverseAssociations = true;
                    }
                    //一对多
                    else if(isCollection(reverseFieldTypeClass)){
                        //获取类类型
                        String genericTypeName = getGenericTypeName(reverseField);
                        if (className.equals(genericTypeName)){
                            addIntoAssociationModelCollection(className, fieldTypeClass.getName(), className, Const.Model.MANY_TO_ONE);
                            reverseAssociations = true;
                        }
                    }
                    //单向的1对1关联关系
                    if ((i == reverseFields.length - 1) && !reverseAssociations){
                        addIntoAssociationModelCollection(className, fieldTypeClass.getName(),fieldTypeClass.getName(), Const.Model.ONE_TO_ONE);
                    }
                }
            }
        }
    }


    private void manyToAnyConditions(String className, Field field, int action) throws ClassNotFoundException {
        if (isCollection(field.getType())){
            //获取到泛型类型的泛型参数
            String genericTypeName = getGenericTypeName(field);
            //获取到genericType类类型，并遍历该类中的字段
            if (LitePalAttr.getIntance().getClassNames().contains(genericTypeName)) {
                Class<?> reverseDynamicClass = Class.forName(genericTypeName);
                Field[] reverseFields = reverseDynamicClass.getDeclaredFields();
                // Look up if there's a reverse association
                // definition in the reverse class.
                boolean reverseAssociations = false;
                for (int i=0; i<reverseFields.length; i++){
                    Field reverseField = reverseFields[i];
                    if (Modifier.isPrivate(reverseField.getModifiers())){

                        Class<?> reverseFieldTypeClass = reverseField.getType();

                        //多对一双向
                        if (className.equals(reverseFieldTypeClass.getName())){

                            addIntoAssociationModelCollection(className, genericTypeName, genericTypeName, Const.Model.MANY_TO_ONE);
                            reverseAssociations = true;
                        }
                        //多对多
                        else if(isCollection(reverseFieldTypeClass)){

                            String reverseGenericTypeName = getGenericTypeName(reverseField);

                            if (className.equals(reverseGenericTypeName)){

                                addIntoAssociationModelCollection(className, genericTypeName, null, Const.Model.MANY_TO_MANY);

                            }
                        }
                        //单项多对一
                        if ((i == reverseFields.length - 1) && !reverseAssociations){
                            addIntoAssociationModelCollection(className,genericTypeName,genericTypeName,Const.Model.MANY_TO_ONE);
                        }
                    }
                }
            }
        }
    }


    /**
     * 获取到List或者Set里的泛型参数
     * @return
     * @param field
     */
    private String getGenericTypeName(Field field) {
        Type genericType = field.getGenericType();
        if (genericType != null){
            if (genericType instanceof ParameterizedType){
                //执行强制类型转换
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                //获取泛型类型的泛型参数
                Class<?> geneticArg = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                return geneticArg.getName();
            }
        }
        return null;
    }

    private boolean isCollection(Class<?> reverseFieldTypeClass) {
        return isList(reverseFieldTypeClass) || isSet(reverseFieldTypeClass) ;
    }


    /**
     * 判断字段类型是否是List
     * isAssignableFrom 是用来判断一个类Class1和另一个类Class2是否相同或是另一个类的超类或接口。
     * 通常调用格式是
     * Class1.isAssignableFrom (Class2)
     * 调用者和参数都是   java.lang.Class   类型。
     * @param fieldType
     * @return
     */
    protected boolean isList(Class<?> fieldType){
        return List.class.isAssignableFrom(fieldType);
    }

    protected boolean isSet(Class<?> fieldType){
        return Set.class.isAssignableFrom(fieldType);
    }

    /**
     * 构建关联关系模型，并添加到mAssociationModels里
     * @param className
     * @param associatedClassName
     * @param classHoldsForeignKey
     * @param associationType
     */
    private void addIntoAssociationModelCollection(String className, String associatedClassName, String classHoldsForeignKey, int associationType) {
        AssociationsModel associationsModel = new AssociationsModel();
        associationsModel.setTableName(DBUtility.getTableNameByClassName(className));
        associationsModel.setAssociatedTableName(DBUtility.getTableNameByClassName(associatedClassName));
        associationsModel.setTableHoldsForeignKey(DBUtility.getTableNameByClassName(classHoldsForeignKey));
        associationsModel.setAssociationType(associationType);
        mAssociationModels.add(associationsModel);
    }



    /**
     * 生成一个删除表的sql语句
     * @param tableModel
     * @return
     */
    protected String generateDropTableSQL(TableModel tableModel) {
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
    protected String generateCreateTableSQL(TableModel tableModel) {
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



}
