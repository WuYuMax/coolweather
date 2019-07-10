package com.example.a33206.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic  {
    @SerializedName("city")
    public String cityname;
    @SerializedName("id")
    public String weatherId;
    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updataTime;
    }
}
