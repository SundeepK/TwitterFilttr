package com.sun.tweetfiltrr.database.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sun.tweetfiltrr.cursorToParcelable.TimelineToParcelable;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;
@Singleton
public class TimelineDao extends ADBDao<ParcelableTweet> {

	private static final String TAG = TimelineDao.class.getName();

    public static final String[] PROJECTIONS = DBUtils.getprojections(TimelineColumn.values());
	public static final String[] FULLY_QUALIFIED_PROJECTIONS = DBUtils.getFullyQualifiedProjections(TimelineColumn.values());
	
	Uri _timelineUri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_TIMELINE + "/" + 110);
	@Inject
	public TimelineDao(ContentResolver contentResolver_, TimelineToParcelable cursorToParcelable_) {
		super(contentResolver_, cursorToParcelable_);
		
	}

	@Override
	protected ContentValues getContentValues(ParcelableTweet timeline_, String[] columns_, boolean shouldSetNull_){
		ContentValues contentValue = new ContentValues();

		for(String column : columns_){
			

            if (shouldSetNull_) {
                if (!(TextUtils.equals(column, TimelineColumn._ID.s()))) {
                    contentValue.putNull(column);
                    Log.v(TAG, "im null value");
                    continue ;
                }
            }

            if (TextUtils.equals(column, TimelineColumn.FRIEND_ID.s())) {
                contentValue.put(TimelineColumn.FRIEND_ID.s(), timeline_.getFriendID());
            } else if (TextUtils.equals(column, TimelineColumn.TWEET_ID.s())) {
                contentValue.put(TimelineColumn.TWEET_ID.s(), timeline_.getTweetID());
            } else if (TextUtils.equals(column, TimelineColumn.TIMELINE_TEXT.s())) {
                contentValue.put(TimelineColumn.TIMELINE_TEXT.s(), timeline_.getTweetText());
            } else if (TextUtils.equals(column, TimelineColumn.DATETIME_INSERTED.s())) {
                contentValue.put(TimelineColumn.DATETIME_INSERTED.s(), timeline_.getTweetDate());
            } else if (TextUtils.equals(column, TimelineColumn.IN_REPLY_SCREENNAME.s())) {
                contentValue.put(TimelineColumn.IN_REPLY_SCREENNAME.s(), timeline_.getInReplyToScreenName());
            } else if (TextUtils.equals(column, TimelineColumn.IN_REPLY_TWEETID.s())) {
                contentValue.put(TimelineColumn.IN_REPLY_TWEETID.s(), timeline_.getInReplyToTweetId());
            } else if (TextUtils.equals(column, TimelineColumn.IN_REPLY_USERID.s())) {
                contentValue.put(TimelineColumn.IN_REPLY_USERID.s(), timeline_.getInReplyToUserId());
            } else if (TextUtils.equals(column, TimelineColumn.PHOTO_URL.s())) {
                contentValue.put(TimelineColumn.PHOTO_URL.s(), timeline_.getPhotoUrl());
            }else if (TextUtils.equals(column, TimelineColumn.IS_RETWEETED.s())) {
                contentValue.put(TimelineColumn.IS_RETWEETED.s(), timeline_.isRetweeted());
            }else if (TextUtils.equals(column, TimelineColumn.IS_FAVOURITE.s())) {
                contentValue.put(TimelineColumn.IS_FAVOURITE.s(), (timeline_.isFavourite() ? 1 : 0) );
            }else if (TextUtils.equals(column, TimelineColumn.IS_MENTION.s())) {
                contentValue.put(TimelineColumn.IS_MENTION.s(), timeline_.isMention());
            }else if (TextUtils.equals(column, TimelineColumn.IS_KEYWORD_SEARCH_TWEET.s())) {
                contentValue.put(TimelineColumn.IS_KEYWORD_SEARCH_TWEET.s(), timeline_.isKeyWordSearchedTweet());
            }

		}
		
		return contentValue;
	}

	@Override
	public Collection<ParcelableTweet> getEntries(String selection_,
			String[] selectionArgs_, String sortOrder_) {
//		Uri uri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_FRIEND + "/"
//				+ TweetFiltrrProvider.FRIEND_TABLE);
		Cursor cursorFriend = _contentResolver.query(
				TweetFiltrrProvider.CONTENT_URI_TIMELINE, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
        return processCursor(cursorFriend);
	}
	

	@Override
	public Cursor getCursor(String selection_, String[] selectionArgs_, String sortOrder_) {
        return _contentResolver.query(
				TweetFiltrrProvider.CONTENT_URI_GROUP, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
	}

	@Override
	public Collection<ParcelableTweet> getEntry(long rowID_, String selection_,
			String[] selectionArgs_, String sortOrder_) {
		Uri uri = Uri.parse(TweetFiltrrProvider.CONTENT_URI_TIMELINE + "/"
				+ rowID_);
		Cursor cursorFriend = _contentResolver.query(uri, FULLY_QUALIFIED_PROJECTIONS, selection_,
				selectionArgs_, sortOrder_);
		Log.v(TAG, "before getting timeline" + cursorFriend.getCount());
		return processCursor(cursorFriend);
	}
	


	@Override
	protected Uri getUpdateUri() {
		return _timelineUri;
	}

    @Override
    protected String[] getTableColumns() {
        return PROJECTIONS;
    }


    @Override
	public int deleteEntries(Collection<ParcelableTweet> entries_) {
		// TODO Auto-generated method stub
		return 0;
	}




	@Override
	public int deleteEntry(long rowID_) {
		// TODO Auto-generated method stub
		return 0;
	}



}
