package com.example.pedometer.model;

import java.io.Serializable;


public class User {

	private int UID;
	private String name;
	private String passwd;   

	private String sex;
	private String email;
	private int weight;
	private int sensitivity;
	private int step_length;
	
	private byte[] pic;
	private int groupId;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public int getSensitivity() {
		return sensitivity;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setSensitivity(int sensitivity) {
		this.sensitivity = sensitivity;
	}
	public int getStep_length() {
		return step_length;
	}
	public void setStep_length(int step_length) {
		this.step_length = step_length;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public byte[] getPic() {
		return pic;
	}
	public void setPic(byte[] pic) {
		this.pic = pic;
	}
	
	public int getUID() {
		return UID;
	}
	public void setUID(int uID) {
		UID = uID;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	
}
