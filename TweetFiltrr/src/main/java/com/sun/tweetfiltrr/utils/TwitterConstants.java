package com.sun.tweetfiltrr.utils;

import android.app.AlarmManager;
import android.os.SystemClock;

public class TwitterConstants {

    public static String TWITTER_OAUTH_KEY = "";
    public static String TWITTER_OAUTH_SECRET = "";
    public static String TWITTER_CONSUMER_KEY = "";
    public static String TWITTER_CONSUMER_SECRET = "";
    public static String TWITTER_CURRENT_TAB = "CURRENT_TAB";
    public static final String ON_NEW_TAB_BROADCAST = "ON_NEW_TAB_BROADCAST";
    public static String TWITTER_CALLBACK_URL = "oauth://twitterfiltrr.Twitter_oAuth";
    public static String TWITTER_AUTH_URL = " https://twitterfiltrr.com";
    public static String URL_PARAMETER_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static String PREFERENCE_TWITTER_OAUTH_TOKEN = "TWITTER_OAUTH_TOKEN";
    public static String PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET = "TWITTER_OAUTH_TOKEN_SECRET";
    public static String PREFERENCE_TWITTER_IS_LOGGED_IN = "TWITTER_IS_LOGGED_IN";
    public static final int FRIENDS_LIST = 1;
    public static final String FRIENDS_BUNDLE = "FRIENDS_BUNDLE";
    public static final String FRIENDS_ROWID = "FRIENDS_ROWID";
    public static final int ADD_FRIEND = 2;
    public static final int SEND_NOTIFICATION = 3;
    public static final int FLUSH_TIMELINE_TO_DB = 4;
    public static final int FLUSH_TIMELINE_FRIENDS_TO_DB_SEND_NOTIFICATION = 5;
    public static final int FLUSH_FRIENDS_TO_DB = 6;
    public static final int ROWS_TO_SHOW = 20;
    public static final int DB_VERSION = 1;
    public static final String SIMPLE_DATE_FORMATE = "yyyy-MM-dd HH:mm:ss";
    public static final long ALARM_TRIGGER_AT_TIME = SystemClock.elapsedRealtime() + 60000;
    public static final long ALARM_INTERVAL = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
    public static final long MAX_UPDATE_TIME = 5;
    public static final String PARCELABLE_FRIEND_WITH_TIMELINE = "FRIEND_WITH_TIMELINE";
    public static final String PARCELABLE_USER_QUEUE = "PARCELABLE_USER_QUEUE";
    public static final String IS_QUOTE_REPLY = "IS_QUOTE_REPLY";
    public static final String PARCELABLE_KEYWORDGROUP_BUNDLE = "KEYWORD_GROUP";
    public static final String PARCELABLE_TWEET = "PARCELABLE_TWEET";
    public static final String ON_BROADCAST_GROUP_SELECTED = "KEYWORD_GROUP";
    public static final String ON_NEW_FRIEND_BROADCAST = "ON_NEW_FRIEND";
    public static final String AUTH_USER_ID = "AUTH_USER_ID";
    public static final String AUTH_USER_SCREEN_BG = "LOGIN_SCREEN_BG";
    public static final String LOGIN_PROFILE_BG = "LOGIN_PROFILE_BG";
    public static final String AUTH_USER_DESC_BG = "LOGIN_DESC_BG";
    public static final String AUTH_USER_NAME_BG = "LOGIN_NAME_BG";
    public static final String SHOULD_LOOK_IF_NO_GROUP = "SHOULD_LOOK_IF_NO_FILTER";
    public static final String SHOULD_LOOK_IF_NO_RESULTS = "SHOULD_LOOK_IF_NO_RESULTS";
    public static final String SIC_SAVE_DIRECTORY = "/storage/sdcard0/Pictures/twitterFiltrr";


}
