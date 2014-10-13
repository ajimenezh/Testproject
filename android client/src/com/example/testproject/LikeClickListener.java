package com.example.testproject;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;

import com.example.testproject.Topics.Topic;

public class LikeClickListener implements View.OnClickListener {
    	
	private Topic topic;
	private Button likeBtn;
	private Context context;
	
	private String PREFS_FOLLOWS_DIR = "LIKES";
	private String PREFS_DIR = "MainPreferences";
	private String PREFS_ID_KEY = "UserId";
	
	public LikeClickListener(Context context, Topic tp, Button btn) {
		this.topic = tp;
		this.likeBtn = btn;
		this.context = context;
	}
	
	public void setTopic(Topic tp) {
		this.topic = tp;
	}
	
	public void setButton(Button btn) {
		this.likeBtn = btn;
	}

	@Override
	public void onClick(View v) {
		
		SharedPreferences prefs = context.getSharedPreferences(PREFS_DIR, Context.MODE_PRIVATE);
		String user_id = prefs.getString(PREFS_ID_KEY, "");
		
		List<BasicNameValuePair> query = new ArrayList<BasicNameValuePair>();
		
		query.add(new BasicNameValuePair("topic_id", topic.getId()));
		query.add(new BasicNameValuePair("user_id", user_id));
		
		String url = "http://192.168.1.34:5000/";

		if (topic.likes()) {
			
			likeBtn.setText("Like");
			topic.unfollow();
			
			url += "unlike";
			
			prefs = context.getSharedPreferences(PREFS_FOLLOWS_DIR, Context.MODE_PRIVATE);
	        SharedPreferences.Editor prefEditor = prefs.edit();
	        prefEditor.putBoolean("topic_id=" + topic.getId(), false);
	        prefEditor.commit();
			
		}
		else {
			
			likeBtn.setText("Dislike");
			topic.follow();
			
			url += "like";
			
			prefs = context.getSharedPreferences(PREFS_FOLLOWS_DIR, Context.MODE_PRIVATE);
			SharedPreferences.Editor prefEditor = prefs.edit();
	        prefEditor.putBoolean("topic_id=" + topic.getId(), true);
	        prefEditor.commit();
			
		}
		
		HTTPRequest req = new HTTPRequest(null, url, query);
		req.setRequestType(0);
		req.execute();
		
	}
	
}