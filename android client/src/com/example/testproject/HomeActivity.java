package com.example.testproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testproject.Topics.CreateTopicFragment;
import com.example.testproject.Topics.Topic;
import com.example.testproject.Topics.TopicsAdapter;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public class HomeActivity extends ActionBarActivity
							implements CreateTopicFragment.OnSendListener, HTTPRequest.OnEndRequestListener, SearchFragment.OnSendListener {
	
	private String ADD_TOPIC_URL = "http://192.168.1.34:5000/add_topic";
	private String GET_TOPICS_URL = "http://192.168.1.34:5000/get_topics";
	private String UPDATE_TOPICS_URL = "http://192.168.1.34:5000/update_topics";
	private String REGISTER_GCM_URL = "http://192.168.1.34:5000/register_gcm";
	
	private String PREFS_DIR = "FOLLOWS";
	private String PREFS_ID_KEY = "TopicId";
	private String PREFS_DIR2 = "MainPreferences";
	private String PREFS_ID_KEY2 = "UserId";
	
	public static BaseAdapter adapter;
    
    public static ArrayList<Topic> topics = new ArrayList<Topic>();
    
    public static Context mContext;
	private ActionBarActivity mActivity;
    
    private PseudoAjax ajaxCall;
	private boolean topicsLoaded = false;
	
	private String user_id;

	@SuppressLint("JavascriptInterface")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_layout);
		
		mContext = this;
		mActivity = this;
		
		
		SharedPreferences prefs = getSharedPreferences(PREFS_DIR2, Context.MODE_PRIVATE);
		user_id = prefs.getString(PREFS_ID_KEY2, "");
		
 
        String regid = getGcmId();
 
        if (regid.equals("")) {
            (new RegisterGCM()).execute();
        }

//		final Context myApp = this;
//
//		/* An instance of this class will be registered as a JavaScript interface */
//		class MyJavaScriptInterface
//		{
//		    @SuppressWarnings("unused")
//		    public void processHTML(String html)
//		    {
//		        Log.w("helo", html);
//		    }
//		}
//
//		final WebView browser = (WebView)findViewById(R.id.webView);
//		/* JavaScript must be enabled if you want it to work, obviously */
//		browser.getSettings().setJavaScriptEnabled(true);
//
//		/* Register a new JavaScript interface called HTMLOUT */
//		browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
//
//		/* WebViewClient must be set BEFORE calling loadUrl! */
//		browser.setWebViewClient(new WebViewClient() {
//		    @Override
//		    public void onPageFinished(WebView view, String url)
//		    {
//		        /* This call inject JavaScript into the page which just finished loading. */
//		        // browser.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//		    }
//		});
//
//		/* load a web page */
//		browser.loadUrl("http://192.168.1.34:5000/test");
		
		topics.clear();
		
		adapter = new TopicsAdapter(this, topics);
		
		ListView myListView = (ListView) findViewById(R.id.list);
		
		myListView.setAdapter(adapter);
		
		HTTPRequest req = new HTTPRequest(this, GET_TOPICS_URL, null);
		req.setProgressBarContainer(R.id.frame_container);
		req.setRequestType(HTTPRequest.GET_TOPICS_ID);
		req.execute();
		
		myListView.setFocusable(true);
		myListView.setFocusableInTouchMode(true);
		myListView.setClickable(true);
		 
		myListView.setOnItemClickListener(new OnItemClickListener() {
			

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				// Create the text message with a string
				Intent mainIntent = new Intent().setClass(
	                    HomeActivity.this, TopicCommentsActivity.class);
				
				mainIntent.putExtra("topic_id", topics.get(position).getId());
				mainIntent.putExtra("title", topics.get(position).getTitle());
				
				// Verify that the intent will resolve to an activity
				if (mainIntent.resolveActivity(getPackageManager()) != null) {
				    startActivity(mainIntent);
				}
				
			} 
		});
        
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
	    		if (SearchFragment.mFragment != null) {
	    	    	if (SearchFragment.req != null) {
	    	    		SearchFragment.cancelRequest();
	    			}
	    	    	try {getSupportFragmentManager().beginTransaction().remove(SearchFragment.mFragment).commit();}finally{}
	    	    }
	            if (topicsLoaded) openCreateTopic();
	            return true;
	        case R.id.action_search:
	    		if (CreateTopicFragment.mFragment != null) {
	    	    	try {getSupportFragmentManager().beginTransaction().remove(CreateTopicFragment.mFragment).commit();}finally{}
	    	    }
	        	if (topicsLoaded) openSearch();
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
	private void openCreateTopic() {
		
		ListView myListView = (ListView) findViewById(R.id.list);
		myListView.setVisibility(View.GONE);
		FrameLayout frame = (FrameLayout) findViewById(R.id.frame_container);
		frame.setVisibility(View.VISIBLE);

		CreateTopicFragment firstFragment = new CreateTopicFragment(); 
		
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        // firstFragment.setArguments(getIntent().getExtras());
        
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_container, firstFragment).commit();

		
	}
	
	/*
	 * Open a new Fragment to perform a search
	 */
	private void openSearch() {

		ListView myListView = (ListView) findViewById(R.id.list);
		myListView.setVisibility(View.GONE);

		SearchFragment firstFragment = new SearchFragment();
		
        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        // firstFragment.setArguments(getIntent().getExtras());
        
        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_container, firstFragment).commit();

		
	}
	

	@Override
	public void onTopicCreated(String text) {

		if (text != null && text.length()>0) {
			
			List<BasicNameValuePair> query = new ArrayList<BasicNameValuePair>();
			
			query.add(new BasicNameValuePair("topictitle", text));
						
			HTTPRequest req = new HTTPRequest(this, ADD_TOPIC_URL, query);
			req.setProgressBarContainer(R.id.frame_container);
			req.setRequestType(HTTPRequest.CREATE_TOPIC_ID);
			req.execute();
			
		}
		
	}
	
	@Override
	public void onBackPressed() {
		
		boolean done = false;
		
		// If the CreateTopicFragment is opened is not going to be NULL.
	    if (CreateTopicFragment.mFragment != null) {
	    	try {
	    		getSupportFragmentManager().beginTransaction().remove(CreateTopicFragment.mFragment).commit();
	    	}
	    	finally{
	    		
	    	}
			
			ListView myListView = (ListView) findViewById(R.id.list);
			myListView.setVisibility(View.VISIBLE);
			
	        done = true;
	    }
	    if (SearchFragment.mFragment != null) {
	    	
	    	if (SearchFragment.req != null) {
	    		SearchFragment.cancelRequest();
			}
	    	
	    	try {
	    		getSupportFragmentManager().beginTransaction().remove(SearchFragment.mFragment).commit();
	    	}
	    	finally{
	    		
	    	}
			
			ListView myListView = (ListView) findViewById(R.id.list);
			myListView.setVisibility(View.VISIBLE);
			
	        done = true;
	    }
	    if (done) {
	    	FrameLayout frame = (FrameLayout) findViewById(R.id.frame_container);
			frame.setVisibility(View.GONE);
	    }
	    if (done) return;

	    // Otherwise defer to system default behavior.
	    super.onBackPressed();
	}


	@Override
	public void onRequest(String response, Integer typeRequest) {
		
			
		if (typeRequest == HTTPRequest.CREATE_TOPIC_ID) {
			
			CheckResponse checker = new CheckResponse(mContext);
			
			checker.execute(response);
			
			if (ProgressBarFragment.mFragment != null) {
				getSupportFragmentManager().beginTransaction().remove(ProgressBarFragment.mFragment).commit();
			}
			
			ListView myListView = (ListView) findViewById(R.id.list);
			myListView.setVisibility(View.VISIBLE);
			
		}
		if (typeRequest == HTTPRequest.GET_TOPICS_ID) {
			
			refreshList(response);
			
			if (ajaxCall == null || ajaxCall.isCancelled()) {
				ajaxCall = new PseudoAjax();
				ajaxCall.execute();
			}
			
			if (ProgressBarFragment.mFragment != null) {
				getSupportFragmentManager().beginTransaction().remove(ProgressBarFragment.mFragment).commit();
			}
			
			ListView myListView = (ListView) findViewById(R.id.list);
			myListView.setVisibility(View.VISIBLE);
			
			topicsLoaded  = true;
						
		}
		if (typeRequest == HTTPRequest.UPDATE_TOPICS_ID) {
			
			
			
		}
	}

	private void refreshList(String response) {
		
		topics = new ArrayList<Topic>();

		try {
			JSONArray arr = (JSONArray) (new JSONObject(response)).get("results");
			
			for (int i=0; i<arr.length(); i++) {
				
				JSONObject obj = (JSONObject) arr.get(i);
				
				Topic tp = new Topic();
				tp.setTitle(obj.getString("title"));
				tp.setId(obj.getString("topic_id"));
				tp.setJSON(obj.toString());
				
				SharedPreferences prefs = getSharedPreferences(PREFS_DIR, Context.MODE_PRIVATE);
				boolean following = prefs.getBoolean("topic_id=" + tp.getId(), false);
				
				if (following) tp.follow();
				
				topics.add(tp);
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		adapter = new TopicsAdapter(this, topics);
		
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
										
					HTTPRequest req = new HTTPRequest(mActivity, UPDATE_TOPICS_URL, null);
					req.setProgressBarContainer(R.id.frame_container);
					req.setRequestType(HTTPRequest.UPDATE_TOPICS_ID);
					req.setAsynchronous(false);
					req.execute();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				} 

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

	@Override
	public void onSearchPerformed(String text) {
		// TODO Auto-generated method stub
		
	}
	
	private String getGcmId() {
		
	    SharedPreferences prefs = getSharedPreferences("GCM_ID", Context.MODE_PRIVATE);
	 
	    String registrationId = prefs.getString("USER_GCM_ID", "");
	 
	    if (registrationId.length() == 0) {
	        return "";
	    }
	 
	    return registrationId;
	}
	
	private class RegisterGCM extends AsyncTask<String,Integer,String>
	{
	    @Override
	        protected String doInBackground(String... params) {
	            String msg = "";
	 
	            try {
	            	
	            	Log.w("hello", user_id);
	            	Log.w("hello", "hola  " + user_id);
	            	
	            	GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);
	 
	                String regid = gcm.register("454154412738");
	                
	                Log.w("hello", regid);
	 
	                List<BasicNameValuePair> query = new ArrayList<BasicNameValuePair>();
	    			
	    			query.add(new BasicNameValuePair("gcm_id", regid));
	    			query.add(new BasicNameValuePair("user_id", user_id));
	    						
	    			HTTPRequest req = new HTTPRequest(null, REGISTER_GCM_URL, query);
	    			req.setRequestType(0);
	    			req.execute();
	 
	                SharedPreferences prefs = getSharedPreferences(
	                	    "GCM_ID",
	                	        Context.MODE_PRIVATE);
			        SharedPreferences.Editor prefEditor = prefs.edit();
			        prefEditor.putString("USER_GCM_ID", regid);
			        prefEditor.commit();
	            }
	            catch (IOException ex) {
	            }
	 
	            return msg;
	        }
	}


}
