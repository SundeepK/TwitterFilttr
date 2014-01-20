package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;

import java.net.URISyntaxException;

public class NewTweetsCursorAdapter extends SimpleCursorAdapter  {


    private static final String TAG = NewTweetsCursorAdapter.class.getName();
	private final LayoutInflater _inflater;
    private  UrlImageLoader _imageLoadermine;
    private int _rowCount = 20;

	public NewTweetsCursorAdapter(Context context, int layout, Cursor c,
                                  String[] from, int[] to, int flags, UrlImageLoader imageLoader_) {
		super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _imageLoadermine = imageLoader_;
	}


	public NewTweetsCursorAdapter(Context context, int listviewForTwitter,
                                  Cursor c, String[] columns, int[] to, int flags) {
		super(context, listviewForTwitter, c, columns, to, flags);
        _inflater = LayoutInflater.from(context);

        
	}
	



	@Override
	public void bindView(View view_, Context context, Cursor cursor) {
		TextView friendName=(TextView)view_.findViewById(R.id.friend_name);
		friendName.setText(cursor.getString(cursor.getColumnIndexOrThrow("friendTable_friendName")));

		
        TextView newTweetCount=(TextView)view_.findViewById(R.id.number_of_tweets);
        String totalNewTweets = cursor.getString(cursor.getColumnIndexOrThrow("friendTable_totalNewTweets"));
        
        if(TextUtils.isEmpty(totalNewTweets)){
        	Log.v(TAG, " number_of_tweets is null");
        }
        
        newTweetCount.setText(totalNewTweets);

        String url = cursor.getString(cursor.getColumnIndexOrThrow("friendTable_profileImageUrl"));
        		
        ImageView thumbNail = (ImageView) view_.findViewById(R.id.list_image);


        if(!TextUtils.isEmpty(url)){
			try {
				_imageLoadermine.displayImage(url, thumbNail, 1);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        

        
        
        
	}

	@Override
	public View newView(Context context, Cursor  cursor, ViewGroup parent) {
		final View view=_inflater.inflate(R.layout.user_home_new_tweets_list_row,parent,false);
        return view;
	}

}
