package com.example.a33206.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import javax.xml.transform.Templates;

public class Forecast{
    @SerializedName("date")
    public String date;
    @SerializedName("cond_txt_d")
    public String cond_day;
    @SerializedName("cond_txt_n")
    public String cond_night;
    @SerializedName("tmp_max")
    public String max;
    @SerializedName("tmp_min")
    public String min;


}
