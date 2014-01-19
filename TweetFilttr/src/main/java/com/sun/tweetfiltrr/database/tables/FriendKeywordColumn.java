package com.sun.tweetfiltrr.database.tables;

public enum FriendKeywordColumn implements DBColumnName {
	 COLUMN_FRIEND_ID (FriendTable.FriendColumn.FRIEND_ID.s()),
	 COLUMN_FRIEND_NAME (FriendTable.FriendColumn.FRIEND_NAME.s()),
	 COLUMN_DESCRIPTION (FriendTable.FriendColumn.DESCRIPTION.s()),
	 COLUMN_FRIEND_SCREENNAME (FriendTable.FriendColumn.FRIEND_SCREENNAME.s()),
	 COLUMN_SINCEID (FriendTable.FriendColumn.COLUMN_SINCEID.s()),
	 COLUMN_PAGE_NO (FriendTable.FriendColumn.COLUMN_MAXID.s()),//need to be fixed
	 COLUMN_IS_FRIEND(FriendTable.FriendColumn.IS_FRIEND.s()),//need to be fixed
	 COLUMN_MAXID(FriendTable.FriendColumn.COLUMN_MAXID.s()),//need to be fixed
	 COLUMN_PROFILE_IMAGE_URL ("profileImageUrl"),
	 COLUMN_LAST_DATETIME_SYNC  (FriendTable.FriendColumn.COLUMN_LAST_DATETIME_SYNC.s()), // YYYY-MM-DD HH:MM:SS.SSS
	 COLUMN_GROUP_ID  ("groupID"),
	 COLUMN_GROUP_NAME ( "groupName" ),
	 COLUMN_KEYWORDS ( "keywords" );


	 private String _col;
	 
	 FriendKeywordColumn (String col_){
		 _col = col_;
	 }

	@Override
	public String s() {
	 return	_col.toString();	 
	}

	@Override
	public String a() {
		return null;
	}

	@Override
	public String p() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String tableName() {
		// TODO Auto-generated method stub
		return null;
	}


	
	
}
