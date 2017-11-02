package com.chaoshaui.coolweather.bean;

/**
 * Created by chaofang on 2017/11/2.
 */

public class Aqi {


    /**
     * city : {"aqi":"44","pm25":"13"}
     */

    public CityBean city;

    public  class CityBean {
        /**
         * aqi : 44
         * pm25 : 13
         */

        public String aqi;
        public String pm25;
    }
}
