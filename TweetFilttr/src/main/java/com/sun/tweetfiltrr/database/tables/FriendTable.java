package com.sun.tweetfiltrr.database.tables;


import com.sun.tweetfiltrr.utils.TwitterConstants;

public class FriendTable {

    public enum FriendColumn implements FriendKeywordDBColumn {
        FRIEND_TABLE("friendTable"),
        _ID("_id"),
        FRIEND_ID("friendID"),
        FRIEND_NAME("friendName"),
        FRIEND_SCREENNAME("friendScreenName"),
        DESCRIPTION("description"),
        COLUMN_SINCEID("sinceID"),
        COLUMN_MAXID("maxID"),
        FRIEND_COUNT("friendCount"),
        TWEET_COUNT("totalTweetCount"),
        LAST_FRIEND_PAGE_NO("lastFriendPageNo"),
        LAST_TIMELINE_PAGE_NO("lastTimelinePageNo"),
        IS_FRIEND("isFriend"),
        PROFILE_IMAGE_URL("profileImageUrl"),
        HAS_ALL_TWEETS_FOR_TODAY("shouldLoadMoreTweets"),
        BACKGROUND_PROFILE_IMAGE_URL("backgroundProfileUrl"),
        BANNER_PROFILE_IMAE_URL("bannerProfileUrl"),
        COLUMN_LAST_DATETIME_SYNC("lastDateTimeSync"), // YYYY-MM-DD HH:MM:SS.SSS
        COLUMN_GROUP_ID("groupID"),
        COLUMN_LAST_FRIEND_INDEX("lastFriendIndex"),
        COLUMN_LAST_FRIEND_SYNC("lastFriendMultiple"),
        COLUMN_CURRENT_FRIEND_COUNT("currentFriendCount"),
        SINCEID_FOR_MENTIONS("sinceIDForMentions"),
        MAXID_FOR_MENTIONS("maxIDForMentions"),
        TOTAL_NEW_TWEETS("totalNewTweets");



        private String _col;

        FriendColumn(String col_) {
            _col = col_;
        }

        @Override
        public String s() {
            return _col.toString();
        }

        @Override
        public String a() {
            return FRIEND_TABLE.s() + "_" + _col;
        }

        @Override
        public String p() {
            return FRIEND_TABLE.s() + "." + _col;
        }

        @Override
        public String tableName() {
            return FRIEND_TABLE.s();
        }

    }

    public static final int DATABASE_VERSION = TwitterConstants.DB_VERSION;

    // Database creation sql statement
    public static final String CREATE_DATABASE = "create table "
            + FriendColumn.FRIEND_TABLE.s() + " ( " + FriendColumn._ID.s()
            + " integer primary key autoincrement, "
            + FriendColumn.FRIEND_ID.s() + " integer not null , "
            + FriendColumn.FRIEND_NAME.s() + " text not null , "
            + FriendColumn.FRIEND_SCREENNAME.s() + " text not null , "
            + FriendColumn.DESCRIPTION.s() + " text , "
            + FriendColumn.COLUMN_SINCEID.s() + " integer default 1 , "
            + FriendColumn.COLUMN_LAST_DATETIME_SYNC.s() + " text , "
            + FriendColumn.PROFILE_IMAGE_URL.s() + " text , "
            + FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s() + " text , "
            + FriendColumn.BANNER_PROFILE_IMAE_URL.s() + " text , "
            + FriendColumn.COLUMN_GROUP_ID.s() + " integer , "
            + FriendColumn.FRIEND_COUNT.s() + " integer , "
            + FriendColumn.TWEET_COUNT.s() + " integer , "
            + FriendColumn.IS_FRIEND.s() + " integer , "
            + FriendColumn.HAS_ALL_TWEETS_FOR_TODAY.s() + " integer , "
            + FriendColumn.LAST_FRIEND_PAGE_NO.s() + " integer default -1, "
            + FriendColumn.LAST_TIMELINE_PAGE_NO.s() + " integer , "
            + FriendColumn.TOTAL_NEW_TWEETS.s() + " integer , "
            + FriendColumn.COLUMN_LAST_FRIEND_INDEX.s() + " integer , "
            + FriendColumn.COLUMN_LAST_FRIEND_SYNC.s() + " integer , "
            + FriendColumn.COLUMN_CURRENT_FRIEND_COUNT.s() + " integer , "
            + FriendColumn.MAXID_FOR_MENTIONS.s() + " integer , "
            + FriendColumn.SINCEID_FOR_MENTIONS.s() + " integer , "
            + FriendColumn.COLUMN_MAXID.s() + " integer default 1 " + ");";
}
