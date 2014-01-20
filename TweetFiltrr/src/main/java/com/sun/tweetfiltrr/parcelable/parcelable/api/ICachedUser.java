package com.sun.tweetfiltrr.parcelable.parcelable.api;

import com.sun.tweetfiltrr.parcelable.ParcelableUser;

/**
 * Created by Sundeep on 19/01/14.
 */
public interface ICachedUser {

    public int getTotalCount();
    public void setTotalCount(int totalCount_);
    public long getLastPageNumber();
    public void setLastPageNumber(long pageNumber_);
    public int getCurrentUserCount();
    public int getLastArrayIndex();
    public void setCurrentCount(int count_);
    public void setLastArrayIndex(int index_);
    public long[] getUserIds();
    public void setUserIds(long[] userIds_);
    public ParcelableUser getUser();
    public long getMaxId();
    public void setMaxId(long id_);
    public long getSinceId();
    public void setSinceId(long id_);

}
