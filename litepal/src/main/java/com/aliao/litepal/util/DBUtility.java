package com.aliao.litepal.util;

import android.text.TextUtils;

/**
 * Created by 丽双 on 2015/6/10.
 */
public class DBUtility {

    /**
     * 通过类名获取表名
     * @param className
     * @return
     */
    public static String getTableNameByClassName(String className){
        if (!TextUtils.isEmpty(className)){
            if ('.' == className.charAt(className.length() - 1)){
                return null;
            }else {
                return className.substring(className.lastIndexOf('.')+1);
            }
        }
        return null;
    }
}
