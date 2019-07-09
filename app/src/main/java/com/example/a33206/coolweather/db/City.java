package com.example.a33206.coolweather.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {
    private  int id;
    private  String CityName;
    private  int Citycode;
    private  int ProvinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public int getCitycode() {
        return Citycode;
    }

    public void setCitycode(int citycode) {
        Citycode = citycode;
    }

    public int getProvinceId() {
        return ProvinceId;
    }

    public void setProvinceId(int provinceId) {
        ProvinceId = provinceId;
    }
}
