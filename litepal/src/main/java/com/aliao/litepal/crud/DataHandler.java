package com.aliao.litepal.crud;

import android.database.sqlite.SQLiteDatabase;

import com.aliao.litepal.LitePalBase;

/**
 * Created by 丽双 on 2015/6/16.
 */
abstract class DataHandler extends LitePalBase {

    public static final String TAG = "DataHandler";

    SQLiteDatabase mDatabase;

}
