package com.aliao.litepal.crud;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.aliao.litepal.crud.model.AssociationsInfo;
import com.aliao.litepal.exceptions.DataSupportException;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by 丽双 on 2015/6/16.
 */
public class SaveHandler extends DataHandler {


    SaveHandler(SQLiteDatabase db){
        mDatabase = db;
    }

    void onSave(DataSupport baseObj){
        String className = baseObj.getClassName();
        List<Field> supportedFields = getSupportedFields(className);
        Collection<AssociationsInfo> associationsInfos = getAssociationInfo(className);

        if (!baseObj.isSaved()){
            //保存
            analyzeAssociatedModels(baseObj, associationsInfos);

            doSaveAction(baseObj, supportedFields);

        }else {
            //更新

        }

    }

    private void doSaveAction(DataSupport baseObj, List<Field> supportedFields) {
        /**
         * 第一步：调用ContentValue的put方法来添加带存储的值。
         *
         * 第一个参数是数据库表中对应的列名，第二个参数是要存储的值
         */
        ContentValues values = new ContentValues();
//        values.put("columnname", "value");
//        values.put("age",25);
        for (Field field : supportedFields){
            if (!isIdColumn(field.getName())){
                try {
                    putContentValues(baseObj, field, values);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         * 第二步：调用insert，向数据库表中插入数据
         *
         * 注意，这里插入的数据都是基本数据类型的字段的值
         */
        long id = mDatabase.insert(baseObj.getTableName(), null, values);

        /**
         * 第三步：把生成的id值赋值给model中的id
         */
        assignIdValue(baseObj, getIdField(supportedFields), id);

        /**
         * 第四步：更新带有外键的关联表
         */
        updateAssociatedTableWithFK(baseObj);

    }

    private void assignIdValue(DataSupport baseObj, Field idField, long id) {
        try {
            giveBaseObjIdValue(baseObj, id);
            if (idField != null){
                giveModelIdValue(baseObj, idField, id);
            }
        } catch (Exception e) {
            throw new DataSupportException(e.getMessage());
        }

    }


    private void updateAssociatedTableWithFK(DataSupport baseObj) {

    }


    /**
     * Assign the generated id value to {@link DataSupport#baseObjId}. This
     * value will be used as identify of this model for system use.
     *
     * @param baseObj
     *            The class of base object.
     * @param id
     *            The value of id.
     */
    protected void giveBaseObjIdValue(DataSupport baseObj, long id) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        if (id > 0) {
            setField(baseObj, "baseObjId", id, DataSupport.class);
        }
    }

    /**
     * 为id属性赋值
     * @param baseObj
     * @param idField
     * @param id
     * @throws IllegalAccessException
     */
    private void giveModelIdValue(DataSupport baseObj, Field idField, long id) throws IllegalAccessException {

        String idName = idField.getName();
        Class<?> idType =  idField.getType();
        if (shouldGiveModelIdValue(idName, idType, id)){
            Object value = null;
            if (idType == int.class || idType == Integer.class){
                value = (int) id;
            }else if (idType == long.class || idType == Long.class){
                value =  id;
            } else {
                throw new DataSupportException(DataSupportException.ID_TYPE_INVALID_EXCEPTION);
            }
            //利用反射为id属性赋值
            setField(baseObj, idName, value, baseObj.getClass());

        }
    }

    static void setField(Object object, String fieldName, Object value, Class<?> objectClass)
            throws SecurityException, IllegalArgumentException, IllegalAccessException {
        try {
            Field objectField = objectClass.getDeclaredField(fieldName);
            objectField.setAccessible(true);
            objectField.set(object, value);
        } catch (NoSuchFieldException e) {
            throw new DataSupportException(DataSupportException.noSuchFieldExceptioin(
                    objectClass.getSimpleName(), fieldName));
        }
    }

    private boolean shouldGiveModelIdValue(String idName, Class<?> idType, long id){
        return idName != null && idType != null && id > 0;
    }

    /**
     * 从所有支持字段中获取id这个字段
     * @param supportedFields
     */
    private Field getIdField(List<Field> supportedFields) {
        for (Field field : supportedFields){
            if (isIdColumn(field.getName())){
                return field;
            }
        }
        return null;
    }

    private void putContentValues(DataSupport baseObj, Field field, ContentValues values) throws IllegalAccessException {
        String fieldName = field.getName();
        String fieldTypeName = field.getType().getName();

        if (fieldTypeName.equals(java.lang.String.class.getName())){
            values.put(fieldName, (String) field.get(baseObj));
        }else if (fieldTypeName.equals(java.lang.Integer.class.getName()) || fieldTypeName.equals("int")){
            values.put(fieldName, field.getInt(baseObj));
        }else if (fieldTypeName.equals(java.lang.Boolean.class) || fieldTypeName.equals("boolean")){
            values.put(fieldName, field.getBoolean(baseObj));
        }else if (fieldTypeName.equals(java.lang.Long.class.getName()) || fieldTypeName.equals("long")){
            values.put(fieldName, field.getLong(baseObj));
        }else if (fieldTypeName.equals(java.lang.Short.class.getName()) || fieldTypeName.equals("short")){
            values.put(fieldName, field.getShort(baseObj));
        }else if (fieldTypeName.equals(java.lang.Float.class.getName()) || fieldTypeName.equals("float")){
            values.put(fieldName, field.getFloat(baseObj));
        }else if (fieldTypeName.equals(java.lang.Double.class.getName()) || fieldTypeName.equals("double")){
            values.put(fieldName, field.getDouble(baseObj));
        }else if (fieldTypeName.equals(java.util.Date.class.getName())){
            if (field.get(baseObj) != null){
                Date date = (Date) field.get(baseObj);
                values.put(fieldName, date.getTime());
            }
        }
    }


}
