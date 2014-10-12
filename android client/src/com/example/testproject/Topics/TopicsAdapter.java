package com.example.testproject.Topics;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.example.testproject.HTTPRequest;
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
    	
    	final Button followBtn = (Button) rootView.findViewById(R.id.follow_btn);
    	
    	if (topics.get(position).following()) {
    		    		
    		followBtn.setText("Unfollow");
    		
    	}
    	
    	followBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				SharedPreferences prefs = context.getSharedPreferences(PREFS_DIR, Context.MODE_PRIVATE);
				String user_id = prefs.getString(PREFS_ID_KEY, "");
				
				List<BasicNameValuePair> query = new ArrayList<BasicNameValuePair>();
				
				query.add(new BasicNameValuePair("topic_id", topics.get(position).getId()));
				query.add(new BasicNameValuePair("user_id", user_id));
				
				String url = "http://192.168.1.34:5000/";

				if (topics.get(position).following()) {
					
					followBtn.setText("follow");
					topics.get(position).unfollow();
					
					url += "unfollow";
					
					prefs = context.getSharedPreferences(PREFS_FOLLOWS_DIR, Context.MODE_PRIVATE);
			        SharedPreferences.Editor prefEditor = prefs.edit();
			        prefEditor.putBoolean("topic_id=" + topics.get(position).getId(), false);
			        prefEditor.commit();
					
				}
				else {
					
					followBtn.setText("unfollow");
					topics.get(position).follow();
					
					url += "follow";
					
					prefs = context.getSharedPreferences(PREFS_FOLLOWS_DIR, Context.MODE_PRIVATE);
					SharedPreferences.Editor prefEditor = prefs.edit();
			        prefEditor.putBoolean("topic_id=" + topics.get(position).getId(), true);
			        prefEditor.commit();
					
				}
				
				HTTPRequest req = new HTTPRequest(null, url, query);
				req.setRequestType(0);
				req.execute();
				
			}
    		
    	});
       
        return rootView;
    }
}
