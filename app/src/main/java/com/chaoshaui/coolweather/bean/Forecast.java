package com.chaoshaui.coolweather.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chaofang on 2017/11/2.
 */

public class Forecast {

    /**
     * data : 2016-08-08
     * cond : {"txt_d":"阵雨"}
     * tmp : {"max":"34","min":"27"}
     */
    @SerializedName("date")
    public String data;
    public CondBean cond;
    public TmpBean  tmp;

    public class CondBean {
        /**
         * txt_d : 阵雨
         */

        public String txt_d;
    }

    public class TmpBean {
        /**
         * max : 34
         * min : 27
         */

        public String max;
        public String min;
    }
}
