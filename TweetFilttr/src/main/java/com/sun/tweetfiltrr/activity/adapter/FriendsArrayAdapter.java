package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.List;

public class FriendsArrayAdapter extends ArrayAdapter<ParcelableUser> {

	List<ParcelableUser> _friendsList;
	int _friendsListView;
	
	public FriendsArrayAdapter(Context context,
			int friendsListView_, List<ParcelableUser> objects_) {
		super(context, friendsListView_, objects_);
		this._friendsListView = friendsListView_;
	}

	
	@Override
	public boolean hasStableIds() {
		return true;
	}
	
	
	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		
		// We need to get the best view (re-used if possible) and then
		// retrieve its corresponding ViewHolder, which optimizes lookup efficiency
		final View view = getWorkingView(convertView);
		final ViewHolder viewHolder = getViewHolder(view);
		final ParcelableUser entry = getItem(position);
		
		// Setting the title view is straightforward
		viewHolder.titleView.setText(entry.getUserName());
		viewHolder.subTitleView.setText("" +entry.getUserId());
		
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
			
			workingView = inflater.inflate(_friendsListView, null);
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
			
			viewHolder.titleView = (TextView) workingView.findViewById(R.id.friend_name);
			viewHolder.subTitleView = (TextView) workingView.findViewById(R.id.friend_desc);
			
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
		public TextView titleView;
		public TextView subTitleView;
	}
	

}
