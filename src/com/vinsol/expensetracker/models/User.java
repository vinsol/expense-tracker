package com.vinsol.expensetracker.models;

public class User {
	
	public User(String name, String email, String password) {
		this.name = name;
		this.email = email;
		this.password = password;
	}
	
	public String deviceId;
	public String idFromServer;
	public String name;
	public String email;
	public String password;
	public String token;
	
}
