package com.example.testproject;

import java.util.List;

import com.example.testproject.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class SearchResultsAdapter extends BaseAdapter {


    private List<SearchResult> results;
    private Context context;


    public SearchResultsAdapter(Context context, List<SearchResult> results){
        this.results = results;
        this.context = context;
    }


    @Override
    public int getCount() {
        return results.size();
    }


    @Override
    public Object getItem(int position) {
        return results.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 100;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	View rootView = LayoutInflater.from(context).inflate(R.layout.topic_layout, parent, false);
    	
    	if (position == 0) {
    		
    		String text = "Search...  " + results.get(position).getText();
    		
    		TextView title = (TextView) rootView.findViewById(R.id.topic_title);
        	
        	title.setText(text);
    		
    	}
    	else {
    		
    		String text = results.get(position).getText();
    		
    		TextView title = (TextView) rootView.findViewById(R.id.topic_title);
        	
        	title.setText(text);
        	
    	}
    	
        return rootView;
    }
}
