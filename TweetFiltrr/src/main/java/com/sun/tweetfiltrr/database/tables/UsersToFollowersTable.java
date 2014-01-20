package com.sun.tweetfiltrr.database.tables;

public class UsersToFollowersTable {

	
	public enum UsersToFollowersColumn implements DBColumnName{
		
		USERS_TO_FOLLOWERS_TABLE("usersToFollowersTable"),
		 _ID ( "_id" ),
		 USER_ID ( "userID" ),
		 FOLLOWER_ID( "followerID" );

		 private String _col;

        UsersToFollowersColumn(String col_){
			 _col = col_;
		 }

		@Override
		public String s() {
		 return	_col.toString();
		}

		@Override
		public String a() {
			return USERS_TO_FOLLOWERS_TABLE.s() + "_" + _col;
		}

		@Override
		public String p() {
			return USERS_TO_FOLLOWERS_TABLE.s() + "." + _col;
		}

		@Override
		public String tableName() {
			return USERS_TO_FOLLOWERS_TABLE.s();
		}
 
		
	}
	
	// Database creation sql statement
	public static final String CREATE_DATABASE =
			"create table "	
	        + UsersToFollowersColumn.USERS_TO_FOLLOWERS_TABLE.s() +	" ( "
			+ UsersToFollowersColumn._ID.s() + " integer primary key autoincrement, "
			+ UsersToFollowersColumn.USER_ID.s()+ " integer not null, "
			+ UsersToFollowersColumn.FOLLOWER_ID.s() + " integer not null ) ;";
}
