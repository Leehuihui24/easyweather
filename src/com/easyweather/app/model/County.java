package com.easyweather.app.model;

public class County {
	private int id;
	private String countyName;
	private String CountyCode;
	private int cityId;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCountyName() {
		return countyName;
	}
	
	public void setCountyName(String countyName) {
		this.countyName = countyName;
	}
	
	public String getCountyCode() {
		return CountyCode;
	}
	
	public void setCountyCode(String countyCode) {
		this.CountyCode = countyCode;
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

}
