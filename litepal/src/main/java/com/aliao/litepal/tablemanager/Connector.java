package com.aliao.litepal.tablemanager;

import android.database.sqlite.SQLiteDatabase;

import com.aliao.litepal.exceptions.InvalidAttributesException;
import com.aliao.litepal.parser.LitePalAttr;
import com.aliao.litepal.parser.LitePalParser;

/**
 * Created by ��˫ on 2015/6/9.
 * ͨ������ȥ�������ݿ⣬ʹ�øõõ����ݿ��ʵ������
 */
public class Connector {

    private static LitePalAttr mLitePalAttr;

    private static LitePalOpenHelper mLitePalHelper;

    public static SQLiteDatabase getDatabase(){
        return getWriteDatabase();
    }

    public synchronized static SQLiteDatabase getWriteDatabase(){
        LitePalOpenHelper openHelper = buildConnection();
        return openHelper.getWritableDatabase();
    }

    /**
     * 1.������litepal.xml�Ľ���
     * 2.����LitePalOpenHelperʵ������
     * @return
     */
    private static LitePalOpenHelper buildConnection() {
        if (mLitePalAttr == null){
            LitePalParser.parseLitePalConfiguration();
            mLitePalAttr = LitePalAttr.getIntance();
        }
        if (mLitePalAttr.checkSelfValid()){
            if (mLitePalHelper == null){
                mLitePalHelper = new LitePalOpenHelper(mLitePalAttr.getDbName(), mLitePalAttr.getVersion());
            }
            return mLitePalHelper;
        }else {
            throw new InvalidAttributesException("Uncaught invalid attributes exception happened");
        }
    }


}
