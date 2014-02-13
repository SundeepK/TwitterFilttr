package com.sun.tweetfiltrr.cursorToParcelable;

import android.database.Cursor;
import android.util.Log;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import javax.inject.Singleton;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;

@Singleton
public class FriendToParcelable implements CursorToParcelable<ParcelableUser> {

	private static final String TAG = FriendToParcelable.class.getName();

    public FriendToParcelable(){};

	@Override
	public ParcelableUser getParcelable(Cursor cursorFriend_) {

		long friendID = cursorFriend_.getInt(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.FRIEND_ID.a()));
		String friendName = cursorFriend_.getString(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.FRIEND_NAME.a()));
		String description = cursorFriend_.getString(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.DESCRIPTION.a()));
		String friendScreenName = cursorFriend_.getString(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.FRIEND_SCREENNAME.a()));
		String backgroundProfileUrl = cursorFriend_.getString(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.a()));
		String bannerProfileUrl = cursorFriend_.getString(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.BANNER_PROFILE_IMAE_URL.a()));
		String profileImage = cursorFriend_.getString(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.PROFILE_IMAGE_URL.a()));
		long sinceID = cursorFriend_.getLong(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.COLUMN_SINCEID.a()));
		long maxID = cursorFriend_.getLong(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.COLUMN_MAXID.a()));
		long groupId = cursorFriend_.getLong(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.COLUMN_GROUP_ID.a()));
		long lastFriendPageNo = cursorFriend_.getLong(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.LAST_FRIEND_PAGE_NO.a()));
		int lastTimelineNo = cursorFriend_.getInt(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.LAST_TIMELINE_PAGE_NO.a()));
		int friendCount = cursorFriend_.getInt(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.FRIEND_COUNT.a()));
		boolean hasLoadedAllTweets = cursorFriend_.getInt(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.HAS_ALL_TWEETS_FOR_TODAY.a())) == 1;
		boolean isFriend = cursorFriend_.getInt(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.IS_FRIEND.a())) == 1;
        int newTweetCount = cursorFriend_.getInt(cursorFriend_
                .getColumnIndexOrThrow(FriendColumn.TOTAL_NEW_TWEETS.a()));
        int totalTweetCount = cursorFriend_.getInt(cursorFriend_
                .getColumnIndexOrThrow(FriendColumn.TWEET_COUNT.a()));
        int lastFriendIndex = cursorFriend_.getInt(cursorFriend_
                .getColumnIndexOrThrow(FriendColumn.COLUMN_LAST_FRIEND_INDEX.a()));
        int currentFriendCount = cursorFriend_.getInt(cursorFriend_
                .getColumnIndexOrThrow(FriendColumn.COLUMN_CURRENT_FRIEND_COUNT.a()));
		
		Log.v(TAG, "Since ID passed for query is : " + sinceID
				+ " with page number " + maxID + " with tweet count: " + newTweetCount);

		ParcelableUser friend = new ParcelableUser(friendID, friendName,sinceID, maxID);
		friend.setLastUpadateDate(cursorFriend_.getString(cursorFriend_
				.getColumnIndexOrThrow(FriendColumn.COLUMN_LAST_DATETIME_SYNC.a())));
		friend.setGroupId(groupId);
		friend.setScreenName(friendScreenName);
		friend.setPofileBackgroundImageUrl(backgroundProfileUrl);
		friend.setProfileBannerImageUrl(bannerProfileUrl);
		friend.setProfileImageUrl(profileImage);
		friend.setDescription(description);
		friend.setLastFriendPageNumber(lastFriendPageNo);
		friend.setFriendCount(friendCount);
		friend.hasLoadedAllTweetsForToday(false);
		friend.setLastTimelinePageNumber(lastTimelineNo);
		friend.setIsFriend(isFriend);
        friend.setNewTweetCount(newTweetCount);
        friend.setTotalTweetCount(totalTweetCount);
        friend.setLastFriendIndex(lastFriendIndex);
        friend.setCurrentFriendTotal(currentFriendCount);
        return friend;
		
	}


}
