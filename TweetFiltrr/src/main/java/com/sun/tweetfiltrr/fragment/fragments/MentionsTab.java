package com.sun.tweetfiltrr.fragment.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.sun.tweetfiltrr.database.utils.DBUtils;
import com.sun.tweetfiltrr.database.dao.impl.FriendDao;
import com.sun.tweetfiltrr.database.dao.impl.TimelineDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.fragment.api.ATimelineFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public class MentionsTab extends ATimelineFragment {

    private static String TAG = MentionsTab.class.getName();

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] projection = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, TimelineDao.FULLY_QUALIFIED_PROJECTIONS);
        Log.v(TAG, "current user mentions :" + getCurrentUser());
        CursorLoader cursorLoader = new CursorLoader(getActivity(),
                TweetFiltrrProvider.CONTENT_URI_TIMELINE_FRIEND, projection, TimelineColumn.IS_MENTION.a() + " = ? " ,
                new String[]{"1"}, TimelineColumn.TWEET_ID.a() + " DESC " + " LIMIT " + getTimeLineCount() + "");
        cursorLoader.getSelection();
        return cursorLoader;
    }

    @Override
    public Collection<Callable<Collection<ParcelableUser>>> getUsersRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();
        callables.add(getTweetRetriver().getMentionsRetriever(getCurrentUser(), shouldRunOnce_, shouldLookForOldTweets, this));
        return callables;
    }

    @Override
    protected int getLoaderID() {
        return 0x55;
    }

}
