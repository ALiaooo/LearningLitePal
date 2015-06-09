package com.aliao.litepal.parser;

import android.text.TextUtils;

import com.aliao.litepal.exceptions.InvalidAttributesException;
import com.aliao.litepal.util.Const;
import com.aliao.litepal.util.SharedUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 丽双 on 2015/6/9.
 */
public class LitePalAttr {

    private static LitePalAttr litePalAttr;

    //数据库版本号
    private int version;
    //数据库名
    private String dbName;
    //所有在数据库中想要有映射关系的实体类
    private List<String> classNames;
    //表名，列名及sql大小写
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
     * 在sqlite里table_schema表示自动生成的，这里一定要手动添加进去
     * table_schema用来查看数据库的元数据，这里的元数据包括表名及表类型
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
     * 这里用双重检测来实现单例模式
     * 整个程序创建一个唯一的litePalAttr对象，可以通过这个对象拿到数据库的基本信息
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
        return "数据库名称:"+dbName+"\n数据库版本:"+version+"\n\n数据库映射对象类名:";
    }
}
