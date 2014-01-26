package com.sun.tweetfiltrr.database.dbupdater.impl;

import com.sun.tweetfiltrr.database.dao.IDBDao;
import com.sun.tweetfiltrr.database.dbupdater.api.IUserUpdater;
import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.Collection;

/**
 * Created by Sundeep.Kahlon on 23/01/14.
 */
public class UserUpdater implements IUserUpdater {

    private IDBDao<ParcelableUser> _userDao;
    private String[] _columns;

    public UserUpdater(IDBDao<ParcelableUser> userDao_, String[] columns_){
        _userDao =userDao_;
        _columns = columns_;
    }

    public UserUpdater(IDBDao<ParcelableUser> userDao_){
        _userDao =userDao_;
    }


    @Override
    public void updateUsersToDB(Collection<ParcelableUser> users_) {
        if(_columns!=null){
            _userDao.insertOrUpdate(users_, _columns);
        }else{
            _userDao.insertOrUpdate(users_);
        }
    }
}
