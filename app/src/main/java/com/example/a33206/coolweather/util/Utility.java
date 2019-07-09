package com.example.a33206.coolweather.util;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.example.a33206.coolweather.db.City;
import com.example.a33206.coolweather.db.County;
import com.example.a33206.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response))
        {
            try {
                JSONArray allPronvences = new  JSONArray(response);
                for (int i=0;i<allPronvences.length();i++)
                {
                    JSONObject provinceObject = allPronvences.getJSONObject(i);
                    Province province= new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } return  false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean handleCityResponse(String response, int PronvenceId){
        if(!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCity = new JSONArray(response);
                for (int i = 0; i < allCity.length(); i++) {
                    JSONObject jsonObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityName(jsonObject.getString("name"));
                    city.setCitycode(jsonObject.getInt("id"));
                    city.setProvinceId(PronvenceId);
                    city.save();
                }
                return  true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }return false;

    }

    public static boolean handleCountyResponse(String response,int CityId)
    {
        if (!TextUtils.isEmpty(response))
        {
            try {
                JSONArray allCounty= new JSONArray(response);
                for(int i=0;i<allCounty.length();i++)
                {
                    JSONObject CountyObject= allCounty.getJSONObject(i);
                    County county =new County();
                    county.setCountyName(CountyObject.getString("name"));
                    county.setCityId(CityId);
                    county.setWeatherId(CountyObject.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
