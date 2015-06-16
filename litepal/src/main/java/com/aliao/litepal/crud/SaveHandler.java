package com.aliao.litepal.crud;

import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
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

    }
}
