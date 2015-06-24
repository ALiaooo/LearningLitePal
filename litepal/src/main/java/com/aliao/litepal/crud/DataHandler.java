package com.aliao.litepal.crud;

import android.database.sqlite.SQLiteDatabase;

import com.aliao.litepal.LitePalBase;
import com.aliao.litepal.crud.model.AssociationsInfo;
import com.aliao.litepal.util.Const;

import java.util.Collection;

/**
 * Created by 丽双 on 2015/6/16.
 */
abstract class DataHandler extends LitePalBase {

    public static final String TAG = "DataHandler";

    SQLiteDatabase mDatabase;

    protected void analyzeAssociatedModels(DataSupport baseObj, Collection<AssociationsInfo> associationsInfos){
        for (AssociationsInfo associationsInfo : associationsInfos){
            if (associationsInfo.getAssociationType() == Const.Model.MANY_TO_ONE){

            }
        }
    }

    protected void putSetMethodValueByField(){

    }

}
