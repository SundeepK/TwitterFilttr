package com.sun.tweetfiltrr.activity.adapter;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;

import java.util.Collection;

/**
 * Created by Sundeep on 09/02/14.
 *
 */
public interface IUpdatedGroup {

    public Collection<ParcelableUser> getChangedGroupIdsForUsers(long groupId_);
}
