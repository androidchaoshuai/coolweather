package com.chaoshaui.coolweather.utils;

import android.text.TextUtils;

import com.chaoshaui.coolweather.db.City;
import com.chaoshaui.coolweather.db.County;
import com.chaoshaui.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chaofang on 2017/11/1.
 */

public class JsonUtils {


    /**
    *  解析和处理服务器返回的省级数据
    */

    public static boolean handleProvinceResponse(String response){

        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray mProvinceArray = new JSONArray(response);
                for (int i = 0; i < mProvinceArray.length(); i++){
                    JSONObject mProvinceObject = mProvinceArray.getJSONObject(i);
                    Province mProvince = new Province();
                    mProvince.setProvinceName(mProvinceObject.getString("name"));
                    mProvince.setProvinceCode(mProvinceObject.getInt("id"));
                    mProvince.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *  解析和处理服务器返回的市级数据
     */

    public static boolean handleCityResponse(String response ,int provinceId){

        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray mCityArray = new JSONArray(response);
                for (int i = 0; i < mCityArray.length(); i++){
                    JSONObject mCityObject = mCityArray.getJSONObject(i);
                    City       mCity       = new City();
                    mCity.setCityName(mCityObject.getString("name"));
                    mCity.setCityCode(mCityObject.getInt("id"));
                    mCity.setProvinceId(provinceId);
                    mCity.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *  解析和处理服务器返回的县级数据
     */

    public static boolean handleCountyResponse(String response ,int CityId){

        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray mCountyArray = new JSONArray(response);
                for (int i = 0; i < mCountyArray.length(); i++){
                    JSONObject mCountyObject = mCountyArray.getJSONObject(i);
                    County     mCounty       = new County();
                    mCounty.setCountyName(mCountyObject.getString("name"));
                    mCounty.setWeatherId(mCountyObject.getString("weather_id"));
                    mCounty.setCityId(CityId);
                    mCounty.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
