package com.sun.tweetfiltrr.utils;

import android.app.Activity;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Sundeep on 10/01/14.
 */
public class UserRetrieverUtils {


    private static final String TAG = UserRetrieverUtils.class.getName();

    public static ParcelableUser getCurrentFocusedUser(Activity context_){

        ParcelableUser loggedInUser = getUserFromBundle(context_);

        if(loggedInUser == null){
            loggedInUser =  TwitterUtil.getInstance().getCurrentUser();
        }

        return loggedInUser;
    }

    /**
     * get most uptodate user from DB, if exsits
     * @param userIDBDao_
     * @param user_
     * @return
     */
    public  static Collection<ParcelableUser>   getUserFromDB(IDBDao<ParcelableUser> userIDBDao_, ParcelableUser user_){
       return
                userIDBDao_.getEntries(FriendTable.FriendColumn.FRIEND_ID.s()  + " = ? ",
                        new String[]{Long.toString(user_.getUserId())}, null);
    }

    public static ArrayList<ParcelableUser> getUserQueue(Activity context_){
        ArrayList<ParcelableUser> users = context_.getIntent().getParcelableArrayListExtra(TwitterConstants.PARCELABLE_USER_QUEUE);
        if(users == null){
            users = new ArrayList<ParcelableUser>();
        }
        return users;
    }

    public static ParcelableUser getUserFromBundle(Activity context_){
        return context_.getIntent().getParcelableExtra(TwitterConstants.FRIENDS_BUNDLE);
    }



}
