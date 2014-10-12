package com.example.testproject;

import com.example.testproject.Topics.Topic;

public class SearchResult{
	private boolean isTopic = false;
	private boolean isUser = false;
	private Topic topic;
	private User user;
	private String text;
	
	public SearchResult() {
		
	}
	
	public SearchResult(String text) {
		
		this.text = text;
		
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void setTopic(Topic topic) {
		this.isUser = false;
		this.isTopic = true;
		
		this.topic = topic;
		
	}
	
	public void setUser(User user) {
		this.isUser = true;
		this.isTopic = false;
		
		this.user = user;
		
	}
	
	public boolean isTopic() {
		return this.isTopic;
	}
	
	public boolean isUser() {
		return this.isUser;
	}
	
	public Topic getTopic() {
		return this.topic;
	}
	
	public User getuser() {
		return this.user;
	}
	
	
	public String getText() {
		return this.text;
	}
	
}