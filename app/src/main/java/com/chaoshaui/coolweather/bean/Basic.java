package com.chaoshaui.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chaofang on 2017/11/2.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    public Update update;

    @SerializedName("id")
    public String weatherId;

    public class Update{

        @SerializedName("loc")
        public String updateTime;
    }
}
