package com.chaoshaui.coolweather;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaoshaui.coolweather.db.City;
import com.chaoshaui.coolweather.db.County;
import com.chaoshaui.coolweather.db.Province;
import com.chaoshaui.coolweather.utils.HttpUtils;
import com.chaoshaui.coolweather.utils.JsonUtils;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by chaofang on 2017/11/1.
 */

public class ChooseFragment extends Fragment {


    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY     = 1;
    private static final int LEVEL_COUNTY   = 2;
    private static final String TAG            = ChooseFragment.class.getSimpleName();
    private Button mBt_back;
    private TextView mTv_title;
    private ListView mLv_weather;
    private List<String> mDataList = new ArrayList<>();
    private ArrayAdapter<String> mAdapter;
    private List<Province>       mProvinces ;//省列表
    private List<City>           mCitys ;//市列表
    private List<County>         mCountys ;//县列表
    private Province mSelectedProvince;//选中的省
    private City mSelectedCity;//选中的市
    private int                  mCurrentLevel;//当前选中的级别
    private ProgressDialog       mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        mBt_back = (Button) view.findViewById(R.id.bt_back);
        mTv_title = (TextView) view.findViewById(R.id.tv_area_title);
        mLv_weather = (ListView) view.findViewById(R.id.lv_weather);
        mAdapter = new ArrayAdapter<>(MyApplication.getContext(), android.R.layout.simple_list_item_1, mDataList);
        mLv_weather.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLv_weather.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(mCurrentLevel == LEVEL_PROVINCE){
                    mSelectedProvince = mProvinces.get(position);
                    Log.d(TAG,"mSelectedProvince is "+mSelectedProvince);
                    queryCities();
                }else if(mCurrentLevel == LEVEL_CITY){
                    mSelectedCity = mCitys.get(position);
                    Log.d(TAG,"mSelectedCity is "+mSelectedCity);
                    queryCounties();
                }else if(mCurrentLevel == LEVEL_COUNTY){
                    String weatherId = mCountys.get(position).getWeatherId();
                    if(getActivity() instanceof MainActivity){

                        Intent intent    = new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity weatherActivity = (WeatherActivity) getActivity();
                        weatherActivity.mDrawerLayout.closeDrawers();
                        weatherActivity.mSw_refresh.setRefreshing(true);
                        weatherActivity.requestWeather(weatherId);
                    }
                }
            }
        });

        mBt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //回退到上级界面 并显示数据
                if(mCurrentLevel == LEVEL_COUNTY){
                    //当前界面在县 则回到市级界面并显示各个市的数据
                    queryCities();
                }else if(mCurrentLevel == LEVEL_CITY){
                    //当前界面在市 则回到省级界面并显示各个省的数据
                    queryProvinces();
                }
            }
        });
        //第一次进来 需要加载出省级的数据
        queryProvinces();
    }


    /**
     * 查询全国所有的省，优先从数据库中读取，数据库中没有的话再从服务器中读取
     */
    private void queryProvinces() {
        mTv_title.setText(R.string.china);
        mBt_back.setVisibility(View.GONE);
        mProvinces = DataSupport.findAll(Province.class);
        if(mProvinces.size()>0){
            mDataList.clear();
            for (Province mP: mProvinces) {
                mDataList.add(mP.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mLv_weather.setSelection(0);
            mCurrentLevel = LEVEL_PROVINCE;
        }else{
            String mAddress = "http://guolin.tech/api/china";
            queryFromServer(mAddress,"province");
        }
    }

    /**
     *  查询选中省内所有的市，优先从数据库中读取，数据库中没有的话再从服务器中读取
     */
    private void queryCounties() {
        mTv_title.setText(mSelectedCity.getCityName());
        mBt_back.setVisibility(View.VISIBLE);
        mCountys = DataSupport.where("cityid = ?",String.valueOf(mSelectedCity.getId())).find(County.class);
        if(mCountys.size()>0){
           mDataList.clear();
            for (County mC:mCountys) {
                mDataList.add(mC.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mLv_weather.setSelection(0);
            mCurrentLevel = LEVEL_COUNTY;
        }else{
            int mProvinceCode = mSelectedProvince.getProvinceCode();
            int mCityCode = mSelectedCity.getCityCode();
            String mAddress = "http://guolin.tech/api/china/"+mProvinceCode+"/"+mCityCode;
            Log.d(TAG,"mSelectedProvince " +mProvinceCode +"   mCityCode"+mCityCode);
            queryFromServer(mAddress,"county");
        }
    }

    /**
     *查询选中市内所有的县，优先从数据库中读取，数据库中没有的话再从服务器中读取
     */
    private void queryCities() {
        mTv_title.setText(mSelectedProvince.getProvinceName());
        mBt_back.setVisibility(View.VISIBLE);
        mCitys = DataSupport.where("provinceid = ?",String.valueOf(mSelectedProvince.getId())).find(City.class);
        if(mCitys.size()>0){
            mDataList.clear();
            for (City mC:mCitys) {
                mDataList.add(mC.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mLv_weather.setSelection(0);
            mCurrentLevel = LEVEL_CITY;
        }else{
            String mAddress = "http://guolin.tech/api/china/"+mSelectedProvince.getProvinceCode();
            Log.d(TAG,"mSelectedProvince " +mSelectedProvince.getProvinceCode());
            queryFromServer(mAddress,"city");
        }
    }

    /**
     *  根据传入的地址和类型去请求服务器数据
     * @param mAddress
     * @param type
     */
    private void queryFromServer(String mAddress,final String type) {
        //弹出进度条 提示加载进度
        showProgressDialog();
        HttpUtils.sendOkhttpRequest(mAddress, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MyApplication.getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String mResponse = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = JsonUtils.handleProvinceResponse(mResponse);
                }else if("city".equals(type)){
                    result = JsonUtils.handleCityResponse(mResponse,mSelectedProvince.getId());
                }else if("county".equals(type)){
                    result = JsonUtils.handleCountyResponse(mResponse,mSelectedCity.getId());
                }

                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 关闭提示进度条
     */
    private void closeProgressDialog() {
        if(mDialog != null){
            mDialog.dismiss();
        }
    }

    /**
     * 显示进度条
     */
    private void showProgressDialog() {
        if(mDialog == null){
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("正在加载中");
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }
}
