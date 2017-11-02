package com.chaoshaui.coolweather.utils;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by chaofang on 2017/11/1.
 */

public class HttpUtils {

    public static void sendOkhttpRequest(String mAddress, Callback mCallback){
        OkHttpClient mClient = new OkHttpClient();
        Request mRequest = new Request.Builder().url(mAddress).build();
        mClient.newCall(mRequest).enqueue(mCallback);
    }

}
