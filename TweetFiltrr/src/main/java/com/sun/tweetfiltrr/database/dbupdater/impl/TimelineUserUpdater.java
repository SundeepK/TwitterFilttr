package com.sun.tweetfiltrr.database.dbupdater.impl;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IUserUpdater;
import com.sun.tweetfiltrr.parcelable.ParcelableTimeLineEntry;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Sundeep.Kahlon on 23/01/14.
 */
public class TimelineUserUpdater implements IUserUpdater {
    private IDBDao<ParcelableTimeLineEntry> _timeLineDao;
    public TimelineUserUpdater(IDBDao<ParcelableTimeLineEntry> timeLineDao_){
        _timeLineDao = timeLineDao_;
    }

    @Override
    public void updateUsersToDB(Collection<ParcelableUser> users_) {
        Collection<ParcelableTimeLineEntry> timelines = new ArrayList<ParcelableTimeLineEntry>();
        for(ParcelableUser users : users_){
            timelines.addAll(users.getUserTimeLine());
        }
        _timeLineDao.insertOrUpdate(timelines);
    }
}
