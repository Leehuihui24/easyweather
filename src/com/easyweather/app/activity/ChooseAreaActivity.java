package com.easyweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.easyweather.app.db.EasyWeatherDB;
import com.easyweather.app.model.City;
import com.easyweather.app.model.County;
import com.easyweather.app.model.Province;
import com.easyweather.app.util.HttpCallbackListener;
import com.easyweather.app.util.HttpUtil;
import com.easyweather.app.util.Utility;
import com.example.easyweather.R;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{
	String TAG = "ChooseAreaActivity";
	
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private EasyWeatherDB easyWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	
	
	
	
	
	//省列表
	private List<Province> provinceList;

	
	//市列表
	private List<City> cityList;
	
	
	//县列表
	private List<County> countyList;
	
	
	
	//选中的省份
	private Province selectedProvince;
	
	//选中的省份
	private City selectedCity;
		
	//当前选中的级别
	private int currentLevel;
	
	
	//是否从weather activity跳转
	private boolean isFromWeatherActivity;


	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		Log.d(TAG, "isFromWeatherActivity="+ isFromWeatherActivity);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ChooseAreaActivity.this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		easyWeatherDB = EasyWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?>arg0, View view,int index, long arg3){
				if (currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(index);
					queryCities();
				}else if (currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(index).getCountyCode();
					Log.d(TAG, countyCode + "h");
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("countyCode", countyCode);
					startActivity(intent);
					finish();
				}
			}

			
	

			
		});
		queryProvinces();   //加载省级数据
	}
	
	
	
	
	

//查询全国的省，优先从数据库查，没有在从服务器上查
	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList = easyWeatherDB.loadProvinces();
		if (provinceList.size() >0){
			dataList.clear();
			for (Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null,"province");
		}
		
	}

	
	
	
	//查询全国的省，优先从数据库查，没有在从服务器上查
	protected void queryCities() {
		// TODO Auto-generated method stub
		cityList = easyWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0){
			dataList.clear();
			for(City city : cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
			
			
		} else {
			queryFromServer(selectedProvince.getProvinceCode(),"city");
			}
		
	}
	
	//查询全国的县，优先从数据库查，没有在从服务器上查
	private void queryCounties() {
		// TODO Auto-generated method stub
		Log.d(TAG, selectedCity.getID()+"");
		countyList = easyWeatherDB.loadCounties(selectedCity.getID());
		if (countyList.size() > 0){
			dataList.clear();
			for (County county : countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
			
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
		
	}
	
	
//根据传入的代号和类型从服务器上查询数据
	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
			@Override
			public void onFinish(String response){
				boolean result = false;
				if("province".equals(type)){
					result = Utility.handleProvincesResponse(easyWeatherDB, response);
				}else if ("city".equals(type)){
					result = Utility.handleCityesResponse(easyWeatherDB, response, selectedProvince.getId());
				}else if ("county".equals(type)){
					result = Utility.handleCountiseResponse(easyWeatherDB, response, selectedCity.getID());
				}
				if (result){
					//通过runOnUiThread（）方法回到主线程
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			
			public void onError(Exception e){
				// 通过runOnUiThread()方法回到主线程处理逻辑
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}


	
	//显示进度
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	
	//关闭进度对话框
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	
	
	//获取back键，根据当前级别来判断，此时应返回上级列表还是退出
	@Override
	public void onBackPressed(){
		if (currentLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel == LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){
				Log.d(TAG, "issi");
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}



}
