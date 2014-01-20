package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.cursorToParcelable.CursorToParcelable;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.net.URISyntaxException;

public class UserFriendCursorAdapter extends SimpleCursorAdapter  {


    private static final String TAG = UserFriendCursorAdapter.class.getName();
	private final LayoutInflater _inflater;
    private UrlImageLoader  _imageLoader;
	private int _layout;
    private CursorToParcelable<ParcelableUser> _userToParcelable;


    public UserFriendCursorAdapter(Context context, int layout, Cursor c,
                                   String[] from, int[] to, int flags,
                                   CursorToParcelable<ParcelableUser> userToParcelable_,
                                   UrlImageLoader imageLoader_) {
		super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _layout = layout;
        _userToParcelable =userToParcelable_;
        _imageLoader = imageLoader_;

	}


	@Override
	public void bindView(View view_, Context context, Cursor cursor_) {
		
		ParcelableUser user = getParcelable(cursor_);
        ImageView profilePic =(ImageView)view_.findViewById(R.id.profile_image);

        loadImage(profilePic, user.getProfileImageUrl());

        TextView friendName=(TextView)view_.findViewById(R.id.friend_name);

		friendName.setText(user.getUserName() + " @" + user.getScreenName());
        TextView tweetTextView =(TextView)view_.findViewById(R.id.friend_desc);
        tweetTextView.setText(user.getDescription());
       
	}

    private void loadImage(ImageView view_,String url_){
        if (!TextUtils.isEmpty(url_)) {
            try {
                _imageLoader.displayImage(url_, view_, 1);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
	
	

	protected ParcelableUser getParcelable(Cursor cursorTimeline_) {
     return _userToParcelable.getParcelable(cursorTimeline_);
	}


	
	@Override
	public View newView(Context context, Cursor  cursor, ViewGroup parent) {
		final View view=_inflater.inflate(_layout,parent,false); 
        return view;
	}




}
