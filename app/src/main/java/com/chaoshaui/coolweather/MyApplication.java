package com.chaoshaui.coolweather;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by chaofang on 2017/11/1.
 */

public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        LitePalApplication.initialize(mContext);
        super.onCreate();
    }

    public static Context getContext() {
        return mContext;
    }
}
