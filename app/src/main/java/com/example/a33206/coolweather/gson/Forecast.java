package com.example.a33206.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import javax.xml.transform.Templates;

public class Forecast{
    public String date;
    @SerializedName("cond")
    public More more;
    @SerializedName("tmp")
    public Temperature temperature;
    public class More {
        @SerializedName("txt_d")
        public String inof;
    }

    public class Temperature {
        public String max;
        public String min;
    }
}
