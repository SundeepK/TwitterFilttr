package com.sun.tweetfiltrr.fragment.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.fragment.api.ATimelineFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public class TimelineTab extends ATimelineFragment {


    @Override
    protected Loader<Cursor> onCreateLoader(int arg0, Bundle arg1, ParcelableUser currentUser_) {
        String[] projection = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, TimelineDao.FULLY_QUALIFIED_PROJECTIONS);
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                TweetFiltrrProvider.CONTENT_URI_TIMELINE_FRIEND, projection, FriendColumn.FRIEND_ID.a() + " = ? ",
                new String[]{getCurrentUser().getUserId() + ""}, TimelineColumn.TWEET_ID.a() + " DESC " + " LIMIT " + getTimeLineCount() + "");

        return cursorLoader;
    }


    @Override
    public Collection<Callable<Collection<ParcelableUser>>> getTweetRetriever(ParcelableUser currentUser_, boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();
        callables.add(getTweetRetriver().getTimeLineRetriever(currentUser_, shouldRunOnce_, shouldLookForOldTweets));
        return callables;
    }



}
