package com.aliao.litepal.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.aliao.litepal.exceptions.DatabaseGenerateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 丽双 on 2015/6/10.
 */
public class DBUtility {

    /**
     * 通过类名获取表名
     * @param className
     * @return
     */
    public static String getTableNameByClassName(String className){
        if (!TextUtils.isEmpty(className)){
            if ('.' == className.charAt(className.length() - 1)){
                return null;
            }else {
                return className.substring(className.lastIndexOf('.')+1);
            }
        }
        return null;
    }

    /**
     * 判断表是否存在，忽略大小写
     * @param tableName
     * @param db
     * @return
     */
    public static boolean isTableExists(String tableName, SQLiteDatabase db){
        boolean exist;
        try {
            exist = BaseUtility.containsIgnoreCases(findAllTableNames(db), tableName);
        } catch (Exception e) {
            e.printStackTrace();
            exist = false;
        }
        return exist;
    }

    /**
     * Find all table names in the database. If there's some wrong happens when
     * finding tables, it will throw exceptions.
     *
     * @param db
     *            Instance of SQLiteDatabase.
     * @return A list with all table names.
     * @throws com.aliao.litepal.exceptions.DatabaseGenerateException
     */
    public static List<String> findAllTableNames(SQLiteDatabase db) {
        List<String> tableNames = new ArrayList<String>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from sqlite_master where type = ?", new String[] { "table" });
            if (cursor.moveToFirst()) {
                do {
                    String tableName = cursor.getString(cursor.getColumnIndexOrThrow("tbl_name"));
                    if (!tableNames.contains(tableName)) {
                        tableNames.add(tableName);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatabaseGenerateException(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tableNames;
    }
}
