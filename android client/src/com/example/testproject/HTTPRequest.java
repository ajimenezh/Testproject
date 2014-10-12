package com.example.testproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


public class HTTPRequest{
	
	public static final Integer CREATE_TOPIC_ID = 1;
	public static final Integer GET_TOPICS_ID = 2;
	public static final Integer UPDATE_TOPICS_ID = 3;
	public static final Integer REGISTER_USER_ID = 4;
	public static final Integer CREATE_COMMENT_ID = 5;
	public static final Integer GET_COMMENTS_ID = 6;
	private String url;
	private Integer fragmentContainer = null;
	private ActionBarActivity activity = null;
	private List<BasicNameValuePair> query;
	
	private OnEndRequestListener mCallback;
	private Integer typeRequest;
	private boolean isAsynchronous = true;
	
	public HTTPRequest(ActionBarActivity ctx) {	
		activity = ctx;
		
		setCallback();
	}
	
	public HTTPRequest(ActionBarActivity ctx, String url, List<BasicNameValuePair> query) {	
		activity = ctx;
		this.url = url;
		this.query = query;
		
		setCallback();
	}
	
	
	
	public HTTPRequest() {
		// TODO Auto-generated constructor stub
	}

	private void setCallback() {

        try {
            mCallback = (OnEndRequestListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
		
	}

	public void execute() {
		
		if (isAsynchronous) {
			(new GetRequest()).execute(url);
		}
		else {
			String response = makeRequest(url);
			
			if (mCallback != null) {
				mCallback.onRequest(response, typeRequest);
			}
		}
	}
	
	private String makeRequest(String url) {
		
		InputStream is = null;
		String response = null;

		try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;           
            
            if(!url.endsWith("?"))
                url += "?";
            
            List<BasicNameValuePair> params = query;

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

	public void setProgressBarContainer(Integer fragmentContainer) {
		this.fragmentContainer = fragmentContainer;
	}
		
	private class GetRequest extends AsyncTask<String, Void, Void> {
		
		String response = null;

		@Override
		protected Void doInBackground(String... params) {
						
			response = makeRequest(params[0]);
			
			return null;
		}
		
		@Override
		protected  void onPreExecute() {
			
			if (fragmentContainer != null) {
				
				ProgressBarFragment pbFragment = new ProgressBarFragment();

				activity.getSupportFragmentManager().beginTransaction()
		                .add(fragmentContainer, pbFragment).commit();
				
			}
			
		}
		
		@Override
		protected void onProgressUpdate(Void... params) {
	    }
		
		@Override
	    protected void onPostExecute(Void v) {
						
			if (typeRequest != 0) mCallback.onRequest(response, typeRequest);
			
	    }

	}
	
	// Container Activity must implement this interface
    public interface OnEndRequestListener {
        public void onRequest(String response, Integer typeRequest);

    }

	public void setRequestType(Integer typeRequest) {

		this.typeRequest = typeRequest;
		
	}

	public void setAsynchronous(boolean flag) {

		this.isAsynchronous  = false;
		
	}

	
}