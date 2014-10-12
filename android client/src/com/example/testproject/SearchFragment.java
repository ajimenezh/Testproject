package com.example.testproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.example.testproject.Topics.Topic;
import com.example.testproject.Topics.TopicsAdapter;
import com.example.testproject.Topics.CreateTopicFragment.OnSendListener;

public class SearchFragment extends Fragment {
	
	public static Fragment mFragment;
	private EditText searchEdit;
	
	private String SEARCH_QUERY_URL = "http://192.168.1.34:5000/search";
	
	public static BaseAdapter adapter;
    
    public static ArrayList<SearchResult> results = new ArrayList<SearchResult>();
	
	private OnSendListener mCallback;
	
	public static SearchQueryRequest req = null;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.search_layout, container, false);
        
    }
    
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		adapter = new SearchResultsAdapter(HomeActivity.mContext, results);
		
		ListView myListView = (ListView) getView().findViewById(R.id.search_results);
		
		myListView.setAdapter(adapter);
		
		myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Create the text message with a string
				Intent mainIntent = new Intent().setClass(
	                    HomeActivity.mContext, TopicCommentsActivity.class);
				
				mainIntent.putExtra("topic_id", results.get(position).getTopic().getId());
				mainIntent.putExtra("title", results.get(position).getTopic().getTitle());
				
				// Verify that the intent will resolve to an activity
				if (mainIntent.resolveActivity(HomeActivity.mContext.getPackageManager()) != null) {
				    startActivity(mainIntent);
				}
				
			} 
		});
				
		mFragment = this;
		
		searchEdit = (EditText) getView().findViewById(R.id.search_query);
		
		searchEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				
				String text = s.toString();
				
				if (req != null) {
					req.cancel(true);
				}
				
				req = new SearchQueryRequest();
				
				req.execute(text);

				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
	       });
		
	}
	
	private class SearchQueryRequest extends AsyncTask<String, Void, Void> {
		
		String response = null;
		String text;

		@Override
		protected Void doInBackground(String... params) {
			
			text = params[0];
			
			response = makeRequest(params[0]);
			
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
			
			if (!isCancelled())
				refreshList(response, text);
			
	    }

	}
	
	// Container Activity must implement this interface
    public interface OnSendListener {
        public void onSearchPerformed(String text);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnSendListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

	public void refreshList(String response, String text) {
		
		results = new ArrayList<SearchResult>();
		
		SearchResult header = new SearchResult(text);
		
		results.add(header);
		
		try {
			JSONArray arr = (JSONArray) (new JSONObject(response)).get("results");
			
			for (int i=0; i<arr.length(); i++) {
								
				SearchResult tp = new SearchResult();
				
				JSONObject obj = (JSONObject) arr.get(i);
				
				if (obj != null) {
									
					if (obj.getString("type").equals("topic")) {
						
						tp.setText((String) obj.get("topic"));
						
						Topic tmp = new Topic();
						
						tmp.setId((String) obj.get("topic_id"));
						tmp.setTitle((String) obj.get("topic"));
						
						tp.setTopic(tmp);
						
					}
					if (obj.getString("type").equals("user")) {
						
						tp.setText((String) obj.get("user"));
						
						User tmp = new User();
						
						tmp.setId((Integer) obj.get("user_id"));
						
						tp.setUser(tmp);
						
					}
									
					results.add(tp);
				}
				
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		adapter = new SearchResultsAdapter(HomeActivity.mContext, results);
		
		ListView myListView = (ListView) getView().findViewById(R.id.search_results);
		
		myListView.setAdapter(adapter);
		
	}

	public String makeRequest(String param) {
		InputStream is = null;
		String response = null;
		
		String url = SEARCH_QUERY_URL;

		try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;           
            
            if(!url.endsWith("?"))
                url += "?";
            
            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            
            params.add(new BasicNameValuePair("query", param));

            if (params != null) {
            	String paramString = URLEncodedUtils.format(params, "utf-8");

                url += paramString;
            }

            HttpGet httpGet = new HttpGet(url);

            httpResponse = httpClient.execute(httpGet);

            httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            response = sb.toString();
            
            
        } catch (Exception e) {
            Log.e("Buffer Error", "Error: " + e.toString());
        }
                
        return response;
	}

	public static void cancelRequest() {

		req.cancel(true);
		
	}

	
}