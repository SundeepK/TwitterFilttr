package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sun.tweetfiltrr.R;

import java.util.List;

public class UserHomeTweetArrayAdapter extends ArrayAdapter<UserTwitterDetails> {

	List<UserTwitterDetails> _currentUser; 
	int _userTweetsListView;
	private static final String TAG = UserHomeTweetArrayAdapter.class.getName();
	
	public UserHomeTweetArrayAdapter(Context context,
			int userTweetsListView_, List<UserTwitterDetails> objects_) {
		super(context, userTweetsListView_, objects_);
		this._userTweetsListView = userTweetsListView_;
	}

	
	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final View view = getWorkingView(convertView);
		final ViewHolder viewHolder = getViewHolder(view);
		final UserTwitterDetails entry = getItem(position);
		Log.v(TAG, "twitterdetails: " + entry.getAttribute().toString());
		Log.v(TAG , "twitterdetails: " + viewHolder.toString());
        Log.v(TAG , "count is: " + getCount());

		viewHolder._titleView.setText(entry.getAttribute());
		viewHolder._numberCount.setText("" +entry.get_count());
		
		// Setting image view is also simple		
		return view;
	}
 
	private View getWorkingView(final View convertView) {
		// The workingView is basically just the convertView re-used if possible
		// or inflated new if not possible
		View workingView = null;
		
		if(null == convertView) {
			final Context context = getContext();
			final LayoutInflater inflater = (LayoutInflater)context.getSystemService
		      (Context.LAYOUT_INFLATER_SERVICE);
			
			workingView = inflater.inflate(_userTweetsListView, null);
		} else {
			workingView = convertView;
		}
		
		return workingView;
	}
	
	private ViewHolder getViewHolder(final View workingView) {
		// The viewHolder allows us to avoid re-looking up view references
		// Since views are recycled, these references will never change
		
		final Object tag = workingView.getTag();
		ViewHolder viewHolder = null;

		
		if(null == tag || !(tag instanceof ViewHolder)) {
			viewHolder = new ViewHolder();
			
			viewHolder._titleView = (TextView) workingView.findViewById(R.id.tweets_heading);
			viewHolder._numberCount = (TextView) workingView.findViewById(R.id.tweet_numbers_numbers);
			
			workingView.setTag(viewHolder);
			Log.v("decodePairArray", workingView.getTag().toString());
		} else {
			Log.v("decodePairArray", tag.toString());
			viewHolder = (ViewHolder) tag;
		}
		
		return viewHolder;
	}
	
	/**
	 * ViewHolder allows us to avoid re-looking up view references
	 * Since views are recycled, these references will never change
	 */
	private static class ViewHolder {
		public TextView _titleView;
		public TextView _numberCount;
	}
	

}
