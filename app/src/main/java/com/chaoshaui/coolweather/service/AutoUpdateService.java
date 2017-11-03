package com.chaoshaui.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.chaoshaui.coolweather.bean.Weather;
import com.chaoshaui.coolweather.utils.HttpUtils;
import com.chaoshaui.coolweather.utils.JsonUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by chaofang on 2017/11/3.
 */

public class AutoUpdateService extends Service {



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        updateBingPic();
        setPendingIntent();
        return super.onStartCommand(intent, flags, startId);
    }

    private void setPendingIntent() {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this,AutoUpdateService.class);
        PendingIntent mPi = PendingIntent.getBroadcast(this,0,intent,0);
        manager.cancel(mPi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+(8*3600*1000),mPi);
    }

    /**
     * 更新必应背景
     */
    private void updateBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtils.sendOkhttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String             mResponse = response.body().string();
                SharedPreferences.Editor mEditor   = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                mEditor.putString("bing_pic" , mResponse);
                mEditor.apply();
            }
        });
    }

    /**
     * 更新天气
     */
    private void updateWeather() {
        SharedPreferences mPre = PreferenceManager.getDefaultSharedPreferences(this);
        String mWeatherStr = mPre.getString("weather",null);
        if(mWeatherStr != null){
            //有缓存时直接解析天气数据
            Weather mWeather = JsonUtils.handleWeatherResponse(mWeatherStr);
            String mWeatherId = mWeather.mBasic.weatherId;
            String mWeatherUrl = "http://guolin.tech/api/weather?cityid=" + mWeatherId +"&key=bc0418b57b2d4918819d3974ac1285d9";
            HttpUtils.sendOkhttpRequest(mWeatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String mResponse = response.body().string();
                    Weather mWeather = JsonUtils.handleWeatherResponse(mResponse);
                    if(mWeather != null && "ok".equals(mWeather.status)){
                        SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        mEditor.putString("weather",mResponse);
                        mEditor.apply();
                    }
                }
            });
        }
    }
}
