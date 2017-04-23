package com.easyweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil {
	
	public static void sendHttpRequest(final String address, final HttpCallbackListener httpCallbackListener){
		new Thread(new Runnable(){
			
			public void run(){
				HttpURLConnection connection = null;
				try{
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader (new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null)
						response.append(line);
					Log.d("HttpUnil", response+"");
					if(httpCallbackListener != null){
						httpCallbackListener.onFinish(response.toString());//�ص�onFinish()����
					}
				}catch(Exception e){
					if (httpCallbackListener != null){
						httpCallbackListener.onError(e);//�ص�onError()����
					}
				}finally{
					if(connection != null){
						connection.disconnect();
					}
				}
			}
		}).start();
	}

}









