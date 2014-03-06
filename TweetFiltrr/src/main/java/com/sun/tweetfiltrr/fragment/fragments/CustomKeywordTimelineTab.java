package com.sun.tweetfiltrr.fragment.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import com.sun.tweetfiltrr.R;
import com.sun.tweetfiltrr.customviews.views.ZoomListView;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.FriendKeywordDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.fragment.api.ATimelineFragment;
import com.sun.tweetfiltrr.fragment.pulltorefresh.PullToRefreshView;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.utils.TwitterUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public class CustomKeywordTimelineTab extends ATimelineFragment {

    @Inject FriendKeywordDao _keywordFriendDao;

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] projection = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, TimelineDao.FULLY_QUALIFIED_PROJECTIONS);
       return new CursorLoader(getActivity(),
                TweetFiltrrProvider.CONTENT_URI_TIMELINE_FRIEND, projection,  TimelineColumn.IS_KEYWORD_SEARCH_TWEET.a() + " = ? ",
                new String[]{ Integer.toString(1)},
               TimelineColumn.TWEET_ID.a() + " DESC " + " LIMIT " + getTimeLineCount());
    }

//    @Override
//    public void onLoad(Collection<Future<Collection<ParcelableUser>>> futureTask_) {
//
//        Collection<IDBDao<ParcelableTweet>> daos = new ArrayList<IDBDao<ParcelableTweet>>();
//        daos.add(_timelineDao);
//        AsyncUserDBUpdateTask< Intcar screeeger> asyncTask =
//                new AsyncUserDBUpdateTask<Integer>(3 , TimeUnit.MINUTES ,
//                        daos, _timelineBufferedDBUpdater, _pullToRefreshHandler);
//
//        asyncTask.execute(futureTask_.toArray(new Future[futureTask_.size()]));
//
//    }

    @Override
    protected PullToRefreshView getPullToRefreshView(SimpleCursorAdapter adapter_, ParcelableUser currentUser_,
                                                     ZoomListView.OnItemFocused listener_, Collection<IDatabaseUpdater> updaters_) {
        return new PullToRefreshView<Collection<ParcelableUser>>(getActivity(), currentUser_, this, adapter_, this, this,
                listener_,updaters_, TwitterUtil.getInstance().getGlobalImageLoader(getActivity()), R.layout.empty_custom_timline_layout);
    }

    private Collection<ParcelableUser> getUsersWithKeywordGroup(int remainingSearchLimit_){
        return _keywordFriendDao.getEntries(null,null,
                FriendTable.FriendColumn.COLUMN_MAXID.p() + " DESC, " +
                        FriendTable.FriendColumn.COLUMN_LAST_DATETIME_SYNC.p() + " ASC " +
                        " LIMIT " + Integer.toString(remainingSearchLimit_) );
    }


    @Override
    public Collection<Callable<Collection<ParcelableUser>>> getUsersRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();

            callables.addAll(getTweetRetriver().getCallableRetrieverList(getUsersWithKeywordGroup(180),
                    shouldRunOnce_, shouldLookForOldTweets, this));

        return callables;
    }

    @Override
    protected int getLoaderID() {
        return 0x51;
    }

}
