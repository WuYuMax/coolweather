package com.example.a33206.coolweather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {
    private int id;
    private String CountyName;
    private String WeatherId;
    private String cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return CountyName;
    }

    public void setCountyName(String countyName) {
        CountyName = countyName;
    }

    public String getWeatherId() {
        return WeatherId;
    }

    public void setWeatherId(String weatherId) {
        WeatherId = weatherId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }
}
