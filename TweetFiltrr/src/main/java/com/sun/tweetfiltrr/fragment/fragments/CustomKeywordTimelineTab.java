package com.sun.tweetfiltrr.fragment.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.sun.tweetfiltrr.daoflyweigth.impl.DaoFlyWeightFactory;
import com.sun.tweetfiltrr.database.DBUtils;
import com.sun.tweetfiltrr.database.dao.FriendDao;
import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dao.TimelineDao;
import com.sun.tweetfiltrr.database.providers.TweetFiltrrProvider;
import com.sun.tweetfiltrr.database.tables.FriendTable;
import com.sun.tweetfiltrr.fragment.api.ATimelineFragment;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;
import com.sun.tweetfiltrr.parcelable.ParcelableUserToKeywords;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.sun.tweetfiltrr.database.tables.FriendTable.FriendColumn;
import static com.sun.tweetfiltrr.database.tables.TimelineTable.TimelineColumn;

public class CustomKeywordTimelineTab extends ATimelineFragment  {


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        String[] projection = DBUtils.concatColumns(FriendDao.FULLY_QUALIFIED_PROJECTIONS, TimelineDao.FULLY_QUALIFIED_PROJECTIONS);
       return new CursorLoader(getActivity(),
                TweetFiltrrProvider.CONTENT_URI_TIMELINE_FRIEND, projection, FriendColumn.IS_FRIEND.a() + " = ?  " + " AND "
               + TimelineColumn.IS_KEYWORD_SEARCH_TWEET.a() + " = ? ",
                new String[]{Integer.toString(1), Integer.toString(1)},
               TimelineColumn.TWEET_ID.a() + " DESC " + " LIMIT " + getTimeLineCount());
    }

    @Override
    public void onLoad(Collection<Future<Collection<ParcelableUser>>> futureTask_) {

//        Collection<IDBDao<ParcelableTimeLineEntry>> daos = new ArrayList<IDBDao<ParcelableTimeLineEntry>>();
//        daos.add(_timelineDao);
//        AsyncUserDBUpdateTask< Integer> asyncTask =
//                new AsyncUserDBUpdateTask<Integer>(3 , TimeUnit.MINUTES ,
//                        daos, _timelineBufferedDBUpdater, _pullToRefreshHandler);
//
//        asyncTask.execute(futureTask_.toArray(new Future[futureTask_.size()]));

    }

    private Collection<ParcelableUserToKeywords> getUsersWithKeywordGroup(int remainingSearchLimit_){
        DaoFlyWeightFactory daoFlyWeightFactory = DaoFlyWeightFactory.getInstance(getActivity().getContentResolver());

        IDBDao<ParcelableUserToKeywords> _keywordFriendDao = (IDBDao<ParcelableUserToKeywords>)
                daoFlyWeightFactory.getDao(DaoFlyWeightFactory.DaoFactory.FRIEND_KEYWORD_DAO, null);
        return _keywordFriendDao.getEntries(null,null,
                FriendTable.FriendColumn.COLUMN_MAXID.p() + " DESC, " +
                        FriendTable.FriendColumn.COLUMN_LAST_DATETIME_SYNC.p() + " ASC " +
                        " LIMIT " + remainingSearchLimit_ );
    }


    @Override
    public Collection<Callable<Collection<ParcelableUser>>> getTweetRetriever(boolean shouldRunOnce_, boolean shouldLookForOldTweets) {
        Collection<Callable<Collection<ParcelableUser>>> callables = new ArrayList<Callable<Collection<ParcelableUser>>>();
        if (!shouldLookForOldTweets) {
          //  callables.addAll(getTweetRetriver().getCallableRetrieverList(getUsersWithKeywordGroup(180), shouldRunOnce_, shouldLookForOldTweets));
        }
        return null;
    }

}
