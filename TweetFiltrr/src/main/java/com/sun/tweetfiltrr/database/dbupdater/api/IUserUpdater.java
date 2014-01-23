package com.sun.tweetfiltrr.database.dbupdater.api;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.Collection;

/**
 * Created by Sundeep.Kahlon on 23/01/14.
 */
public interface IUserUpdater {

    public void updateUsersToDB(Collection<ParcelableUser> users_);

}
