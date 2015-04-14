package com.example.pedometer.model;



public class PedometerData{
	
	private int PID;
	private int paces;
	private float kilometers;
	private float calories;
	private String walkDate;
	
	
	public PedometerData(){}
	
	public PedometerData(int paces,float kilometers,float calories,String date){
		this.paces = paces;
		this.kilometers = kilometers;
		this.calories = calories;
		this.walkDate = date;
	}
	
	

	public int getPID() {
		return PID;
	}

	public void setPID(int pID) {
		PID = pID;
	}

	public String getWalkDate() {
		return walkDate;
	}

	public void setWalkDate(String walkDate) {
		this.walkDate = walkDate;
	}

	public int getPaces() {
		return paces;
	}
	public void setPaces(int paces) {
		this.paces = paces;
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
