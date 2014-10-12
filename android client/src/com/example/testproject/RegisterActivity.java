package com.example.testproject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.testproject.Topics.CreateTopicFragment;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class RegisterActivity extends ActionBarActivity
								implements HTTPRequest.OnEndRequestListener {
	
	private Button sendBtn;
	private EditText usernameText;
	
	private static final String SERVER_IP = "192.168.1.34";
	private static final int SERVERPORT = 8000;
	private String REGISTER_USER_URL = "http://192.168.1.34:5000/register_user";
	private Socket socket;
	
	private String PREFS_DIR = "MainPreferences";
	private String PREFS_ID_KEY = "UserId";
	private String PREFS_ID_USERNAME = "Username";
	
	private Context mContext;
	private ActionBarActivity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_layout);
		
		mContext = this;
		mActivity = this;

		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		
		sendBtn = (Button) findViewById(R.id.send);
		usernameText = (EditText) findViewById(R.id.userName);
		
		// In order to ensure the uniqueness of the usernames
		// fast, we stablish a connection to the server, and check constantly the 
		// text written so far
		new Thread(new UserNameChecker()).start();
        
	}
	
	class UserNameChecker implements Runnable {

		@Override
		public void run() {

			InetAddress serverAddr;
			// The user has pressed the send button
			sendBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {

					Editable editText = usernameText.getText();
					
					String userName = editText.toString();
					
					// We have to check first if the username is valid before 
					// making a request to the server
					// Here we check is the text is not empty
					if (userName == null || userName.length() == 0) {
						
						Toast toast1 =
					            Toast.makeText(getApplicationContext(),
					            		R.string.invalid_username_empty, Toast.LENGTH_SHORT);
					 
					        toast1.show();
						
					}
					else {
						
						// If the username is not empty, we send a request to the server
						// to register.
						
						// We use redis to check fast if the name exist, if it does not 
						// exist in the redis hash, we try to insert it in the database.
						// Because a collision may still exist we have to wait to the response
						// also in this case.
						
						Log.w("hola", userName);
						
						List<BasicNameValuePair> query = new ArrayList<BasicNameValuePair>();
						
						query.add(new BasicNameValuePair("username", userName));
									
						HTTPRequest req = new HTTPRequest(mActivity, REGISTER_USER_URL, query);
						req.setProgressBarContainer(R.id.frame_container);
						req.setRequestType(HTTPRequest.REGISTER_USER_ID);
						req.execute();
						
//							Log.w("Write", userName);
//							
//							byte[] message = userName.toString().getBytes();
//							OutputStream socketOutputStream;
//							try {
//								socketOutputStream = socket.getOutputStream();
//								socketOutputStream.write(message);
//								
//								Date clock = new Date();
//								
//								long startTime = clock.getTime();
//								
//								Log.w("Start", "--------------");
//								
//								for (;;) {
//									Log.w("Clock", "" + (new Date()).getTime() + "  :  " + startTime + "  --->  " + ((new Date()).getTime() - startTime));
//									
//									if ((new Date()).getTime() - startTime > 5000) break;
//									
//									BufferedInputStream inputS = new BufferedInputStream(socket.getInputStream());
//									byte[] buffer = new byte[1024];    //If you handle larger data use a bigger buffer size
//									int read;
//									while((read = inputS.read(buffer)) != -1) {
//										
//										if ((new Date()).getTime() - startTime > 5000) break;
//
//										Log.w("read", "" + read + "   " + buffer.toString());
//										
//									}
//
//								}
//								
//								Log.w("End", "--------------");
//								
//								
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						
						
					}
					
					
				}
			});
		
			
		}
		
	}
	
	class RequestTask extends AsyncTask<String, String, String>{

	    @Override
	    protected String doInBackground(String... uri) {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(uri[0]));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	            //TODO Handle problems..
	        } catch (IOException e) {
	            //TODO Handle problems..
	        }
	        return responseString;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        //Do anything with response..
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRequest(String response, Integer typeRequest) {

		if (typeRequest == HTTPRequest.REGISTER_USER_ID) {
			
			if (ProgressBarFragment.mFragment != null) {
				getSupportFragmentManager().beginTransaction().remove(ProgressBarFragment.mFragment).commit();
			}
			
			if (response.substring(0, 4).equals("Fail")) {
			
				Toast toast1 =
			            Toast.makeText(getApplicationContext(),
			            		R.string.username_in_use, Toast.LENGTH_SHORT);
			 
			        toast1.show();
			}
			else {
				
				try {
					JSONObject obj = new JSONObject(response);
					
					String user_id = obj.getString("user_id");
					String username = obj.getString("username");
					
					SharedPreferences prefs = getSharedPreferences(PREFS_DIR, Context.MODE_PRIVATE);
			        SharedPreferences.Editor prefEditor = prefs.edit();
			        prefEditor.putString(PREFS_ID_USERNAME, username);
			        prefEditor.putString(PREFS_ID_KEY, user_id);
			        prefEditor.commit();
			        
			        Intent mainIntent = new Intent().setClass(
		                    RegisterActivity.this, HomeActivity.class);
			        
			        startActivity(mainIntent);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
		}
		
	}

}