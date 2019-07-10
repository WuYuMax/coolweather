package com.example.a33206.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {
    public  AQIcity city;

    private class AQIcity {
        @SerializedName("api")
        public String api;
        @SerializedName("pm25")
        public String pm25;
    }
}
