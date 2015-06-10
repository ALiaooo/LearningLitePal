package com.aliao.litepal;

import android.app.Application;
import android.content.Context;

import com.aliao.litepal.exceptions.GlobalException;

/**
 * Created by 丽双 on 2015/6/9.
 */
public class LitePalApplication extends Application {
    private static Context sContext;

    public LitePalApplication() {
        sContext = this;
    }

    public static void initialize(Context context) {
        sContext = context;
    }

    public static Context getContext(){
        if (sContext == null){
            throw new GlobalException(GlobalException.APPLICATION_CONTEXT_IS_NULL);
        }
        return sContext;
    }
}
