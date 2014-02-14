package com.sun.tweetfiltrr.fragment.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.TimeLineDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.fragment.api.ATimelineFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public class CustomKeywordTimelineTab extends ATimelineFragment {

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] projection = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, TimeLineDao.FULLY_QUALIFIED_PROJECTIONS);
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

    private Collection<ParcelableUser> getUsersWithKeywordGroup(int remainingSearchLimit_){
        DaoFlyWeightFactory daoFlyWeightFactory = DaoFlyWeightFactory.getInstance(getActivity().getContentResolver());

        IDBDao<ParcelableUser> _keywordFriendDao = (IDBDao<ParcelableUser>)
                daoFlyWeightFactory.getDao(DaoFlyWeightFactory.DaoFactory.FRIEND_KEYWORD_DAO, null);
        return _keywordFriendDao.getEntries(null,null,
                FriendTable.FriendColumn.COLUMN_MAXID.p() + " DESC, " +
                        FriendTable.FriendColumn.COLUMN_LAST_DATETIME_SYNC.p() + " ASC " +
                        " LIMIT " + Integer.toString(remainingSearchLimit_) );
    }


    @Override
    public Collection<Callable<Collection<ParcelableUser>>> getTweetRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();

            callables.addAll(getTweetRetriver().getCallableRetrieverList(getUsersWithKeywordGroup(180),
                    shouldRunOnce_, shouldLookForOldTweets, this));

        return callables;
    }


}
