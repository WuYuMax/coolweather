package com.example.a33206.coolweather;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a33206.coolweather.db.City;
import com.example.a33206.coolweather.db.County;
import com.example.a33206.coolweather.db.Province;
import com.example.a33206.coolweather.util.HttpUtil;
import com.example.a33206.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreFragment extends Fragment {
    //选中编码
    private static final int Level_Province=0;
    private static final int Level_City=1;
    private static final int Level_county=2;
    //选中
    private Province SelectProvince;
    private City SelectCity;
    private County SelectCounty;
    private  int currentLevel;
    //声明
    private TextView tittleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter ;
    private ProgressDialog progressDialog;
    private List<String> dataList = new ArrayList<>();
    //加载列表
    private List<Province> ProvinceList;
    private List<City> CityList;
    private List<County> CountyList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.choose_area,container,false);
        tittleText=view.findViewById(R.id.tittle_text);
        backButton =view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        adapter =new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==Level_Province) {
                SelectProvince=ProvinceList.get(position);
                queryCities();
                }
                else if (currentLevel==Level_City) {
                SelectCity=CityList.get(position);
                queryCounts();
                }

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLevel==Level_county) {
                    queryCities();
                }
                else if(currentLevel==Level_City) {
                    queryProvince();
                }
            }
        });
        queryProvince();
    }

    private void queryProvince() {
        tittleText.setText("中国");
        backButton.setVisibility(View.GONE);
        ProvinceList=DataSupport.findAll(Province.class);
        if(ProvinceList.size()>0)
        {
            for(Province province:ProvinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=Level_Province;

        }
        else {
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"Province");
        }
    }


    private void queryCounts() {
        tittleText.setText(SelectProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        CityList =DataSupport.where("Prounvinceid = ?",String.valueOf(SelectProvince.getId())).find(City.class);
        if(CityList.size()>0) {
            for(City city:CityList)
                dataList.add(city.getCityName());
        }
        else
        {
            int provinceCode = SelectProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address,"City");
        }
    }

    private void queryCities() {
        tittleText.setText(SelectCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        CountyList =DataSupport.where("Cityid = ?",String.valueOf(SelectCity.getId())).find(County.class);
        if(CountyList.size()>0) {
            for(County county:CountyList)
                dataList.add(county.getCountyName());
        }
        else
        {
            int citycode = SelectCity.getCitycode();
            String address="http://guolin.tech/api/china/"+citycode;
            queryFromServer(address,"County");
        }
    }

    //网络服务
    private void queryFromServer(final String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"网络拒绝了你",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result =false;
                if("Prounce".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("City".equals(type)){
                    result = Utility.handleCityResponse(responseText,SelectProvince.getId());
                }else if ("County".equals(type)){
                    result = Utility.handleCountyResponse(responseText,SelectCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("Prounce".equals(type)) {
                                queryProvince();
                            }else if ("City".equals(type)){
                                queryCities();
                            }else if ("County".equals(type)){
                                queryCounts();
                            }
                        }
                    });

                }

            }
        });
    }
    //关闭加载动画
    private void closeProgressDialog() {
        if (progressDialog!=null) {
            progressDialog.dismiss();
        }
    }
    //打开加载动画
    private void showProgressDialog() {
        if (progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("网络拼命加载中");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

}

