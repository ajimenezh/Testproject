package com.example.testproject;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testproject.Comments.Comment;
import com.example.testproject.Comments.CommentsAdapter;
import com.example.testproject.Comments.CreateCommentFragment;
import com.example.testproject.Topics.CreateTopicFragment;
import com.example.testproject.Topics.Topic;
import com.example.testproject.Topics.TopicsAdapter;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class TopicCommentsActivity extends ActionBarActivity
							implements CreateCommentFragment.OnSendListener, HTTPRequest.OnEndRequestListener {
	
	private String ADD_COMMENT_URL = "http://192.168.1.34:5000/add_comment";
	private String GET_COMMENTS_URL = "http://192.168.1.34:5000/get_comments";
	private String UPDATE_COMMENTS_URL = "http://192.168.1.34:5000/update_comments";;
	
	public static BaseAdapter adapter;
    
    public static ArrayList<Comment> comments = new ArrayList<Comment>();
    
    public Context mContext;
	private ActionBarActivity mActivity;
    
    private PseudoAjax ajaxCall;

    private Topic topic = null;
    
	@SuppressLint("JavascriptInterface")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments_layout);
		
		mContext = this;
		mActivity = this;
		
		topic = new Topic();

		
		Intent intent = getIntent();
									
		topic.setTitle(intent.getStringExtra("title"));
		topic.setId(intent.getStringExtra("topic_id"));

		comments.clear();
		
		adapter = new CommentsAdapter(this, comments);
		
		ListView myListView = (ListView) findViewById(R.id.list);
		
		View headerView = LayoutInflater.from(this).inflate(R.layout.comments_header_layout, null);
		
		TextView title = (TextView) headerView.findViewById(R.id.topic_title);
		title.setText(topic.getTitle());
		
		myListView.addHeaderView(headerView);
		
		myListView.setAdapter(adapter);
		
		Button addComment = (Button) findViewById(R.id.add);
		
		addComment.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				openCreateComment();
				
			}
		});
		
		List<BasicNameValuePair> query = new ArrayList<BasicNameValuePair>();
		
		query.add(new BasicNameValuePair("topic", "" + topic.getId()));
		
		Log.w("hello", "" + topic.getId());
		
		HTTPRequest req = new HTTPRequest(this, GET_COMMENTS_URL, query);
		req.setProgressBarContainer(R.id.frame_container);
		req.setRequestType(HTTPRequest.GET_COMMENTS_ID);
		req.execute();
		
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		// Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_create_topic:
	            openCreateComment();
	            return true;
	        case R.id.action_settings:
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }

	}

	/*
	 * Open a new Fragment to create a topic
	 */
	private void openCreateComment() {
		
		ListView myListView = (ListView) findViewById(R.id.list);
		myListView.setVisibility(View.GONE);
		Button addComment = (Button) findViewById(R.id.add);
		addComment.setVisibility(View.GONE);

		CreateCommentFragment firstFragment = new CreateCommentFragment();
		
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        // firstFragment.setArguments(getIntent().getExtras());
        
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_container, firstFragment).commit();

		
	}
	

	@Override
	public void onCommentCreated(String text) {

		if (text != null && text.length()>0) {
			
			List<BasicNameValuePair> query = new ArrayList<BasicNameValuePair>();
			
			query.add(new BasicNameValuePair("comment", text));
			query.add(new BasicNameValuePair("topic", "" + topic.getId()));
						
			HTTPRequest req = new HTTPRequest(this, ADD_COMMENT_URL, query);
			req.setProgressBarContainer(R.id.frame_container);
			req.setRequestType(HTTPRequest.CREATE_COMMENT_ID);
			req.execute();
			
		}
		
	}
	
	@Override
	public void onBackPressed() {
		
		// If the CreateTopicFragment is opened is not going to be NULL.
	    if (CreateTopicFragment.mFragment != null) {
			getSupportFragmentManager().beginTransaction().remove(CreateTopicFragment.mFragment).commit();
			
			ListView myListView = (ListView) findViewById(R.id.list);
			myListView.setVisibility(View.VISIBLE);
			Button addComment = (Button) findViewById(R.id.add);
			addComment.setVisibility(View.VISIBLE);
			
	        return;
	    }

	    // Otherwise defer to system default behavior.
	    super.onBackPressed();
	}


	@Override
	public void onRequest(String response, Integer typeRequest) {
		
				
		if (typeRequest == HTTPRequest.CREATE_COMMENT_ID) {
			
			CheckResponse checker = new CheckResponse(mContext);
			
			checker.execute(response);
			
			if (ProgressBarFragment.mFragment != null) {
				getSupportFragmentManager().beginTransaction().remove(ProgressBarFragment.mFragment).commit();
			}
			
			ListView myListView = (ListView) findViewById(R.id.list);
			myListView.setVisibility(View.VISIBLE);
			Button addComment = (Button) findViewById(R.id.add);
			addComment.setVisibility(View.VISIBLE);
			
		}
		
		if (typeRequest == HTTPRequest.GET_COMMENTS_ID) {
			
			refreshList(response);
			
//			if (ajaxCall == null || ajaxCall.isCancelled()) {
//				ajaxCall = new PseudoAjax();
//				ajaxCall.execute();
//			}
			
			if (ProgressBarFragment.mFragment != null) {
				getSupportFragmentManager().beginTransaction().remove(ProgressBarFragment.mFragment).commit();
			}
			
			ListView myListView = (ListView) findViewById(R.id.list);
			myListView.setVisibility(View.VISIBLE);
			Button addComment = (Button) findViewById(R.id.add);
			addComment.setVisibility(View.VISIBLE);
			
		}

	}

	private void refreshList(String response) {
		
		comments = new ArrayList<Comment>();

		try {
			JSONArray arr = (JSONArray) (new JSONObject(response)).get("results");
			
			for (int i=0; i<arr.length(); i++) {
				
				JSONObject obj = (JSONObject) arr.get(i);
				
				Comment cm = new Comment();
				cm.setText(obj.getString("text"));
				
				comments.add(cm);
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		adapter = new CommentsAdapter(this, comments);
		
		ListView myListView = (ListView) findViewById(R.id.list);
		
		myListView.setAdapter(adapter);
		
	}
	
	private class PseudoAjax extends AsyncTask<String, Void, Void> {


		@Override
		protected Void doInBackground(String... params) {

			int k = 0;
			while (!isCancelled()) {
				try {
					Thread.sleep(20000);
										
					HTTPRequest req = new HTTPRequest(mActivity, UPDATE_COMMENTS_URL, null);
					req.setProgressBarContainer(R.id.frame_container);
					req.setRequestType(HTTPRequest.UPDATE_TOPICS_ID);
					req.setAsynchronous(false);
					req.execute();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} 
				
				k++;
				
				if (k==3) break;
			}
			
			return null;
		}
		
		@Override
		protected  void onPreExecute() {

			
		}
		
		@Override
		protected void onProgressUpdate(Void... params) {
	    }
		
		@Override
	    protected void onPostExecute(Void v) {

			
	    }

	}
	
	@Override
    protected void onDestroy() {
		if (ajaxCall != null)
			ajaxCall.cancel(true);
	    
	    super.onDestroy();
	}
	
	@Override
    protected void onStop() {
		if (ajaxCall != null)
			ajaxCall.cancel(true);
	    
	    super.onStop();
	}
	
	@Override
    protected void onStart() {
		if (ajaxCall == null || ajaxCall.isCancelled()) {
			ajaxCall = new PseudoAjax();
			ajaxCall.execute();
		}
	    
	    super.onStart();
	}


}
