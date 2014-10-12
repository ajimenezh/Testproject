package com.example.testproject;

import android.content.Context;
import android.widget.Toast;

public class CheckResponse {
	
	private Context mContext;


	public CheckResponse(Context mContext) {
		this.mContext = mContext;
	}
	
	public void execute(String response) {
		
		Toast toast1;
		if (response.substring(0,2).equals("FA")) {
			toast1 =
		            Toast.makeText(mContext,
		                    "Failed", Toast.LENGTH_SHORT);
		}
		else {
			toast1 =
		            Toast.makeText(mContext,
		                    "OK", Toast.LENGTH_SHORT);
		}
	 
	    toast1.show();
		
	}
	
}