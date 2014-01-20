package com.sun.tweetfiltrr.database.dao;

import android.database.Cursor;

import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

import java.util.Collection;

public interface IDBDao<T extends IParcelableTwitter> {

	public Collection<T> getEntry(long rowID_);
	
	public Collection<T> getEntry(long rowID_, String selection_, String[] selectionArgs_, String sortOrder_);
	
	public Collection<T> getEntries(String selection_, String[] selectionArgs_, String sortOrder_);
	
	public Collection<T> getAllEntries();
	
	public void insertOrUpdate(Collection<T> entries_);
	
	public void updateToNull(Collection<T> entries_, String[] columns_);
	
	public int deleteEntries(Collection<T> entries_);
	
	public int deleteEntry(long rowID_);
	
	public Cursor getCursor(String selection_, String[] selectionArgs_, String sortOrder_);

    public void insertOrUpdate(Collection<T> friends, String[] strings);
}
