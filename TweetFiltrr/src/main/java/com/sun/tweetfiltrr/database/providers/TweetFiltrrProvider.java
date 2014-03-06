package com.sun.tweetfiltrr.database.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.sun.tweetfiltrr.database.utils.DBUtils;

import java.util.HashMap;
import java.util.Map;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.KeywordGroupTable.KeywordGroupColumn;
import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;
import static com.sun.tweetfiltrr.database.tables.UsersToFollowersTable.UsersToFollowersColumn;
import static com.sun.tweetfiltrr.database.tables.UsersToFriendsTable.UsersToFriendsColumn;

public class TweetFiltrrProvider extends ContentProvider {
    private static final String TAG = TweetFiltrrProvider.class.getName();
    private static final String AUTHORITY = "com.tweetfiltrr.database.TweetFiltrrProvider";

    public static final int FRIEND_TABLE = 1000;
    public static final int FRIEND_TABLE_ID = 1100;

    public static final int TIMELINE_TABLE = 2000;
    public static final int TIMELINE_TABLE_ID = 2100;

    public static final int TIMELINE_FRIEND_TABLE = 3000;
    public static final int TIMELINE_FRIEND_TABLE_ID = 3100;

    public static final int FREIND_GROUP_TABLE = 4000;
    public static final int FRIEND_GROUP_TABLE_ID = 4100;

    public static final int GROUP_TABLE = 5000;
    public static final int GROUP_TABLE_ID = 5100;

    public static final int USERS_TO_FRIENDS = 6000;
    public static final int USERS_TO_FRIENDS_ID = 6100;

    public static final int USERS = 6000;
    public static final int USERS_ID = 6100;

    public static final int USERS_TO_FOLLOWERS = 7000;
    public static final int USERS_TO_FOLLOWERS_ID = 7100;

    public static final int FRIENDS_LEFT_JOIN_KEYWORDS = 8000;

    private static final String FRIENDS_PATH = "friends";
    private static final String USER_TO_FRIENDS_PATH = "usersToFriends";
    private static final String USER_TO_FOLLOWERS_PATH = "usersToFollowers";
    private static final String TIMELINE_PATH = "timeline";
    private static final String TIMELINE_FRIEND_PATH = "friendTimeline";
    private static final String FREIND_GROUP_PATH = "friendGroup";
    private static final String GROUP_TABLE_PATH = "keywordGroup";
    private static final String USERS_TABLE_PATH = "users";
    private static final String FRIENDS_LEFT_GROUP = "friendsLeftJoinGroup";


    public static final Uri CONTENT_URI_FRIEND = Uri.parse("content://" + AUTHORITY + "/" + FRIENDS_PATH);
    public static final Uri CONTENT_URI_USER_TO_FRIEND = Uri.parse("content://" + AUTHORITY + "/" + USER_TO_FRIENDS_PATH);
    public static final Uri CONTENT_URI_USER_TO_FOLLOWERS = Uri.parse("content://" + AUTHORITY + "/" + USER_TO_FOLLOWERS_PATH);
    public static final Uri CONTENT_URI_TIMELINE = Uri.parse("content://" + AUTHORITY + "/" + TIMELINE_PATH);
    public static final Uri CONTENT_URI_TIMELINE_FRIEND = Uri.parse("content://" + AUTHORITY + "/" + TIMELINE_FRIEND_PATH);
    public static final Uri CONTENT_URI_FRIEND_GROUP = Uri.parse("content://" + AUTHORITY + "/" + FREIND_GROUP_PATH);
    public static final Uri CONTENT_URI_GROUP = Uri.parse("content://" + AUTHORITY + "/" + GROUP_TABLE_PATH);
    public static final Uri CONTENT_URI_USERS = Uri.parse("content://" + AUTHORITY + "/" + USERS_TABLE_PATH);
    public static final Uri CONTENT_URI_FRIENDS_LEFT_GROUP = Uri.parse("content://" + AUTHORITY + "/" + FRIENDS_LEFT_GROUP);


    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/tweetFiltrr";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/tweetFiltrr";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, FRIENDS_PATH, FRIEND_TABLE);
        sURIMatcher.addURI(AUTHORITY, FRIENDS_PATH + "/#", FRIEND_TABLE_ID);

        sURIMatcher.addURI(AUTHORITY, TIMELINE_PATH, TIMELINE_TABLE);
        sURIMatcher.addURI(AUTHORITY, TIMELINE_PATH + "/#", TIMELINE_TABLE_ID);

        sURIMatcher.addURI(AUTHORITY, TIMELINE_FRIEND_PATH, TIMELINE_FRIEND_TABLE);
        sURIMatcher.addURI(AUTHORITY, TIMELINE_FRIEND_PATH + "/#", TIMELINE_FRIEND_TABLE_ID);

        sURIMatcher.addURI(AUTHORITY, FREIND_GROUP_PATH, FREIND_GROUP_TABLE);
        sURIMatcher.addURI(AUTHORITY, FREIND_GROUP_PATH + "/#", FRIEND_GROUP_TABLE_ID);


        sURIMatcher.addURI(AUTHORITY, GROUP_TABLE_PATH, GROUP_TABLE);
        sURIMatcher.addURI(AUTHORITY, GROUP_TABLE_PATH + "/#", GROUP_TABLE_ID);

        sURIMatcher.addURI(AUTHORITY, USER_TO_FRIENDS_PATH, USERS_TO_FRIENDS);
        sURIMatcher.addURI(AUTHORITY, USER_TO_FRIENDS_PATH + "/#", USERS_TO_FRIENDS_ID);

        sURIMatcher.addURI(AUTHORITY, USER_TO_FOLLOWERS_PATH, USERS_TO_FOLLOWERS);
        sURIMatcher.addURI(AUTHORITY, USER_TO_FOLLOWERS_PATH + "/#", USERS_TO_FOLLOWERS_ID);

        sURIMatcher.addURI(AUTHORITY, USERS_TABLE_PATH, USERS);
        sURIMatcher.addURI(AUTHORITY, USERS_TABLE_PATH + "/#", USERS_ID);

        sURIMatcher.addURI(AUTHORITY, FRIENDS_LEFT_GROUP, FRIENDS_LEFT_JOIN_KEYWORDS);


    }

    Map<String, String> _columnMap;

    private SQLiteOpenHelper _tweetFiltrr;

    @Override
    public boolean onCreate() {
        _tweetFiltrr = new TweetFiltrrDBHelper(getContext());
        return false;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = _tweetFiltrr.getWritableDatabase();
        int rowsAffected = 0;
        switch (sURIMatcher.match(uri)) {
            case GROUP_TABLE_ID:
                rowsAffected = sqlDB.delete(KeywordGroupColumn.KEYWORD_GROUP_TABLE.s(), selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        return rowsAffected;
    }


    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        SQLiteDatabase sqlDB = _tweetFiltrr.getWritableDatabase();
        switch (sURIMatcher.match(uri)) {
            case FRIEND_TABLE_ID:

                updateOrInsertContentValues(sqlDB, values, uri, FriendColumn.FRIEND_TABLE.s(), FriendColumn.FRIEND_ID.s() + "=?", new String[]{FriendColumn.FRIEND_ID.s()});
                break;

            case TIMELINE_TABLE_ID:
                updateOrInsertContentValues(sqlDB, values, uri, TimelineColumn.TIMELINE_TABLE.s(), TimelineColumn.TWEET_ID.s() + "=?", new String[]{TimelineColumn.TWEET_ID.s()});
                break;

            case GROUP_TABLE_ID:
                updateOrInsertContentValues(sqlDB, values, uri, KeywordGroupColumn.KEYWORD_GROUP_TABLE.s(), KeywordGroupColumn.COLUMN_ID.s() + "=?", new String[]{KeywordGroupColumn.COLUMN_ID.s()});
                break;

            case USERS_TO_FRIENDS_ID:
                updateOrInsertContentValues(sqlDB, values, uri, UsersToFriendsColumn.USERS_TO_FRIENDS_TABLE.s(), UsersToFriendsColumn.FRIEND_ID.s() + "=? " + " AND " + UsersToFriendsColumn.USER_ID.s() + "=? ", new String[]{UsersToFriendsColumn.FRIEND_ID.s(), UsersToFriendsColumn.USER_ID.s()});
                break;

            case USERS_TO_FOLLOWERS_ID:
                updateOrInsertContentValues(sqlDB, values, uri, UsersToFollowersColumn.USERS_TO_FOLLOWERS_TABLE.s(), UsersToFollowersColumn.FOLLOWER_ID.s() + "=? " + " AND " + UsersToFollowersColumn.USER_ID.s() + "=? ", new String[]{UsersToFollowersColumn.FOLLOWER_ID.s(), UsersToFollowersColumn.USER_ID.s()});
                break;


        }

        return values.length;


    }

    private Map<String, String> ensureMap() {
        if (_columnMap == null) {
            _columnMap = new HashMap<String, String>();

            _columnMap.putAll(DBUtils.getAliasProjectionMap(FriendColumn.values()));
            _columnMap.putAll(DBUtils.getAliasProjectionMap(KeywordGroupColumn.values()));
            _columnMap.putAll(DBUtils.getAliasProjectionMap(UsersToFriendsColumn.values()));
            _columnMap.putAll(DBUtils.getAliasProjectionMap(TimelineColumn.values()));
            _columnMap.putAll(DBUtils.getAliasProjectionMap(UsersToFollowersColumn.values()));


            _columnMap.put("friendTable._id", "friendTable._id as _id");
//		_columnMap.put("friendTable.friendID", "friendTable.friendID as friendTable_friendID");
//		_columnMap.put("friendTable.friendName", "friendTable.friendName as friendTable_friendName");
//		_columnMap.put("friendTable.friendScreenName", "friendTable.friendScreenName as friendTable_friendScreenName");
//
//		_columnMap.put("friendTable.friendKeywords", "friendTable.friendKeywords as friendTable_friendKeywords");
//		_columnMap.put("friendTable.description", "friendTable.description as friendTable_description");
//		_columnMap.put("friendTable.friendKeywords", "friendTable.friendKeywords as friendTable_friendKeywords");
//		_columnMap.put("friendTable.regex", "friendTable.regex as friendTable_regex");
//		_columnMap.put("friendTable.sinceID", "friendTable.sinceID as friendTable_sinceID");	
//		_columnMap.put("friendTable.maxID", "friendTable.maxID as friendTable_maxID");
//		_columnMap.put("friendTable.backgroundProfileUrl", "friendTable.backgroundProfileUrl as friendTable_backgroundProfileUrl");
//		_columnMap.put("friendTable.bannerProfileUrl", "friendTable.bannerProfileUrl as friendTable_bannerProfileUrl");
//		_columnMap.put("friendTable.profileImageUrl", "friendTable.profileImageUrl as friendTable_profileImageUrl");
//		_columnMap.put("friendTable.lastDateTimeSync", "friendTable.lastDateTimeSync as friendTable_lastDateTimeSync");
//		_columnMap.put("friendTable.groupID", "friendTable.groupID as friendTable_groupID");

//		_columnMap.put("keywordGroupTable._id", "keywordGroupTable._id as group_id");
//		_columnMap.put("usersToFriendsTable._id", "usersToFriendsTable._id as usersToFriend_id");
//		_columnMap.put("usersToFriendsTable.userID", "usersToFriendsTable.userID as usersToFriend_userID");
//		_columnMap.put("usersToFriendsTable.friendID", "usersToFriendsTable.friendID as usersToFriend_friendID");
//		_columnMap.put("userTable.userID", "userTable.userID as userTable_userID");


        }

        return _columnMap;
    }

    private void updateOrInsertContentValues(SQLiteDatabase database_, ContentValues[] contentValues_, Uri uri_, String tableName_, String selection_, String[] selectionArgs_) {
        database_.beginTransaction();
        String[] selectionArgs = new String[selectionArgs_.length];
        try {

            for (ContentValues cv : contentValues_) {

                for (int i = 0; i < selectionArgs_.length; i++) {
                    selectionArgs[i] = cv.getAsString(selectionArgs_[i]);
                }

                int affected = database_.update(tableName_,
                        cv, selection_, selectionArgs);

                if (affected == 0) {
                    long id = database_.insertWithOnConflict(tableName_, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                    Log.v(TAG, "the ID returned " + id);

                    if (id <= 0) {
                        throw new SQLException("Failed to insert row into " + uri_);
                    }
                }
            }
            database_.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri_, null);
            getContext().getContentResolver().notifyChange(CONTENT_URI_TIMELINE_FRIEND, null);
            getContext().getContentResolver().notifyChange(CONTENT_URI_USER_TO_FRIEND, null);
            getContext().getContentResolver().notifyChange(CONTENT_URI_USER_TO_FOLLOWERS, null);
            getContext().getContentResolver().notifyChange(CONTENT_URI_FRIENDS_LEFT_GROUP, null);

        } finally {
            database_.endTransaction();
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = _tweetFiltrr.getWritableDatabase();
        switch (sURIMatcher.match(uri)) {
            case FRIEND_TABLE_ID:
                long id = db.insert(FriendColumn.FRIEND_TABLE.s(), null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(CONTENT_URI_FRIEND + "/" + id);

            case TIMELINE_TABLE_ID:
                long id2 = db.insert(TimelineColumn.TIMELINE_TABLE.s(), null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(CONTENT_URI_TIMELINE + "/" + id2);

            case GROUP_TABLE_ID:
                long id3 = db.insert(KeywordGroupColumn.KEYWORD_GROUP_TABLE.s(), null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.parse(CONTENT_URI_TIMELINE + "/" + id3);

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = sURIMatcher.match(uri);
        Log.v(TAG, "URI sent for query is " + uri.toString() + " with id " + uriType);
        queryBuilder.setProjectionMap(ensureMap());

        switch (uriType) {
            case GROUP_TABLE:
                queryBuilder.setProjectionMap(null);

                queryBuilder.setTables(KeywordGroupColumn.KEYWORD_GROUP_TABLE.s());

                break;

            case GROUP_TABLE_ID:
                queryBuilder.setProjectionMap(ensureMap());

//                queryBuilder.setProjectionMap(null);

                queryBuilder.setTables(KeywordGroupColumn.KEYWORD_GROUP_TABLE.s());
                queryBuilder.appendWhere(KeywordGroupColumn.KEYWORD_GROUP_TABLE.s() + "."
                        + KeywordGroupColumn.COLUMN_ID.s() + "=" + uri.getLastPathSegment());

                break;

            case FREIND_GROUP_TABLE:
                queryBuilder.setProjectionMap(ensureMap());
                //	queryBuilder.setProjectionMap(null);

                queryBuilder.setTables(
                        FriendColumn.FRIEND_TABLE.s() + " JOIN " + KeywordGroupColumn.KEYWORD_GROUP_TABLE.s()
                                + " ON "
                                + FriendColumn.FRIEND_TABLE.s() + "." + FriendColumn.COLUMN_GROUP_ID.s() + " = "
                                + KeywordGroupColumn.KEYWORD_GROUP_TABLE.s() + "." + KeywordGroupColumn.COLUMN_ID.s());
                break;

            case FRIEND_GROUP_TABLE_ID:
                queryBuilder.setProjectionMap(ensureMap());
                //queryBuilder.setProjectionMap(null);

                queryBuilder.setTables(
                        FriendColumn.FRIEND_TABLE.s() + " JOIN " + KeywordGroupColumn.KEYWORD_GROUP_TABLE.s()
                                + " ON "
                                + FriendColumn.FRIEND_TABLE.s() + "." + FriendColumn.COLUMN_GROUP_ID.s() + " = "
                                + KeywordGroupColumn.KEYWORD_GROUP_TABLE.s() + "." + KeywordGroupColumn.COLUMN_ID.s());
                queryBuilder.appendWhere(FriendColumn.FRIEND_TABLE.s() + "." + FriendColumn._ID.s() + "=" + uri.getLastPathSegment());
                break;

            case USERS_TO_FRIENDS:
                Log.v(TAG, " Im in USERS_TO_FRIENDS");
                queryBuilder.setProjectionMap(ensureMap());

                queryBuilder.setTables(
                        FriendColumn.FRIEND_TABLE.s() + " JOIN " + UsersToFriendsColumn.USERS_TO_FRIENDS_TABLE.s()
                                + " ON "
                                + FriendColumn.FRIEND_TABLE.s() + "." + FriendColumn.FRIEND_ID.s() + " = "
                                + UsersToFriendsColumn.USERS_TO_FRIENDS_TABLE.s() + "." + UsersToFriendsColumn.FRIEND_ID.s()
                );



                break;

            case USERS_TO_FRIENDS_ID:
                Log.v(TAG, " Im in USERS_TO_FRIENDS");
                queryBuilder.setTables(
                        FriendColumn.FRIEND_TABLE.s() + " JOIN " + FriendColumn.FRIEND_TABLE.s()
                                + " ON "
                                + FriendColumn.FRIEND_TABLE.s() + "." + FriendColumn.FRIEND_ID.s() + " = "
                                + UsersToFriendsColumn.USERS_TO_FRIENDS_TABLE.s() + "." + UsersToFriendsColumn.FRIEND_ID.s()

                );
                queryBuilder.appendWhere(UsersToFriendsColumn.USERS_TO_FRIENDS_TABLE.s() + "." + UsersToFriendsColumn._ID.s() + "=" + uri.getLastPathSegment());
                break;

            case USERS_TO_FOLLOWERS:
                Log.v(TAG, " Im in USERS_TO_FOLLOWERS");
                queryBuilder.setProjectionMap(ensureMap());

                queryBuilder.setTables(
                        FriendColumn.FRIEND_TABLE.s() + " JOIN "
                                + UsersToFollowersColumn.USERS_TO_FOLLOWERS_TABLE.s()
                                + " ON "
                                + FriendColumn.FRIEND_TABLE.s() + "." + FriendColumn.FRIEND_ID.s() + " = "
                                + UsersToFollowersColumn.USERS_TO_FOLLOWERS_TABLE.s()
                                + "." + UsersToFollowersColumn.FOLLOWER_ID.s()
                );
                break;

            case  USERS_TO_FOLLOWERS_ID:
                Log.v(TAG, " Im in USERS_TO_FOLLOWERS_ID");
                queryBuilder.setTables(
                        FriendColumn.FRIEND_TABLE.s() + " JOIN "
                                + UsersToFollowersColumn.USERS_TO_FOLLOWERS_TABLE.s()
                                + " ON "
                                + FriendColumn.FRIEND_TABLE.s() + "." + FriendColumn.FRIEND_ID.s() + " = "
                                + UsersToFollowersColumn.USERS_TO_FOLLOWERS_TABLE.s()
                                + "." + UsersToFollowersColumn.FOLLOWER_ID.s()
                );

                queryBuilder.appendWhere(UsersToFollowersColumn.USERS_TO_FOLLOWERS_TABLE.s()
                        + "." + UsersToFollowersColumn._ID.s()
                        + "=" + uri.getLastPathSegment());
                break;

            case FRIEND_TABLE_ID:
                queryBuilder.setTables(FriendColumn.FRIEND_TABLE.s());
                Log.v(TAG, " Im in FRIEND_TABLE_ID");

                queryBuilder.appendWhere(FriendColumn._ID.s() + "="
                        + uri.getLastPathSegment());
                break;
            case TIMELINE_TABLE:
               queryBuilder.setProjectionMap(ensureMap());
//                queryBuilder.setProjectionMap(null);

                Log.v(TAG, " Im in TIMELINE_TABLE");
                queryBuilder.setTables(TimelineColumn.TIMELINE_TABLE.s());

                break;
            case TIMELINE_TABLE_ID:
//                queryBuilder.setProjectionMap(null);
                queryBuilder.setProjectionMap(ensureMap());

                Log.v(TAG, " Im in TIMELINE_TABLE");
                queryBuilder.setTables(TimelineColumn.TIMELINE_TABLE.s());
                queryBuilder.appendWhere(TimelineColumn._ID.s() + "="
                        + uri.getLastPathSegment());

                break;
            case FRIEND_TABLE:
                queryBuilder.setTables(FriendColumn.FRIEND_TABLE.s());
                Log.v(TAG, " Im in FRIEND_TABLE");

                break;
            case TIMELINE_FRIEND_TABLE:
                Log.v(TAG, " Im in TIMELINE_FRIEND_TABLE");

                queryBuilder.setProjectionMap(ensureMap());

                queryBuilder.setTables(
                        FriendColumn.FRIEND_TABLE.s() + " JOIN " + TimelineColumn.TIMELINE_TABLE.s()
                                + " ON "
                                + FriendColumn.FRIEND_TABLE.s() + "." + FriendColumn.FRIEND_ID.s() + " = "
                                + TimelineColumn.TIMELINE_TABLE.s() + "." + TimelineColumn.FRIEND_ID.s()

                );
                break;
            case TIMELINE_FRIEND_TABLE_ID:
                Log.v(TAG, " Im in TIMELINE_FRIEND_TABLE_ID");
                queryBuilder.setProjectionMap(ensureMap());

                queryBuilder.setTables(
                        FriendColumn.FRIEND_TABLE.s() + " JOIN " + TimelineColumn.TIMELINE_TABLE.s()
                                + " ON "
                                + FriendColumn.FRIEND_TABLE.s() + "." + FriendColumn.FRIEND_ID.s() + " = "
                                + TimelineColumn.TIMELINE_TABLE.s() + "." + TimelineColumn.FRIEND_ID.s()

                );

                queryBuilder.appendWhere(TimelineColumn.TIMELINE_TABLE.s() + "." + TimelineColumn._ID.s() + "=" + uri.getLastPathSegment());


                Log.v(TAG, "Friend ID " + uri.getLastPathSegment());

            case FRIENDS_LEFT_JOIN_KEYWORDS:
                queryBuilder.setProjectionMap(ensureMap());

                queryBuilder.setTables(
                        FriendColumn.FRIEND_TABLE.s() + " LEFT JOIN " + KeywordGroupColumn.KEYWORD_GROUP_TABLE.s()
                                + " ON "
                                + FriendColumn.FRIEND_TABLE.s() + "." + FriendColumn.COLUMN_GROUP_ID.s() + " = "
                                + KeywordGroupColumn.KEYWORD_GROUP_TABLE.s() + "." + KeywordGroupColumn.COLUMN_ID.s());
                break;

            default:
                Log.v(TAG, "unknown URI is: " +  uriType);
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(_tweetFiltrr.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        //	queryBuilder.setProjectionMap(null);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

}
