package com.sun.tweetfiltrr.cursorToParcelable;

import android.database.Cursor;

import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;


public interface CursorToParcelable<T extends IParcelableTwitter> {

	public T getParcelable(Cursor cursor_);
	
}
