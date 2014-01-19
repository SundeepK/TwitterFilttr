package com.sun.tweetfiltrr.cursorToParcelable;

import android.database.Cursor;

import com.sun.tweetfiltrr.parcelable.ParcelableKeywordGroup;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.ParcelableUserToKeywords;


public class KeywordFriendToParcelable implements CursorToParcelable<ParcelableUserToKeywords> {

	CursorToParcelable<ParcelableKeywordGroup> _cursorToKeywordGroup;
	CursorToParcelable<ParcelableUser> _cursorToFriend;
	
	public KeywordFriendToParcelable(CursorToParcelable<ParcelableUser> cursorToFriend_, 
			CursorToParcelable<ParcelableKeywordGroup> cursorToKeywordGroupDao_){
		_cursorToFriend = cursorToFriend_;
		_cursorToKeywordGroup = cursorToKeywordGroupDao_;
	}
	
	@Override
	public ParcelableUserToKeywords getParcelable(Cursor cursor_) {

		 ParcelableUser user = _cursorToFriend.getParcelable(cursor_);
		 ParcelableKeywordGroup keywordGroup = _cursorToKeywordGroup.getParcelable(cursor_);
		 
		return new ParcelableUserToKeywords(keywordGroup, user);
	}


}
