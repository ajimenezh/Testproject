package com.example.testproject.Comments;

import java.io.Console;
import java.util.Date;
import java.util.List;

import com.example.testproject.R;
import com.example.testproject.Topics.Topic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class CommentsAdapter extends BaseAdapter {


    private List<Comment> comments;
    private Context context;


    public CommentsAdapter(Context context, List<Comment> comments){
        this.comments = comments;
        this.context = context;
    }


    @Override
    public int getCount() {
        return comments.size();
    }


    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 100;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	View rootView = LayoutInflater.from(context).inflate(R.layout.comment_layout, parent, false);

    	TextView title = (TextView) rootView.findViewById(R.id.topic_title);
    	
    	title.setText(comments.get(position).getText());
       
        return rootView;
    }
}
