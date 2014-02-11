package com.sun.tweetfiltrr.database.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sun.tweetfiltrr.cursorToParcelable.CursorToParcelable;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;


public abstract class ADBDao<T extends IParcelableTwitter> implements IDBDao<T> {

	private static final String TAG = ADBDao.class.getName();
	protected ContentResolver _contentResolver;
	protected CursorToParcelable<T> _cursorToParcelable;
	
	/**
	 * 
	 * 
	 * @param contentResolver_
	 * 				{@link android.content.ContentResolver} used to query the SQLite DB through the content provider
	 * @param cursorToParcelable_
	 * 				{@link CursorToParcelable} used to convert the data from the {@link android.database.Cursor} to a {@link android.os.Parcelable} object
	 * 				that can be used later
	 */

	protected ADBDao (ContentResolver contentResolver_, CursorToParcelable<T> cursorToParcelable_){
		_contentResolver = contentResolver_;
		_cursorToParcelable = cursorToParcelable_;
	}
	

	
	
	protected ContentValues[] getContentValuesForInsertOrUpdate(Collection<T> entries_, String[] columns_, boolean shouldSetNull_){
		Collection<ContentValues> contentValues = new ArrayList<ContentValues>();
		for(T friend : entries_){
			contentValues.add(getContentValues(friend, columns_, shouldSetNull_));
		}
		
		ContentValues[] contentValuesArray = new ContentValues[contentValues.size()];
		return contentValues.toArray(contentValuesArray);
	}
	
	protected abstract ContentValues getContentValues(T friend_, String[] columns_, boolean shouldSetNull_);

	
	protected Collection<T> processCursor(Cursor cursor_) {
		Collection<T> parcelables = new LinkedList<T>();

		if (cursor_.getCount() > 0) {
			Log.v(TAG, "Size of curos " + cursor_.getCount());
			cursor_.moveToFirst();
			while (!cursor_.isAfterLast()) {
				T friend = getParcelable(cursor_);
				parcelables.add(friend);
				cursor_.moveToNext();
			}
		}else{
			Log.v(TAG, "Cursor size was 0");
		}

		return parcelables;
	}
	
	protected T getParcelable(Cursor cursor_) {
		return _cursorToParcelable.getParcelable(cursor_);
	}
	
	@Override
	public Collection<T> getEntry(long rowID_) {
		return getEntry(rowID_, null,null,null);
	}
	
	@Override
	public Collection<T> getAllEntries() {
		return getEntries(null,null,null);
	}
	
	@Override
	public void updateToNull(Collection<T> entries_,
			String[] columns_) {
		ContentValues[] values = getContentValuesForInsertOrUpdate(entries_, columns_, true);
		
		for(T friend : entries_){
			Log.v(TAG, friend.toString() + "has some null values");
		}
		
		_contentResolver.bulkInsert(getUpdateUri(), values);
		
	}
	

	@Override
         public void insertOrUpdate(Collection<T> entries_) {
        String[] columns = getTableColumns();
        ContentValues[] values = getContentValuesForInsertOrUpdate(entries_, columns, false);

        for(T friend : entries_){
            Log.v(TAG, friend.toString());
        }

        _contentResolver.bulkInsert(getUpdateUri(), values);

    }

    @Override
    public void insertOrUpdate(Collection<T> entries_, String[] columns_) {
        ContentValues[] values = getContentValuesForInsertOrUpdate(entries_, columns_, false);

        for(T friend : entries_){
            Log.v(TAG, friend.toString());
        }

        _contentResolver.bulkInsert(getUpdateUri(), values);

    }
	
	protected abstract Uri getUpdateUri();

    protected abstract String[] getTableColumns();

}
