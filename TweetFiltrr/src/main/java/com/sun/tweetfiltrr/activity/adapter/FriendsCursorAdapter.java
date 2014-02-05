package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.zoomlistview.ZoomListView;

import java.net.URISyntaxException;

public class FriendsCursorAdapter extends SimpleCursorAdapter implements ZoomListView.OnItemFocused {

	
    private static final String TAG = FriendsCursorAdapter.class.getName();
	private final LayoutInflater _inflater;
    private  UrlImageLoader _imageLoadermine;
    private int _rowCount = 20;
    private SparseArray<Boolean> _enabledItems;

	public FriendsCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags, UrlImageLoader imageLoader_) {
		super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _imageLoadermine = imageLoader_;
        _enabledItems = new SparseArray<Boolean>();
	}


//	@Override
//	public void changeCursor(Cursor cursor) {
//		super.changeCursor(cursor);
//	}


//	public void setRowNumbers(int rowCount_){
//		_rowCount = rowCount_;
//	}
//	
//	@Override
//	public int getCount() {
//	  if(super.getCursor()==null || super.getCount() == 0) 
//	     return 0; 
//	  return _rowCount;
//	}


	public FriendsCursorAdapter(Context context, int listviewForTwitter,
			Cursor c, String[] columns, int[] to, int flags
			) {
		super(context, listviewForTwitter, c, columns, to, flags);
        _inflater = LayoutInflater.from(context);

	}
	



	@Override
	public void bindView(View view_, Context context, Cursor cursor) {
		TextView friendName=(TextView)view_.findViewById(R.id.friend_name);
		friendName.setText(cursor.getString(cursor.getColumnIndexOrThrow("friendTable_friendName")));
        Log.v(TAG, "im in friendcursoir laoder in bind view");
		
        TextView friendDescription=(TextView)view_.findViewById(R.id.friend_desc);
        String desc = cursor.getString(cursor.getColumnIndexOrThrow("friendTable_description"));
        
        if(TextUtils.isEmpty(desc)){
        	Log.v(TAG, " desc is null");
        }
        
        friendDescription.setText(desc);

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
		final View view=_inflater.inflate(R.layout.twitter_friends_list_view_row,parent,false); 
        return view;
	}

    @Override
    public boolean isEnabled(int position) {
        Boolean isenabled = _enabledItems.get(position, true);
        return isenabled;
    }


    @Override
    public void onItemScaleOut(int position, View view, boolean status_) {

    }

    @Override
    public void onItemRestore(int position, View view, boolean status_) {

    }

    @Override
    public View onItemFocused(View focusedView_, int listViewPosition_, long uniqueId_) {
        return null;
    }
}
