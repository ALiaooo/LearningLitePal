package com.aliao.litepal.tablemanager;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 丽双 on 2015/6/10.
 */
public abstract class Generator {

    static void create(SQLiteDatabase db){
        create(db, true);
        addAssociation(db, true);
    }

    private static void addAssociation(SQLiteDatabase db, boolean force) {
        AssociationCreator associationCreator = new Creator();
        associationCreator.addOrUpdateAssociation(db, force);
    }
    protected abstract void addOrUpdateAssociation(SQLiteDatabase db, boolean force);

    private static void create(SQLiteDatabase db, boolean force){
        Creator creator = new Creator();
        creator.createOrUpgradeTable(db, force);
    }

}
