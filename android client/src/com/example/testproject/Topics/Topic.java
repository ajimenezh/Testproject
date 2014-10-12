package com.example.testproject.Topics;

public class Topic{
	
	private String title;
	private String topicJSON;
	private String topicId;
	private boolean following = false;
	
	public Topic() {
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}

	public void setJSON(String topicJSON) {

		this.topicJSON = topicJSON;
		
	}
	
	public String getJSON() {

		return this.topicJSON;
		
	}

	public String getId() {
		return this.topicId;
	}
	
	public void setId(String string) {
		this.topicId = string;
	}
	
	public void follow() {
		following = true;
	}
	
	public boolean following() {
		return this.following;
	}

	public void unfollow() {

		following = false;
		
	}
	
}