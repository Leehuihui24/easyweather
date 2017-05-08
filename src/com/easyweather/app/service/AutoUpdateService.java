
package com.easyweather.app.service;

import com.easyweather.app.receiver.AutoUpdateReceiver;
import com.easyweather.app.util.HttpCallbackListener;
import com.easyweather.app.util.HttpUtil;
import com.easyweather.app.util.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public int onStratCommand(Intent intent, int flags, int startId){
		new Thread(new Runnable(){
			
			public void run(){
				updateWeather();
			}
		}).start();
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 60*60*1000*8;  //八小时的毫秒数
		long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
		Intent intent1 = new Intent(this, AutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent1, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime, pi);
		return  super.onStartCommand(intent1, flags, startId);
	}


	protected void updateWeather() {
		// TODO Auto-generated method stub
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = sharedPreferences.getString("weather_code", "");
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode +".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
			
			public void onFinish(String response){
				Utility.handlerWeatherResponse(AutoUpdateService.this, response);
			}
			
			public void onError(Exception e){
				e.printStackTrace();
			}
		});
		
	}

}


