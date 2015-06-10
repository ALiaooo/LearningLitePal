package com.aliao.litepal.tablemanager;

import android.database.sqlite.SQLiteDatabase;

import com.aliao.litepal.parser.LitePalAttr;
import com.aliao.litepal.tablemanager.model.TableModel;
import com.aliao.litepal.util.BaseUtility;
import com.aliao.litepal.util.DBUtility;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by 丽双 on 2015/6/10.
 */
public class Creator {

    private Collection<TableModel> mTableModels;

    /**
     * 根据表的映射对象来创建表
     * @param db
     * @param force
     */
    protected void createOrUpgradeTable(SQLiteDatabase db, boolean force){
        for (TableModel tableModel:getAllTableModels()){

        }
    }

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

    private TableModel getTableModel(String className) {
        String tableName = DBUtility.getTableNameByClassName(className);
        TableModel tableModel = new TableModel();
        tableModel.setTableName(tableName);
        tableModel.setClassName(className);
        List<Field> supportedFields = getSupportedFields(className);
        for (Field field : supportedFields){

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

}
