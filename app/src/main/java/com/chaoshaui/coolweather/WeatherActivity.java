package com.chaoshaui.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chaoshaui.coolweather.bean.Forecast;
import com.chaoshaui.coolweather.bean.Weather;
import com.chaoshaui.coolweather.service.AutoUpdateService;
import com.chaoshaui.coolweather.utils.HttpUtils;
import com.chaoshaui.coolweather.utils.JsonUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by chaofang on 2017/11/2.
 */

public class WeatherActivity extends AppCompatActivity {


    private static final String TAG = WeatherActivity.class.getSimpleName();
    private ScrollView mSView;
    private TextView mTv_title_city;
    private TextView mTv_title_updatetime;
    private TextView mTv_now_template;
    private TextView mTv_now_info;
    private TextView mTv_aqi_title;
    private TextView mTv_aqi_pm;
    private TextView mTv_suggestion_comfort;
    private TextView mTv_suggestion_carwash;
    private TextView mTv_suggestion_sport;
    private LinearLayout mLL_forecast;
    private TextView mWt_date;
    private TextView mWt_info;
    private TextView mMax_info;
    private TextView mMin_info;
    private ImageView mImageView;
    public SwipeRefreshLayout mSw_refresh;
    public DrawerLayout mDrawerLayout;
    private Button mNav_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getMyWindow();
        initView();
        initData();
    }

    /**
     *
     */
    private void getMyWindow() {
        if(Build.VERSION.SDK_INT >=21){
            View  mDecor= getWindow().getDecorView();
            mDecor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dr_layout);
        mNav_button = (Button) findViewById(R.id.nav_button);
        mSw_refresh = (SwipeRefreshLayout) findViewById(R.id.sw_refresh);
        mSView = (ScrollView) findViewById(R.id.sv_weather);
        mTv_title_city = (TextView) findViewById(R.id.title_city);
        mTv_title_updatetime = (TextView) findViewById(R.id.title_updatetime);
        mTv_now_template = (TextView) findViewById(R.id.title_template);
        mTv_now_info = (TextView) findViewById(R.id.title_info);
        mTv_aqi_title = (TextView) findViewById(R.id.tv_title);
        mTv_aqi_pm = (TextView) findViewById(R.id.tv_pm);
        mLL_forecast = (LinearLayout) findViewById(R.id.ll_forecast);
        mTv_suggestion_comfort = (TextView) findViewById(R.id.tv_comfort);
        mTv_suggestion_carwash = (TextView) findViewById(R.id.tv_carwash);
        mTv_suggestion_sport = (TextView) findViewById(R.id.tv_sport);
        mImageView = (ImageView) findViewById(R.id.big_photo);
    }

    private void initData() {
        mSw_refresh.setColorSchemeResources(R.color.colorPrimary);
        mNav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        SharedPreferences mPre = PreferenceManager.getDefaultSharedPreferences(this);
        String mWeatherStr = mPre.getString("weather",null);
        String mBingPhoto = mPre.getString("bing_pic",null);
        if(mBingPhoto != null){
            Glide.with(WeatherActivity.this).load(mBingPhoto).into(mImageView);
        }else{
            loadBingPic();
        }
        final String weatherId;
        if(mWeatherStr != null){
            //有缓存时读取数据库数据
            Weather mWeather = JsonUtils.handleWeatherResponse(mWeatherStr);
            weatherId = mWeather.mBasic.weatherId;
            showWeatherInfo(mWeather);
        }else{
            //没缓存时请求服务器获取数据
            weatherId = getIntent().getStringExtra("weather_id");
            mSView.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        mSw_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

    }

    /**
     *
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtils.sendOkhttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String             mResponse = response.body().string();
                SharedPreferences.Editor mEditor   = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                mEditor.putString("bing_pic" , mResponse);
                mEditor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(mResponse).into(mImageView);
                    }
                });
            }
        });
    }

    /**
     * 通过weather实体类 显示天气信息
     * @param mWeather
     */
    private void showWeatherInfo(Weather mWeather) {

        Log.d(TAG,"mWeather is "+mWeather.mBasic.update);
        String mCityName = mWeather.mBasic.cityName;
        String mUpdateTime = mWeather.mBasic.update.updateTime.split(" ")[1];
        String mTemp = mWeather.mNow.tmp +"℃";
        String mNowInfo = mWeather.mNow.cond.txt;
        mTv_title_city.setText(mCityName);
        mTv_title_updatetime.setText(mUpdateTime);
        mTv_now_template.setText(mTemp);
        mTv_now_info.setText(mNowInfo);
        mLL_forecast.removeAllViews();
        for (Forecast mForecast:mWeather.forecastList) {
            View view = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item,mLL_forecast,false);
            mWt_date = (TextView) view.findViewById(R.id.wt_date);
            mWt_info = (TextView) view.findViewById(R.id.wt_info);
            mMax_info = (TextView) view.findViewById(R.id.max_info);
            mMin_info = (TextView) view.findViewById(R.id.min_info);
            mWt_date.setText(mForecast.data);
            mWt_info.setText(mForecast.cond.txt_d);
            mMax_info.setText(mForecast.tmp.max);
            mMin_info.setText(mForecast.tmp.min);
            mLL_forecast.addView(view);
        }
        if(mWeather.mAqi != null){
            mTv_aqi_title.setText(mWeather.mAqi.city.aqi);
            mTv_aqi_pm.setText(mWeather.mAqi.city.pm25);
        }
        String mComfort = getResources().getString(R.string.comfort)+mWeather.mSuggestion.comf.txt;
        String mWash_index = getResources().getString(R.string.wash_index)+mWeather.mSuggestion.cw.txt;
        String mSport = getResources().getString(R.string.sport)+mWeather.mSuggestion.sport.txt;
        mTv_suggestion_comfort.setText(mComfort);
        mTv_suggestion_carwash.setText(mWash_index);
        mTv_suggestion_sport.setText(mSport);
        mSView.setVisibility(View.VISIBLE);
    }

    /**
     * 根据天气 WeatherId 访问服务器获取当前天气信息
     * @param mWeatherId    
     */
    public void requestWeather(final String mWeatherId) {
        Log.d(TAG,"mWeatherId : "+ mWeatherId);
        //https://free-api.heweather.com/s6/weather/forecast?location=CN101240302&key=d6b14f1172c245ea9ae47d173094cd41
        /*String mWeatherUrl = "https://free-api.heweather.com/s6/weather/forecast?location="
                + mWeatherId + "&key=d6b14f1172c245ea9ae47d173094cd41";*/
        //CN101240302
        String mWeatherUrl = "http://guolin.tech/api/weather?cityid=" + mWeatherId +"&key=bc0418b57b2d4918819d3974ac1285d9";
        //http://guolin.tech/api/weather?cityid=CN101240302&key=bc0418b57b2d4918819d3974ac1285d9
        HttpUtils.sendOkhttpRequest(mWeatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"请求获取天气信息失败",Toast.LENGTH_SHORT).show();
                        mSw_refresh.setRefreshing(false);
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String  mResponse = response.body().string();
                final Weather mWeather  = JsonUtils.handleWeatherResponse(mResponse);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mWeather != null && "ok".equals(mWeather.status)){
                            SharedPreferences.Editor mEditor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            mEditor.putString("weather" , mResponse);
                            Log.d(TAG,mResponse);
                            mEditor.apply();
                            showWeatherInfo(mWeather);
                            Intent intent = new Intent(WeatherActivity.this, AutoUpdateService.class);
                            startService(intent);
                        }else{
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        mSw_refresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }
}
