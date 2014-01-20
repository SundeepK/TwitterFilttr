package com.sun.tweetfiltrr.database.tables;

public class UsersToFriendsTable {

	
	public enum UsersToFriendsColumn implements DBColumnName{
		
		USERS_TO_FRIENDS_TABLE ("usersToFriendsTable"),
		 _ID ( "_id" ),
		 USER_ID ( "userID" ),
		 FRIEND_ID ( "friendID" );

		 private String _col;
		 
		 UsersToFriendsColumn (String col_){
			 _col = col_;
		 }

		@Override
		public String s() {
		 return	_col.toString();
		}

		@Override
		public String a() {
			return USERS_TO_FRIENDS_TABLE.s() + "_" + _col;
		}

		@Override
		public String p() {
			return USERS_TO_FRIENDS_TABLE.s() + "." + _col;
		}

		@Override
		public String tableName() {
			return USERS_TO_FRIENDS_TABLE.s();
		}
 
		
	}
	
	// Database creation sql statement
	public static final String CREATE_DATABASE =
			"create table "	
	        + UsersToFriendsColumn.USERS_TO_FRIENDS_TABLE.s() +	" ( "
			+ UsersToFriendsColumn._ID.s() + " integer primary key autoincrement, "
			+ UsersToFriendsColumn.USER_ID.s()+ " integer not null, "
			+ UsersToFriendsColumn.FRIEND_ID.s() + " integer not null ) ;";

}
