package com.sun.tweetfiltrr.database.dbupdater.impl;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDBUpdater;
import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sundeep on 10/01/14.
 */
public class SimpleDBUpdater<T extends IParcelableTwitter> implements IDBUpdater<T> {

    private static final String TAG = SimpleDBUpdater.class.getName();

    @Override
    public void flushToDB(IDBDao<T> daosToFlushTo_, Collection<T> itemsToFlushToDB_)
            throws ExecutionException, InterruptedException {
        daosToFlushTo_.insertOrUpdate(itemsToFlushToDB_);
    }

    @Override
    public void flushToDB(Collection<IDBDao<T>> daosToFlushTo_, Collection<T> itemsToFlushToDB_)
            throws ExecutionException, InterruptedException {

        for(IDBDao<T> dao : daosToFlushTo_){
            Log.v(TAG, "updating with dao" + dao.toString() + "with count " + itemsToFlushToDB_.size());
            dao.insertOrUpdate(itemsToFlushToDB_);
        }

    }
}
