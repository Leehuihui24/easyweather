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
	//�ϴΰ�back����ϵͳʱ��
	private long lastBackTime = 0;
	//��ǰ����back��ϵͳʱ��
	private long currentBackTime = 0;
	private LinearLayout weatherInfoLayout;
	
	//��ʾ������
	private TextView cityNameText;
	//��ʾ����ʱ��
	private TextView publishText;
	//��ʾ��������
	private TextView weatherdespText;
	//��ʾ����1
	private TextView tempText1;
	//��ʾ����2
	private TextView tempText2;
	//��ʾ��ǰ����
	private TextView currentDateText;
	
	
	private Button switchCity;
	private Button refreshWeather;
	
	
	
	@Override
	protected void onCreate(Bundle sevedInstanceState){
		super.onCreate(sevedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//��ʼ�������ؼ�
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
			//���ش���ʱ��ȥ��ѯ����
			publishText.setText("ͬ����");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeathercode(countyCode);
		} else {
			//û���ش���
			showWeather();
		}
		
	}
	
	
	
	//��sharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ
	private void showWeather() {
		// TODO Auto-generated method stub
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(sharedPreferences.getString("city_name", ""));
		tempText1.setText(sharedPreferences.getString("temp1", ""));
		tempText2.setText(sharedPreferences.getString("temp2", ""));
		weatherdespText.setText(sharedPreferences.getString("weather_Desp", ""));
		publishText.setText("����" + sharedPreferences.getString("publish_time", "")+ "����");
		currentDateText.setText(sharedPreferences.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		//Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
		
	}
	
	
	

	//��ѯ�ؼ������Ӧ��������
	private void queryWeathercode(String countyCode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
		
	}
	
	
	

	//���ݴ����ַ���ͺ�ȥ���ѯ�������Ż���������Ϣ
	private void queryFromServer(String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						//�ӷ��������ص������н�����������
						String[] array = response.split("\\|");
						if(array != null && array.length == 2);{
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)){
					//������������ص���Ϣ
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
						publishText.setText("ͬ��ʧ��");
					}
					
				});
				
			}
			
		});
		
	}


	
	//��ѯ�������Ŷ�Ӧ������
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
			publishText.setText("ͬ����");
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
	
	
	
	//�����η��ؼ��˳�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		//��ȡ���·��ؼ����¼�
		if(keyCode == KeyEvent.KEYCODE_BACK){
			//��ȡϵͳʱ�������
			currentBackTime = System.currentTimeMillis();
			//�Ƚ����ΰ��·��ؼ�ʱ������������룬����ʾ�ٰ�һ���˳�
			if(currentBackTime - lastBackTime > 2*1000){
				Toast.makeText(this, "�ٰ�һ�η��ؼ��˳�", Toast.LENGTH_SHORT).show();
				lastBackTime = currentBackTime;
			}else{//���������ʱ���С��2�룬���˳�����
				finish();
				
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}


