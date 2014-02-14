package com.sun.tweetfiltrr.fragment.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.UserFollowersDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.fragment.api.AUsersFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.callables.FollowersRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UsersFollowerRetriever;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.UsersToFollowersTable.UsersToFollowersColumn;


public class FollowersTab extends AUsersFragment {
    private static final String TAG = FollowersTab.class.getName();


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

        String[] pro = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, UserFollowersDao.FULLY_QUALIFIED_PROJECTIONS);
        Log.v(TAG, "on loader create user is: " + getCurrentUser().toString());
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                TweetFiltrrProvider.CONTENT_URI_USER_TO_FOLLOWERS, pro,
                UsersToFollowersColumn.USERS_TO_FOLLOWERS_TABLE.s() + "." + UsersToFollowersColumn.USER_ID.s() + "=?",
                new String[]{"" + getCurrentUser().getUserId()}, FriendColumn._ID.s() + " ASC " + " LIMIT " + getTimeLineCount() + "");
        return cursorLoader;
    }

    @Override
    public Collection<Callable<Collection<ParcelableUser>>> getTweetRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();
        ITwitterAPICall<Collection<ParcelableUser>> followerRetriver = new UsersFollowerRetriever(true);
        callables.add(new FollowersRetriever(getCurrentUser(), followerRetriver, this));
        return callables;
    }


}

