package com.sun.tweetfiltrr.database.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.sun.tweetfiltrr.cursorToParcelable.CursorToParcelable;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.tables.FriendKeywordColumn;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.parcelable.ParcelableUserToKeywords;

import java.util.Collection;

public class FriendKeywordDao extends ADBDao<ParcelableUserToKeywords> {

	private static final String TAG = FriendKeywordDao.class.getName();
	String[] _projection = {
//			FriendColumn.FRIEND_TABLE.s() + "."+ FriendColumn._ID.s(),
//			FriendKeywordColumn.COLUMN_FRIEND_NAME.s(),
//			FriendKeywordColumn.COLUMN_FRIEND_SCREENNAME.s(),
//			FriendKeywordColumn.COLUMN_DESCRIPTION.s(),
//			FriendKeywordColumn.COLUMN_PAGE_NO.s(),
//			FriendKeywordColumn.COLUMN_SINCEID.s(),
//			FriendKeywordColumn.COLUMN_LAST_DATETIME_SYNC.s(),
//			FriendKeywordColumn.COLUMN_GROUP_ID.s(),
			FriendKeywordColumn.COLUMN_GROUP_NAME.s(),
			FriendKeywordColumn.COLUMN_KEYWORDS.s(),
			FriendKeywordColumn.COLUMN_IS_FRIEND.s()
			};

    String[] _projections = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, KeywordGroupDao.FULLY_QUALIFIED_PROJECTIONS);

    Uri _keywordGroupUri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_FRIEND_GROUP + "/" + TweetFiltrrProvider.FRIEND_GROUP_TABLE_ID);

	/**
	 * Used to query the DB for the group that a user is associated with and return a {@link com.sun.tweetfiltrr.parcelable.ParcelableUserToKeywords} object which
	 * maps a user to the group they belong to.
	 * 
	 * This DAO object is only used for query purposes and (atm) cannot be used to update users and groups (update methods are no-ops)
	 * 
	 */
	public FriendKeywordDao(ContentResolver contentResolver_, CursorToParcelable<ParcelableUserToKeywords> cursorToKeywordUser_) {
		super(contentResolver_, cursorToKeywordUser_);
	}

	@Override
	public Collection<ParcelableUserToKeywords> getEntry(long rowID_,
			String selection_, String[] selectionArgs_, String sortOrder_) {
		Uri uri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_FRIEND_GROUP + "/"
				+ rowID_);
		Cursor cursorFriend = _contentResolver.query(uri, _projections, selection_,
				selectionArgs_, sortOrder_);
		Log.v(TAG, "before getting timeline" + cursorFriend.getCount());

		Collection<ParcelableUserToKeywords> friendKeyword = processCursor(cursorFriend);

		return friendKeyword;
	}

	@Override
	public Collection<ParcelableUserToKeywords> getEntries(String selection_,
			String[] selectionArgs_, String sortOrder_) {

		Cursor cursorFriend = _contentResolver.query(
				TweetFiltrrProvider.CONTENT_URI_FRIEND_GROUP, _projections,
				selection_, selectionArgs_, sortOrder_);

		Collection<ParcelableUserToKeywords> friendKeyword = processCursor(cursorFriend);

		return friendKeyword;
	}

	@Override
	public Cursor getCursor(String selection_, String[] selectionArgs_, String sortOrder_) {
		Cursor cursorFriend = _contentResolver.query(
				TweetFiltrrProvider.CONTENT_URI_FRIEND_GROUP, _projections,
				selection_, selectionArgs_, sortOrder_);
		
		return cursorFriend;
	}
	
	
	
	
	/*
	 * No-op 
	 * @see com.tweetfiltrr.dao.ADBDao#updateToNull(java.util.Collection, V[])
	 */
	@Override
	public void updateToNull(Collection<ParcelableUserToKeywords> entries_,
			String[] columns_) {
	}

	/**
	 * No-op 
	 */
	@Override
	public void insertOrUpdate(Collection<ParcelableUserToKeywords> entries_) {
	}

	/**
	 * No-op 
	 */
	@Override
	protected ContentValues[] getContentValuesForInsertOrUpdate(
			Collection<ParcelableUserToKeywords> entries_,
            String[] columns_, boolean shouldSetNull_) {
		return null;
	}

	/**
	 * No-op 
	 */
	@Override
	protected ContentValues getContentValues(ParcelableUserToKeywords friendKeyword_,
                                             String[] columns_, boolean shouldSetNull_) {
		return null;
	}

	@Override
	protected Uri getUpdateUri() {
		return _keywordGroupUri;
	}

    @Override
    protected String[] getTableColumns() {
        return null;
    }

    /**
	 * No-op 
	 */
	@Override
	public int deleteEntries(Collection<ParcelableUserToKeywords> entries_) {
		return 0;
	}

	/**
	 * No-op 
	 */
	@Override
	public int deleteEntry(long rowID_) {
		// TODO Auto-generated method stub
		return 0;
	}


	

}
