package com.example.testproject.Topics;

public class Topic{
	
	private String title;
	private String topicJSON;
	private String topicId;
	private boolean following = false;
	private Integer numberComments = 0;
	private boolean likes = false;
	private int numberLikes = 0;
	
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
	
	public void setNumberComments(Integer n) {
		this.numberComments = n;
	}
	
	public Integer getNumberComments() {
		return this.numberComments;
	}

	public boolean likes() {
		return this.likes;
	}
	
	public void like() {
		this.likes  = true;
	}
	
	public void dislike() {
		this.likes = false;
	}

	public void setNumberLikes(int n) {

		this.numberLikes  = n;
		
	}
	
	public Integer getNumberLikes() {

		return this.numberLikes;
		
	}
	
}