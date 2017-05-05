package com.easyweather.app.activity;

import com.easyweather.app.util.HttpCallbackListener;
import com.easyweather.app.util.HttpUtil;
import com.easyweather.app.util.Utility;
import com.example.easyweather.R;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener{
	private static final Intent intent = null;
	String TAG = "WeatherActivity";
	//上次按back键的系统时间
	private long lastBackTime = 0;
	//当前按下back键系统时间
	private long currentBackTime = 0;
	private LinearLayout weatherInfoLayout;
	
	//显示城市名
	private TextView cityNameText;
	//显示发布时间
	private TextView publishText;
	//显示天气描述
	private TextView weatherdespText;
	//显示气温1
	private TextView tempText1;
	//显示气温2
	private TextView tempText2;
	//显示当前日期
	private TextView currentDateText;
	
	
	private Button switchCity;
	private Button refreshWeather;
	
	
	
	@Override
	protected void onCreate(Bundle sevedInstanceState){
		super.onCreate(sevedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//初始化各个控件
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherdespText = (TextView) findViewById(R.id.weather_desp);
		tempText1 = (TextView) findViewById(R.id.temp1);
		tempText2 = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
		Log.d(TAG, "hello");
		String countyCode = getIntent().getStringExtra("county_code");
		Log.d(TAG, "haha" + countyCode + "haha");
		if(!countyCode.isEmpty()){
			//有县代号时就去查询天气
			publishText.setText("同步中");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeathercode(countyCode);
		} else {
			//没有县代号
			showWeather();
		}
		
	}
	
	
	
	//从sharedPreferences文件中读取存储的天气信息，并显示
	private void showWeather() {
		// TODO Auto-generated method stub
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(sharedPreferences.getString("city_name", ""));
		tempText1.setText(sharedPreferences.getString("temp1", ""));
		tempText2.setText(sharedPreferences.getString("temp2", ""));
		weatherdespText.setText(sharedPreferences.getString("weather_Desp", ""));
		publishText.setText("今天" + sharedPreferences.getString("publish_time", "")+ "发布");
		currentDateText.setText(sharedPreferences.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		//Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
		
	}
	
	
	

	//查询县级代码对应天气代号
	private void queryWeathercode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
		
	}
	
	
	

	//根据传入地址类型和去向查询天气代号或者天气信息
	private void queryFromServer(String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						//从服务器返回的数据中解析天气代号
						String[] array = response.split("\\|");
						if(array != null && array.length == 2);{
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)){
					//处理服务器返回的信息
					Utility.handlerWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable(){
						
						@Override
						public void run(){
							showWeather();
						}
					});
				}
				
			}



			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){
					
					@Override
					public void run(){
						publishText.setText("同步失败");
					}
					
				});
				
			}
			
		});
		
	}


	
	//查询天气代号对应的天气
	private void queryWeatherInfo(String weatherCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		Log.d(TAG, address);
		queryFromServer(address,"weatherCode");
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			Log.d(TAG, 123+"");
			intent.putExtra("from_weather_activity", true);
			Log.d(TAG, 12223+"");
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中");
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = sharedPreferences.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
		break;
		}
		
	}
	
	
	
	//按两次返回键退出
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		//获取按下返回键的事件
		if(keyCode == KeyEvent.KEYCODE_BACK){
			//获取系统时间毫秒数
			currentBackTime = System.currentTimeMillis();
			//比较两次按下返回键时间差，如果大于两秒，则提示再按一次退出
			if(currentBackTime - lastBackTime > 2*1000){
				Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
				lastBackTime = currentBackTime;
			}else{//如果按两次时间差小于2秒，则退出程序
				finish();
				
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}


