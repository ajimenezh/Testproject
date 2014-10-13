package com.example.testproject.Topics;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.example.testproject.HTTPRequest;
import com.example.testproject.FollowClickListener;
import com.example.testproject.LikeClickListener;
import com.example.testproject.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class TopicsAdapter extends BaseAdapter {
	
	private String PREFS_FOLLOWS_DIR = "FOLLOWS";
	private String PREFS_DIR = "MainPreferences";
	private String PREFS_ID_KEY = "UserId";


    private List<Topic> topics;
    private Context context;


    public TopicsAdapter(Context context, List<Topic> topics){
        this.topics = topics;
        this.context = context;
    }


    @Override
    public int getCount() {
        return topics.size();
    }


    @Override
    public Object getItem(int position) {
        return topics.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 100;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

    	View rootView = LayoutInflater.from(context).inflate(R.layout.topic_layout, parent, false);

    	TextView title = (TextView) rootView.findViewById(R.id.topic_title);
    	
    	title.setText(topics.get(position).getTitle());
    	
    	TextView numberComments = (TextView) rootView.findViewById(R.id.number_comments);
    	
    	numberComments.setText(topics.get(position).getNumberComments() + " comments");
    	
    	TextView numberLikes = (TextView) rootView.findViewById(R.id.number_likes);
    	
    	numberLikes.setText("+" + topics.get(position).getNumberLikes());
    	
    	final Button followBtn = (Button) rootView.findViewById(R.id.follow_btn);
    	
    	if (topics.get(position).following()) {
    		    		
    		followBtn.setText("Unfollow");
    		
    	}
    	
    	final Button likeBtn = (Button) rootView.findViewById(R.id.like_btn);
    	
    	if (topics.get(position).likes()) {
    		    		
    		likeBtn.setText("Dislike");
    		
    	}
    	
    	// Set the follow button custom click listener.
    	followBtn.setOnClickListener(new FollowClickListener(context, topics.get(position), followBtn));
    	
    	likeBtn.setOnClickListener(new LikeClickListener(context, topics.get(position), likeBtn));
    	
       
        return rootView;
    }
    
}
