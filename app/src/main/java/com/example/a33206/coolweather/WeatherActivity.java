package com.example.a33206.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.a33206.coolweather.gson.Forecast;
import com.example.a33206.coolweather.gson.Weather;
import com.example.a33206.coolweather.util.HttpUtil;
import com.example.a33206.coolweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView tittleCity;
    private TextView tittleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forcastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeathId;
    public DrawerLayout drawerLayout;
    private Button home_buttom;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
//        加载控件
        drawerLayout=findViewById(R.id.drawer_layout);
        home_buttom=findViewById(R.id.nav_button);
        bingPicImg=findViewById(R.id.bing_pic_img);
        weatherLayout=findViewById(R.id.weather_layout);
        tittleCity=findViewById(R.id.title_city);
        tittleUpdateTime=findViewById(R.id.title_updata_time);
        degreeText=findViewById(R.id.degree_text);
        weatherInfoText=findViewById(R.id.weather_info_text);
        forcastLayout=findViewById(R.id.forecast_layout);
        aqiText=findViewById(R.id.aqi_text);
        pm25Text=findViewById(R.id.pm25_text);
        comfortText=findViewById(R.id.comfort_text);
        carWashText=findViewById(R.id.sport_wash_text);
        sportText=findViewById(R.id.sport_text);
        swipeRefreshLayout=findViewById(R.id.swipe_refesh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        String Bingpic=prefs.getString("bing_pic",null);

        home_buttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
//        如果已经缓存了数据直接加载，否则调用网络数据
        if(bingPicImg!=null){
            Glide.with(this).load(Bingpic).into(bingPicImg);
        }else {
            loadBingPic();
        }
        if (weatherString!=null){
            Weather weather=Utility.handleWeatherResponse(weatherString);
            mWeathId= weather.basic.weatherId;
            showWeatherInfo(weather);

        }
        else{
            mWeathId = getIntent().getStringExtra("weather_id");
//            String weatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.VISIBLE);

            requestWeather(mWeathId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeathId);
            }
        });
    }

    private void loadBingPic() {
        String address ="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this)
                        .edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    public void requestWeather(final String weatherId) {
        final String WeatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=bc0418b57b2d4918819d3674ac1285d9";
        HttpUtil.sendOkHttpRequest(WeatherUrl, new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"网络抛弃了你",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather =Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor =PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            mWeathId=weather.basic.weatherId;
                            showWeatherInfo(weather);
                        }else if (weather==null){
                            Toast.makeText(WeatherActivity.this,"网络拒绝了你",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(WeatherActivity.this,"数据拒绝了你",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        loadBingPic();
        swipeRefreshLayout.setRefreshing(false);
    }
//加载页面信息
    private void showWeatherInfo(Weather weather) {
        String CityName = weather.basic.cityname;
        String UpdateTime= weather.basic.update.updataTime.split(" ")[1];
        String degree = weather.now.tmp+" ℃";
        String weatherInfo = weather.now.more.txt;
        List<Forecast> forecasts=weather.forecastList;

        tittleCity.setText(CityName);
        tittleUpdateTime.setText(UpdateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forcastLayout.removeAllViews();
//      添加循环节点
        for (Forecast forecast: forecasts){
            View view=LayoutInflater.from(this).inflate(R.layout.forecast_item,forcastLayout,false);
            TextView dataText =view.findViewById(R.id.data_text);
            TextView InfoText =view.findViewById(R.id.info_text);
            TextView maxText =view.findViewById(R.id.max_text);
            TextView minText =view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            InfoText.setText(forecast.more.inof);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forcastLayout.addView(view);
        }
        if(weather.aqi!=null)
        {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度："+weather.suggestion.comfort.info;
        String Washcar = "洗车舒适度："+weather.suggestion.carWash.info;
        String sport= "运动建议："+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(Washcar);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
