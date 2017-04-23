package com.easyweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import com.easyweather.app.db.EasyWeatherDB;
import com.easyweather.app.model.City;
import com.easyweather.app.model.County;
import com.easyweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utility {
	static String TAG = "Utility";
	
	//解析处理服务器返回的省级数据
	public synchronized static boolean handleProvincesResponse(EasyWeatherDB easyWeatherDB, String response){
		if(!TextUtils.isEmpty(response)){
			Log.d("utility response", response);
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0){
				for(String p : allProvinces){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);//讲解析出来的数据存到Province表中
					easyWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	
	
	//解析和处理服务器返回的市级数据
	public static boolean handleCityesResponse(EasyWeatherDB easyWeatherDB,String response, int provinceId){
		if (!TextUtils.isEmpty(response)){
			Log.d("unility response", response);
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0){
				for (String c : allCities){
					String[] array = c.split("\\|");
					City city = new City();
					
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);//将解析出来的数据存到city表
					easyWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	
	//解析和处理服务器返回的县级数据
		public static boolean handleCountiseResponse(EasyWeatherDB easyWeatherDB,String response, int cityId){
			if (!TextUtils.isEmpty(response)){
				Log.d("utility response", response);
				String[] allCountise = response.split(",");
				if (allCountise != null && allCountise.length > 0){
					for (String c : allCountise){
						String[] array = c.split("\\|");
						County county = new County();
						
						county.setCountyCode(array[0]);
						county.setCountyName(array[1]);
						county.setCityId(cityId);//将解析出来的数据存到city表
						easyWeatherDB.saveCounty(county);
					}
					return true;
				}
			}
			return false;
		}
		
		
		
	
		
		
		
		
		
		
		
		
		
		//解析服务器传回来的数据，并存入数据库
		public static void handlerWeatherResponse(Context context, String response){
			try{
				JSONObject jsonObject = new JSONObject(response);
				JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
				String cityName = weatherInfo.getString("city");
				String weatherCode = weatherInfo.getString("cityId");
				String temp1 = weatherInfo.getString("temp1");
				String temp2 = weatherInfo.getString("temp2");
				String weatherDesp = weatherInfo.getString("weather");
				String publishTime = weatherInfo.getString("ptime");
				saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime );
				
			} catch (Exception e){
				//TODO: handle exception
			}
		}
		
		
		
		//将服务器返回的数据存储到shared preferences中
		private static void saveWeatherInfo(Context context, String cityName,
				String weatherCode, String temp1, String temp2,
				String weatherDesp, String publishTime) {
			// TODO Auto-generated method stub
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
			editor.putBoolean("city_selected", true);
			editor.putString("city_name", cityName);
			editor.putString("weather_Code", weatherCode);
			editor.putString("temp1", temp1);
			editor.putString("temp2", temp2);
			editor.putString("weather_Desp", weatherDesp);
			editor.putString("publish_time", publishTime);
			editor.putString("current_date", simpleDateFormat.format(new Date()));
			editor.commit();
			
			
		}

}
