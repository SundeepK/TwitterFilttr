package com.sun.tweetfiltrr.database.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sun.tweetfiltrr.cursorToParcelable.CursorToParcelable;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.Collection;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.UsersToFollowersTable.UsersToFollowersColumn;
public class UserFollowersDao extends  ADBDao<ParcelableUser> {


	private static final String TAG = UserFollowersDao.class.getName();
    public static final String[] PROJECTIONS = DBUtils.getprojections(UsersToFollowersColumn.values());
	public static final String[] FULLY_QUALIFIED_PROJECTIONS = DBUtils.getFullyQualifiedProjections(UsersToFollowersColumn.values());

	Uri _usersTofriendUri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_USER_TO_FOLLOWERS + "/" + 110);

	ParcelableUser _currentFriend;

	public UserFollowersDao(ContentResolver contentResolver_, CursorToParcelable<ParcelableUser> cusorToFriend_, ParcelableUser currentFriend_) {
		super(contentResolver_, cusorToFriend_);
		_currentFriend = currentFriend_;
	}
	
	

	@Override
	public Collection<ParcelableUser> getEntries(String selection_,
			String[] selectionArgs_, String sortOrder_) {

		Cursor cursorFriend = _contentResolver.query(
				TweetFiltrrProvider.CONTENT_URI_USER_TO_FOLLOWERS, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
		return processCursor(cursorFriend);
	}
	
	@Override
	public Cursor getCursor(String selection_, String[] selectionArgs_, String sortOrder_) {
		return _contentResolver.query(
				TweetFiltrrProvider.CONTENT_URI_USER_TO_FOLLOWERS, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
	}

	@Override
	public Collection<ParcelableUser> getEntry(long rowID_, String selection_,
			String[] selectionArgs_, String sortOrder_) {
		Uri uri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_USER_TO_FOLLOWERS + "/"
				+ rowID_);
		Cursor cursorFriend = _contentResolver.query(uri, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
		Log.v(TAG, "before getting timeline" + cursorFriend.getCount());

		return processCursor(cursorFriend);
	}

	
	
	protected ContentValues getContentValues(ParcelableUser friend_, String[] columns_, boolean shouldSetNull_){
		ContentValues contentValue = new ContentValues();

		for(String column : columns_){
			
            if (shouldSetNull_) {
                if (!(TextUtils.equals(column, FriendColumn._ID.s()))) {
                    contentValue.putNull(column);
                    Log.v(TAG, "im null value");
                    continue ;
                }
            }


            if (TextUtils.equals(column, UsersToFollowersColumn.FOLLOWER_ID.s())) {
                contentValue.put(UsersToFollowersColumn.FOLLOWER_ID.s(),friend_.getUserId());
            } else if (TextUtils.equals(column, UsersToFollowersColumn.USER_ID.s())) {
                contentValue.put(UsersToFollowersColumn.USER_ID.s(),_currentFriend.getUserId());
            }

		}
		
		return contentValue;
	}


	@Override
	protected Uri getUpdateUri() {
		return _usersTofriendUri;
	}

    @Override
    protected String[] getTableColumns() {
        return PROJECTIONS;
    }


    @Override
	public int deleteEntries(Collection<ParcelableUser> entries_) {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public int deleteEntry(long rowID_) {
		// TODO Auto-generated method stub
		return 0;
	}


}
