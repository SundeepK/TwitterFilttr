package com.sun.tweetfiltrr.database.tables;


import com.sun.tweetfiltrr.utils.TwitterConstants;

public class KeywordGroupTable {
	
	public enum KeywordGroupColumn implements FriendKeywordDBColumn{
		 KEYWORD_GROUP_TABLE ("keywordGroupTable"),
		 COLUMN_GROUP_NAME ( "groupName" ),
		 COLUMN_ID ("_id"),
		 COLUMN_KEYWORDS ( "keywords" );

		 private String _col;
		 
		 KeywordGroupColumn (String col_){
			 _col = col_;
		 }


		@Override
		public String s() {
			return _col;
		}


		@Override
		public String a() {
			return KEYWORD_GROUP_TABLE.s() + "_" + _col;
		}


		@Override
		public String p() {
			return KEYWORD_GROUP_TABLE.s() + "." + _col;
		}


		@Override
		public String tableName() {
			return KEYWORD_GROUP_TABLE.s();
		}

	}

	public static final int DATABASE_VERSION = TwitterConstants.DB_VERSION;
	
	// Database creation sql statement
	public static final String CREATE_DATABASE = "create table "
			+ KeywordGroupColumn.KEYWORD_GROUP_TABLE.s() + " ( " + KeywordGroupColumn.COLUMN_ID.s()	+ " integer primary key autoincrement, "
			+ KeywordGroupColumn.COLUMN_GROUP_NAME.s() + " text not null , "
			+ KeywordGroupColumn.COLUMN_KEYWORDS.s() + " text );";
}
