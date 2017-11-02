package com.chaoshaui.coolweather.bean;

/**
 * Created by chaofang on 2017/11/2.
 */

public class Suggestion {

    /**
     * comf : {"txt":"2016-08-08 21:58"}
     * cw : {"txt":"2016-08-08 21:58"}
     * sport : {"txt":"2016-08-08 21:58"}
     */

    public ComfBean comf;
    public CwBean    cw;
    public SportBean sport;

    public  class ComfBean {
        /**
         * txt : 2016-08-08 21:58
         */

        public String txt;
    }

    public  class CwBean {
        /**
         * txt : 2016-08-08 21:58
         */

        public String txt;
    }

    public  class SportBean {
        /**
         * txt : 2016-08-08 21:58
         */

        public String txt;
    }
}
