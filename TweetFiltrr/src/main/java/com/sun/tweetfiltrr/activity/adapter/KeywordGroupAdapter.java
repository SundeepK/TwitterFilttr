package com.sun.tweetfiltrr.activity.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;

public class KeywordGroupAdapter extends SimpleCursorAdapter  {


    private static final String TAG = KeywordGroupAdapter.class.getName();
	private final LayoutInflater _inflater;
    Lock _lock;
    Cursor _cachedCursor;
	private IDBDao<ParcelableKeywordGroup> _keywordGroupDao;

	public KeywordGroupAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags, IDBDao<ParcelableKeywordGroup> keywordGroupDao_) {
		super(context, layout, c, from, to, flags);
        _inflater = LayoutInflater.from(context);
        _lock = new ReentrantLock(true);
        _keywordGroupDao = keywordGroupDao_;
        
	}

	private synchronized Cursor getCachedCursor(Cursor cachedCursor_){
			if(_cachedCursor == null ){
				Log.v(TAG, "creating new ionstance ");
				_cachedCursor = cachedCursor_;
				if( _cachedCursor.getCount() > 0 )
					_cachedCursor.moveToFirst();
			}
			return _cachedCursor;
	}


	@Override
	public void bindView(View view_, Context context, Cursor _cursorToIterate) {
		
		TextView friendName=(TextView)view_.findViewById(R.id.keyword_group_details);
		//cursor.moveToFirst();
//		Cursor cur = getCachedCursor(_cursorToIterate);
//		
//		if(!cur.isLast()){
//		new AsyncKeywordGroupLoader(friendName, _lock,cur ).execute();
//		}
//		String groupName;
//		String keywords;
//		int groupID;
//		groupName =	cursor.getString(cursor.getColumnIndex(KeywordGroupColumn.COLUMN_GROUP_NAME));
//		int groupID = cursor.getInt(cursor.getColumnIndex(FriendTable.COLUMN_GROUP_ID));
//		keywords = 	cursor.getString(cursor.getColumnIndex(KeywordGroupColumn.COLUMN_KEYWORDS));
//		friendName.setText(groupName);
//		
		String groupName =	_cursorToIterate.getString(_cursorToIterate.getColumnIndex(KeywordGroupColumn.COLUMN_GROUP_NAME.s()));
		String	keywords = 	_cursorToIterate.getString(_cursorToIterate.getColumnIndex(KeywordGroupColumn.COLUMN_KEYWORDS.s()));
		long rowId = 	_cursorToIterate.getLong((_cursorToIterate.getColumnIndex(KeywordGroupColumn.COLUMN_ID.s())));
		Log.v(TAG, groupName);
		StringBuilder builder = new StringBuilder();
		builder.append(groupName);
		builder.append("\n");
		builder.append(keywords);
		friendName.setText(builder.toString());
		 Button deleteBut = (Button) view_.findViewById(R.id.delete_group);
		 deleteBut.setOnClickListener(getDeleteButtonLis(_keywordGroupDao, rowId));

	}
	
	private View.OnClickListener getDeleteButtonLis(final IDBDao<ParcelableKeywordGroup> keywordGroupDao_, final long rowId_){
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				keywordGroupDao_.deleteEntry(rowId_);
			}
		};
	}

	@Override
	public View newView(Context context, Cursor  cursor, ViewGroup parent) {
		final View view=_inflater.inflate(R.layout.keyword_group_expandable_list_item,parent,false); 
        return view;
	}

}

