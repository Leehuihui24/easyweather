package com.easyweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.easyweather.app.model.City;
import com.easyweather.app.model.County;
import com.easyweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class easyweatherDB {
	
	public static final String DB_NAME = "easyweather";//�������ݿ���
	
	public static final int VERSION = 1;//���ݿ�汾
	private static easyweatherDB easyweatherDB;
	private SQLiteDatabase db;
	
	private easyweatherDB(Context context){//���췽��˽�л�
		easyweatheropenhelper dbHelper = new easyweatheropenhelper(context,DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	//��ȡeasyweatherʵ��
	public synchronized static easyweatherDB getInstance (Context context){
		if (easyweatherDB == null){
			easyweatherDB = new easyweatherDB(context);
		}
		return easyweatherDB;
	}
	
	
	
	
	
	
	//��provinceʵ���洢�����ݿ�
	public void saveProvince (Province province){
		if (province != null){
			ContentValues values = new ContentValues();
			values.put("province_name",province.getProvinceName());
			values.put("province_code",province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	//�����ݿ��ж�ȡȫ������ʡ����Ϣ
	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while (cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}return list;
	}
	
	
	
	
	
	
	//��cityʵ���洢�����ݿ�
	public void saveCity (City city){
		if (city != null){
			ContentValues values = new ContentValues();
			values.put("province_name",city.getCityName());
			values.put("province_code",city.getCityCode());
			db.insert("Province", null, values);
		}
	}

	//�����ݿ��ж�ȡĳʡ�����г�����Ϣ
	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("City", null,"province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
		if (cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while (cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}return list;
	}

	
	
	
	
	//��countyʵ���洢�����ݿ�
	public void saveCounty (County county){
		if (county != null){
			ContentValues values = new ContentValues();
			values.put("county_name",county.getCountyName());
			values.put("county_code",county.getCountyCode());
			db.insert("County", null, values);
		}
	}	

	//�����ݿ��ж�ȡĳ��������������Ϣ
	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query("County", null,"city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
		if (cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				list.add(county);
			}while (cursor.moveToNext());
		}
		if(cursor != null){
			cursor.close();
		}return list;
	}
	
}