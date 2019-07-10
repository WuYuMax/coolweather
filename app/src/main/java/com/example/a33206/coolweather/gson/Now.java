package com.example.a33206.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String tmp;
    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String txt;
    }
}
