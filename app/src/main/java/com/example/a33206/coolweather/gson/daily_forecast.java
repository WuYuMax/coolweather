package com.example.a33206.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import javax.xml.transform.Templates;

public class daily_forecast {
    @SerializedName("date")
    public String date;
    @SerializedName("cond")
    public More more;
    @SerializedName("temp")
    public Temperature temperature;
    private class More {
        @SerializedName("txt_d")
        public String inof;
    }

    private class Temperature {
        public String max;
        public String min;
    }
}
