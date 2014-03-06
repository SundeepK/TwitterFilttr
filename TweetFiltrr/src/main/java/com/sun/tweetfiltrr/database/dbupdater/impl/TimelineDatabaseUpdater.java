package com.sun.tweetfiltrr.database.dbupdater.impl;

import com.sun.tweetfiltrr.database.dao.api.IDBDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.parcelable.ParcelableTweet;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Sundeep.Kahlon on 23/01/14.
 */
public class TimelineDatabaseUpdater implements IDatabaseUpdater {
    private IDBDao<ParcelableTweet> _timeLineDao;
    public TimelineDatabaseUpdater(IDBDao<ParcelableTweet> timeLineDao_){
        _timeLineDao = timeLineDao_;
    }

    @Override
    public void updateUsersToDB(Collection<ParcelableUser> users_) {
        Collection<ParcelableTweet> timelines = new ArrayList<ParcelableTweet>();
        for(ParcelableUser users : users_){
            timelines.addAll(users.getUserTimeLine());
        }
        _timeLineDao.insertOrUpdate(timelines);
    }
}
