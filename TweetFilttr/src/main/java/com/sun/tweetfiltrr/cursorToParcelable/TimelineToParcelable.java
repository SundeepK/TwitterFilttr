package com.sun.tweetfiltrr.cursorToParcelable;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;

import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;


public class TimelineToParcelable implements CursorToParcelable<ParcelableTimeLineEntry>{

    private static final String TAG = TimelineToParcelable.class.getName();

    @Override
	public ParcelableTimeLineEntry getParcelable(Cursor cursorTimeline_) {
		long friendID = cursorTimeline_.getLong(cursorTimeline_
				.getColumnIndexOrThrow(TimelineColumn.FRIEND_ID.a()));
		long tweetID = cursorTimeline_.getLong(cursorTimeline_
				.getColumnIndexOrThrow(TimelineColumn.TWEET_ID.a()));
		String tweetDate = cursorTimeline_.getString(cursorTimeline_
				.getColumnIndexOrThrow(TimelineColumn.DATETIME_INSERTED.a()));
		String tweetText = cursorTimeline_.getString(cursorTimeline_
				.getColumnIndexOrThrow(TimelineColumn.TIMELINE_TEXT.a()));
		long inReplyToUserId = cursorTimeline_.getLong(cursorTimeline_
				.getColumnIndexOrThrow(TimelineColumn.IN_REPLY_USERID.a()));
		long inReplyToTweetId = cursorTimeline_.getLong(cursorTimeline_
				.getColumnIndexOrThrow(TimelineColumn.IN_REPLY_TWEETID.a()));
		String inReplyToScreenName = cursorTimeline_.getString(cursorTimeline_
				.getColumnIndexOrThrow(TimelineColumn.IN_REPLY_SCREENNAME.a()));
        String photoUrl = cursorTimeline_.getString(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.PHOTO_URL.a()));
        boolean isFav = cursorTimeline_.getInt(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.IS_FAVOURITE.a()))  == 1 ? true : false ;
        boolean isRetweeted = cursorTimeline_.getInt(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.IS_RETWEETED.a())) == 1 ? true : false ;
        boolean isMention = cursorTimeline_.getInt(cursorTimeline_
                .getColumnIndexOrThrow(TimelineColumn.IS_MENTION.a())) == 1 ? true : false ;

        if(!TextUtils.isEmpty(photoUrl)){
            Log.v(TAG, "Url for image " + photoUrl);
        }


		return new ParcelableTimeLineEntry(tweetText, tweetDate, tweetID, friendID,
				inReplyToScreenName,inReplyToTweetId,inReplyToUserId, photoUrl, isFav, isRetweeted , isMention);
	}


}
