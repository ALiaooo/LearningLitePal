package com.aliao.litepal.tablemanager;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 丽双 on 2015/6/10.
 */
public class Generator {

    static void create(SQLiteDatabase db){
        create(db, true);
    }

    private static void create(SQLiteDatabase db, boolean force){
        Creator creator = new Creator();
        creator.createOrUpgradeTable(db, force);
    }

}
