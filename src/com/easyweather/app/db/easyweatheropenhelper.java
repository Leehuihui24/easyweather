package com.easyweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class easyweatheropenhelper extends SQLiteOpenHelper {
	
	public easyweatheropenhelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
 
	
	
	//province表建表语句
	public static final String CREATE_PROVINCE = "create table Province ("
			+ "id integer Primary key autoincrement, "
			+ "province_name text, "
			+ "province_code text) ";

	
	//city表建表语句
	public static final String CREATE_CITY = "create table City ( "
			+ "id integer primary key autoincrement, "
			+ "city_name text, "
			+ "city_code text, "
			+ "province_id integer) ";
	 
	
	//county表建表语句
	public static final String CREATE_COUNTY = "create table Country ("
			+ "id integer primary key autoincrement"
			+ "county_name text,"
			+ "county_code text,"
			+ "city_id integer) ";
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
