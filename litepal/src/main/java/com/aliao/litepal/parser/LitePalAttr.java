package com.aliao.litepal.parser;

import android.text.TextUtils;

import com.aliao.litepal.exceptions.InvalidAttributesException;
import com.aliao.litepal.util.Const;
import com.aliao.litepal.util.SharedUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ��˫ on 2015/6/9.
 */
public class LitePalAttr {

    private static LitePalAttr litePalAttr;

    //���ݿ�汾��
    private int version;
    //���ݿ���
    private String dbName;
    //���������ݿ�����Ҫ��ӳ���ϵ��ʵ����
    private List<String> classNames;
    //������������sql��Сд
    private String cases;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * ��sqlite��table_schema��ʾ�Զ����ɵģ�����һ��Ҫ�ֶ���ӽ�ȥ
     * table_schema�����鿴���ݿ��Ԫ���ݣ������Ԫ���ݰ���������������
     * @return
     */
    public List<String> getClassNames() {
        if (classNames == null){
            classNames = new ArrayList<>();
            classNames.add("com.aliao.parser.entity.Table_Schema");
        }else if (classNames.isEmpty()){
            classNames.add("com.aliao.parser.entity.Table_Schema");
        }
        return classNames;
    }

    public void addClassName(String className){
        getClassNames().add(className);
    }

    /**
     * ������˫�ؼ����ʵ�ֵ���ģʽ
     * �������򴴽�һ��Ψһ��litePalAttr���󣬿���ͨ����������õ����ݿ�Ļ�����Ϣ
     * @return
     */
    public static LitePalAttr getIntance(){
        if (litePalAttr == null){
            synchronized (LitePalAttr.class){
                if (litePalAttr == null){
                    litePalAttr = new LitePalAttr();
                }
            }
        }
        return litePalAttr;
    }

    public String getCases() {
        return cases;
    }

    public void setCases(String cases) {
        this.cases = cases;
    }

    public boolean checkSelfValid(){
        if (TextUtils.isEmpty(dbName)){
            throw new InvalidAttributesException(InvalidAttributesException.DBNAME_IS_EMPTY_OR_NOT_DEFINED);
        }
        if (!dbName.endsWith(Const.LitePal.DB_NAME_SUFFIX)){
            dbName = dbName+Const.LitePal.DB_NAME_SUFFIX;
        }
        if (version < 1) {
            throw new InvalidAttributesException(
                    InvalidAttributesException.VERSION_OF_DATABASE_LESS_THAN_ONE);
        }
        if (version < SharedUtil.getLastVersion()){
            throw new InvalidAttributesException(
                    InvalidAttributesException.VERSION_IS_EARLIER_THAN_CURRENT);
        }
        if (TextUtils.isEmpty(cases)) {
            cases = Const.LitePal.CASES_LOWER;
        } else {
            if (!cases.equals(Const.LitePal.CASES_UPPER)
                    && !cases.equals(Const.LitePal.CASES_LOWER)
                    && !cases.equals(Const.LitePal.CASES_KEEP)) {
                throw new InvalidAttributesException(cases
                        + InvalidAttributesException.CASES_VALUE_IS_INVALID);
            }

        }
        return true;
    }

    @Override
    public String toString() {
        return "���ݿ�����:"+dbName+"\n���ݿ�汾:"+version+"\n\n���ݿ�ӳ���������:";
    }
}
