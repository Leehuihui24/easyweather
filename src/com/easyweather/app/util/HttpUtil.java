package com.easyweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil {
	
	public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
		new Thread(new Runnable(){
			
			public void run(){
				HttpURLConnection connection = null;
				try{
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					InputStream inputStream = connection.getInputStream();
					BufferedReader reader = new BufferedReader (new InputStreamReader(inputStream));
					StringBuilder response = new StringBuilder();
					String line;
					while((line = reader.readLine()) != null)
						response.append(line);
					Log.d("HttpUnil", response+"");
					if(listener != null)
						listener.onFinish(response.toString());//�ص�onFinish()����
					
				}catch(Exception e){
					if (listener != null)
						listener.onError(e);//�ص�onError()����
					
				}finally{
					if(connection != null)
						connection.disconnect();
					
				}
			}
		}).start();
	}

}









