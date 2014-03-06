package com.sun.tweetfiltrr.database.dbupdater.api;


import com.sun.tweetfiltrr.database.dao.api.IDBDao;
import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

/**
 * Created by Sundeep on 07/12/13.
 */
public interface IDBUpdater<T extends IParcelableTwitter> {

    public void flushToDB(IDBDao<T> daosToFlushTo_, Collection<T> itemsToFlushToDB_) throws ExecutionException, InterruptedException;
    public void flushToDB(Collection<IDBDao<T>> daosToFlushTo_, Collection<T> itemsToFlushToDB_) throws ExecutionException, InterruptedException;

}
