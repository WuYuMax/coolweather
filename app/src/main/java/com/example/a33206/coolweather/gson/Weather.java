package com.example.a33206.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Update update;
    @SerializedName("lifestyle")
    public List<LifeStyle> lifeStyleList;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
