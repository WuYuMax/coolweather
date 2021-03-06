package com.example.a33206.coolweather;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.bumptech.glide.Glide;
import com.example.a33206.coolweather.gson.Forecast;
import com.example.a33206.coolweather.gson.LifeStyle;
import com.example.a33206.coolweather.gson.Weather;
import com.example.a33206.coolweather.util.HttpUtil;
import com.example.a33206.coolweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.internal.http2.ErrorCode;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView tittleCity;
    private TextView tittleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forcastLayout;
    private TextView shiduText;
    private TextView jiangshuiText;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String mWeathId;
    public DrawerLayout drawerLayout;
    private Button home_buttom;
    private Button location_button;
    public LocationClient mloctionClient;
    public  List<String> permissionList =new ArrayList<>();
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
        shiduText=findViewById(R.id.shidu_text);
        jiangshuiText=findViewById(R.id.jiangshui_text);
        comfortText=findViewById(R.id.comfort_text);
        carWashText=findViewById(R.id.sport_wash_text);
        sportText=findViewById(R.id.sport_text);
        swipeRefreshLayout=findViewById(R.id.swipe_refesh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        location_button=findViewById(R.id.location_button);
        mloctionClient = new LocationClient(getApplicationContext());
        mloctionClient.registerLocationListener(new MyLoctionListener());

//        定位对象初始化

        SharedPreferences prefs =PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        String Bingpic=prefs.getString("bing_pic",null);

        location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(WeatherActivity.this,"点击响应",Toast.LENGTH_LONG).show();
             LimitRequest();
//                Toast.makeText(WeatherActivity.this,"定位结束",Toast.LENGTH_LONG).show();
            }
        });

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

    private void LimitRequest() {
        if (ContextCompat.checkSelfPermission(WeatherActivity
                .this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(WeatherActivity
                .this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        } if (ContextCompat.checkSelfPermission(WeatherActivity
                .this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String [] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(WeatherActivity.this,permissions,1);
        }else {
//            Toast.makeText(WeatherActivity.this,"发送信号",Toast.LENGTH_LONG).show();
            requestLocation();
//            Toast.makeText(WeatherActivity.this,"信号结束",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            switch (requestCode){
                case 1:
                    if (grantResults.length>0){
                        for (int result: grantResults){
                            if (result!=PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }
                        }
                        requestLocation();
                    }else {
                        Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
                    default:
            }
        }


    private void requestLocation() {
        mloctionClient.start();
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
        final String WeatherUrl="https://free-api.heweather.net/s6/weather/?location="+weatherId+"&key=e92664f83999433cb1cb2c2c9a42b90c";
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
        String UpdateTime= weather.update.update.split(" ")[1];
        String degree = weather.now.tmp+" ℃";
        String weatherInfo = weather.now.cond;
        String shidu=weather.now.shidu;
        String jiangshui=weather.now.jiangshui;
        List<Forecast> forecasts=weather.forecastList;
        List<LifeStyle> lifeStyles=weather.lifeStyleList;
        tittleCity.setText(CityName);
        tittleUpdateTime.setText(UpdateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forcastLayout.removeAllViews();
        shiduText.setText(shidu);
        jiangshuiText.setText(jiangshui);
//      添加循环节点
        for (Forecast forecast: forecasts){
            View view=LayoutInflater.from(this).inflate(R.layout.forecast_item,forcastLayout,false);
            TextView dataText =view.findViewById(R.id.data_text);
            TextView InfoText =view.findViewById(R.id.info_text);
            TextView maxText =view.findViewById(R.id.max_text);
            TextView minText =view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            if (forecast.cond_day.equals(forecast.cond_night))
            {InfoText.setText(forecast.cond_day);}
            else{
                InfoText.setText(forecast.cond_day+"转"+forecast.cond_night);
            }
            maxText.setText(forecast.max);
            minText.setText(forecast.min);
            forcastLayout.addView(view);
        }

        for (LifeStyle lifeStyle: lifeStyles) {
            if (lifeStyle.type.equals("comf")) {
                String comfort = "舒适度：" + lifeStyle.txt;
                comfortText.setText(comfort);
            } else if (lifeStyle.type.equals("cw")) {
                String Washcar = "洗车舒适度：" + lifeStyle.txt;
                carWashText.setText(Washcar);
            } else if (lifeStyle.type.equals("sport")) {
                String sport = "运动建议：" + lifeStyle.txt;
                sportText.setText(sport);
            }
        }
        weatherLayout.setVisibility(View.VISIBLE);
    }

    public class MyLoctionListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
//            Toast.makeText(WeatherActivity.this,"已进入gps定位",Toast.LENGTH_LONG).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   StringBuilder currentPosition = new StringBuilder();
                   double lactionx = bdLocation.getLongitude();
                   double lactiony = bdLocation.getLatitude();
                   int citynamee = bdLocation.getLocType();
                   String address=+lactionx+","+lactiony;
//                   Toast.makeText(WeatherActivity.this,""+citynamee+"\n"+lactionx+","+lactiony,Toast.LENGTH_LONG).show();
                   requestWeather(address);
                }
            });
        }
    }
}
