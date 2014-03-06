package com.sun.tweetfiltrr.database.dbupdater.impl;

import android.util.Log;

import com.sun.tweetfiltrr.database.dao.api.IDBDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IDatabaseUpdater;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Sundeep.Kahlon on 23/01/14.
 */
public class DatabaseUpdater implements IDatabaseUpdater {

    private static final String TAG = DatabaseUpdater.class.getName();
    private IDBDao<ParcelableUser> _userDao;
    private String[] _columns;

    public DatabaseUpdater(IDBDao<ParcelableUser> userDao_, String[] columns_){
        _userDao =userDao_;
        _columns = columns_;
    }

    public DatabaseUpdater(IDBDao<ParcelableUser> userDao_){
        _userDao =userDao_;
    }


    @Override
    public void updateUsersToDB(Collection<ParcelableUser> users_) {
        if(_columns!=null){
            Log.v(TAG, "updating users" + Arrays.toString(users_.toArray()) );
            _userDao.insertOrUpdate(users_, _columns);
        }else{
            Log.v(TAG, "updating users" + Arrays.toString(users_.toArray()) );
            _userDao.insertOrUpdate(users_);

        }
    }
}
