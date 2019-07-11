package com.example.a33206.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic  {
    @SerializedName("location")
    public String cityname;
    @SerializedName("cid")
    public String weatherId;

}
