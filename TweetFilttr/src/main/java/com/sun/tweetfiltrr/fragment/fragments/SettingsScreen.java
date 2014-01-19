package com.sun.tweetfiltrr.fragment.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.utils.TwitterConstants;


public class SettingsScreen extends SherlockFragmentActivity{
    private SharedPreferences _sharedPreferences;
	private SharedPreferences.Editor _editor;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.settings);

		ToggleButton noGroupToggle = (ToggleButton) findViewById(R.id.look_for_all_tweets_if_no_group_results);
		ToggleButton noResultToggle = (ToggleButton) findViewById(R.id.look_for_all_tweets_if_no_result_from_filter);

		_sharedPreferences   = PreferenceManager
					.getDefaultSharedPreferences(this);
		
		noGroupToggle.setChecked(_sharedPreferences.getBoolean(TwitterConstants.SHOULD_LOOK_IF_NO_GROUP, false));
		noResultToggle.setChecked(_sharedPreferences.getBoolean(TwitterConstants.SHOULD_LOOK_IF_NO_RESULTS, false));

		noGroupToggle.setOnClickListener(getToggleStateLis(TwitterConstants.SHOULD_LOOK_IF_NO_GROUP));
		noResultToggle.setOnClickListener(getToggleStateLis(TwitterConstants.SHOULD_LOOK_IF_NO_RESULTS));

	}
	
	private OnClickListener getToggleStateLis(final String const_){
		return new OnClickListener() {
			@Override
			public void onClick(View v) {
				SharedPreferences.Editor editor = _sharedPreferences.edit();
				ToggleButton toggle = (ToggleButton) v;
				editor.putBoolean(const_, toggle.isChecked());
				editor.commit();
			}
		};
	}
	
}
