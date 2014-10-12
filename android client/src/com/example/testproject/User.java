package com.example.testproject;

public class User {
	
	private String username;
	private String user_id;
	
	public User(){
		
	}
	
	public User(String username, String user_id) {
		this.username = username;
		this.user_id = user_id;
	}

	public void setId(Integer id) {

		this.user_id = "" + id;
		
	}
	
}
