package com.sun.tweetfiltrr.database.tables;

public interface DBColumnName {

	/**
	 * Get the string representation of the column
	 * @return
	 * 		String column
	 */
	public String s();
	
	
	/**
	 * Get the alias of the column used to reference columns with AS in the SQL
	 * @return
	 * 		String column
	 */
	public String a();
	
	/**
	 * Get the fully qualified name of the column, e.g FriendTable.friendName
	 * @return
	 * 		String qualified name
	 */
	public String p();
	
	public String tableName();
	
}
