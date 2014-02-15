package com.sun.tweetfiltrr.database.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sun.tweetfiltrr.cursorToParcelable.FriendToParcelable;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;

@Singleton
public class FriendDao extends ADBDao<ParcelableUser> {

	private static final String TAG = FriendDao.class.getName();

    public static final	String[] FULLY_QUALIFIED_PROJECTIONS = DBUtils.getFullyQualifiedProjections(FriendColumn.values());
    public static final String[] PROJECTIONS = DBUtils.getprojections(FriendColumn.values());
	
	Uri _friendUri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_FRIEND + "/" + 110);

    @Inject
	public FriendDao(ContentResolver contentResolver_, FriendToParcelable cursorToParcelable_) {
		super(contentResolver_, cursorToParcelable_);
	}
	
	protected ContentValues getContentValues(ParcelableUser friend_, String[] columns_, boolean shouldSetNull_){
		ContentValues contentValue = new ContentValues();

		for(String column : columns_){
			
			if (shouldSetNull_) {
				if (!(TextUtils.equals(column,FriendColumn.FRIEND_ID.s())  ||
                        !TextUtils.equals(column, FriendColumn._ID.s()))) {
					contentValue.putNull(column);
					Log.v(TAG, "im null value");
					continue ;
				}
			}


            if (TextUtils.equals(column, FriendColumn.FRIEND_ID.s())) {
                contentValue.put(FriendColumn.FRIEND_ID.s(), friend_.getUserId());
            } else if (TextUtils.equals(column, FriendColumn.FRIEND_NAME.s())) {
                contentValue.put(FriendColumn.FRIEND_NAME.s(), friend_.getUserName());
            } else if (TextUtils.equals(column, FriendColumn.FRIEND_SCREENNAME.s())) {
                contentValue.put(FriendColumn.FRIEND_SCREENNAME.s(), friend_.getScreenName());
            } else if (TextUtils.equals(column, FriendColumn.DESCRIPTION.s())) {
                contentValue.put(FriendColumn.DESCRIPTION.s(), friend_.getDescription());
            } else if (TextUtils.equals(column, FriendColumn.COLUMN_MAXID.s())) {
                contentValue.put(FriendColumn.COLUMN_MAXID.s(), friend_.getMaxId());
            } else if (TextUtils.equals(column, FriendColumn.COLUMN_SINCEID.s())) {
                contentValue.put(FriendColumn.COLUMN_SINCEID.s(), friend_.getSinceId());
            } else if (TextUtils.equals(column, FriendColumn.COLUMN_LAST_DATETIME_SYNC.s())) {
                contentValue.put(FriendColumn.COLUMN_LAST_DATETIME_SYNC.s(), friend_.getLastUpadateDate());
            } else if (TextUtils.equals(column, FriendColumn.COLUMN_GROUP_ID.s())) {
                contentValue.put(FriendColumn.COLUMN_GROUP_ID.s(), friend_.getGroupId());
            } else if (TextUtils.equals(column, FriendColumn.PROFILE_IMAGE_URL.s())) {
                contentValue.put(FriendColumn.PROFILE_IMAGE_URL.s(), friend_.getProfileImageUrl());
            } else if (TextUtils.equals(column, FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s())) {
                contentValue.put(FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s(), friend_.getProfileBackgroundImageUrl());
            } else if (TextUtils.equals(column, FriendColumn.BANNER_PROFILE_IMAE_URL.s())) {
                contentValue.put(FriendColumn.BANNER_PROFILE_IMAE_URL.s(), friend_.getProfileBannerImageUrl());
            } else if (TextUtils.equals(column, FriendColumn.HAS_ALL_TWEETS_FOR_TODAY.s())) {
                contentValue.put(FriendColumn.HAS_ALL_TWEETS_FOR_TODAY.s(), friend_.hasLoadedAllTweetsForToday());
            } else if (TextUtils.equals(column, FriendColumn.LAST_TIMELINE_PAGE_NO.s())) {
                contentValue.put(FriendColumn.LAST_TIMELINE_PAGE_NO.s(), friend_.getLastTimelinePageNumber());
            } else if (TextUtils.equals(column, FriendColumn.IS_FRIEND.s())) {
                contentValue.put(FriendColumn.IS_FRIEND.s(), friend_.isFriend() ? 1 : 0);
            } else if (TextUtils.equals(column, FriendColumn.TOTAL_NEW_TWEETS.s())) {
                contentValue.put(FriendColumn.TOTAL_NEW_TWEETS.s(), friend_.getNewTweetCount());
                //friends
            } else if (TextUtils.equals(column, FriendColumn.COLUMN_CURRENT_FRIEND_COUNT.s())) {
                contentValue.put(FriendColumn.COLUMN_CURRENT_FRIEND_COUNT.s(), friend_.getCurrentFriendCount());
            }else if (TextUtils.equals(column, FriendColumn.COLUMN_LAST_FRIEND_INDEX.s())) {
                contentValue.put(FriendColumn.COLUMN_LAST_FRIEND_INDEX.s(), friend_.getLastFriendIndex());
            }else if (TextUtils.equals(column, FriendColumn.FRIEND_COUNT.s())) {
                contentValue.put(FriendColumn.FRIEND_COUNT.s(), friend_.getTotalFriendCount());
            } else if (TextUtils.equals(column, FriendColumn.LAST_FRIEND_PAGE_NO.s())) {
                contentValue.put(FriendColumn.LAST_FRIEND_PAGE_NO.s(), friend_.getLastFriendPageNumber());

            }else if (TextUtils.equals(column, FriendColumn.TWEET_COUNT.s())) {
                contentValue.put(FriendColumn.TWEET_COUNT.s(), friend_.getTotalTweetCount());
                //followers
            }else if (TextUtils.equals(column, FriendColumn.FOLLOWER_COUNT.s())) {
                contentValue.put(FriendColumn.FOLLOWER_COUNT.s(), friend_.getTotalFollowerCount());
            }else if (TextUtils.equals(column, FriendColumn.LAST_FOLLOWER_PAGE_NO.s())) {
                contentValue.put(FriendColumn.LAST_FOLLOWER_PAGE_NO.s(), friend_.getLastFollowerPageNumber());
            }else if (TextUtils.equals(column, FriendColumn.COLUMN_LAST_FOLLOWER_INDEX.s())) {
                contentValue.put(FriendColumn.COLUMN_LAST_FOLLOWER_INDEX.s(), friend_.getLastFollowerIndex());
            }else if (TextUtils.equals(column, FriendColumn.COLUMN_CURRENT_FOLLOWER_COUNT.s())) {
                contentValue.put(FriendColumn.COLUMN_CURRENT_FOLLOWER_COUNT.s(), friend_.getCurrentFollowerCount());
            }
		}
		
		return contentValue;
	}

	@Override
	public Collection<ParcelableUser> getEntries(String selection_,
			String[] selectionArgs_, String sortOrder_) {
		Cursor cursorFriend = getCursor(selection_, selectionArgs_, sortOrder_);
		return processCursor(cursorFriend);
	}
	
	@Override
	public Cursor getCursor(String selection_, String[] selectionArgs_, String sortOrder_) {
		return _contentResolver.query(
				TweetFiltrrProvider.CONTENT_URI_FRIEND, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
	}


    @Override
	public Collection<ParcelableUser> getEntry(long rowID_, String selection_,
			String[] selectionArgs_, String sortOrder_) {
		Uri uri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_FRIEND + "/"
				+ rowID_);
		Cursor cursorFriend = _contentResolver.query(uri, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
		Log.v(TAG, "before getting timeline" + cursorFriend.getCount());
        return  processCursor(cursorFriend);
	}
	


	/**
	 * Return a {@link ParcelableUser} contained in the cursor.
	 * 
	 * @param cursorFriend_
	 *            used to extract the various fields from
	 * @return {@link ParcelableUser}
	 */
	@Override
	protected ParcelableUser getParcelable(Cursor cursorFriend_) {
		return super.getParcelable(cursorFriend_);
	}


	@Override
	protected Uri getUpdateUri() {
		return _friendUri;
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
