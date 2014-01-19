package com.sun.tweetfiltrr.parcelable;

import com.sun.tweetfiltrr.parcelable.parcelable.api.ICachedUser;

/**
 * Created by Sundeep on 19/01/14.
 */
public class CachedFollowerDetails implements ICachedUser {

    private ParcelableUser _user;
    public CachedFollowerDetails(ParcelableUser user_){
        _user = user_;
    }


    @Override
    public int getTotalCount() {
       return _user.getTotalFollowerCount();
    }

    @Override
    public void setTotalCount(int totalCount_) {
        _user.setTotalFollowerCount(totalCount_);
    }

    @Override
    public long getLastPageNumber() {
        return _user.getLastFollowerPageNumber();
    }

    @Override
    public void setLastPageNumber(long pageNumber_) {
        _user.setLastFollowerPageNumber(pageNumber_);
    }

    @Override
    public int getCurrentUserCount() {
       return _user.getCurrentFollowerCount();
    }

    @Override
    public int getLastArrayIndex() {
        return _user.getLastFollowerIndex();
    }

    @Override
    public void setCurrentCount(int count_) {
        _user.setCurrentFollowerCount(count_);
    }

    @Override
    public void setLastArrayIndex(int index_) {
        _user.setLastFollowerIndex(index_);
    }

    @Override
    public long[] getUserIds() {
        return _user.getFollowerIDs();
    }

    @Override
    public void setUserIds(long[] userIds_) {
            _user.setFollowerIDs(userIds_);
    }

    @Override
    public ParcelableUser getUser() {
        return _user;
    }
}
