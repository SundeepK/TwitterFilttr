package com.sun.tweetfiltrr.database.tables;

public class TimelineTable {
	
	public enum TimelineColumn implements DBColumnName{
		TIMELINE_TABLE ( "timelineTable"),
		_ID ( "_id"),
		FRIEND_ID ( "friendID"),
		TWEET_ID("tweetID"),
		TIMELINE_TEXT ("timelineText"),
		DATETIME_INSERTED ( "dateTimeInserted"), // YYYY-MM-DD HH:MM:SS.SSS
		IN_REPLY_SCREENNAME ( "inReplyToScreenName"),
		IN_REPLY_USERID("inReplyToUserId"),
		IN_REPLY_TWEETID("inReplyToTweetId"),
        IS_RETWEETED("isRetweeted"),
        PHOTO_URL("photoUrl"),
        IS_KEYWORD_SEARCH_TWEET("isKeyworkSearchedTweet"),
        IS_MENTION("isMention"),
        IS_FAVOURITE("isFavourite");


        private String _col;
		 
		 TimelineColumn (String col_){
			 _col = col_;
		 }

		@Override
		public String s() {
		 return	_col.toString();
		}

		@Override
		public String a() {
			return TIMELINE_TABLE.s() + "_" + _col;
		}

		@Override
		public String p() {
			return TIMELINE_TABLE.s() + "." + _col;
		}

		@Override
		public String tableName() {
			return TIMELINE_TABLE.s();
		}
		

		 
		
	}

	// Database creation sql statement
	public static final String CREATE_DATABASE = "create table "
			+ TimelineColumn.TIMELINE_TABLE.s() + " ( " 
			+ TimelineColumn._ID.s()	+ " integer primary key autoincrement, " 
			+ TimelineColumn.FRIEND_ID.s()	+ " integer , "
			+ TimelineColumn.TIMELINE_TEXT.s()  + " text , "
			+ TimelineColumn.TWEET_ID.s()  + "  integer not null , "
			+ TimelineColumn.IN_REPLY_SCREENNAME.s()  + " text , "
            + TimelineColumn.PHOTO_URL.s()  + " text , "
            + TimelineColumn.IN_REPLY_USERID.s()  + " integer , "
			+ TimelineColumn.IN_REPLY_TWEETID.s()  + " integer , "
            + TimelineColumn.IS_FAVOURITE.s()  + " integer , "
            + TimelineColumn.IS_RETWEETED.s()  + " integer , "
            + TimelineColumn.IS_MENTION.s()  + " integer , "
            + TimelineColumn.IS_KEYWORD_SEARCH_TWEET.s()  + " integer default 0 , "
            + TimelineColumn.DATETIME_INSERTED.s() + " text " + ");";

}
