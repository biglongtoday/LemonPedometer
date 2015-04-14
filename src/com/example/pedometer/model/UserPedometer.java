package com.example.pedometer.model;

public class UserPedometer {

	//user 个人当天的计步情况
	private int PID;
	private int UID;
	
	private int paces;	
	private float kilometers;
	private float calories;
	private String date;
	
	public int getUID() {
		return UID;
	}

	public void setUID(int uID) {
		UID = uID;
	}

	public int getPID() {
		return PID;
	}

	public void setPID(int pID) {
		PID = pID;
	}

	public int getPaces() {
		return paces;
	}

	public void setPaces(int paces) {
		this.paces = paces;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public float getKilometers() {
		return kilometers;
	}

	public void setKilometers(float kilometers) {
		this.kilometers = kilometers;
	}

	public float getCalories() {
		return calories;
	}

	public void setCalories(float calories) {
		this.calories = calories;
	}

	

	
}
