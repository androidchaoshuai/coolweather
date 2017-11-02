package com.chaoshaui.coolweather.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by chaofang on 2017/11/2.
 */

public class Weather {

    public String status;
    @SerializedName("aqi")
    public Aqi mAqi;
    @SerializedName("basic")
    public Basic mBasic;
    @SerializedName("now")
    public Now mNow;
    @SerializedName("suggestion")
    public Suggestion mSuggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
