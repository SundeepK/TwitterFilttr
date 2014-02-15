package com.sun.tweetfiltrr.fragment.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.UserFollowersDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.database.dbupdater.impl.DatabaseUpdater;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.fragment.api.AUsersFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.twitter.api.ITwitterAPICall;
import com.sun.tweetfiltrr.twitter.callables.FollowersRetriever;
import com.sun.tweetfiltrr.twitter.twitterretrievers.api.UsersFollowerRetriever;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.UsersToFollowersTable.UsersToFollowersColumn;


public class FollowersTab extends AUsersFragment {
    private static final String TAG = FollowersTab.class.getName();
    private final static int ID = 0x011;

    @Inject FriendDao _friendDao;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        rootView.setId(ID);
        return rootView;
    }

    @Override
    protected int getLoaderID() {
        return 0x025;
    }

    @Override
    protected Collection<IDatabaseUpdater> getDBUpdaters() {
        Collection<IDatabaseUpdater> updaters = new ArrayList<IDatabaseUpdater>();
        DaoFlyWeightFactory flyWeight = DaoFlyWeightFactory.getInstance(getActivity().getContentResolver());
        String[] cols = new String[]{FriendColumn.FRIEND_ID.s(), FriendColumn.FRIEND_NAME.s(), FriendColumn.FRIEND_SCREENNAME.s(),
                FriendColumn.FOLLOWER_COUNT.s(), FriendColumn.LAST_FOLLOWER_PAGE_NO.s(),
                FriendColumn.COLUMN_CURRENT_FOLLOWER_COUNT.s(), FriendColumn.LAST_FOLLOWER_PAGE_NO.s(),
                FriendColumn.IS_FRIEND.s(), FriendColumn.PROFILE_IMAGE_URL.s(), FriendColumn.BACKGROUND_PROFILE_IMAGE_URL.s(),
                FriendColumn.BANNER_PROFILE_IMAE_URL.s(), FriendColumn.COLUMN_LAST_DATETIME_SYNC.s(),
                FriendColumn.DESCRIPTION.s()};
        IDBDao<ParcelableUser> followersDao=   (IDBDao<ParcelableUser>)
                flyWeight.getDao(DaoFlyWeightFactory.DaoFactory.USER_FOLLOWER_DAO, getCurrentUser());
        updaters.add(new DatabaseUpdater(_friendDao, cols));
        updaters.add(new DatabaseUpdater(followersDao));
        return updaters;
    }

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
    public Collection<Callable<Collection<ParcelableUser>>> getUsersRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();
        ITwitterAPICall<Collection<ParcelableUser>> followerRetriver = new UsersFollowerRetriever(true);
        callables.add(new FollowersRetriever(getCurrentUser(), followerRetriver, this));
        return callables;
    }


}

