package com.aliao.litepal.tablemanager;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.aliao.litepal.LitePalApplication;

/**
 * Created by 丽双 on 2015/6/9.
 */
public class LitePalOpenHelper extends SQLiteOpenHelper{

    public LitePalOpenHelper(String name,int version) {
        //将数据库名及版本号传给父类SQLiteOpenHelper
        super(LitePalApplication.getContext(), name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
