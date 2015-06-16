package com.aliao.litepal.crud;

import android.database.sqlite.SQLiteDatabase;

import com.aliao.litepal.tablemanager.Connector;

/**
 * Created by 丽双 on 2015/6/16.
 * DataSupport为应用连接了类和SQLite数据库表去建立一个几乎零配置持久层。
 * 在应用程序的上下文，这些类通常被称作模型。模型也可以被连接到其他模型。
 *
 * DataSupport严重依赖于命名，因为她使用类和关联名去建立相应的数据库表和外键列之间的映射关系
 *
 * 自动映射类与表，属性与列
 */
public class DataSupport {


    protected String getClassName() {
        return getClass().getName();
    }

    public synchronized boolean save(){
        try {
            saveThrows();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public synchronized void saveThrows() {
        SQLiteDatabase db = Connector.getDatabase();
        db.beginTransaction();

        SaveHandler saveHandler = new SaveHandler(db);
        saveHandler.onSave(this);



    }
}
