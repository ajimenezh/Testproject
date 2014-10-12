package com.example.testproject.Topics;

import com.example.testproject.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class CreateTopicFragment extends Fragment {
	
	private Button sendBtn;
	private EditText topicEdit;
	
	public static Fragment mFragment;
	
	private OnSendListener mCallback;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.create_topic_layout, container, false);
        
    }
    
	public void onActivityCreated (Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		sendBtn = (Button) getView().findViewById(R.id.send);
		topicEdit = (EditText) getView().findViewById(R.id.topic_title_edit);
		
		mFragment = this;
		
		sendBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String topicName = (topicEdit.getEditableText()).toString();

				mCallback.onTopicCreated(topicName);
				getActivity().getSupportFragmentManager().beginTransaction().remove(mFragment).commit();
				
			}
		});
		
	}
	
	// Container Activity must implement this interface
    public interface OnSendListener {
        public void onTopicCreated(String text);
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

	
}