package com.example.testproject;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends ActionBarActivity {
	
	private Intent mainIntent;
	private String PREFS_DIR = "MainPreferences";
	private String PREFS_ID_KEY = "UserId";
//	private long SPLASH_SCREEN_DELAY = 1*60*1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set parameters to show splash screen
        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		setContentView(R.layout.splash_screen);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		
		// First we have to check if we have login (sign up).
		// For this we store the id of the user in SharedPreferences.
		
		SharedPreferences prefs = getSharedPreferences(PREFS_DIR, Context.MODE_PRIVATE);
		String id = prefs.getString(PREFS_ID_KEY, "");
		
		if (id == null || id.length()==0) {
			
			// First Case: Not registered
			// We go to RegisterActivity
            mainIntent = new Intent().setClass(
                    MainActivity.this, RegisterActivity.class);
			
		}
		else {
			
			// Second Case: Registered
			// We go to HomeActivity
			mainIntent = new Intent().setClass(
                    MainActivity.this, HomeActivity.class);
		}
		
		if (mainIntent != null) {
			startActivity(mainIntent);
		}
		
//		TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
// 
//                // Start the next activity
//                Intent mainIntent = new Intent().setClass(
//                        MainActivity.this, MainActivity.class);
//                startActivity(mainIntent);
// 
//                // Close the activity so the user won't able to go back this
//                // activity pressing Back button
//                finish();
//            }
//        };
// 
//        // Simulate a long loading process on application startup.
//        Timer timer = new Timer();
//        timer.schedule(task, SPLASH_SCREEN_DELAY);
        
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
}
