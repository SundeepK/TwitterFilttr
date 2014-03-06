package com.sun.tweetfiltrr.database.dbupdater.impl;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.api.IDBDao;
import com.sun.tweetfiltrr.parcelable.parcelable.api.IParcelableTwitter;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Created by Sundeep on 07/12/13.
 */
public class DBFlusherTask<T extends IParcelableTwitter> implements Callable<Void> {

    private IDBDao<T> _dao;
    private Collection<T> _parcelablesToFlush;
    private String[] _columns;
    private static String TAG = DBFlusherTask.class.getName();

    public DBFlusherTask(IDBDao<T> dao_, String[] columns_, Collection<T> parcelablesToFlush_ ){
        _dao = dao_;
        _parcelablesToFlush = parcelablesToFlush_;
        _columns = columns_;
    }

    @Override
    public Void call() throws Exception {
        Log.v(TAG, "Inserting into DB" );
        _dao.insertOrUpdate(_parcelablesToFlush, _columns);
        return null;
    }

    protected IDBDao<T> getDao() {
        return _dao;
    }

    protected Collection<T> getParcelablesToFlush() {
        return _parcelablesToFlush;
    }

    protected String[] getColumns() {
        return _columns;
    }
}
