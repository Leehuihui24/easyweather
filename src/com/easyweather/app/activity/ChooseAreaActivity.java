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
	
	
	
	
	
	
	//ʡ�б�
	private List<Province> provinceList;

	
	//���б�
	private List<City> cityList;
	
	
	//���б�
	private List<County> countyList;
	
	
	
	//ѡ�е�ʡ��
	private Province selectedProvince;
	
	//ѡ�е�ʡ��
	private City selectedCity;
		
	//��ǰѡ�еļ���
	private int currentLevel;
	
	
	//�Ƿ��weather activity��ת
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
		queryProvinces();   //����ʡ������
	}
	
	
	
	
	

//��ѯȫ����ʡ�����ȴ����ݿ�飬û���ڴӷ������ϲ�
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
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null,"province");
		}
		
	}

	
	
	
	//��ѯȫ����ʡ�����ȴ����ݿ�飬û���ڴӷ������ϲ�
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
	
	//��ѯȫ�����أ����ȴ����ݿ�飬û���ڴӷ������ϲ�
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
	
	
//���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯ����
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
					//ͨ��runOnUiThread���������ص����߳�
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
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}


	
	//��ʾ����
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	
	//�رս��ȶԻ���
	private void closeProgressDialog(){
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	
	
	
	//��ȡback�������ݵ�ǰ�������жϣ���ʱӦ�����ϼ��б����˳�
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
