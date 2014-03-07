package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sun.imageloader.core.UrlImageLoader;
import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.customviews.views.ZoomListView;
import com.sun.tweetfiltrr.utils.ImageLoaderUtils;

public class FriendsCursorAdapter extends ResourceCursorAdapter implements ZoomListView.OnItemFocused {

	
    private static final String TAG = FriendsCursorAdapter.class.getName();
	private final LayoutInflater _inflater;
    private  UrlImageLoader _imageLoadermine;
    private SparseArray<Boolean> _enabledItems;

	public FriendsCursorAdapter(Context context, int layout, Cursor c,
			 UrlImageLoader imageLoader_) {
		super(context, layout, c, 0);
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


	@Override
	public void bindView(View view_, Context context, Cursor cursor) {
		TextView friendName=(TextView)view_.findViewById(R.id.friend_name);
		friendName.setText(cursor.getString(cursor.getColumnIndexOrThrow("friendTable_friendName")));
        TextView friendDescription=(TextView)view_.findViewById(R.id.friend_desc);
        String desc = cursor.getString(cursor.getColumnIndexOrThrow("friendTable_description"));
        friendDescription.setText(desc);
        String url = cursor.getString(cursor.getColumnIndexOrThrow("friendTable_profileImageUrl"));
        ImageView thumbNail = (ImageView) view_.findViewById(R.id.list_image);
        ImageLoaderUtils.attemptLoadImage(thumbNail, _imageLoadermine, url, 1, null);
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
