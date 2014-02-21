package com.sun.tweetfiltrr.database.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.database.tables.KeywordGroupTable;
import com.sun.tweetfiltrr.database.tables.TimelineTable;
import com.sun.tweetfiltrr.database.tables.UsersToFollowersTable;
import com.sun.tweetfiltrr.utils.TwitterConstants;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;
import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;
import static com.sun.tweetfiltrr.database.tables.UsersToFollowersTable.UsersToFollowersColumn;
import static com.sun.tweetfiltrr.database.tables.UsersToFriendsTable.CREATE_DATABASE;
import static com.sun.tweetfiltrr.database.tables.UsersToFriendsTable.UsersToFriendsColumn;


public class TweetFiltrrDBHelper extends SQLiteOpenHelper{
	private static final String TAG = TweetFiltrrDBHelper.class.getName();
	public static final String DATABASE_NAME = "tweetFiltrr.db";
	
	public TweetFiltrrDBHelper(Context context) {
		super(context, DATABASE_NAME, null, TwitterConstants.DB_VERSION);
	
	}

	@Override
	public void onCreate(SQLiteDatabase database_) {
		database_.execSQL("DROP TABLE IF EXISTS " + FriendColumn.FRIEND_TABLE.s());
		database_.execSQL("DROP TABLE IF EXISTS " + UsersToFriendsColumn.USERS_TO_FRIENDS_TABLE.s());
		database_.execSQL("DROP TABLE IF EXISTS " + TimelineColumn.TIMELINE_TABLE.s());
		database_.execSQL("DROP TABLE IF EXISTS " + KeywordGroupColumn.KEYWORD_GROUP_TABLE.s());
        database_.execSQL("DROP TABLE IF EXISTS " + UsersToFollowersColumn.USERS_TO_FOLLOWERS_TABLE.s());

        database_.execSQL(FriendTable.CREATE_DATABASE);
		database_.execSQL(CREATE_DATABASE);
		database_.execSQL(TimelineTable.CREATE_DATABASE);
		database_.execSQL(KeywordGroupTable.CREATE_DATABASE);
        database_.execSQL(UsersToFollowersTable.CREATE_DATABASE);
        Log.v(TAG, "created DB");
		
//		int key  = 1234;
//		String sql = "INSERT INTO timelineTable (friendID, timelineText) VALUES (" + key + ",'TEST')";
//		database_.execSQL(sql);

//		int key  = 1;
//		String sql = "INSERT INTO keywordGroupTable (groupName, keywords) VALUES ('TEST','test')";
//		database_.execSQL(sql);
//		 key++;
//		String sql2 = "INSERT INTO keywordGroupTable (groupName, keywords) VALUES ('TEST2','test2')";
//		database_.execSQL(sql2);
//		String sql3 = "INSERT INTO keywordGroupTable ( groupName, keywords) VALUES ('TEST3','test3')";
//		database_.execSQL(sql3);
//		int key1  = 2345;
//		String sql2 = "INSERT INTO userTable (userID, userName, description) VALUES (" + key1 + ",'TEST1','test1')";
//		database_.execSQL(sql2);
//
//		


	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(TAG, "Upgrading database. Existing contents will be lost. ["
	            + oldVersion + "]->[" + newVersion + "]");
	    db.execSQL("DROP TABLE IF EXISTS " + FriendColumn.FRIEND_TABLE.s());
	    onCreate(db);
	}
	
	

}
