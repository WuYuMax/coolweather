package com.example.a33206.coolweather.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {
    private int id;
    private String ProvinceName;
    private int CityCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }

    public int getCityCode() {
        return CityCode;
    }

    public void setCityCode(int cityCode) {
        CityCode = cityCode;
    }
}
