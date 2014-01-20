package com.sun.tweetfiltrr.fragment.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sun.tweetfiltrr.asyncretriever.api.ITwitterRetriever;
import com.sun.tweetfiltrr.asyncretriever.api.UsersFollowerRetriever;
import com.sun.tweetfiltrr.asyncretriever.callables.FollowersRetriever;
import com.sun.tweetfiltrr.concurrent.AsyncFutureDBUpdatetask;
import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.UserFollowersDao;
import com.sun.tweetfiltrr.database.dbupdater.impl.SimpleDBUpdater;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.fragment.api.UsersTab;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.UsersToFollowersTable.UsersToFollowersColumn;


public class FollowersTab extends UsersTab{
    private static final String TAG = FollowersTab.class.getName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
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
    public void onLoad(Collection<Future<Collection<ParcelableUser>>>  futureTask_) {
        Log.v(TAG, "On load startyed with future size:" + futureTask_.size());

        DaoFlyWeightFactory flyWeight = DaoFlyWeightFactory.getInstance(getActivity().getContentResolver());


        IDBDao<ParcelableUser> _usersToFriendDao = (IDBDao<ParcelableUser>)
                flyWeight.getDao(DaoFlyWeightFactory.DaoFactory.USER_FOLLOWER_DAO, getCurrentUser());
        IDBDao<ParcelableUser> _friendDao = (IDBDao<ParcelableUser>)
                flyWeight.getDao(DaoFlyWeightFactory.DaoFactory.FRIEND_DAO, getCurrentUser());

        SimpleDBUpdater<ParcelableUser> _userUpdater = new SimpleDBUpdater<ParcelableUser>();

        Collection<IDBDao<ParcelableUser>> daos = new ArrayList<IDBDao<ParcelableUser>>();
        daos.add(_usersToFriendDao);
        daos.add(_friendDao);


        AsyncFutureDBUpdatetask<ParcelableUser, Integer> updatetask =
                new AsyncFutureDBUpdatetask<ParcelableUser, Integer>(1, TimeUnit.MINUTES, daos, _userUpdater);
        updatetask.execute(futureTask_.toArray(new Future[futureTask_.size()]) );
    }


    @Override
    public Collection<Callable<Collection<ParcelableUser>>> getTweetRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();
        ITwitterRetriever<Collection<ParcelableUser>> followerRetriver = new UsersFollowerRetriever(true);
        callables.add(new FollowersRetriever(getCurrentUser(), followerRetriver));
        return callables;
    }

}

