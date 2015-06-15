package com.aliao.litepal.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.aliao.litepal.exceptions.DatabaseGenerateException;
import com.aliao.litepal.tablemanager.model.TableModel;

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



    /**
     * Test if a column exists in a table. Cases are ignored.
     *
     * @param columnName
     *            The column name.
     * @param tableName
     *            The table name.
     * @param db
     *            Instance of SQLiteDatabase.
     * @return If there's a column named as the column name passed in, return
     *         true. Or return false. If any of the passed in parameters is null
     *         or empty, return false.
     */
    public static boolean isColumnExists(String columnName, String tableName, SQLiteDatabase db) {
        if (TextUtils.isEmpty(columnName) || TextUtils.isEmpty(tableName)) {
            return false;
        }
        boolean exist = false;
        try {
            exist = BaseUtility.containsIgnoreCases(findPragmaTableInfo(tableName, db)
                    .getColumnNames(), columnName);
        } catch (Exception e) {
            e.printStackTrace();
            exist = false;
        }
        return exist;
    }


    /**
     * Look from the database to find a table named same as the table name in
     * table model. Then iterate the columns and types of this table to create a
     * new instance of table model. If there's no such a table in the database,
     * then throw DatabaseGenerateException.
     *
     * @param tableName
     *            Table name.
     * @param db
     *            Instance of SQLiteDatabase.
     * @return A table model object with values from database table.
     * @throws com.aliao.litepal.exceptions.DatabaseGenerateException
     */
    public static TableModel findPragmaTableInfo(String tableName, SQLiteDatabase db) {
        if (isTableExists(tableName, db)) {
            TableModel tableModelDB = new TableModel();
            tableModelDB.setTableName(tableName);
            String checkingColumnSQL = "pragma table_info(" + tableName + ")";
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(checkingColumnSQL, null);
                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                        String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                        tableModelDB.addColumns(name, type);
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
            return tableModelDB;
        } else {
            throw new DatabaseGenerateException(
                    DatabaseGenerateException.TABLE_DOES_NOT_EXIST_WHEN_EXECUTING + tableName);
        }
    }

    /**
     * Create intermediate join table name by the concatenation of the two
     * target table names in alphabetical order with underline in the middle.
     *
     * @param tableName
     *            First table name.
     * @param associatedTableName
     *            The associated table name.
     * @return The table name by the concatenation of the two target table names
     *         in alphabetical order with underline in the middle. If the table
     *         name or associated table name is null of empty, return null.
     */
    public static String getIntermediateTableName(String tableName, String associatedTableName) {
        if (!(TextUtils.isEmpty(tableName) || TextUtils.isEmpty(associatedTableName))) {
            String intermediateTableName = null;
            if (tableName.toLowerCase().compareTo(associatedTableName.toLowerCase()) <= 0) {
                intermediateTableName = tableName + "_" + associatedTableName;
            } else {
                intermediateTableName = associatedTableName + "_" + tableName;
            }
            return intermediateTableName;
        }
        return null;
    }

}
